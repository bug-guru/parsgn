package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ResultType;
import net.developithecus.parser.TransparentExpressionChecker;
import net.developithecus.parser.exceptions.ParsingException;

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

    class Checker extends TransparentExpressionChecker {
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
        public ResultType checkChildCommit() throws ParsingException {
            if (checkingEndCondition) {
                return doCommitOrRollback();
            } else {
                checkingEndCondition = true;
                turnsPassed++;
                return ResultType.CONTINUE;
            }
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return checkChildRollback();
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            if (checkingEndCondition) {
                checkingEndCondition = false;
                return ResultType.CONTINUE;
            } else {
                return doCommitOrRollback();
            }
        }

        private ResultType doCommitOrRollback() throws ParsingException {
            if (minOccurrences == 0 && turnsPassed == 0) {
                return ResultType.ROLLBACK_OPTIONAL;
            } else if (turnsPassed >= minOccurrences) {
                return ResultType.COMMIT;
            } else {
                return ResultType.ROLLBACK;
            }
        }

    }
}
