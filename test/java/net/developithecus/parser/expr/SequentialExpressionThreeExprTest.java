package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class SequentialExpressionThreeExprTest extends SequentialExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();
    private FakeExpression expr2 = new FakeExpression();
    private FakeExpression expr3 = new FakeExpression();

    public SequentialExpressionThreeExprTest() {
        expression.setExpressions(expr1, expr2, expr3);
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr1, checker.next());
        assertSame(expr2, checker.next());
        assertSame(expr3, checker.next());
    }

    /*
        ROLLBACK
        COMMIT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK
        COMMIT, COMMIT, COMMIT
        COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, ROLLBACK_OPT, COMMIT
        COMMIT, ROLLBACK_OPT, ROLLBACK
        COMMIT, ROLLBACK_OPT, ROLLBACK_OPT
        ROLLBACK_OPT, COMMIT, COMMIT
        ROLLBACK_OPT, COMMIT, ROLLBACK
        ROLLBACK_OPT, COMMIT, ROLLBACK_OPT
        ROLLBACK_OPT, ROLLBACK_OPT, COMMIT
        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_OR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CCC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_CCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_COC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_COR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_COO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_OCC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_OCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_OCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildOptionalRollback());
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
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildOptionalRollback());
    }

}
