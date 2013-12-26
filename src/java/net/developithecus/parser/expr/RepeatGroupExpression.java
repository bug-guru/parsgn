package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.GroupExpressionChecker;
import net.developithecus.parser.ParsingContext;

import java.util.Iterator;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class RepeatGroupExpression extends GroupExpression {

    @Override
    public boolean isOptional() {
        return true;
    }

    @Override
    public ExpressionChecker checker(ParsingContext ctx) {
        return new Checker(ctx);
    }

    private class Checker extends GroupExpressionChecker {
        private int turnsPassed = -1;
        private int turnBeginIndex;
        private Iterator<Expression> expressions;
        private Expression curExpr;
        private ExpressionChecker curChecker;

        protected Checker(ParsingContext ctx) {
            super(ctx);
            nextTurn();
        }

        private void nextTurn() {
            turnsPassed++;
            expressions = getExpressions().iterator();
            turnBeginIndex = getCtx().getNextIndex();
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
                    doContinue();
                    break;
                case ROLLBACK:
                    doContinueCommitOrRollback();
                    break;
                case CONTINUE:
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + getCtx().getResult());
            }
        }

        private void doContinueCommitOrRollback() {
            if (curExpr.isOptional()) {
                doContinue();
            } else if (turnsPassed > 0) {
                getCtx().setNextIndex(turnBeginIndex);
                commitNodes();
            } else {
                rollback();
            }
        }

        private void doContinue() {
            if (expressions.hasNext()) {
                nextExpr();
            } else if (getNodes().isEmpty()) {
                throw new IllegalStateException("group without result");
            } else {
                nextTurn();
            }
            continueProcessing();
        }

    }
}
