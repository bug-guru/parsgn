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
