/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser;

import net.developithecus.parser.ExpressionCache.CacheItem;
import net.developithecus.parser.ExpressionCache.CommitItem;
import net.developithecus.parser.ExpressionCache.RollbackItem;
import net.developithecus.parser.definitions.AbstractDefinition;
import net.developithecus.parser.definitions.AbstractExpression;
import net.developithecus.parser.definitions.Rule;

import java.util.Deque;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private final StringBuilder source = new StringBuilder();
    private final Deque<Entry> stack = new LinkedList<Entry>();
    private final ExpressionCache cache = new ExpressionCache();
    private Group rootGroup;
    private Position position;
    private ErrorMessage errorMessage = null;
    private boolean exception;
    private boolean closed = false;
    private int iterations;
    private long timeSpent;
    private AbstractDefinition mainDefinition;

    public void setMainRule(AbstractDefinition definition) throws ParserException {
        mainDefinition = definition;
        clear();
    }

    public void clear() throws ParserException {
        cache.clear();
        stack.clear();
        source.setLength(0);
        closed = false;
        errorMessage = null;
        exception = false;
        timeSpent = 0;
        rootGroup = null;
        position = new Position();
        iterations = 0;
    }

    public void append(char ch) throws ParserException {
        checkClosed();
        source.append(ch);
        process();
    }

    public void append(CharSequence str) throws ParserException {
        checkClosed();
        source.append(str);
        process();
    }

    public void close() throws ParserException {
        if (closed) {
            return;
        }
        closed = true;
        process();
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, "iterations {0}; source len: {1}; time: {2} ms", new Object[]{iterations, source.length(), timeSpent / 1000000.0});
        }
    }

    public Group getRootGroup() {
        return rootGroup;
    }

    private void checkClosed() throws ParserClosedException {
        if (closed) {
            throw new ParserClosedException();
        }
    }

    private void process() throws ParserException {
        long start = System.nanoTime();
        logger.entering("Parser", "process");
        try {
            if (stack.isEmpty()) {
                Entry p = new Entry(null, mainDefinition.buildExpression());
                stack.push(p);
                p.doInit();
            }
            while (position.getIndex() < source.length() || !stack.isEmpty() && closed) {
                iterations++;
                stack.peek().turn();
                if (exception) {
                    throw new ParserException(errorMessage.toString());
                }
            }
        } finally {
            logger.exiting("Parser", "process");
            timeSpent += System.nanoTime() - start;
        }
    }

    private static StringBuilder compactValueCache = new StringBuilder();

    public static String buildCompactValue(Group group) {
        compactValueCache.setLength(0);
        buildCompactValue(group, compactValueCache);
        return compactValueCache.toString();
    }

    public static void buildCompactValue(Group group, StringBuilder result) {
        if (group.getValue() != null && !group.isRule()) {
            result.append(group.getValue());
        }
        for (Group c : group.getChildren()) {
            buildCompactValue(c, result);
        }
    }

    private void makeCommittedGroup(AbstractExpression expr, Group group, Group parentGroup) {
        if (expr.isIgnore()) {
            return;
        }

        if (expr.isCompact()) {
            group.setValue(Parser.buildCompactValue(group));
            group.clear();
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "{0}: Committed value: [{1}]", new Object[]{this, group});
            }
        }
        if (parentGroup == null) {
            rootGroup = group;
            return;
        }
        if (group.getValue() == null) {
            for (Group c : group.getChildren()) {
                parentGroup.add(c);
            }
        } else {
            parentGroup.add(group);
        }
    }

    private class Entry implements Turn {

        private final Entry parentEntry;
        private final AbstractExpression expression;
        private final Group resultGroup;
        private final Position startPosition;
        private String name;

        private Entry(Entry parent, AbstractExpression expr) {
            this.expression = expr;
            boolean isRule = expr.getDefinition() instanceof Rule;
            this.resultGroup = new Group(isRule);
            if (isRule) {
                this.resultGroup.setValue(((Rule) expr.getDefinition()).getName());
            }
            this.startPosition = position.clone();
            this.parentEntry = parent;
        }

        @Override
        public void rollback() throws ParserException {
            rollback(null);
        }

        @Override
        public void rollback(String msg) throws ParserException {
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0}: ROLLBACK@{1}; msg: [{2}]", new Object[]{this, position, msg});
            }
            position = startPosition;
            cache.addRollback(startPosition, expression.getDefinition(), msg);
            errorMessage = new ErrorMessage(getName(), msg, position, errorMessage);
            stack.pop();
            if (stack.isEmpty()) {
                exception = true;
            } else {
                stack.peek().doAfterRollback();
            }
        }

        private void doAfterRollback() throws ParserException {
            expression.afterRollback(this);
        }

        private void nextPosition() {
            if (position.getIndex() < source.length()) {
                position.next(source.charAt(position.getIndex()));
            } else {
                position.next((char) 0);
            }
        }

        private String getName() {
            if (expression.getDefinition() instanceof Rule) {
                return ((Rule) expression.getDefinition()).getName();
            } else {
                return null;
            }
        }

        @Override
        public void commit() throws ParserException {
            commit(null);
        }

        @Override
        public void commit(Object value) throws ParserException {
            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "{0}: COMMIT@{1}; result: [{2}]", new Object[]{this, position, resultGroup});
            }
            if (value != null) {
                resultGroup.setValue(value);
            }
            if (expression.isReapply()) {
                position = startPosition;
            }
            cache.addCommit(startPosition, expression.getDefinition(), resultGroup, position);
            errorMessage = null;
            stack.pop();
            makeCommittedGroup(expression, resultGroup, parentEntry == null ? null : parentEntry.resultGroup);
            if (!stack.isEmpty()) {
                stack.peek().doAfterCommit();
            }
        }

        private void doAfterCommit() throws ParserException {
            expression.afterCommit(this);
        }

        @Override
        public void accept() {
            nextPosition();
        }

        private StringBuilder buildNameCache = new StringBuilder();

        private void buildName() {
            buildNameCache.setLength(0);
            Entry e = this;
            while (e != null) {
                buildNameCache.append(e.expression.toString());
                e = e.parentEntry;
                if (e != null) {
                    buildNameCache.append("<");
                }
            }
            name = buildNameCache.toString();
        }

        @Override
        public String toString() {
            if (name == null) {
                buildName();
            }
            return "Entry{" + name + "; " + startPosition + '}';
        }

        @Override
        public Group result() {
            return resultGroup;
        }

        private void doInit() throws ParserException {
            expression.init(this);
        }

        @Override
        public void push(AbstractExpression expr) throws ParserException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "{0}: PUSH@{1}; expr: {2}", new Object[]{this, position, expr});
            }
            CacheItem cacheItem = cache.getCacheItem(position, expr.getDefinition());
            if (cacheItem == null) {
                Entry p = new Entry(this, expr);
                stack.push(p);
                p.doInit();
            } else if (cacheItem instanceof RollbackItem) {
                RollbackItem item = (RollbackItem) cacheItem;
                errorMessage = new ErrorMessage(getName(), item.getMsg(), position, errorMessage);
                doAfterRollback();
            } else {
                CommitItem item = (CommitItem) cacheItem;
                errorMessage = null;
                if (!expr.isReapply()) {
                    position = item.getResultPosition().clone();
                }
                makeCommittedGroup(expr, item.getResultGroup(), resultGroup);
                doAfterCommit();
            }
        }

        @Override
        public void exception(String msg) {
            errorMessage = new ErrorMessage(getName(), msg, startPosition, null);
            exception = true;
        }

        private void turn() throws ParserException {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "{0}: TURN@{1}", new Object[]{this, position});
            }


            int ch = -1;
            if (!closed || position.getIndex() < source.length()) {
                ch = source.charAt(position.getIndex());
            }
            expression.turn(this, ch);

        }
    }
}
