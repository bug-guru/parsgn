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
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private int pos;

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            if (pos >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(pos));
            }
            if (codePoints[pos] != codePoint) {
                return Result.MISMATCH;
            }
            return ++pos == len ? Result.MATCH : Result.MORE;
        }

        @Override
        protected boolean isOptional() {
            return false;
        }
    }
}
