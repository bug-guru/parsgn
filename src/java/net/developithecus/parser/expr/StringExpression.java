package net.developithecus.parser.expr;

import net.developithecus.parser.*;

import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class StringExpression extends Expression {
    private static final Logger logger = Logger.getLogger(StringExpression.class.getName());
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
            ParsingContext ctx = getCtx();
            logger.entering("StringExpression.Checker", "check", ctx);
            if (offset >= len) {
                throw new IndexOutOfBoundsException(String.valueOf(offset));
            }
            int codePoint = ctx.getCodePoint();
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
            logger.exiting("StringExpression.Checker", "check", ctx);
        }

        @Override
        protected String getName() {
            return "str[" + value + "]";
        }
    }
}
