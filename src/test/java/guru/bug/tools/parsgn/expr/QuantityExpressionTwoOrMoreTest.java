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
public class QuantityExpressionTwoOrMoreTest extends QuantityExpressionTestBase {

    public QuantityExpressionTwoOrMoreTest() {
        expression.minOccurrences(2).maxOccurrences(Integer.MAX_VALUE);
    }

    /*
        ROLLBACK
        ROLLBACK_OPT
        COMMIT, ROLLBACK
        COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, COMMIT, COMMIT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_O() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_CR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_CCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK));
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
    public void testChecker_CCCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CCCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }

    @Test
    public void testChecker_CCCCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_CCCCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.CONTINUE, checker.check(ResultType.COMMIT));
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }
}
