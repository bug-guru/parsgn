package net.developithecus.parser;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private boolean negative = false;
    private boolean preserve = false;


    public Expression negative() {
        this.negative = true;
        return this;
    }

    public Expression preserve() {
        this.preserve = true;
        return this;
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

        protected abstract boolean isOptional();

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
