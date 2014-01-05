package net.developithecus.parser;

import org.junit.Test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class EBNFTest {

    private Parser generateDefaultParser() throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = getClass().getResourceAsStream("config.rules")
        ) {
            return builder.createParser(ebnfInput);
        }
    }

    private String parseToTree(Parser parser) throws Exception {
        final StringWriter result = new StringWriter(2048);
        try (
                InputStream input = getClass().getResourceAsStream("config.rules")
        ) {
            parser.parse(input, new NodeTreeVisitor() {
                int indent = -1;

                @Override
                public void startNode(ParseNode node) {
                    indent++;
                    printNode(node);
                }

                @Override
                public void endNode(ParseNode node) {
                    indent--;
                }

                @Override
                public void leafNode(ParseNode node) {
                    startNode(node);
                    endNode(node);
                }

                private void printNode(ParseNode node) {
                    for (int i = 0; i < indent; i++) {
                        result.append("    ");
                    }
                    StringUtils.escape(result, node.getName());
                    if (node.getValue() != null) {
                        result.write("=\"");
                        StringUtils.escape(result, node.getValue());
                        result.write("\"");
                    }
                    result.write(String.format(" {%s-%d}\n", node.getBeginPosition(), node.getLength()));
                }

            });
        }
        result.close();
        return result.toString();
    }

    @Test
    public void compareGeneratedAndBuiltIn() throws Exception {
        Parser generated = generateDefaultParser();
        Parser builtIn = new EBNFParserBuilder().createParser();
        String generatedTree = parseToTree(generated);
        String builtInTree = parseToTree(builtIn);
        assertEquals(builtInTree, generatedTree);
    }


    //    @Test
    public void printXml() throws Exception {
        EBNFParserBuilder builder = new EBNFParserBuilder();
        Parser parser = builder.createParser();
        try (
                InputStream input = getClass().getResourceAsStream("config.rules")
        ) {
            XmlResultBuilder resultBuilder = new XmlResultBuilder();
            parser.parse(input, resultBuilder);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StreamResult result = new StreamResult(System.out);
            DOMSource source = new DOMSource(resultBuilder.getResult());
            transformer.transform(source, result);
        }
    }
}
