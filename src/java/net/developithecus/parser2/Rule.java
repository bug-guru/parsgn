package net.developithecus.parser2;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Rule {
    private String name;
    private Expression expression;

    public String name() {
        return name;
    }

    public Rule name(String name) {
        this.name = name;
        return this;
    }

    public Expression expression() {
        return expression;
    }

    public Rule expression(Expression expression) {
        this.expression = expression;
        return this;
    }

    public Expression.ExpressionChecker checker() {
        return checker(null);
    }

    public Expression.ExpressionChecker checker(Node parent) {
        Node result = parent == null ? new Node() : parent.newChild();
        return expression.checker(result);
    }
}
