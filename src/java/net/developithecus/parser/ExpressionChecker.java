package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public abstract class ExpressionChecker {
    private final ParsingContext ctx;
    private final int beginIndex;

    protected ExpressionChecker(ParsingContext ctx) {
        this.beginIndex = ctx.getNextIndex();
        this.ctx = ctx;
    }

    public abstract void check();

    public ParsingContext getCtx() {
        return ctx;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    protected void rollback() {
        ctx.rollback(beginIndex);
    }

    protected void commitLeafNode(String value) {
        Node node = new Node(value, beginIndex, ctx.getNextIndex());
        ctx.setResult(ResultType.COMMIT);
        ctx.commitSingleNode(node);
    }

    protected void continueProcessing() {
        ctx.setResult(ResultType.CONTINUE);
        ctx.clearCommitted();
    }

}
