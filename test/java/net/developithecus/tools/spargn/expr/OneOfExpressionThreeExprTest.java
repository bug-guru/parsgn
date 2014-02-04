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

package net.developithecus.tools.spargn.expr;

import net.developithecus.tools.spargn.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class OneOfExpressionThreeExprTest extends OneOfExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();
    private FakeExpression expr2 = new FakeExpression();
    private FakeExpression expr3 = new FakeExpression();

    public OneOfExpressionThreeExprTest() {
        expression.setExpressions(expr1, expr2, expr3);
    }

    @Test
    public void testChecker_next() {
        assertSame(expr1, checker.next());
        assertSame(expr2, checker.next());
        assertSame(expr3, checker.next());
    }

    /*
        COMMIT
        ROLLBACK, COMMIT
        ROLLBACK_OPT, COMMIT
        ROLLBACK, ROLLBACK, COMMIT
        ROLLBACK, ROLLBACK_OPT, COMMIT
        ROLLBACK_OPT, ROLLBACK, COMMIT
        ROLLBACK_OPT, ROLLBACK_OPT, COMMIT

        ROLLBACK, ROLLBACK, ROLLBACK
        ROLLBACK, ROLLBACK, ROLLBACK_OPT

        ROLLBACK, ROLLBACK_OPT, ROLLBACK
        ROLLBACK, ROLLBACK_OPT, ROLLBACK_OPT

        ROLLBACK_OPT, ROLLBACK, ROLLBACK
        ROLLBACK_OPT, ROLLBACK, ROLLBACK_OPT

        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_C() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_RC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_OC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_RRC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_ROC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_ORC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_OOC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_RRR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_RRO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_ROR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_ROO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_ORR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_ORO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_OOR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_OOO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }
}
