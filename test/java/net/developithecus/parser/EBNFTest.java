package net.developithecus.parser;

import org.junit.Test;

import java.io.InputStream;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class EBNFTest {

    @Test
    public void printConfigFile() throws Exception {
        EBNFParserBuilder builder = new EBNFParserBuilder();
        Parser parser = builder.createParser();
        try (
                InputStream input = getClass().getResourceAsStream("config.rules")
        ) {
            parser.parse(input, new NodeTreeVisitor() {
                int indent = -1;

                @Override
                public void startNode(Node node) {
                    indent++;
                    printNode(node);
                }

                @Override
                public void endNode(Node node) {
                    indent--;
                }

                @Override
                public void leafNode(Node node) {
                    startNode(node);
                    endNode(node);
                }

                private void printNode(Node node) {
                    for (int i = 0; i < indent; i++) {
                        System.out.append("    ");
                    }
                    StringUtils.escape(System.out, node.getName());
                    if (node.getValue() != null) {
                        System.out.append("=\"");
                        StringUtils.escape(System.out, node.getValue());
                        System.out.append("\"");
                    }
                    System.out.format(" {%s-%d}\n", node.getBeginPosition(), node.getLength());
                }

            });
        }
    }
}
