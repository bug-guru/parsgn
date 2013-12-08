package net.developithecus.parser2;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class Node {
    private Node parent;
    private List<Node> children = new ArrayList<>();
    private String value;

    public Node parent() {
        return parent;
    }

    public void apply(Node child) {
        child.parent = this;
        children.add(child);
    }

    public String value() {
        return value;
    }

    public void value(String value) {
        this.value = value;
    }
}
