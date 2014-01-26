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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dima
 */
public class StringExpressionTest {
    protected final StringExpression expression;
    protected StringExpression.Checker checker;

    public StringExpressionTest() {
        expression = new StringExpression();
    }

    @Before
    public void setUp() {
        checker = (StringExpression.Checker) expression.checker();
    }

    @Test
    public void testChecker_next() throws Exception {
        assertNull(checker.next());
    }

    @Test
    public void testChecker_OK() throws Exception {
        expression.setValue("Hello, World!");
        expression.setTransform(null);
        test(ResultType.COMMIT, "Hello, World!");
        assertEquals(null, checker.getResult());
    }

    @Test
    public void testChecker_FAIL() throws Exception {
        expression.setValue("Hello, World!");
        test(ResultType.ROLLBACK, "Hello, World?");
    }

    @Test
    public void testChecker_Transform() throws Exception {
        expression.setValue("Hello, World!");
        expression.setTransform("Bye-bye!");
        test(ResultType.COMMIT, "Hello, World!");
        assertEquals("Bye-bye!", checker.getResult());
    }

    private void test(ResultType expectedResult, String input) throws Exception {
        for (int i = 0; i < input.length() - 1; i++) {
            assertSame(ResultType.CONTINUE, checker.check(input.codePointAt(i)));
        }
        assertSame(expectedResult, checker.check(input.codePointAt(input.length() - 1)));
    }
}
