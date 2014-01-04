package net.developithecus.parser.expr;

import org.junit.Before;

/**
 * @author dima
 */
public class OneOfExpressionTestBase {
    protected final OneOfExpression expression = new OneOfExpression();
    protected OneOfExpression.Checker checker;

    @Before
    public void setUp() {
        checker = (OneOfExpression.Checker) expression.checker();
    }
}
