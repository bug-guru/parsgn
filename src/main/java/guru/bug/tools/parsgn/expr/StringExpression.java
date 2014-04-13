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
import guru.bug.tools.parsgn.StringUtils;
import guru.bug.tools.parsgn.exceptions.ParsingException;

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

    public StringExpression transform(String transform) {
        setTransform(transform);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends LeafExpressionChecker {
        private int offset;

        @Override
        public void commitResult(StringBuilder result) {
            if (transform != null) {
                result.append(transform);
            }
        }

        @Override
        public ResultType check(int codePoint) throws ParsingException {
            if (codePoints[offset] != codePoint) {
                return ResultType.ROLLBACK;
            } else {
                offset++;
                if (offset == len) {
                    return ResultType.COMMIT;
                } else {
                    return ResultType.CONTINUE;
                }
            }
        }

        @Override
        public String toString() {
            return "str:" + value;
        }
    }
}
