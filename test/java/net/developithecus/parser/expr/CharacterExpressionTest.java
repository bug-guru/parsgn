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
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class CharacterExpressionTest {

    @Test
    public void testChecker_digits() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.DIGIT);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check('a'));
        assertSame(ResultType.ROLLBACK, checker.check(-1));
        assertSame(ResultType.ROLLBACK, checker.check('\n'));
        assertSame(ResultType.ROLLBACK, checker.check('S'));
        assertSame(ResultType.COMMIT, checker.check('1'));
        assertSame(ResultType.COMMIT, checker.check('2'));
        assertSame(ResultType.COMMIT, checker.check('3'));
        assertSame(ResultType.COMMIT, checker.check('0'));
    }

    @Test
    public void testChecker_eof() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.EOF);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check('a'));
        assertSame(ResultType.COMMIT, checker.check(-1));
    }

    @Test
    public void testChecker_eol() throws Exception {
        CharacterExpression expr = new CharacterExpression();
        expr.setCharType(CharType.LINE_SEPARATOR);
        CharacterExpression.Checker checker = (CharacterExpression.Checker) expr.checker();
        assertSame(ResultType.ROLLBACK, checker.check(' '));
        assertSame(ResultType.COMMIT, checker.check('\n'));
        assertSame(ResultType.COMMIT, checker.check('\r'));
    }
}
