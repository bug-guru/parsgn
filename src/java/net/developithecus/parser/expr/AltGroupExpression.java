package net.developithecus.parser.expr;

import net.developithecus.parser.*;

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
    public boolean isOptional() {
        return false;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends GroupExpressionChecker {
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
                    collectNodes();
                    commitNodes();
                    break;
                case ROLLBACK:
                    doRollbackOrContinue();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx.getResult());
            }
            logger.exiting("AltGroupExpression.Checker", "check", ctx);
        }

        private void doRollbackOrContinue() {
            if (expressions.hasNext()) {
                continueProcessing();
            } else {
                rollback();
            }
        }

        @Override
        protected String getName() {
            return "alt";
        }
    }
}
