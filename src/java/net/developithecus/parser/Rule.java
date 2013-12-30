package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public abstract class Rule {
    private String name;
    private Expression expression;

    protected Rule(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract Expression getExpression();
}
