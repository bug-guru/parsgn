package net.developithecus.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Node {
    private final List<Node> children;
    private final String value;
    private final int beginIndex;
    private final int endIndex;

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
}
