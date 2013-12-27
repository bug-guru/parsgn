package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;
import net.developithecus.parser.Rule;

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

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) {
        this.reference = reference;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {

        @Override
        public Expression next() {
            return getReference();
        }

        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                    ctx().markForCommitGroup(reference.getName());
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    ctx().markForRollback();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx().getResult());
            }
        }

        @Override
        protected String getName() {
            return "ref[" + getReference().getName() + "]";
        }

    }
}
