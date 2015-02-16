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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class OneOfExpressionTest {
    private static final Expression[] ONE = new Expression[]{new FakeExpression()};
    private static final Expression[] TWO = new Expression[]{new FakeExpression(), new FakeExpression()};
    private static final Expression[] THREE = new Expression[]{new FakeExpression(), new FakeExpression(), new FakeExpression()};
    private final OneOfExpression expression = new OneOfExpression();
    private OneOfExpression.Checker checker;

    @Before
    public void createChecker() {
        checker = (OneOfExpression.Checker) expression.checker(null);
    }

    @Test
    public void testChecker_1_COMMIT() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_1_ROLLBACK() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_1_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_2_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_2_ROLLBACK_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_2_ROLLBACK_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_2_ROLLBACK_ROLLBACK() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_2_ROLLBACKOPTIONAL_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_3_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_3_ROLLBACK_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_ROLLBACK() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.ROLLBACK_OPTIONAL));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }
}
