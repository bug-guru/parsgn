package net.developithecus.parser2;


/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public abstract class Expression {
    private int minRepeats = 1;
    private int maxRepeats = 1;
    private boolean negative = false;
    private boolean preserve = false;

    public static SequentialGroup sequence(Expression... expressions) {
        return new SequentialGroup().addAll(expressions);
    }

    public static ParallelGroup oneOf(Expression... expressions) {
        return new ParallelGroup().addAll(expressions);
    }

    public static StringExpression string(String str) {
        return new StringExpression().value(str);
    }

    public static ReferenceExpression ref(Rule reference) {
        return new ReferenceExpression().reference(reference);
    }

    public int minRepeats() {
        return minRepeats;
    }

    public Expression requiredAtLeast(int min) {
        this.minRepeats = min;
        return this;
    }

    public Expression optional() {
        this.minRepeats = 0;
        return this;
    }

    public int maxRepeats() {
        return maxRepeats;
    }

    public Expression repeatNoMore(int max) {
        this.maxRepeats = max;
        return this;
    }

    public Expression repeat() {
        this.maxRepeats = Integer.MAX_VALUE;
        return this;
    }

    public Expression negative() {
        this.negative = true;
        return this;
    }

    public Expression preserve() {
        this.preserve = true;
        return this;
    }

    public abstract ExpressionChecker checker(Node result);

    public abstract class ExpressionChecker {
        protected final Node parent;

        protected ExpressionChecker(Node parent) {
            this.parent = parent;
        }

        protected abstract Boolean doCheck(int codePoint);

        protected abstract String value();

        protected abstract void reset();

        public Boolean check(int codePoint) {
            Boolean result = doCheck(codePoint);
            if (result == null) {
                return null;
            } else if (result) {
                parent.newChild().apply(value());
            }
            return result;
        }

    }
}
