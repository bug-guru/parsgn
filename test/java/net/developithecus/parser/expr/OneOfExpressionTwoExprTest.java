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
        COMMIT
        ROLLBACK, COMMIT
        ROLLBACK, ROLLBACK
        ROLLBACK, ROLLBACK_OPT
        ROLLBACK_OPT, COMMIT
        ROLLBACK_OPT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_C() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_RC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_RR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_RO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_OC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_OR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_OO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }
}
