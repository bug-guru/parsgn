package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionCheckerException;
import net.developithecus.parser.Node;
import net.developithecus.parser.Result;

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
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            ExpressionChecker newChecker = expression.checker(getCurrentPosition());
            checkers.add(newChecker);
            Iterator<ExpressionChecker> checkerIterator = checkers.iterator();
            while (checkerIterator.hasNext()) {
                ExpressionChecker checker = checkerIterator.next();
                Result result = checker.check(codePoint);
                if (result == Result.MORE_FROM_REQUIRED) {
                    continue;
                }
                checkerIterator.remove();
                if (result == Result.MATCH) {
                    Node node = checker.getNode();
                    backlog.add(node);
                }
            }
            processBacklog();
            if (chainIsBroken()) {
                backlog.clear();
                checkers.clear();
                return getNode().hasChild() ? Result.MATCH : Result.MISMATCH_FROM_REQUIRED;
            } else {
                return Result.MORE_FROM_REQUIRED;
            }
        }

        private boolean chainIsBroken() {
            if (checkers.isEmpty()) {
                return true;
            }
            ExpressionChecker firstChecker = checkers.get(0);
            int startFrom = calcEffectivePosition();
            return startFrom != firstChecker.getStartPosition();
        }

        private void processBacklog() {
            int startFrom = calcEffectivePosition();
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

        private int calcEffectivePosition() {
            Node node = getNode().lastChild();
            return node == null ? getStartPosition() : node.getStart() + node.getLength();
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
