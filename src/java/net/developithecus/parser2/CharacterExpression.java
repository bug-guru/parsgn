package net.developithecus.parser2;

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
    public ExpressionChecker checker(Node result) {
        return new Checker(result);
    }


    private class Checker extends ExpressionChecker {
        private StringBuilder builder = new StringBuilder(2);

        public Checker(Node result) {
            super(result);
        }

        @Override
        protected Boolean doCheck(int codePoint) {
            builder.setLength(0);
            builder.appendCodePoint(codePoint);
            return charType.apply(codePoint);
        }

        @Override
        protected String value() {
            return builder.toString();
        }

        @Override
        protected void reset() {
            /TODO
        }
    }
}
