package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class CharacterExpression extends Expression {
    private CharType charType;

    public CharType getCharType() {
        return charType;
    }

    public void setCharType(CharType charType) {
        this.charType = charType;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    private class Checker extends ExpressionChecker {

        @Override
        public Expression next() {
            return null;
        }

        @Override
        public void check() throws ParsingException {
            int codePoint = ctx().getCodePoint();
            if (charType.apply(codePoint)) {
                StringBuilder builder = new StringBuilder(2);
                builder.appendCodePoint(codePoint);
                ctx().markForCommit(builder.toString());
            } else {
                ctx().markForRollback();
            }
        }

        @Override
        protected String getName() {
            return "char[" + charType + "]";
        }
    }


}
