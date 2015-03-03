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
import guru.bug.tools.parsgn.processing.Result;
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
public class SequentialExpression extends Expression {
    private List<Expression> expressions;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public SequentialExpression expressions(Expression... expressions) {
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
        for (Expression e : expressions) {
            result.append(e).append(" ");
        }
        result.setLength(result.length() - 1);
        result.append("]");
        return result.toString();
    }

    class Checker extends BranchExpressionChecker {
        private final Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;
        private boolean hasCommitted;

        @Override
        public Expression next() {
            curExpr = expressions.next();
            return curExpr;
        }

        @Override
        public Result check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case MATCH:
                    hasCommitted = true;
                case MISMATCH_BUT_OPTIONAL:
                    return doCommitOrContinue();
                case MISMATCH:
                    return ResultType.MISMATCH.andRollback();
                default:
                    throw new ParsingException("unknown result: " + childResult);
            }
        }

        private Result doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                return ResultType.CONTINUE.noAction();
            } else if (!hasCommitted) {
                // TODO review this. It seems weird to treat sequence as optinal if no one value were submitted.
                return ResultType.MISMATCH_BUT_OPTIONAL.andRollback();
            } else {
                return ResultType.MATCH.andMerge();
            }
        }
    }
}
