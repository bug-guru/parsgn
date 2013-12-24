package net.developithecus.parser;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.11.12
 * @since 1.0
 */
public class Path {
    // TODO This should be list of NodeHolder. Each NodeHolder has it's own list of applied children nodes
    // TODO and it's own copy of ExpressionChecker.
    private Deque<NodeHolder> nodes = new LinkedList<>();

    public Path(Expression.ExpressionChecker leaf) {
        this(Collections.<NodeHolder>emptyList(), leaf);
    }

    // TODO Copying nodes need to copy not only ExpressionChecker, but list of applied children as well
    // TODO so applied children list will be independent copy and will not be shared between paths.
    // TODO Finally only one path will win after processing all data. This will be a tree of tokens.
    public Path(Path path, Expression.ExpressionChecker newLeaf) {
        this(path.nodes, newLeaf);
    }

    public Path(Collection<NodeHolder> original, Expression.ExpressionChecker newLeaf) {
        if (original != null) {
            copyNodes(original);
        }
        NodeHolder node = new NodeHolder(newLeaf);
        nodes.push(node);
    }

    private void copyNodes(Collection<NodeHolder> original) {
        for (NodeHolder node : original) {
            NodeHolder nodeCopy = new NodeHolder(node);
            nodes.add(nodeCopy);
        }
    }

    public Expression.ExpressionChecker leaf() {
        return nodes.peek().getChecker();
    }

    public Expression.ExpressionChecker removeLeaf() {
        nodes.pop();
        return leaf();
    }

    public Expression.ExpressionChecker applyLeaf() {
        NodeHolder oldLeaf = nodes.pop();
        NodeHolder newLeaf = nodes.peek();
        newLeaf.applyChild(oldLeaf);
        return leaf();
    }

    public void push(int codePoint) throws ExpressionCheckerException {
        Result result = leaf().push(codePoint);
        while (result != Result.MORE) {
            if (result == Result.MATCH) {
                applyLeaf();
            } else {
                removeLeaf();
            }
        }
    }
}
