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

public class AltGroupExpression extends GroupExpression {
    private static final Logger logger = Logger.getLogger(AltGroupExpression.class.getName());

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
            logger.entering("AltGroupExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                    ctx.markForCommit();
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    doRollbackOrContinue();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx.getResult());
            }
            logger.exiting("AltGroupExpression.Checker", "check", ctx);
        }

        private void doRollbackOrContinue() {
            if (expressions.hasNext()) {
                getCtx().markForContinue();
            } else {
                getCtx().markForRollback();
            }
        }

        @Override
        protected String getName() {
            return "alt";
        }
    }
}
