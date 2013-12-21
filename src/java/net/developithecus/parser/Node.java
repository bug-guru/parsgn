package net.developithecus.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Node {
    private List<Node> children = new ArrayList<>();
    private String value;
    private int start;
    private int length = Integer.MIN_VALUE;

    public Node(int start) {
        this.start = start;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addChild(Node child) {
        start = Math.min(child.getStart(), start);
        length = Math.max(child.getLength(), length);
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean hasChild() {
        return !children.isEmpty();
    }

    public Node lastChild() {
        return children.isEmpty() ? null : children.get(children.size() - 1);
    }
}
