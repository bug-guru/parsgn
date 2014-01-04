package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
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
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildCommit());
    }

    @Test
    public void testChecker_0x_optional2() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        assertSame(loop, checker.next());
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildRollback());
    }

    @Test
    public void testChecker_1x_optional1() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        assertSame(loop, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        assertSame(cond, checker.next());
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_1x_optional2() throws Exception {
        expression.setMinOccurrences(0);
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        assertSame(loop, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        assertSame(cond, checker.next());
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        assertSame(loop, checker.next());
        assertSame(ResultType.COMMIT, checker.checkChildRollback());
    }
}
