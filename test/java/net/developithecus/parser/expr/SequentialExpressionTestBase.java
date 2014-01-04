package net.developithecus.parser.expr;

import org.junit.Before;

/**
 * @author dima
 */
public class SequentialExpressionTestBase {
    protected final SequentialExpression expression;
    protected SequentialExpression.Checker checker;

    public SequentialExpressionTestBase() {
        expression = new SequentialExpression();
    }

    @Before
    public void setUp() {
        checker = (SequentialExpression.Checker) expression.checker();
    }

}
