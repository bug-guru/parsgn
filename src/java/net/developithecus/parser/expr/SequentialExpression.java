package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialExpression extends Expression {
    private List<Expression> expressions;

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public SequentialExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;

        @Override
        public Expression next() {
            curExpr = expressions.next();
            return curExpr;
        }

        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                case ROLLBACK_OPTIONAL:
                    doCommitOrContinue();
                    break;
                case ROLLBACK:
                    ctx().markForRollback();
                    break;
                case CONTINUE:
                    ctx().markForContinue();
                    break;
                default:
                    throw new InternalParsingException("unknown result: " + ctx().getResult());
            }
        }

        private void doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                ctx().markForContinue();
            } else if (!ctx().hasCommitted()) {
                throw new ParsingException("sequence without result");
            } else {
                ctx().markForCommit();
            }
        }

        @Override
        protected String getName() {
            return "sequence";
        }
    }
}
