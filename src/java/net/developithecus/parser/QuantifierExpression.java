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
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }

    private class Checker extends ExpressionChecker {
        private int round = 0;
        private ExpressionChecker checker;

        private Checker(int pos) {
            super(pos);
            checker = expression.checker(pos);
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            if (round >= maxRepeats) {
                throw new FlowCompleteException();
            }
            Result result = checker.check(codePoint);
            if (result == Result.MISMATCH) {
                return isOptional() && round > 0 ? Result.MATCH : Result.MISMATCH;
            }
            if (result == Result.MORE) {
                return Result.MORE;
            }
            round++;
            if (result == Result.MATCH) {
                getNode().addChild(checker.getNode());
                if (round < maxRepeats) {
                    checker = expression.checker();
                    return Result.MORE;
                } else {
                    return Result.MATCH;
                }
            }
            throw new IllegalStateException();
        }

        @Override
        protected boolean isOptional() {
            return minRepeats <= round;
        }

    }
}
