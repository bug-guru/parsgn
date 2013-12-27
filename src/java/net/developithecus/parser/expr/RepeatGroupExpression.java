package net.developithecus.parser.expr;

import net.developithecus.parser.*;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class RepeatGroupExpression extends GroupExpression {
    private static final Logger logger = Logger.getLogger(RepeatGroupExpression.class.getName());
    private Expression endCondition;

    @Override
    public boolean isOptional() {
        return true;
    }

    public Expression getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(Expression endCondition) {
        this.endCondition = endCondition;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends GroupExpressionChecker {
        private boolean checkingEndCondition = endCondition != null;
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            if (checkingEndCondition) {
                return endCondition;
            } else {
                SequentialGroupExpression seq = new SequentialGroupExpression();
                seq.addAll(getExpressions());
                return seq;
            }
        }


        @Override
        public void check() throws ParsingException {
            ParsingContext ctx = getCtx();
            logger.entering("RepeatGroupExpression.Checker", "check", ctx);
            switch (ctx.getResult()) {
                case COMMIT:
                    collectNodes();
                    if (checkingEndCondition) {
                        commitNodes();
                    } else {
                        doContinue();
                    }
                    break;
                case ROLLBACK:
                    if (checkingEndCondition) {
                        checkingEndCondition = false;
                        continueProcessing();
                    } else {
                        doCommitOrRollback();
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx.getResult());
            }
            logger.exiting("RepeatGroupExpression.Checker", "check", ctx);
        }

        private void doCommitOrRollback() {
            if (turnsPassed > 0) {
                commitNodes();
            } else {
                rollback();
            }
        }

        private void doContinue() {
            turnsPassed++;
            continueProcessing();
            checkingEndCondition = endCondition != null;
        }

        @Override
        protected String getName() {
            return "repeat";
        }

    }
}
