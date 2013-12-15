package net.developithecus.parser;

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
    public ExpressionChecker checker() {
        return new Checker();
    }


    private class Checker extends ExpressionChecker {
        private Node node = new Node();

        public Checker() {
            super();
        }

        public Node getNode() {
            return node;
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            StringBuilder builder = new StringBuilder(2);
            builder.appendCodePoint(codePoint);
            if (charType.apply(codePoint)) {
                node.setValue(builder.toString());
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
