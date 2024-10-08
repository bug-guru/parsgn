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

import guru.bug.tools.parsgn.processing.ResultType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class CharacterExpressionTest {

    @Test
    public void testChecker_digits() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.DIGIT);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker(null);
        assertSame(ResultType.MISMATCH, checker.check('a').getBasicResult());
        assertSame(ResultType.MISMATCH, checker.check(-1).getBasicResult());
        assertSame(ResultType.MISMATCH, checker.check('\n').getBasicResult());
        assertSame(ResultType.MISMATCH, checker.check('S').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('1').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('2').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('3').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('0').getBasicResult());
    }

    @Test
    public void testChecker_eol() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.LINE_SEPARATOR);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker(null);
        assertSame(ResultType.MISMATCH, checker.check(' ').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('\n').getBasicResult());
        assertSame(ResultType.MATCH, checker.check('\r').getBasicResult());
    }
}
