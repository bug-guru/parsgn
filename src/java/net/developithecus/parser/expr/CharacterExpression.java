package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingContext;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class CharacterExpression extends Expression {
    private CharType charType;

    @Override
    public boolean isOptional() {
        return false;
    }

    public CharType getCharType() {
        return charType;
    }

    public void setCharType(CharType charType) {
        this.charType = charType;
    }

    @Override
    public ExpressionChecker checker(ParsingContext ctx) {
        return new Checker(ctx);
    }

    private class Checker extends ExpressionChecker {

        public Checker(ParsingContext ctx) {
            super(ctx);
        }

        @Override
        public void check() {
            int codePoint = getCtx().getCodePoint();
            if (charType.apply(codePoint)) {
                StringBuilder builder = new StringBuilder(2);
                builder.appendCodePoint(codePoint);
                commitLeafNode(builder.toString());
            } else {
                rollback();
            }
        }
    }
}
