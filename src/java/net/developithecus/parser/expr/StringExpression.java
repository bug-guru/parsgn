package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;
import net.developithecus.parser.StringUtils;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class StringExpression extends Expression {
    private String value;
    private int[] codePoints;
    private int len;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.codePoints = StringUtils.toCodePoints(value);
        this.len = codePoints.length;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private int offset;

        @Override
        public Expression next() {
            return null;
        }

        @Override
        public void check() throws ParsingException {
            if (offset >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(offset));
            }
            int codePoint = ctx().getCodePoint();
            if (codePoints[offset] != codePoint) {
                ctx().markForRollback();
            } else {
                offset++;
                if (offset == len) {
                    ctx().markForCommit(value);
                } else {
                    ctx().markForContinue();
                }
            }
        }

        @Override
        protected String getName() {
            return "str[" + value + "]";
        }
    }
}
