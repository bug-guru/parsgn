package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.exceptions.InternalParsingException;
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

    private class Checker extends ExpressionChecker {
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            return expression;
        }


        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                    doCommitOrContinue();
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    doCommitOrRollback();
                    break;
                default:
                    throw new InternalParsingException("unknown result: " + ctx().getResult());
            }
        }

        private void doCommitOrRollback() throws ParsingException {
            if (minOccurrences == 0 && turnsPassed == 0) {
                ctx().markForRollbackOptional();
            } else if (turnsPassed >= minOccurrences) {
                ctx().markForCommit();
            } else {
                ctx().markForRollback();
            }
        }

        private void doCommitOrContinue() throws ParsingException {
            turnsPassed++;
            if (maxOccurrences == turnsPassed) {
                ctx().markForCommit();
            } else {
                ctx().markForContinue();
            }
        }

        @Override
        protected String getName() {
            return "qty";
        }

    }
}
