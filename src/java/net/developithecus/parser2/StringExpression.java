package net.developithecus.parser2;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class StringExpression extends Expression {
    private String value;
    private int[] codePoints;
    private int len;

    public String value() {
        return value;
    }

    public StringExpression value(String value) {
        this.value = value;
        this.codePoints = StringUtils.toCodePoints(value);
        this.len = codePoints.length;
        return this;
    }

    @Override
    public ExpressionChecker checker(Node result) {
        return new Checker(result);
    }

    private class Checker extends ExpressionChecker {
        private int pos;

        protected Checker(Node parent) {
            super(parent);
        }

        @Override
        protected Boolean doCheck(int codePoint) {
            if (pos >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(pos));
            }
            if (codePoints[pos] != codePoint) {
                return Boolean.FALSE;
            }
            return ++pos == len ? Boolean.TRUE : null;
        }

        @Override
        protected String value() {
            return value;
        }
    }
}
