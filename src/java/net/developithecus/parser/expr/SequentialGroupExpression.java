package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingContext;
import net.developithecus.parser.ParsingException;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialGroupExpression extends GroupExpression {
    private static final Logger logger = Logger.getLogger(SequentialGroupExpression.class.getName());

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
            ParsingContext ctx = getCtx();
            logger.entering("SequentialGroupExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                case ROLLBACK_OPTIONAL:
                    doCommitOrContinue();
                    break;
                case ROLLBACK:
                    ctx.markForRollback();
                    break;
                case CONTINUE:
                    ctx.markForContinue();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx.getResult());
            }
            logger.exiting("SequentialGroupExpression.Checker", "check", ctx);
        }

        private void doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                getCtx().markForContinue();
            } else if (!getCtx().hasCommitted()) {
                throw new ParsingException("group without result");
            } else {
                getCtx().markForCommit();
            }
        }

        @Override
        protected String getName() {
            return "group";
        }
    }
}
