package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class ReferenceExpression extends Expression {
    private Rule reference;

    public Rule reference() {
        return reference;
    }

    public ReferenceExpression reference(Rule reference) {
        this.reference = reference;
        return this;
    }
}
