package net.developithecus.parser;

import java.util.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Node {
    private final String name;
    private final Position beginPosition;
    private final int length;
    private final List<Node> children;
    private final String value;

    public Node(String name, Position beginPosition, int length) {
        this.name = name;
        this.beginPosition = beginPosition;
        this.length = length;
        this.value = null;
        this.children = null;
    }

    public Node(String name, Position beginPosition, int length, String value) {
        this.name = name;
        this.beginPosition = beginPosition;
        this.length = length;
        this.value = value;
        this.children = null;
    }

    public Node(String name, Position beginPosition, int length, List<Node> children) {
        this.name = name;
        this.beginPosition = beginPosition;
        this.length = length;
        this.value = null;
        this.children = Collections.unmodifiableList(children);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<Node> getChildren() {
        return children;
    }

    public Position getBeginPosition() {
        return beginPosition;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        if (value != null) {
            result.append('=').append(value);
        }
        if (children != null && !children.isEmpty()) {
            result.append(children);
        }
        return result.toString();
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
        StringUtils.escape(result, node.name);
        if (node.value != null) {
            result.append("=\"");
            StringUtils.escape(result, node.value);
            result.append("\"");
        }
        result
                .append(" {")
                .append(node.beginPosition)
                .append("-")
                .append(node.length)
                .append("}\n");
    }

}
