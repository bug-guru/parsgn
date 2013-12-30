package net.developithecus.parser;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private boolean silent;


    public abstract ExpressionChecker checker();


    public boolean isSilent() {
        return silent;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public Expression silent() {
        silent = true;
        return this;
    }

    public boolean isCompact() {
        return false;
    }
}
