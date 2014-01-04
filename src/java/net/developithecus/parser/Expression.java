package net.developithecus.parser;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private boolean hidden;

    public abstract ExpressionChecker checker();

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Expression hide() {
        setHidden(true);
        return this;
    }
}
