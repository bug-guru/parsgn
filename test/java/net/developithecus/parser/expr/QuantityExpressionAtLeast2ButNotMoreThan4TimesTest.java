package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class QuantityExpressionAtLeast2ButNotMoreThan4TimesTest extends QuantityExpressionTestBase {

    public QuantityExpressionAtLeast2ButNotMoreThan4TimesTest() {
        expression.minOccurrences(2).maxOccurrences(4);
    }

    /*
        ROLLBACK
        ROLLBACK_OPT
        COMMIT, ROLLBACK
        COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, COMMIT, ROLLBACK
        COMMIT, COMMIT, COMMIT, ROLLBACK_OPT
        COMMIT, COMMIT, COMMIT, COMMIT
     */

    @Test
    public void testChecker_R() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_O() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
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
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_CCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildRollback());
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
    public void testChecker_CCCR() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildRollback());
    }

    @Test
    public void testChecker_CCCO() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildOptionalRollback());
    }

    @Test
    public void testChecker_CCCC() throws Exception {
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.CONTINUE, checker.checkChildCommit());
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }
}
