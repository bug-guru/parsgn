package net.developithecus.parser;

import net.developithecus.parser.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class DefaultParserBuilder extends ParserBuilder {
    public static final Parser EBNF_PARSER = new EBNFParserBuilder().createParser();

    public DefaultParserBuilder(InputStream input) throws IOException, ParsingException {
        Node root = EBNF_PARSER.parse(input);

    }
}
