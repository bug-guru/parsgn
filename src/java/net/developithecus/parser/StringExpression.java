package net.developithecus.parser;

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
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }

    private class Checker extends ExpressionChecker {
        private int offset;

        private Checker(int pos) {
            super(pos);
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            if (offset >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(offset));
            }
            if (codePoints[offset] != codePoint) {
                return Result.MISMATCH;
            }
            return ++offset == len ? Result.MATCH : Result.MORE;
        }

        @Override
        protected boolean isOptional() {
            return false;
        }
    }
}
