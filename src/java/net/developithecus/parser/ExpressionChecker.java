package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public abstract class ExpressionChecker {
    private CheckerContext ctx;

    void init(CheckerContext ctx) {
        this.ctx = ctx;
    }

    public abstract Expression next();

    public abstract void check() throws ParsingException;

    protected abstract String getName();

    protected final CheckerContext ctx() {
        return ctx;
    }

    @Override
    public String toString() {
        return getName();
    }
}
