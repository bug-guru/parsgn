package net.developithecus.parser2;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialGroup extends Group<SequentialGroup> {
    @Override
    public ExpressionChecker checker(Node result) {
        return new Checker(result);
    }

    private class Checker extends ExpressionChecker {
        public Checker(Node result) {
            super(result);
        }

        @Override
        protected Boolean doCheck(int codePoint) {
            return null;
        }

        @Override
        protected String value() {
            return null;
        }

        @Override
        protected void reset() {

        }
    }
}
