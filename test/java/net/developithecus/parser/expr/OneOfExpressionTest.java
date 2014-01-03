package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ResultType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class OneOfExpressionTest {
    private static final Expression[] ONE = new Expression[]{new FakeExpression()};
    private static final Expression[] TWO = new Expression[]{new FakeExpression(), new FakeExpression()};
    private static final Expression[] THREE = new Expression[]{new FakeExpression(), new FakeExpression(), new FakeExpression()};
    private final OneOfExpression expression = new OneOfExpression();
    private OneOfExpression.Checker checker;

    @Before
    public void createChecker() {
        checker = (OneOfExpression.Checker) expression.checker();
    }

    @Test
    public void testChecker_1_COMMIT() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_1_ROLLBACK() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_1_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(ONE);
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_2_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_2_ROLLBACK_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_2_ROLLBACK_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_2_ROLLBACK_ROLLBACK() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_2_ROLLBACKOPTIONAL_COMMIT() throws Exception {
        expression.setExpressions(TWO);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_3_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_3_ROLLBACK_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_COMMIT() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_ROLLBACK() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_3_ROLLBACK_ROLLBACKOPTIONAL_ROLLBACKOPTIONAL() throws Exception {
        expression.setExpressions(THREE);
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }
}
