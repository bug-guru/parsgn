package net.developithecus.parser;

import org.junit.Test;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class ConfigParserTest {

    @Test
    public void testConfigFile() throws Exception {
        ConfigParser parser = new ConfigParser();
        Node root = parser.parse("ConfigFile: Rule {Rule} {I};\n" +
                "\n" +
                "Rule: {I} Token {I} \":\" ExpressionList \";\";\n");
        System.out.println(root);
    }
}
