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

    public abstract ExpressionChecker checker(Node result);

    public class CheckerIterator {
        private int round = -1;
    }

    public abstract class ExpressionChecker {
        private int round = -1;
        protected final Node parent;
        private Node current;
        private CheckerStatus status;

        protected ExpressionChecker(Node parent) {
            this.parent = parent;
            prepareNextRound();
        }

        private void updateStatus() {
            if (status == CheckerStatus.DONE) {
                return;
            }
            if (minRepeats > round) {
                status = CheckerStatus.REQUIRED;
            } else if (maxRepeats > round) {
                status = CheckerStatus.OPTIONAL;
            } else {
                status = CheckerStatus.DONE;
            }
        }

        protected abstract Result doCheck(int codePoint);

        protected abstract String value();

        protected abstract void reset();

        public CheckerStatus status() {
            return status;
        }

        private void prepareNextRound() {
            round++;
            current = new Node();
            updateStatus();
            reset();
        }

        public Result check(int codePoint) {
            Result result = doCheck(codePoint);
            switch (result) {
                case MATCH:
                    processMatch();
                    break;
                case MISMATCH:
                    result = processMismatch();
                    break;
                case MORE:
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(result));
            }
            return result;
        }

        private Result processMismatch() {
            current = null;
            Result result = status == CheckerStatus.OPTIONAL ? Result.MATCH : Result.MISMATCH;
            status = CheckerStatus.DONE;
            return result;
        }

        private void processMatch() {
            current.setValue(value());
            parent.addChild(current);
            prepareNextRound();
        }

    }
}
