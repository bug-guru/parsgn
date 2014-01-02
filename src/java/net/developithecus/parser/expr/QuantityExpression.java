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
public class QuantityExpression extends Expression {
    private Expression expression;
    private int minOccurrences = 0;
    private int maxOccurrences = 1;

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public QuantityExpression loop(Expression expression) {
        setExpression(expression);
        return this;
    }

    public int getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(int minOccurrences) {
        this.minOccurrences = minOccurrences;
    }

    public QuantityExpression minOccurrences(int minOccurrences) {
        setMinOccurrences(minOccurrences);
        return this;
    }

    public int getMaxOccurrences() {
        return maxOccurrences;
    }

    public void setMaxOccurrences(int maxOccurrences) {
        this.maxOccurrences = maxOccurrences;
    }

    public QuantityExpression maxOccurrences(int maxOccurrences) {
        setMaxOccurrences(maxOccurrences);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends TransparentExpressionChecker {
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            return expression;
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            return doCommitOrContinue();
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return doCommitOrRollback();
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return doCommitOrRollback();
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

        private ResultType doCommitOrContinue() throws ParsingException {
            turnsPassed++;
            if (maxOccurrences == turnsPassed) {
                return ResultType.COMMIT;
            } else {
                return ResultType.CONTINUE;
            }
        }
    }
}
