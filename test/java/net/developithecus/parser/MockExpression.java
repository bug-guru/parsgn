package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class MockExpression extends Expression {

    private boolean optional;
    private Result result;

    public boolean optional() {
        return optional;
    }

    public MockExpression optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public Result result() {
        return result;
    }

    public MockExpression result(Result result) {
        this.result = result;
        return this;
    }

    @Override
    public ExpressionChecker checker(int pos) {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private Checker() {
            super();
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            return result;
        }

        @Override
        protected boolean isOptional() {
            return optional;
        }
    }
}
