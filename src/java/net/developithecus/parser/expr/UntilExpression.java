package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class UntilExpression extends Expression {
    private Expression loopExpression;
    private Expression conditionExpression;
    private int minOccurrences;

    public Expression getLoopExpression() {
        return loopExpression;
    }

    public void setLoopExpression(Expression loopExpression) {
        this.loopExpression = loopExpression;
    }

    public UntilExpression loop(Expression loopExpression) {
        setLoopExpression(loopExpression);
        return this;
    }

    public Expression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(Expression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public UntilExpression condition(Expression conditionExpression) {
        setConditionExpression(conditionExpression);
        return this;
    }

    public int getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(int minOccurrences) {
        this.minOccurrences = minOccurrences;
    }

    public UntilExpression min(int minOccurrences) {
        setMinOccurrences(minOccurrences);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private boolean checkingEndCondition = true;
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            if (checkingEndCondition) {
                return conditionExpression;
            } else {
                return loopExpression;
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
                        checkingEndCondition = true;
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
            if (minOccurrences == 0 && turnsPassed == 0) {
                ctx().markForRollbackOptional();
            } else if (turnsPassed >= minOccurrences) {
                ctx().markForCommit();
            } else {
                ctx().markForRollback();
            }
        }

        private void doContinue() {
            turnsPassed++;
            ctx().markForContinue();
        }

        @Override
        protected String getName() {
            return "until";
        }

    }
}
