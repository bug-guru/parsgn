package net.developithecus.parser;

import java.util.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.15.12
 * @since 1.0
 */
public class ManyExpression extends Expression {
    private Expression expression;

    public Expression expression() {
        return expression;
    }

    public ManyExpression expression(Expression expression) {
        this.expression = expression;
        return this;
    }

    @Override
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }

    private class Checker extends ExpressionChecker {
        private List<Node> backlog = new ArrayList<>();
        private List<ExpressionChecker> checkers = new ArrayList<>();

        private Checker(int pos) {
            super(pos);
            ExpressionChecker checker = expression.checker(pos);
            checkers.add(checker);
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            Iterator<ExpressionChecker> checkerIterator = checkers.iterator();
            while (checkerIterator.hasNext()) {
                ExpressionChecker checker = checkerIterator.next();
                if (checker.getPosition() < calcStartFrom()) {
                    checkerIterator.remove();
                    continue;
                }
                Result result = checker.check(codePoint);
                if (result == Result.MATCH) {
                    checkerIterator.remove();
                    Node node = checker.getNode();
                    backlog.add(node);
                    processBacklog();
                } else if (result == Result.MISMATCH) {
                    checkerIterator.remove();
                    return getNode().hasChild() ? Result.MATCH : Result.MISMATCH;
                }
                if (result == Result.MORE) {
                    return Result.MORE;
                }
                round++;
                if (result == Result.MATCH) {
                    getNode().addChild(checker.getNode());
                    if (round < maxRepeats) {
                        checker = expression.checker(getPosition());
                        return Result.MORE;
                    } else {
                        return Result.MATCH;
                    }
                }
                throw new IllegalStateException();
            }
        }

        private void processBacklog() {
            int startFrom = calcStartFrom();
            sortBacklog();
            Iterator<Node> iterator = backlog.iterator();
            while (iterator.hasNext()) {
                Node node = iterator.next();
                int nodeStart = node.getStart();
                if (nodeStart > startFrom) {
                    break;
                }
                iterator.remove();
                if (nodeStart == startFrom) {
                    startFrom += node.getLength();
                    getNode().addChild(node);
                }
            }
        }

        private int calcStartFrom() {
            Node node = getNode().lastChild();
            return node == null ? getPosition() : node.getStart() + node.getLength();
        }

        private void sortBacklog() {
            Collections.sort(backlog, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return o1.getStart() - o2.getStart();
                }
            });
        }

        @Override
        protected boolean isOptional() {
            return optional() || getNode().hasChild();
        }

    }
}
