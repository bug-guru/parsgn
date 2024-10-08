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

import guru.bug.tools.parsgn.processing.Result;
import guru.bug.tools.parsgn.processing.ResultType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class StringExpressionTest {
    protected final StringExpression expression;
    protected Expression.LeafExpressionChecker checker;

    public StringExpressionTest() {
        expression = new StringExpression();
    }

    @BeforeEach
    public void setUp() {
        expression.setCaseSensitive(true);
        checker = (StringExpression.Checker) expression.checker(null);
    }

    @Test
    public void testChecker_OK() throws Exception {
        expression.setValue("Hello, World!");
        expression.setTransform(null);
        test(ResultType.MATCH, "Hello, World!");
    }

    @Test
    public void testChecker_OK_Insensitive() throws Exception {
        expression.setCaseSensitive(false);
        expression.setValue("Hello, World!");
        expression.setTransform(null);
        test(ResultType.MATCH, "hElLo, WORLD!");
    }

    @Test
    public void testChecker_FAIL_Sensitive() throws Exception {
        expression.setValue("Hello, World!");
        expression.setTransform(null);
        test(ResultType.MISMATCH, "h");
    }

    @Test
    public void testChecker_FAIL() throws Exception {
        expression.setValue("Hello, World!");
        test(ResultType.MISMATCH, "Hello, World?");
    }

    @Test
    public void testChecker_Transform() throws Exception {
        expression.setValue("Hello, World!");
        expression.setTransform("Bye-bye!");
        test(ResultType.MATCH, "Hello, World!");
    }

    private void test(ResultType expectedResult, String input) throws Exception {
        for (int i = 0; i < input.length() - 1; i++) {
            Result result = checker.check(input.codePointAt(i));
            assertNotNull(result);
            assertNotNull(result.getBasicResult());
            assertSame(ResultType.CONTINUE, result.getBasicResult());
        }
        Result result = checker.check(input.codePointAt(input.length() - 1));
        assertNotNull(result);
        assertNotNull(result.getBasicResult());
        assertSame(expectedResult, result.getBasicResult());
    }
}
