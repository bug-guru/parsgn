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

    public static QuantifierExpression optional(Expression expression) {
        return repeat(expression, 0, 1);
    }

    public static QuantifierExpression optional(Rule rule) {
        return optional(ref(rule));
    }

    public static QuantifierExpression oneOrMore(Expression expression) {
        return repeat(expression, 1, Integer.MAX_VALUE);
    }

    public static QuantifierExpression oneOrMore(Rule rule) {
        return oneOrMore(ref(rule));
    }

    public static QuantifierExpression zeroOrMore(Expression expression) {
        return repeat(expression, 0, Integer.MAX_VALUE);
    }

    public static QuantifierExpression zeroOrMore(Rule rule) {
        return zeroOrMore(ref(rule));
    }

    public static QuantifierExpression repeat(Expression expression, int minRepeat, int maxRepeat) {
        return new QuantifierExpression().expression(expression).minRepeats(minRepeat).maxRepeats(maxRepeat);
    }

    public static QuantifierExpression repeat(Rule rule, int minRepeat, int maxRepeat) {
        return repeat(ref(rule), minRepeat, maxRepeat);
    }

}
