package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class OneOfExpressionThreeExprTest extends OneOfExpressionTestBase {

    public OneOfExpressionThreeExprTest() {
        expression.setExpressions(new FakeExpression(), new FakeExpression(), new FakeExpression());
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
