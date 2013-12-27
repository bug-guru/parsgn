package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public abstract class ExpressionChecker {
    private ParsingContext ctx;

    void init(ParsingContext ctx) {
        this.ctx = ctx;
    }

    public abstract Expression next();

    public abstract void check() throws ParsingException;

    protected abstract String getName();

    public ParsingContext getCtx() {
        return ctx;
    }

    @Override
    public String toString() {
        return getName();
    }
}
