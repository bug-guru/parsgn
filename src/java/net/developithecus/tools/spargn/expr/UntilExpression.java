/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
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

package net.developithecus.tools.spargn.expr;

import net.developithecus.tools.spargn.ResultType;
import net.developithecus.tools.spargn.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class UntilExpression extends Expression {
    private Expression loopExpression;
    private Expression conditionExpression;
    private int minOccurrences;

    public Expression getLoopExpression() {
        return loopExpression;
    }

    public void setLoopExpression(Expression loopExpression) {
        this.loopExpression = loopExpression;
    }

    public UntilExpression loop(Expression loopExpression) {
        setLoopExpression(loopExpression);
        return this;
    }

    public Expression getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(Expression conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public UntilExpression condition(Expression conditionExpression) {
        setConditionExpression(conditionExpression);
        return this;
    }

    public int getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(int minOccurrences) {
        this.minOccurrences = minOccurrences;
    }

    public UntilExpression min(int minOccurrences) {
        setMinOccurrences(minOccurrences);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends IntermediateExpressionChecker {
        private boolean checkingEndCondition = true;
        private int turnsPassed = 0;

        @Override
        public Expression next() {
            if (checkingEndCondition) {
                return conditionExpression;
            } else {
                return loopExpression;
            }
        }

        @Override
        public ResultType check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case COMMIT:
                    if (checkingEndCondition) {
                        return doCommitOrRollback();
                    } else {
                        checkingEndCondition = true;
                        turnsPassed++;
                        return ResultType.CONTINUE;
                    }
                case ROLLBACK_OPTIONAL:
                case ROLLBACK:
                    if (checkingEndCondition) {
                        checkingEndCondition = false;
                        return ResultType.CONTINUE;
                    } else {
                        return doCommitOrRollback();
                    }
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

        @Override
        public String toString() {
            return "until";
        }

    }
}
