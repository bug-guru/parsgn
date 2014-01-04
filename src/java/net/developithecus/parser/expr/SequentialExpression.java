package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ResultType;
import net.developithecus.parser.TransparentExpressionChecker;
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

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public SequentialExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends TransparentExpressionChecker {
        private final Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;
        private boolean hasCommitted;

        @Override
        public Expression next() {
            curExpr = expressions.next();
            return curExpr;
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            hasCommitted = true;
            return doCommitOrContinue();
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return doCommitOrContinue();
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return ResultType.ROLLBACK;
        }

        private ResultType doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                return ResultType.CONTINUE;
            } else if (!hasCommitted) {
                return ResultType.ROLLBACK_OPTIONAL;
            } else {
                return ResultType.COMMIT;
            }
        }
    }
}
