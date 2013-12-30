package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class OneOfExpression extends Expression {
    private List<Expression> expressions;

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public OneOfExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private Iterator<Expression> exprIterator = expressions.iterator();
        private Expression curExpr;

        @Override
        public Expression next() {
            curExpr = exprIterator.next();
            return curExpr;
        }

        @Override
        public void check() throws ParsingException {
            switch (ctx().getResult()) {
                case COMMIT:
                    ctx().markForCommit();
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    doRollbackOrContinue();
                    break;
                default:
                    throw new IllegalStateException("unknown result: " + ctx().getResult());
            }
        }

        private void doRollbackOrContinue() {
            if (exprIterator.hasNext()) {
                ctx().markForContinue();
            } else {
                ctx().markForRollback();
            }
        }

        @Override
        protected String getName() {
            return "oneOf";
        }
    }
}
