package net.developithecus.parser.expr;

import net.developithecus.parser.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class ReferenceExpression extends Expression {
    private Rule reference;

    public ReferenceExpression(Rule reference) {
        this.reference = reference;
    }

    public ReferenceExpression() {
    }

    @Override
    public boolean isOptional() {
        return false;
    }

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) {
        this.reference = reference;
    }

    @Override
    public ExpressionChecker checker(ParsingContext ctx) {
        return new Checker(ctx);
    }

    private class Checker extends ExpressionChecker {
        private ExpressionChecker checker;

        private Checker(ParsingContext ctx) {
            super(ctx);
            checker = reference.checker(ctx);
        }

        @Override
        public void check() {
            checker.check();
            switch (getCtx().getResult()) {
                case COMMIT:
                    commitWrappingNodes(reference.getName());
                    break;
                case CONTINUE:
                    continueProcessing();
                    break;
                case ROLLBACK:
                    rollback();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + getCtx().getResult());
            }
        }

        protected void commitWrappingNodes(String value) {
            Node node = new Node(value, getBeginIndex(), getCtx().getNextIndex(), getCtx().takeAndClearCommitted());
            getCtx().commitSingleNode(node);
        }

    }
}
