package net.developithecus.parser.expr;

import net.developithecus.parser.*;

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
            logger.entering("SequentialGroupExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                    collectNodes();
                    doCommitOrContinue();
                    break;
                case ROLLBACK:
                    doRollback();
                    break;
                case CONTINUE:
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx.getResult());
            }
            logger.exiting("SequentialGroupExpression.Checker", "check", ctx);
        }

        private void doRollback() throws ParsingException {
            if (curExpr.isOptional()) {
                doCommitOrContinue();
            } else {
                rollback();
            }
        }

        private void doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                continueProcessing();
            } else if (getNodes().isEmpty()) {
                throw new ParsingException("group without result");
            } else {
                commitNodes();
            }
        }

        @Override
        protected String getName() {
            return "group";
        }
    }
}
