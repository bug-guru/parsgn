package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;

import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class RepeatGroupExpression extends GroupExpression {
    private Expression endCondition;

    public Expression getEndCondition() {
        return endCondition;
    }

    public void setEndCondition(Expression endCondition) {
        this.endCondition = endCondition;
    }

    public RepeatGroupExpression endCondition(Expression endCondition) {
        setEndCondition(endCondition);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private boolean checkingEndCondition = endCondition != null;
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            if (checkingEndCondition) {
                return endCondition;
            } else {
                List<Expression> expressions = getExpressions();
                if (expressions.size() == 1) {
                    return expressions.get(0);
                } else {
                    SequentialGroupExpression seq = new SequentialGroupExpression();
                    seq.addAll(expressions);
                    return seq;
                }
            }
        }


        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                    if (checkingEndCondition) {
                        ctx().markForCommit();
                    } else {
                        doContinue();
                    }
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    if (checkingEndCondition) {
                        checkingEndCondition = false;
                        ctx().markForContinue();
                    } else {
                        doCommitOrRollback();
                    }
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx().getResult());
            }
        }

        private void doCommitOrRollback() {
            if (turnsPassed > 0) {
                ctx().markForCommit();
            } else {
                ctx().markForRollbackOptional();
            }
        }

        private void doContinue() {
            turnsPassed++;
            ctx().markForContinue();
            checkingEndCondition = endCondition != null;
        }

        @Override
        protected String getName() {
            return "repeat";
        }

    }
}
