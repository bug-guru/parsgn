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
