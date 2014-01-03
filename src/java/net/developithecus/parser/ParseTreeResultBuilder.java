package net.developithecus.parser;

import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class ParseTreeResultBuilder extends ResultBuilder<ParseNode> {

    private ParseNode root;

    @Override
    protected ParseNode createNode(String name, Position beginPosition, int length, String value, List<ParseNode> children) {
        return new ParseNode(name, beginPosition, length, value, children);
    }

    @Override
    protected void committedRoot(ParseNode root) {
        this.root = root;
    }

    public ParseNode getRoot() {
        return root;
    }
}
