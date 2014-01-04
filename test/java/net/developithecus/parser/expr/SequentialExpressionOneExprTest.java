package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class SequentialExpressionOneExprTest extends SequentialExpressionTestBase {
    private FakeExpression expr1 = new FakeExpression();

    public SequentialExpressionOneExprTest() {
        expression.setExpressions(expr1);
    }

    @Test
    public void testChecker_next() throws Exception {
        assertSame(expr1, checker.next());
    }

    /*
        COMMIT
        ROLLBACK
        ROLLBACK_OPT
     */

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
