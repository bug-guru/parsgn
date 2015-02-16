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

import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.calc.CalculationContext;
import guru.bug.tools.parsgn.processing.ResultType;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CharacterExpression extends Expression {
    private CharType charType;

    public CharType getCharType() {
        return charType;
    }

    public void setCharType(CharType charType) {
        this.charType = charType;
    }

    @Override
    public ExpressionChecker checker(CalculationContext cCtx) {
        return new Checker();
    }

    @Override
    public String toString() {
        return "#" + charType.toString();
    }

    class Checker extends LeafExpressionChecker {
        private int result;

        @Override
        public void commitResult(StringBuilder sb) {
            if (Character.isValidCodePoint(result)) {
                sb.appendCodePoint(result);
            }
        }

        @Override
        public ResultType check(int codePoint) throws ParsingException {
            if (charType.apply(codePoint)) {
                result = codePoint;
                return ResultType.COMMIT;
            } else {
                return ResultType.ROLLBACK;
            }
        }
    }


}
