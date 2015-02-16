/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.expr;

import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.calc.CalculationContext;
import guru.bug.tools.parsgn.processing.ResultType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class OneOfExpression extends Expression {
    private List<Expression> expressions;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public OneOfExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    public OneOfExpression expressions(List<Expression> expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker(CalculationContext cCtx) {
        return new Checker();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for(Expression e : expressions) {
            result.append(e).append(" | ");
        }
        result.setLength(result.length() - 3);
        result.append("]");
        return result.toString();
    }

    class Checker extends BranchExpressionChecker {
        private Iterator<Expression> exprIterator;

        @Override
        public Expression next() {
            if (exprIterator == null) {
                exprIterator = expressions.iterator();
            }
            return exprIterator.next();
        }

        @Override
        public ResultType check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case COMMIT:
                    return ResultType.COMMIT;
                case ROLLBACK_OPTIONAL:
                case ROLLBACK:
                    return doRollbackOrContinue();
                default:
                    throw new ParsingException("unknown result: " + childResult);
            }
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
