package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;
import net.developithecus.parser.exceptions.SyntaxErrorException;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public class ParsingContext<T> {
    private int index;
    private ParsingEntry entry;
    private int nextIndex;
    private final Deque<Holder> stack = new LinkedList<>();
    private Position maxPos;
    private final ResultBuilder<T> builder;

    public ParsingContext(Expression root, ResultBuilder<T> builder) {
        this.builder = builder;
        maxPos = new Position(1, 1);
        entry = new ParsingEntry(maxPos, 0);
        Holder holder = new ResultHolder();
        holder.beginPosition = maxPos;
        stack.push(holder);
        pushExpression(root);
    }

    private void reset(ParsingEntry entry) {
        if (maxPos == null || maxPos.compareTo(entry.getPosition()) < 0) {
            maxPos = entry.getPosition();
        }
        this.index = nextIndex;
        this.entry = entry;
        this.nextIndex = index + 1;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public void next(ParsingEntry entry) throws ParsingException {
        reset(entry);
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while ((nextExpr = peek().checker.next()) != null) {
            pushExpression(nextExpr);
        }
    }

    private void process() throws ParsingException {
        ResultType prevResult = null;
        loop:
        while (!builder.isFinished()) {
            Holder holder = stack.peek();
            ExpressionChecker currentChecker = holder.checker;
            CheckResult checkResult = currentChecker.check(entry.getCodePoint(), prevResult);
            prevResult = checkResult.doAction(this);
            switch (prevResult) {
                case CONTINUE:
                    break loop;
                case COMMIT:
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    if (stack.size() == 1) {
                        throw new SyntaxErrorException(maxPos);
                    }
                    break;
                default:
                    throw new ParsingException("unknown result: " + prevResult);
            }
        }
    }

    private void pushExpression(Expression nextExpr) {
        ExpressionChecker nextChecker = nextExpr.checker();
        Holder holder = new Holder();
        holder.beginIndex = index;
        holder.beginPosition = entry.getPosition();
        holder.checker = nextChecker;
        stack.push(holder);
    }

    public Holder pop() {
        return stack.pop();
    }

    public Holder peek() {
        return stack.peek();
    }

    public void setNextIndex(int nextIndex) {
        this.nextIndex = nextIndex;
    }

    public class Holder {
        ExpressionChecker checker;
        int beginIndex;
        Position beginPosition;
        List<T> committedNodes;
        StringBuilder committedValue;

        protected void initValue() throws InternalParsingException {
            if (committedValue == null) {
                committedValue = new StringBuilder();
            }
        }

        protected void initNodes() throws InternalParsingException {
            if (committedNodes == null) {
                committedNodes = new ArrayList<>();
            }
        }

        protected void addNode(T node) throws InternalParsingException {
            initNodes();
            committedNodes.add(node);
        }

        protected void addNodes(List<T> nodes) {
            if (nodes == null || nodes.isEmpty()) {
                return;
            }
            if (committedNodes == null || committedNodes.isEmpty()) {
                committedNodes = nodes;
            } else {
                committedNodes.addAll(nodes);
            }
        }

        protected int length() {
            return nextIndex - beginIndex;
        }

        public void commitValue(String value) throws InternalParsingException {
            if (value == null || value.isEmpty()) {
                return;
            }
            initValue();
            committedValue.append(value);
        }

        public void commitValue(int codePoint) throws InternalParsingException {
            if (Character.isValidCodePoint(codePoint)) {
                initValue();
                committedValue.appendCodePoint(codePoint);
            }
        }

        public void commitValue(StringBuilder builder) throws InternalParsingException {
            if (builder == null || builder.length() == 0) {
                return;
            }
            initValue();
            committedValue.append(builder);
        }

        public void commitNode(String nodeName, Holder child) throws ParsingException {
            T node = wrapNode(nodeName, child);
            addNode(node);
        }

        protected T wrapNode(String nodeName, Holder child) {
            boolean emptyNodes = child == null || child.committedNodes == null || child.committedNodes.isEmpty();
            boolean emptyValue = child == null || child.committedValue == null || child.committedValue.length() == 0;
            String value = emptyValue ? null : child.committedValue.toString();
            List<T> nodes = emptyNodes ? null : child.committedNodes;
            return builder.createNode(nodeName, beginPosition, length(), value, nodes);
        }

        public void merge(Holder another) throws ParsingException {
            commitValue(another.committedValue);
            addNodes(another.committedNodes);
        }
    }

    private class ResultHolder extends Holder {

        @Override
        public void commitNode(String nodeName, Holder child) throws ParsingException {
            T node = wrapNode(nodeName, child);
            builder.committedRoot(node);
            builder.setFinished(true);
        }
    }
}
