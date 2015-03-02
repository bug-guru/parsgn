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
import guru.bug.tools.parsgn.expr.calc.Term;
import guru.bug.tools.parsgn.processing.Result;
import guru.bug.tools.parsgn.processing.ResultType;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class QuantityExpression extends Expression {
    private Expression expression;
    private Term minOccurrences = Term.constant(0);
    private Term maxOccurrences = Term.constant(1);

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        if (expression == null) {
            throw new NullPointerException("expression");
        }
        this.expression = expression;
    }

    public QuantityExpression loop(Expression expression) {
        setExpression(expression);
        return this;
    }

    public Term getMinOccurrences() {
        return minOccurrences;
    }

    public void setMinOccurrences(Term minOccurrences) {
        if (minOccurrences == null) {
            throw new NullPointerException("minOccurrences");
        }
        this.minOccurrences = minOccurrences;
    }

    public QuantityExpression minOccurrences(int minOccurrences) {
        return minOccurrences(Term.constant(minOccurrences));
    }

    public QuantityExpression minOccurrences(Term minOccurrences) {
        setMinOccurrences(minOccurrences);
        return this;
    }

    public Term getMaxOccurrences() {
        return maxOccurrences;
    }

    public void setMaxOccurrences(Term maxOccurrences) {
        if (maxOccurrences == null) {
            throw new NullPointerException("maxOccurrences");
        }
        this.maxOccurrences = maxOccurrences;
    }

    public QuantityExpression maxOccurrences(int maxOccurrences) {
        return maxOccurrences(Term.constant(maxOccurrences));
    }

    public QuantityExpression maxOccurrences(Term maxOccurrences) {
        setMaxOccurrences(maxOccurrences);
        return this;
    }

    @Override
    public ExpressionChecker checker(CalculationContext cCtx) {
        Checker result = new Checker();
        Integer min = (Integer) this.minOccurrences.evaluate(cCtx);
        Integer max = (Integer) this.maxOccurrences.evaluate(cCtx);
        result.minOccurrences = min;
        result.maxOccurrences = max;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(expression);
        boolean isMinConst = minOccurrences.isConstant();
        boolean isMaxConst = maxOccurrences.isConstant();
        if (isMinConst && isMaxConst) {
            int minConst = (int) minOccurrences.evaluate(null);
            int maxConst = (int) maxOccurrences.evaluate(null);
            if (minConst == maxConst) {
                result.append("{").append(minConst).append("}");
            } else if (minConst == 0 && maxConst == Integer.MAX_VALUE) {
                result.append("*");
            } else if (minConst == 1 && maxConst == Integer.MAX_VALUE) {
                result.append("+");
            } else if (minConst == 0 && maxConst == 1) {
                result.append("?");
            } else if (maxConst == Integer.MAX_VALUE) {
                result.append("{").append(minConst).append(",}");
            } else {
                result.append("{").append(minConst).append(", ").append(maxConst).append("}");
            }
        } else if (isMaxConst && ((int) maxOccurrences.evaluate(null)) == Integer.MAX_VALUE) {
            result.append("{").append(minOccurrences).append(",}");
        } else {
            result.append("{").append(minOccurrences).append(", ").append(maxOccurrences).append("}");
        }
        return result.toString();
    }

    class Checker extends BranchExpressionChecker {
        private int turnsPassed = 0;
        private int minOccurrences;
        private int maxOccurrences;

        @Override
        public Expression next() {
            return minOccurrences == 0 && maxOccurrences == 0
                    ? AlwaysMatchExpression.INSTANCE
                    : expression;
        }

        @Override
        public Result check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case MATCH:
                    return doCommitOrContinue();
                case MISMATCH:
                case MISMATCH_BUT_OPTIONAL:
                    return doCommitOrRollback();
                default:
                    throw new ParsingException("unknown result: " + childResult);
            }
        }

        private Result doCommitOrRollback() throws ParsingException {
            if (turnsPassed >= minOccurrences && turnsPassed > 0) {
                return ResultType.MATCH.andMerge();
            } else if (turnsPassed == 0 && minOccurrences == 0) {
                return ResultType.MISMATCH_BUT_OPTIONAL.andRollback();
            } else {
                return ResultType.MISMATCH.andRollback();
            }
        }

        private Result doCommitOrContinue() throws ParsingException {
            turnsPassed++;
            if (maxOccurrences == turnsPassed) {
                return ResultType.MATCH.andMerge();
            } else {
                return ResultType.CONTINUE.noAction();
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(expression);
            if (minOccurrences == maxOccurrences) {
                result.append("{").append(minOccurrences).append("}");
            } else if (minOccurrences == 0 && maxOccurrences == Integer.MAX_VALUE) {
                result.append("*");
            } else if (minOccurrences == 1 && maxOccurrences == Integer.MAX_VALUE) {
                result.append("+");
            } else if (minOccurrences == 0 && maxOccurrences == 1) {
                result.append("?");
            } else if (minOccurrences == Integer.MAX_VALUE) {
                result.append("{").append(minOccurrences).append(",}");
            } else {
                result.append("{").append(minOccurrences).append(", ").append(maxOccurrences).append("}");
            }
            return result.toString();
        }
    }
}
