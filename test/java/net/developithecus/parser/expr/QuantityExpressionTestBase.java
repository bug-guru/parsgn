package net.developithecus.parser.expr;

import org.junit.Before;

/**
 * @author dima
 */
public class QuantityExpressionTestBase {
    protected final QuantityExpression expression;
    protected QuantityExpression.Checker checker;

    public QuantityExpressionTestBase() {
        expression = new QuantityExpression();
        expression.setExpression(new FakeExpression());
    }

    @Before
    public void setUp() {
        checker = (QuantityExpression.Checker) expression.checker();
    }

}
