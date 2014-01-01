package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;
import net.developithecus.parser.exceptions.SyntaxErrorException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public class ParsingContext implements CheckerContext {
    private int index;
    private ParsingEntry entry;
    private int nextIndex;
    private ResultType result;
    private Deque<Holder> stack = new LinkedList<>();
    private Position maxPos;
    private ResultBuilder<?> builder;

    public ParsingContext(Expression root, ResultBuilder<?> builder) {
        this.builder = builder;
        maxPos = new Position(1, 1);
        entry = new ParsingEntry(maxPos, 0);
        pushExpression(root);
    }

    private void reset(ParsingEntry entry) {
        this.index = nextIndex;
        this.entry = entry;
        this.nextIndex = index + 1;
        this.result = ResultType.CONTINUE;
    }

    public int getCodePoint() {
        return entry.getCodePoint();
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public ResultType getResult() {
        return result;
    }

    @Override
    public void markForCommit(String nodeValue) throws ParsingException {
        Holder holder = stack.peek();
        holder.commit(nodeValue);
        result = ResultType.COMMIT;
    }

    @Override
    public void markForCommit(int codePoint) throws ParsingException {
        Holder holder = stack.peek();
        holder.commit(codePoint);
        result = ResultType.COMMIT;
    }

    @Override
    public void markForCommitGroup(String groupNodeValue) throws ParsingException {
        Holder holder = stack.peek();
        holder.commitGroup(groupNodeValue);
        result = ResultType.COMMIT;
    }

    @Override
    public void markForCommit() throws ParsingException {
        result = ResultType.COMMIT;
    }

    @Override
    public void markForRollback() throws ParsingException {
        markForRollback(false);
    }

    @Override
    public void markForRollbackOptional() throws ParsingException {
        markForRollback(true);
    }

    private void markForRollback(boolean optional) {
        this.result = optional ? ResultType.ROLLBACK_OPTIONAL : ResultType.ROLLBACK;
        Holder holder = stack.peek();
        this.nextIndex = holder.beginIndex;
        holder.committed = false;
    }

    @Override
    public void markForContinue() throws ParsingException {
        this.result = ResultType.CONTINUE;
    }

    public ExpressionChecker peekChecker() {
        Holder holder = stack.peek();
        return holder == null ? null : holder.checker;
    }

    public boolean hasCommitted() {
        Holder holder = stack.peek();
        return holder.committed;
    }

    public void next(ParsingEntry entry) throws ParsingException {
        reset(entry);
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while ((nextExpr = peekChecker().next()) != null) {
            pushExpression(nextExpr);
        }
    }

    private void process() throws ParsingException {
        boolean loop = true;
        while (loop) {
            Holder holder = stack.peek();
            ExpressionChecker currentChecker = holder.checker;
            currentChecker.check();
            switch (getResult()) {
                case CONTINUE:
                    loop = false;
                    break;
                case COMMIT:
                    if (maxPos == null || maxPos.compareTo(holder.beginPosition) < 0) {
                        maxPos = holder.beginPosition;
                    }
                    popChecker();
                    Holder parent = stack.peek();
                    if (parent == null) {
                        builder.mergeNodes();
                        return;
                    } else {
                        parent.addCommitted();
                    }
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    if (!popChecker()) {
                        throw new SyntaxErrorException(maxPos);
                    }
                    builder.removeHead();
                    break;
                default:
                    throw new ParsingException("unknown result: " + getResult());
            }
        }
    }

    private void pushExpression(Expression nextExpr) {
        Holder top = stack.peek();
        ExpressionChecker nextChecker = nextExpr.checker();
        nextChecker.init(this);
        Holder holder = newHolder(nextExpr, top);
        holder.beginIndex = index;
        holder.beginPosition = entry.getPosition();
        holder.expression = nextExpr;
        holder.checker = nextChecker;
        stack.push(holder);
    }

    private Holder newHolder(Expression nextExpression, Holder topHolder) {
        if (nextExpression.isSilent() || topHolder != null && topHolder instanceof SilentHolder) {
            return new SilentHolder();
        } else if (nextExpression.isCompact() && !(topHolder instanceof CompactHolder)) {
            return new ValueRootHolder();
        } else if (nextExpression.isCompact() || topHolder instanceof CompactHolder || topHolder instanceof ValueRootHolder) {
            return new CompactHolder();
        } else {
            return new DefaultHolder();
        }
    }

    public boolean popChecker() {
        stack.pop();
        return !stack.isEmpty();
    }

    @Override
    public String toString() {
        return "ParsingContext{" +
                "result=" + result +
                ", nextIndex=" + nextIndex +
                ", entry=" + entry +
                ", index=" + index +
                ", stack=" + stack +
                '}';
    }

    private abstract class Holder {
        Expression expression;
        ExpressionChecker checker;
        int beginIndex;
        Position beginPosition;
        boolean committed;

        private Holder() {
            builder.push();
        }

        protected void committed() {
            this.committed = true;
        }

        protected int length() {
            return nextIndex - beginIndex;
        }

        abstract void commit(String value) throws InternalParsingException;

        abstract void commit(int codePoint) throws InternalParsingException;

        abstract void commitGroup(String nodeName);

        abstract void addCommitted() throws InternalParsingException;

    }

    private class DefaultHolder extends Holder {
        @Override
        void commit(String value) throws InternalParsingException {
            committed();
            builder.addNode(value, beginPosition, length());
        }

        @Override
        void commit(int codePoint) throws InternalParsingException {
            committed();
            builder.addNode(StringUtils.fromCodePoint(codePoint), beginPosition, length());
        }

        @Override
        void commitGroup(String nodeName) {
            committed();
            builder.addNodeWithChildren(nodeName, beginPosition, length());
        }

        @Override
        void addCommitted() throws InternalParsingException {
            committed();
            builder.mergeNodes();
        }
    }

    private class SilentHolder extends Holder {

        void commit(String value) throws InternalParsingException {
            committed();
        }

        public void commit(int codePoint) throws InternalParsingException {
            committed();
        }

        void commitGroup(String nodeName) {
            committed();
        }

        void addCommitted() throws InternalParsingException {
            committed();
            builder.removeHead();
        }
    }

    private class CompactHolder extends Holder {

        void commit(String value) throws InternalParsingException {
            committed();
            builder.appendValue(value);
        }

        public void commit(int codePoint) throws InternalParsingException {
            committed();
            builder.appendValue(codePoint);
        }

        void commitGroup(String nodeName) {
            committed();
        }

        void addCommitted() throws InternalParsingException {
            committed();
            builder.mergeValue();
        }
    }

    private class ValueRootHolder extends Holder {

        void commit(String value) throws InternalParsingException {
            committed();
            throw new InternalParsingException("valueRoot is set");
        }

        public void commit(int codePoint) throws InternalParsingException {
            committed();
            throw new InternalParsingException("valueRoot is set");
        }

        void commitGroup(String nodeName) {
            committed();
            builder.addNodeWithValue(nodeName, beginPosition, length());
        }

        void addCommitted() throws InternalParsingException {
            committed();
            builder.mergeValue();
        }
    }
}
