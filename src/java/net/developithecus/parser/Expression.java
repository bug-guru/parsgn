package net.developithecus.parser;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private boolean optional = false;

    public Expression optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean optional() {
        return optional;
    }

    public abstract ExpressionNode createNode();

}
