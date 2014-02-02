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
public class SequentialExpressionThreeExprTest extends SequentialExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();
    private FakeExpression expr2 = new FakeExpression();
    private FakeExpression expr3 = new FakeExpression();

    public SequentialExpressionThreeExprTest() {
        expression.setExpressions(expr1, expr2, expr3);
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr1, checker.next());
        assertSame(expr2, checker.next());
        assertSame(expr3, checker.next());
    }

    /*
        ROLLBACK
        COMMIT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK
        COMMIT, COMMIT, COMMIT
        COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, ROLLBACK_OPT, COMMIT
        COMMIT, ROLLBACK_OPT, ROLLBACK
        COMMIT, ROLLBACK_OPT, ROLLBACK_OPT
        ROLLBACK_OPT, COMMIT, COMMIT
        ROLLBACK_OPT, COMMIT, ROLLBACK
        ROLLBACK_OPT, COMMIT, ROLLBACK_OPT
        ROLLBACK_OPT, ROLLBACK_OPT, COMMIT
        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_OR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CCC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_CCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_COC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_COR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_COO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_OCC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_OCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_OCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK_OPTIONAL));
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
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

}
