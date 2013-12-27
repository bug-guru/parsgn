package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingContext;
import net.developithecus.parser.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.27.12
 * @since 1.0
 */
public class StringLiteralExpression extends Expression {
    private static final int QUOTE_CP = '"';
    private static final int ESCAPE_CP = '\\';

    @Override
    public boolean isOptional() {
        return false;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {
        private boolean started;
        private boolean escaped;
        private StringBuilder result = new StringBuilder();

        @Override
        public Expression next() {
            return null;
        }

        @Override
        public void check() throws ParsingException {
            ParsingContext ctx = getCtx();
            int codePoint = ctx.getCodePoint();
            if (started) {
                if (escaped) {
                    result.appendCodePoint(codePoint);
                    escaped = false;
                } else if (codePoint == QUOTE_CP) {
                    commitLeafNode(result.toString());
                } else if (codePoint == ESCAPE_CP) {
                    escaped = true;
                }
            } else if (codePoint == QUOTE_CP) {
                continueProcessing();
                started = true;
            } else {
                rollback();
            }
        }

        @Override
        protected String getName() {
            return "StrLit";
        }
    }
}
