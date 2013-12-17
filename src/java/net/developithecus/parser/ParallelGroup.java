package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class ParallelGroup extends Group<ParallelGroup> {
    @Override
    public ExpressionChecker checker(int pos) {
        return new Checker(pos);
    }

    private class Checker extends ExpressionChecker {
        private List<ExpressionChecker> children = new ArrayList<>();
        private List<Node> candidates = new ArrayList<>();

        private Checker(int pos) {
            super(pos);
            prepareNextRound();
        }

        private void prepareNextRound() {
            for (Expression expr : getExpressions()) {
                ExpressionChecker checker = expr.checker();
                children.add(checker);
            }
        }

        @Override
        protected Result check(int codePoint) throws ExpressionCheckerException {
            List<ExpressionChecker> newCheckers = new ArrayList<>();
            Iterator<ExpressionChecker> i = children.iterator();
            boolean hasMore = false;
            while (i.hasNext()) {
                ExpressionChecker checker = i.next();
                Result result = checker.check(codePoint);
                switch (result) {
                    case MATCH:
                        Node node = checker.getNode();
                        candidates.add(node);
                        if (node.getLength() == 1) {
                            newCheckers.add(checker.getExpression().checker());
                        }
                        i.remove();
                        break;
                    case MISMATCH:
                        newCheckers.add(checker.getExpression().checker());
                        i.remove();
                        break;
                    case MORE:
                        hasMore = true;
                        newCheckers.add(checker.getExpression().checker());
                        break;
                    default:
                        throw new IllegalStateException(String.valueOf(result));
                }
            }
            if (hasMore) {
                children.addAll(newCheckers);
                return Result.MORE;
            }
            if (children.isEmpty() && candidates.isEmpty()) {
                return Result.MISMATCH;
            }
            // TODO process candidates; which one should "win"?
        }

        @Override
        protected boolean isOptional() {
            return false;
        }
    }
}
