package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public interface NodeTreeVisitor {
    public void startNode(ParseNode node);

    public void endNode(ParseNode node);

    public void leafNode(ParseNode node);
}
