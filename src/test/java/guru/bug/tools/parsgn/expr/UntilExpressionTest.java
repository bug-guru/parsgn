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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class UntilExpressionTest {
    protected final UntilExpression expression;
    private final FakeExpression loop = new FakeExpression();
    private final FakeExpression cond = new FakeExpression();
    protected UntilExpression.Checker checker;

    public UntilExpressionTest() {
        expression = new UntilExpression();
        expression.setConditionExpression(cond);
        expression.setLoopExpression(loop);
    }

    @Before
    public void setUp() {
        checker = (UntilExpression.Checker) expression.checker();
    }

    @Test
    public void testChecker_0x_optional1() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_0x_optional2() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        assertSame(loop, checker.next());
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_1x_optional1() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        assertSame(loop, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        assertSame(cond, checker.next());
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_1x_optional2() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        assertSame(loop, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        assertSame(loop, checker.next());
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK));
    }
}
