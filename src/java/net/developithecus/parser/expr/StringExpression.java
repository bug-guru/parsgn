package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingContext;
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

    @Override
    public boolean isOptional() {
        return false;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.codePoints = StringUtils.toCodePoints(value);
        this.len = codePoints.length;
    }

    @Override
    public ExpressionChecker checker(ParsingContext ctx) {
        return new Checker(ctx);
    }

    private class Checker extends ExpressionChecker {
        private int offset;

        private Checker(ParsingContext ctx) {
            super(ctx);
        }

        @Override
        public void check() {
            if (offset >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(offset));
            }
            int codePoint = getCtx().getCodePoint();
            if (codePoints[offset] != codePoint) {
                rollback();
            } else {
                offset++;
                if (offset == len) {
                    commitLeafNode(value);
                } else {
                    continueProcessing();
                }
            }
        }
    }
}
