package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.24.12
 * @since 1.0
 */
class NodeHolder {
    private final Expression.ExpressionChecker checker;
    private final Node node;

    public NodeHolder(Expression.ExpressionChecker checker) {
        this.checker = checker;
        this.node = new Node();
    }

    /**
     * Copy constructor. It creates it's own copy of ExpressionChecker, Node and list of children nodes
     * (but not children nodes themselves)
     *
     * @param nodeHolder NodeHolder to be copied.
     */
    public NodeHolder(NodeHolder nodeHolder) {
        this.checker = nodeHolder.checker.copy();
        this.node = new Node(nodeHolder.node);
    }

    public void apply() {
        node.setValue(checker.getValue());
        node.setPosition(checker.getStartPosition());
        node.setLength(checker.getLength());
    }

    public void applyChild(NodeHolder child) {
        child.apply();
        node.addChild(child.node);
        checker.updateStatus(true, child.getChecker());
    }

    public void cancelChild(NodeHolder child) {
        checker.updateStatus(false, child.getChecker());
    }

    public Expression.ExpressionChecker getChecker() {
        return checker;
    }

    public Node getNode() {
        return node;
    }
}
