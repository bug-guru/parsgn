package net.developithecus.parser;

import java.util.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Node {
    private final String value;
    private final int beginIndex;
    private final int endIndex;
    private final List<Node> children;

    public Node(String value, int beginIndex, int endIndex, List<Node> children) {
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.value = value;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public Node(String value, int beginIndex, int endIndex) {
        this.children = null;
        this.value = value;
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;
    }

    public String getValue() {
        return value;
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getBeginIndex() {
        return beginIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    @Override
    public String toString() {
        if (children == null || children.isEmpty()) {
            return value;
        } else {
            return value + children;
        }
    }

    public String toTreeString() {
        StringBuilder result = new StringBuilder();
        Deque<Iterator<Node>> stack = new LinkedList<>();
        formatNode(result, 0, this);
        stack.push(children.iterator());

        while (!stack.isEmpty()) {
            Iterator<Node> current = stack.peek();
            if (current.hasNext()) {
                Node node = current.next();
                formatNode(result, stack.size(), node);
                if (node.children != null && !node.children.isEmpty()) {
                    stack.push(node.children.iterator());
                }
            } else {
                stack.pop();
            }
        }
        return result.toString();
    }

    private void formatNode(StringBuilder result, int indent, Node node) {
        for (int i = 0; i < indent; i++) {
            result.append("    ");
        }
        result.append('[');
        StringUtils.escape(result, node.value);
        result.append("]\n");
    }

}
