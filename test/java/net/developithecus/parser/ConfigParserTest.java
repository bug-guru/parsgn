package net.developithecus.parser;

import org.junit.Test;

import java.io.InputStream;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class ConfigParserTest {

    @Test
    public void testConfigFile() throws Exception {
        EBNFParserBuilder builder = new EBNFParserBuilder();
        Parser parser = builder.createParser();
        long t1 = System.currentTimeMillis();
        try (
                InputStream input = getClass().getResourceAsStream("config.rules")
        ) {
            Node root = parser.parse(input);
            long t2 = System.currentTimeMillis();
            System.out.println(root.toTreeString());
            System.out.println("time: " + (t2 - t1) + "ms");
        }
    }
}
