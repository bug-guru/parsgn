package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;

import java.util.Iterator;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialGroupExpression extends GroupExpression {

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;

        @Override
        public Expression next() {
            curExpr = expressions.next();
            return curExpr;
        }

        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                case ROLLBACK_OPTIONAL:
                    doCommitOrContinue();
                    break;
                case ROLLBACK:
                    ctx().markForRollback();
                    break;
                case CONTINUE:
                    ctx().markForContinue();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx().getResult());
            }
        }

        private void doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                ctx().markForContinue();
            } else if (!ctx().hasCommitted()) {
                throw new ParsingException("group without result");
            } else {
                ctx().markForCommit();
            }
        }

        @Override
        protected String getName() {
            return "group";
        }
    }
}
