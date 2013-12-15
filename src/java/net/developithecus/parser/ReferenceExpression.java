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

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private ExpressionChecker checker;

        private Checker() {
            checker = reference.expression().checker();
            getNode().setValue(reference.name());
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            Result result = checker.check(codePoint);
            if (result == Result.MATCH) {
                getNode().addChild(checker.getNode());
            }
            return result;
        }

        @Override
        protected boolean isOptional() {
            return false;
        }
    }
}
