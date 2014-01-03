package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;

/**
 * @author dima
 */
public class FakeExpression extends Expression {
    @Override
    public ExpressionChecker checker() {
        return null;
    }
}
