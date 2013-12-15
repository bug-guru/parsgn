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

    public abstract ExpressionChecker checker();

    public abstract class ExpressionChecker {
        private Node node = new Node();

        protected abstract Result check(int codePoint) throws ExpressionCheckerException;

        protected abstract boolean isOptional();

        public Node getNode() {
            return node;
        }
    }
}
