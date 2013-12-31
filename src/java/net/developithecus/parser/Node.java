package net.developithecus.parser;

import java.util.Collections;
import java.util.List;

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

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}
