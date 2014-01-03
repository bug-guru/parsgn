package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Test;

import static org.junit.Assert.assertSame;

/**
 * @author dima
 */
public class OneOfExpressionOneExprTest extends OneOfExpressionTestBase {

    public OneOfExpressionOneExprTest() {
        expression.setExpressions(new FakeExpression());
    }

    @Test
    public void testChecker_COMMIT() throws Exception {
        checker.next();
        assertSame(ResultType.COMMIT, checker.checkChildCommit());
    }

    @Test
    public void testChecker_ROLLBACK() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildRollback());
    }

    @Test
    public void testChecker_ROLLBACKOPTIONAL() throws Exception {
        checker.next();
        assertSame(ResultType.ROLLBACK, checker.checkChildOptionalRollback());
    }
}
