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

import guru.bug.tools.parsgn.exceptions.NumberOfParametersException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.calc.CalculationContext;
import guru.bug.tools.parsgn.expr.calc.Term;
import guru.bug.tools.parsgn.processing.Result;
import guru.bug.tools.parsgn.processing.ResultType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ReferenceExpression extends Expression {
    private final String ruleName;
    private Rule reference;
    private List<Term> paramExpressions;

    public ReferenceExpression(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) throws NumberOfParametersException {
        this.reference = reference;
        validate();
    }

    private void validate() throws NumberOfParametersException {
        List<String> paramNames = reference.getParams();
        int exprCount = paramExpressions == null ? 0 : paramExpressions.size();
        int paramCount = paramNames == null ? 0 : paramNames.size();
        if (exprCount != paramCount) {
            throw new NumberOfParametersException("ruleName");
        }
    }

    @Override
    public ExpressionChecker checker(CalculationContext cCtx) {
        List<String> paramNames = reference.getParams();
        Checker result = new Checker();
        if (paramExpressions != null && !paramNames.isEmpty()) {
            Iterator<Term> expressionIterator = paramExpressions.iterator();
            Iterator<String> namesIterator = paramNames.iterator();
            List<Object> values = new ArrayList<>(Math.min(paramNames.size(), paramExpressions.size()));
            while (namesIterator.hasNext() && expressionIterator.hasNext()) {
                Term expr = expressionIterator.next();
                String name = namesIterator.next();
                Object value = expr.evaluate(cCtx);
                cCtx.setValue(name, value);
                values.add(value);
            }
            result.paramValues = values;
        }
        return result;
    }

    public Expression params(List<Term> paramExpressions) {
        if (paramExpressions == null) {
            this.paramExpressions = null;
        } else {
            this.paramExpressions = new ArrayList<>(paramExpressions);
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(ruleName);
        if (paramExpressions != null) {
            result.append("(");
            for (Term e : paramExpressions) {
                result.append(e).append(", ");
            }
            result.setLength(result.length() - 2);
            result.append(")");
        }
        return result.toString();
    }

    class Checker extends BranchExpressionChecker {
        private List<Object> paramValues;

        @Override
        public Expression next() {
            return getReference().getExpression();
        }

        @Override
        public Result check(ResultType childResult) throws ParsingException {
            switch (childResult) {
                case MATCH:
                    if (reference.isHidden()) {
                        return ResultType.MATCH.andSkip();
                    } else {
                        return ResultType.MATCH.andCommitGroup(reference.getName());
                    }
                case MISMATCH:
                    return ResultType.MISMATCH.andRollback();
                case MISMATCH_BUT_OPTIONAL:
                    return ResultType.MISMATCH_BUT_OPTIONAL.andRollback();
                default:
                    throw new IllegalArgumentException(childResult.toString());
            }
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder();
            result.append(ruleName);
            if (paramValues != null) {
                result.append("(");
                paramValues.forEach(p -> result.append(p).append(", "));
                result.setLength(result.length() - 2);
                result.append(")");
            }
            return result.toString();
        }
    }
}
