package net.developithecus.parser.expr;

import net.developithecus.parser.ResultType;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author dima
 */
public class StringExpressionTest {
    protected final StringExpression expression;
    protected StringExpression.Checker checker;

    public StringExpressionTest() {
        expression = new StringExpression();
    }

    @Before
    public void setUp() {
        checker = (StringExpression.Checker) expression.checker();
    }

    @Test
    public void testChecker_next() throws Exception {
        assertNull(checker.next());
    }

    @Test
    public void testChecker_OK() throws Exception {
        expression.setValue("Hello, World!");
        expression.setResult(null);
        test(ResultType.COMMIT, "Hello, World!");
        assertEquals(null, checker.getResult());
    }

    @Test
    public void testChecker_FAIL() throws Exception {
        expression.setValue("Hello, World!");
        test(ResultType.ROLLBACK, "Hello, World?");
    }

    @Test
    public void testChecker_Transform() throws Exception {
        expression.setValue("Hello, World!");
        expression.setResult("Bye-bye!");
        test(ResultType.COMMIT, "Hello, World!");
        assertEquals("Bye-bye!", checker.getResult());
    }

    private void test(ResultType expectedResult, String input) throws Exception {
        for (int i = 0; i < input.length() - 1; i++) {
            assertSame(ResultType.CONTINUE, checker.check(input.codePointAt(i)));
        }
        assertSame(expectedResult, checker.check(input.codePointAt(input.length() - 1)));
    }
}
