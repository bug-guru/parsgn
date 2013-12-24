package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionCheckerException;
import net.developithecus.parser.Result;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class CharacterExpression extends Expression {
    private CharType charType;

    public CharType type() {
        return charType;
    }

    public CharacterExpression type(CharType charType) {
        this.charType = charType;
        return this;
    }

    @Override
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }


    private class Checker extends ExpressionChecker {

        public Checker(int pos) {
            super(pos);
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            StringBuilder builder = new StringBuilder(2);
            builder.appendCodePoint(codePoint);
            if (charType.apply(codePoint)) {
                getNode().setValue(builder.toString());
                return Result.MATCH;
            } else {
                return Result.MISMATCH;
            }
        }

        @Override
        protected boolean isOptional() {
            return false;
        }

    }
}
