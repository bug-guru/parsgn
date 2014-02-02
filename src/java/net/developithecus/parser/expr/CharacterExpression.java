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

package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
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
    public ExpressionChecker checker() {
        return new Checker();
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

        @Override
        public String toString() {
            return charType.toString();
        }

    }


}
