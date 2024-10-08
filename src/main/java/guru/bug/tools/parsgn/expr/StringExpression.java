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
import guru.bug.tools.parsgn.utils.StringUtils;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class StringExpression extends Expression {
    private String value;
    private String transform;
    private int[] codePoints;
    private int len;
    private boolean caseSensitive = true;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.codePoints = StringUtils.toCodePoints(value);
        this.len = codePoints.length;
    }

    public StringExpression value(String value) {
        setValue(value);
        return this;
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public StringExpression transform(String transform) {
        setTransform(transform);
        return this;
    }

    @Override
    public ExpressionChecker checker(CalculationContext cCtx) {
        return new Checker();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\"").append(StringUtils.escape(value)).append("\"");
        if (transform != null) {
            result.append(" -> \"").append(StringUtils.escape(transform)).append("\"");
        }
        return result.toString();
    }

    class Checker extends LeafExpressionChecker {
        private int offset;

        @Override
        public Result check(int codePoint) throws ParsingException {
            int expected = caseSensitive ? codePoints[offset] : Character.toLowerCase(codePoints[offset]);
            int actual = caseSensitive ? codePoint : Character.toLowerCase(codePoint);
            if (expected != actual) {
                return ResultType.MISMATCH.andRollback();
            } else {
                offset++;
                if (offset == len) {
                    return transform == null
                            ? ResultType.MATCH.andSkip()
                            : ResultType.MATCH.andCommitString(transform);
                } else {
                    return ResultType.CONTINUE.noAction();
                }
            }
        }
    }
}
