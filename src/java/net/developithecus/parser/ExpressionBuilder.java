package net.developithecus.parser;

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

}
