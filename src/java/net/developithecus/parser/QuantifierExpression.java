package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class QuantifierExpression extends Expression {
    private int minRepeats = 1;
    private int maxRepeats = 1;
    private Expression expression;

    public int minRepeats() {
        return minRepeats;
    }

    public QuantifierExpression minRepeats(int minRepeats) {
        this.minRepeats = minRepeats;
        return this;
    }


    public int maxRepeats() {
        return maxRepeats;
    }

    public QuantifierExpression maxRepeats(int maxRepeats) {
        this.maxRepeats = maxRepeats;
        return this;
    }

    public Expression expression() {
        return expression;
    }

    public QuantifierExpression expression(Expression expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public ExpressionChecker checker(Node result) {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {

        private Checker(Node parent) {
            super(parent);
        }

        @Override
        protected Result doCheck(int codePoint) {
            return null;
        }

        @Override
        protected String value() {
            return null;
        }

        @Override
        protected void reset() {

        }
    }
}
