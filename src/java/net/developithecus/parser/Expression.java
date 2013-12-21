package net.developithecus.parser;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private boolean optional = false;

    public Expression optional(boolean optional) {
        this.optional = optional;
        return this;
    }

    public boolean optional() {
        return optional;
    }

    public abstract ExpressionChecker checker(int pos);

    public abstract class ExpressionChecker {
        private final Node node;
        private final int startPosition;
        private int length;

        protected ExpressionChecker(int pos) {
            this.node = new Node(pos);
            this.startPosition = pos;
        }

        protected abstract Result check(int codePoint) throws ExpressionCheckerException;

        protected boolean isOptional() {
            return optional;
        }

        public Result push(int codePoint) throws ExpressionCheckerException {
            length++;
            return check(codePoint);
        }

        public int getPosition() {
            return startPosition + length - 1;
        }

        public Node getNode() {
            return node;
        }

        public Expression getExpression() {
            return Expression.this;
        }
    }
}
