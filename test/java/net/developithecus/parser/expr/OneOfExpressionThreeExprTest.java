package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
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
    public void testChecker_OC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_RRC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_ROC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_ORC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_OOC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_RRR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_RRO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_ROR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_ROO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_ORR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_ORO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_OOR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_OOO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }
}
