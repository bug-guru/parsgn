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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.expr;

import guru.bug.tools.parsgn.CalcExpressionContext;
import guru.bug.tools.parsgn.ResultType;
import guru.bug.tools.parsgn.exceptions.ParsingException;

public class UntilExpressionConditionProxy extends Expression {
    private Expression conditionExpression;

    public Expression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(Expression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public UntilExpressionConditionProxy condition(Expression conditionExpression) {
        setConditionExpression(conditionExpression);
        return this;
    }

    @Override
    public ExpressionChecker checker(CalcExpressionContext cCtx) {
        return new Checker();
    }

    @Override
    public String toString() {
        return "proxy:" + conditionExpression.toString();
    }

    class Checker extends BranchExpressionChecker {

        @Override
        public Expression next() {
            return conditionExpression;
        }

        @Override
        public ResultType check(ResultType childResult) throws ParsingException {
            if (childResult == ResultType.COMMIT) {
                return ResultType.COMMIT;
            } else {
                return childResult;
            }
        }

        @Override
        public boolean isIgnored() {
            return true;
        }
    }
}

