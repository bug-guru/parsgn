package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import net.developithecus.parser.exceptions.ParsingException;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class SequentialExpressionTwoExprTest extends SequentialExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();
    private FakeExpression expr2 = new FakeExpression();

    public SequentialExpressionTwoExprTest() {
        expression.setExpressions(expr1, expr2);
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr1, checker.next());
        assertSame(expr2, checker.next());
    }

    /*
        ROLLBACK
        COMMIT, COMMIT
        COMMIT, ROLLBACK
        COMMIT, ROLLBACK_OPT
        ROLLBACK_OPT, COMMIT
        ROLLBACK_OPT, ROLLBACK
        ROLLBACK_OPT, ROLLBACK_OPT
     */

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_CR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildOptionalRollback());
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

    @Test(expected = ParsingException.class)
    public void testChecker_OO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildOptionalRollback());
        checker.next();
        checker.checkChildOptionalRollback();
    }
}
