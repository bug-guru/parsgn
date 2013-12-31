package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public interface NodeTreeVisitor {
    public void startNode(Node node);

    public void endNode(Node node);

    public void leafNode(Node node);
}
