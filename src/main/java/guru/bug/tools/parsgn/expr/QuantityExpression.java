/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.expr;

import guru.bug.tools.parsgn.ResultType;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.model.CalcExpression;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class QuantityExpression extends Expression {
    private Expression expression;
    private CalcExpression minOccurrences = new CalcExpression(null, 0);
    private CalcExpression maxOccurrences = new CalcExpression(null, 1);

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

    public CalcExpression getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(CalcExpression minOccurrences) {
        this.minOccurrences = minOccurrences;
    }

    public QuantityExpression minOccurrences(CalcExpression minOccurrences) {
        setMinOccurrences(minOccurrences);
        return this;
    }

    public CalcExpression getMaxOccurrences() {
        return maxOccurrences;
    }

    public void setMaxOccurrences(CalcExpression maxOccurrences) {
        this.maxOccurrences = maxOccurrences;
    }

    public QuantityExpression maxOccurrences(CalcExpression maxOccurrences) {
        setMaxOccurrences(maxOccurrences);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends BranchExpressionChecker {
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            return expression;
        }

        @Override
        public ResultType check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case COMMIT:
                    return doCommitOrContinue();
                case ROLLBACK_OPTIONAL:
                case ROLLBACK:
                    return doCommitOrRollback();
                default:
                    throw new ParsingException("unknown result: " + childResult);
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

        private ResultType doCommitOrContinue() throws ParsingException {
            turnsPassed++;
            if (maxOccurrences == turnsPassed) {
                return ResultType.COMMIT;
            } else {
                return ResultType.CONTINUE;
            }
        }

        @Override
        public String toString() {
            return "Quantity";
        }
    }
}
