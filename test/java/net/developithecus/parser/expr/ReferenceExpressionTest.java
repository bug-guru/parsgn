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
import net.developithecus.parser.RuleDef;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;


/**
 * @author dima
 */
public class ReferenceExpressionTest {
    private RuleDef rule = new RuleDef("fake");
    private ReferenceExpression expression = new ReferenceExpression(rule);
    private FakeExpression expr = new FakeExpression();
    private ReferenceExpression.GroupingChecker checker;

    public ReferenceExpressionTest() {
        rule.setExpression(expr);
    }

    @Before
    public void setUp() throws Exception {
        checker = (ReferenceExpression.GroupingChecker) expression.checker();
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr, checker.next());
    }

    @Test
    public void testChecker_C() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_O() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildOptionalRollback());
    }
}
