package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class QuantityExpressionZeroOrOneTest extends QuantityExpressionTestBase {

    public QuantityExpressionZeroOrOneTest() {
        expression.minOccurrences(0).maxOccurrences(1);
    }

    /*
        COMMIT
        ROLLBACK
        ROLLBACK_OPT
     */

    @Test
    public void testChecker_COMMIT() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_ROLLBACK() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildRollback());
    }

    @Test
    public void testChecker_ROLLBACK_OPTIONAL() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK_OPTIONAL, checker.checkChildOptionalRollback());
    }
}
