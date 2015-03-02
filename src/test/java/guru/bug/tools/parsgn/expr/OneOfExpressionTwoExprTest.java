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
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class OneOfExpressionTwoExprTest extends OneOfExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();
    private FakeExpression expr2 = new FakeExpression();

    public OneOfExpressionTwoExprTest() {
        expression.setExpressions(expr1, expr2);
    }

    @Test
    public void testChecker_next() {
        assertSame(expr1, checker.next());
        assertSame(expr2, checker.next());
    }

    /*
        MATCH
        MISMATCH, MATCH
        MISMATCH, MISMATCH
        MISMATCH, ROLLBACK_OPT
        ROLLBACK_OPT, MATCH
        ROLLBACK_OPT, MISMATCH
        ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_C() throws Exception {
        checker.next();
        assertSame(ResultType.MATCH, checker.check(ResultType.MATCH).getBasicResult());
    }

    @Test
    public void testChecker_RC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH).getBasicResult());
        checker.next();
        assertSame(ResultType.MATCH, checker.check(ResultType.MATCH).getBasicResult());
    }

    @Test
    public void testChecker_RR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH).getBasicResult());
        checker.next();
        assertSame(ResultType.MISMATCH, checker.check(ResultType.MISMATCH).getBasicResult());
    }

    @Test
    public void testChecker_RO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH).getBasicResult());
        checker.next();
        assertSame(ResultType.MISMATCH, checker.check(ResultType.MISMATCH_BUT_OPTIONAL).getBasicResult());
    }

    @Test
    public void testChecker_OC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH_BUT_OPTIONAL).getBasicResult());
        checker.next();
        assertSame(ResultType.MATCH, checker.check(ResultType.MATCH).getBasicResult());
    }

    @Test
    public void testChecker_OR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH_BUT_OPTIONAL).getBasicResult());
        checker.next();
        assertSame(ResultType.MISMATCH, checker.check(ResultType.MISMATCH).getBasicResult());
    }

    @Test
    public void testChecker_OO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.MISMATCH_BUT_OPTIONAL).getBasicResult());
        checker.next();
        assertSame(ResultType.MISMATCH, checker.check(ResultType.MISMATCH_BUT_OPTIONAL).getBasicResult());
    }
}
