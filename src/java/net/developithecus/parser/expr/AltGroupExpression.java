package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.GroupExpressionChecker;
import net.developithecus.parser.ParsingContext;

import java.util.Iterator;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class AltGroupExpression extends GroupExpression {

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public ExpressionChecker checker(ParsingContext ctx) {
        return new Checker(ctx);
    }

    private class Checker extends GroupExpressionChecker {
        private Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;
        private ExpressionChecker curChecker;

        private Checker(ParsingContext ctx) {
            super(ctx);
            nextExpr();
        }

        private void nextExpr() {
            curExpr = expressions.next();
            curChecker = curExpr.checker(getCtx());
        }

        @Override
        public void check() {
            curChecker.check();
            switch (getCtx().getResult()) {
                case COMMIT:
                    collectNodes();
                    commitNodes();
                    break;
                case ROLLBACK:
                    doRollbackOrContinue();
                    break;
                case CONTINUE:
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + getCtx().getResult());
            }
        }

        private void doRollbackOrContinue() {
            if (expressions.hasNext()) {
                nextExpr();
                continueProcessing();
            } else {
                rollback();
            }
        }
    }
}
