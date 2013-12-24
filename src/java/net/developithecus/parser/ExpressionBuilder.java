package net.developithecus.parser;

import net.developithecus.parser.expr.ManyExpression;
import net.developithecus.parser.expr.ReferenceExpression;
import net.developithecus.parser.expr.SequentialGroup;
import net.developithecus.parser.expr.StringExpression;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.6.12
 * @since 1.0
 */
public final class ExpressionBuilder {
    private ExpressionBuilder() {

    }

    public static SequentialGroup sequence(Expression... expressions) {
        return new SequentialGroup().addAll(expressions);
    }

    public static ParallelGroup oneOf(Expression... expressions) {
        return new ParallelGroup().addAll(expressions);
    }

    public static StringExpression string(String str) {
        return new StringExpression().value(str);
    }

    public static ReferenceExpression ref(Rule reference) {
        return new ReferenceExpression().reference(reference);
    }

    public static ManyExpression many(Expression expression) {
        return new ManyExpression().expression(expression);
    }

    public static ManyExpression many(Rule rule) {
        return many(ref(rule));
    }

}
