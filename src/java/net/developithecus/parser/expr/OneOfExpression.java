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

public class OneOfExpression extends Expression {
    private List<Expression> expressions;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public OneOfExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends TransparentExpressionChecker {
        private Iterator<Expression> exprIterator;

        @Override
        public Expression next() {
            if (exprIterator == null) {
                exprIterator = expressions.iterator();
            }
            return exprIterator.next();
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            return ResultType.COMMIT;
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return doRollbackOrContinue();
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return doRollbackOrContinue();
        }

        private ResultType doRollbackOrContinue() throws ParsingException {
            if (exprIterator.hasNext()) {
                return ResultType.CONTINUE;
            } else {
                return ResultType.ROLLBACK;
            }
        }
    }
}
