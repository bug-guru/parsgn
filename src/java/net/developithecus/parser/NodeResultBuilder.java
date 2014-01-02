package net.developithecus.parser;

import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class NodeResultBuilder extends ResultBuilder<Node> {

    private Node root;

    @Override
    protected Node createNode(String name, Position beginPosition, int length, String value, List<Node> children) {
        return new Node(name, beginPosition, length, value, children);
    }

    @Override
    protected void committedRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}
