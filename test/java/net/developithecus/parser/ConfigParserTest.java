package net.developithecus.parser;

import org.junit.Test;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class ConfigParserTest {
    private static final String rules = "ConfigFile: Rule {Rule} {I};\n\n" +
            "Rule: {I} Token {I} \":\" ExpressionList \";\";\n" +
            "\n" +
            "I: WhiteSpace | SingleLineComment | MultiLineComment;\n" +
            "\n" +
            "WhiteSpace: #WHITESPACE;\n" +
            "\n" +
            "SingleLineComment: \"//\" {#DEFINED} #EOL;\n" +
            "\n" +
            "MultiLineComment: \"/*\" {#DEFINED} \"*/\";\n" +
            "\n" +
            "Token: #UNICODE_IDENTIFIER_START {#UNICODE_IDENTIFIER_PART};\n" +
            "\n" +
            "Expression: RuleRef | AltGroup | CharType | String | Group | RepeatGroup | OptGroup;\n" +
            "\n" +
            "RuleRef: Token;\n" +
            "\n" +
            "AltGroup: AltGroupItem {I} \"|\" {I} AltGroupItem {{I} \"|\" {I} AltGroupItem};\n" +
            "\n" +
            "AltGroupItem: RuleRef | CharType | String | Group;\n" +
            "//TODO: list all possible char types\n" +
            "CharType: \"#\" Token;" +
            "\n" +
            "String: \"\\\"\" {\"\\\\\\\"\" | \"\\\\\\\\\" | #DEFINED} \"\\\"\"; \n" +
            "\n" +
            "Group: \"(\" ExpressionList \")\";\n" +
            "\n" +
            "RepeatGroup: \"{\" ExpressionList \"}\";\n" +
            "\n" +
            "OptGroup: \"[\" ExpressionList \"]\";\n" +
            "\n" +
            "ExpressionList: {I} Expression {{I} Expression} {I};";

    @Test
    public void testConfigFile() throws Exception {
        ConfigParser parser = new ConfigParser();
        long t1 = System.currentTimeMillis();
        Node root = parser.parse(rules);
        long t2 = System.currentTimeMillis();
        System.out.println(root.toTreeString());
        System.out.println("time: " + (t2 - t1) + "ms");
    }
}
