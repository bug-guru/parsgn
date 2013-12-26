package net.developithecus.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public abstract class GroupExpressionChecker extends ExpressionChecker {
    private final List<Node> nodes = new ArrayList<>();

    protected GroupExpressionChecker(ParsingContext ctx) {
        super(ctx);
    }

    protected void collectNodes() {
        nodes.addAll(getCtx().takeAndClearCommitted());
    }

    public List<Node> getNodes() {
        return nodes;
    }

    protected void commitNodes() {
        getCtx().commitNodes(nodes);
    }

}
