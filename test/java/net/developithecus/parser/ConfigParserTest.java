package net.developithecus.parser;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class ConfigParserTest {
    private static final String rules = "/*012345678901234567890123456789*\n" +
            "  012345678901234567890123456789012345678901234 */\n" +
            "ConfigFile: Rule {Rule} {I};\n" +
            "Rule: {I} Token {I} \":\" ExpressionList \";\";\n" +
            "\n" +
            "// Ignorable\n" +
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
            "\n" +
            "CharType: \"#\" Token; //TODO: list all possible char types\n" +
            "\n" +
            "String: STRING_LITERAL;\n" +
            "\n" +
            "Group: \"(\" ExpressionList \")\";\n" +
            "\n" +
            "RepeatGroup: \"{\" ExpressionList \"}\";\n" +
            "\n" +
            "OptGroup: \"[\" ExpressionList \"]\";\n" +
            "\n" +
            "ExpressionList: {I} Expression {{I} Expression} {I};";

    @BeforeClass
    public static void init() throws IOException {
//        LogManager.getLogManager().readConfiguration(new FileInputStream("C:\\Users\\Dimitry\\logging.properties"));
    }

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
