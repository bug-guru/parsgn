package net.developithecus.parser2;

/**
 *
 */
public class ReferenceRule extends Rule {
    private Rule reference;

    public Rule reference() {
        return reference;
    }

    public ReferenceRule reference(Rule reference) {
        this.reference = reference;
        return this;
    }
}
