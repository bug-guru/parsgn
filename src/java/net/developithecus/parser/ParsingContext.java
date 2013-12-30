package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public class ParsingContext implements CheckerContext {
    private int index;
    private int codePoint;
    private int nextIndex;
    private ResultType result;
    private Deque<Holder> stack = new LinkedList<>();
    private Node resultTree;

    public ParsingContext(Expression root) {
        pushExpression(root);
    }

    private void reset(int codePoint) {
        this.index = nextIndex;
        this.codePoint = codePoint;
        this.nextIndex = index + 1;
        this.result = ResultType.CONTINUE;
    }

    public int getCodePoint() {
        return codePoint;
    }

    public int getNextIndex() {
        return nextIndex;
    }

    public ResultType getResult() {
        return result;
    }

    public void markForCommit(String nodeValue) {
        Holder holder = stack.peek();
        holder.commit(nodeValue);
        result = ResultType.COMMIT;
    }

    public void markForCommitGroup(String groupNodeValue) {
        Holder holder = stack.peek();
        holder.commitGroup(groupNodeValue);
        result = ResultType.COMMIT;
    }

    public void markForCommit() {
        result = ResultType.COMMIT;
    }

    public void markForRollback() {
        markForRollback(false);
    }

    public void markForRollbackOptional() {
        markForRollback(true);
    }

    private void markForRollback(boolean optional) {
        this.result = optional ? ResultType.ROLLBACK_OPTIONAL : ResultType.ROLLBACK;
        Holder holder = stack.peek();
        this.nextIndex = holder.beginIndex;
        holder.committedNodes = null;
        holder.committed = false;
    }

    public void markForContinue() {
        this.result = ResultType.CONTINUE;
    }

    public ExpressionChecker peekChecker() {
        Holder holder = stack.peek();
        return holder == null ? null : holder.checker;
    }

    public boolean hasCommitted() {
        Holder holder = stack.peek();
        return holder.committed;
    }

    public void next(int codePoint) throws ParsingException {
        reset(codePoint);
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while ((nextExpr = peekChecker().next()) != null) {
            pushExpression(nextExpr);
        }
    }

    private void process() throws ParsingException {
        boolean loop = true;
        while (loop) {
            Holder holder = stack.peek();
            ExpressionChecker currentChecker = holder.checker;
            currentChecker.check();
            switch (getResult()) {
                case CONTINUE:
                    loop = false;
                    break;
                case COMMIT:
                    holder.turn++;
                    if (!popChecker()) {
                        resultTree = holder.committedNodes.get(0);
                        return;
                    }
                    Holder parent = stack.peek();
                    parent.addCommitted(holder);
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    if (!popChecker()) {
                        throw new ParsingException("Syntax error");
                    }
                    break;
                default:
                    throw new ParsingException("unknown result: " + getResult());
            }
        }
    }

    private void pushExpression(Expression nextExpr) {
        Holder top = stack.peek();
        ExpressionChecker nextChecker = nextExpr.checker();
        nextChecker.init(this);
        Holder holder = new Holder();
        holder.beginIndex = index;
        holder.expression = nextExpr;
        holder.checker = nextChecker;
        holder.silent = nextExpr.isSilent() || top != null && top.silent;
        holder.compact = nextExpr.isCompact() || top != null && top.compact;
        stack.push(holder);
    }

    public boolean popChecker() {
        stack.pop();
        return !stack.isEmpty();
    }

    public Node getResultTree() {
        return resultTree;
    }

    @Override
    public String toString() {
        return "ParsingContext{" +
                "result=" + result +
                ", nextIndex=" + nextIndex +
                ", codePoint=" + codePoint +
                ", index=" + index +
                ", stack=" + stack +
                '}';
    }

    private class Holder {
        Expression expression;
        ExpressionChecker checker;
        int turn;
        int beginIndex;
        boolean committed;
        boolean silent;
        boolean compact;
        List<Node> committedNodes;
        StringBuilder compactValue;

        void commit(String nodeValue) {
            committed = true;
            if (silent) {
                return;
            }
            if (compact) {
                compactCommit(nodeValue);
            } else {
                nodeCommit(nodeValue);
            }
        }

        private void compactCommit(String nodeValue) {
            if (compactValue == null) {
                compactValue = new StringBuilder(nodeValue);
            } else {
                compactValue.append(nodeValue);
            }
        }

        private void nodeCommit(String nodeValue) {
            if (committedNodes == null) {
                committedNodes = new ArrayList<>();
            }
            Node node = new Node(nodeValue, beginIndex, nextIndex);
            committedNodes.add(node);
        }

        void commitGroup(String nodeValue) {
            committed = true;
            if (silent) {
                return;
            }
            if (compact) {
                compactCommitGroup(nodeValue);
            } else {
                nodeCommitGroup(nodeValue);
            }
        }

        private void compactCommitGroup(String nodeValue) {
            if (compactValue == null) {
                compactCommit(nodeValue);
            } else {
                compactValue.insert(0, nodeValue);
            }
        }

        private void nodeCommitGroup(String nodeValue) {
            if (committedNodes == null) {
                nodeCommit(nodeValue);
            } else {
                Node node = new Node(nodeValue, beginIndex, nextIndex, committedNodes);
                committedNodes = new ArrayList<>();
                committedNodes.add(node);
            }
        }

        void addCommitted(Holder another) {
            committed = true;
            if (silent ||
                    (another.committedNodes == null || another.committedNodes.isEmpty())
                            && (another.compactValue == null || another.compactValue.length() == 0)) {
                return;
            }
            if (compact) {
                compactAddCommitted(another);
            } else {
                nodeAddCommitted(another);
            }
        }

        private void compactAddCommitted(Holder another) {
            if (compactValue == null) {
                compactValue = another.compactValue;
            } else {
                compactValue.append(another.compactValue);
            }
        }

        private void nodeAddCommitted(Holder another) {
            if (another.compact) {
                commit(another.compactValue.toString());
            } else if (committedNodes == null) {
                committedNodes = another.committedNodes;
            } else {
                committedNodes.addAll(another.committedNodes);
            }
        }

        @Override
        public String toString() {
            return "Holder{" +
                    "checker=" + checker +
                    ", beginIndex=" + beginIndex +
                    ", committed=" + committed +
                    ", silent=" + silent +
                    ", committedNodes=" + committedNodes +
                    '}';
        }
    }
}
