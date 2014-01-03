package net.developithecus.parser;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class ParseNode {
    private final String name;
    private final Position beginPosition;
    private final int length;
    private final List<ParseNode> children;
    private final String value;

    public ParseNode(String name, Position beginPosition, int length, String value, List<ParseNode> children) {
        this.name = name;
        this.beginPosition = beginPosition;
        this.length = length;
        this.value = value;
        this.children = children == null ? null : Collections.unmodifiableList(children);
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<ParseNode> getChildren() {
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
