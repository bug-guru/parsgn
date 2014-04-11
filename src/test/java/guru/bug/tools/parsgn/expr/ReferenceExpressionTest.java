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
import guru.bug.tools.parsgn.Rule;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;


/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 */
public class ReferenceExpressionTest {
    private FakeExpression expr;
    private ReferenceExpression.Checker checker;

    @Before
    public void setUp() throws Exception {
        Rule rule = new Rule("fake");
        ReferenceExpression expression = new ReferenceExpression("fake");
        expression.setReference(rule);
        expr = new FakeExpression();
        rule.setExpression(expr);
        checker = (ReferenceExpression.Checker) expression.checker();
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr, checker.next());
    }

    @Test
    public void testChecker_C() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.check(ResultType.COMMIT));
    }

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.check(ResultType.ROLLBACK));
    }

    @Test
    public void testChecker_O() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.check(ResultType.ROLLBACK_OPTIONAL));
    }
}
