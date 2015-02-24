/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.ebnf;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.builder.ConfigFileBuilder;
import guru.bug.tools.parsgn.exceptions.InternalParsingException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.utils.XmlResultBuilder;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.Reader;

/**
 * This builder can be used to read your EBNF file and create the {@link guru.bug.tools.parsgn.Parser},
 * which further will be used against files of format described by the EBNF.
 * Instance of the {@code DefaultParserBuilder} is threadsafe. So it can be used to create multiple different parsers even in parallel.
 *
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class DefaultParserBuilder {
    private final Parser EBNF_PARSER = new EBNFParser();
    private final JAXBContext jaxbContext;

    /**
     * Creates new instance of {@code DefaultParserBuilder}.
     */
    public DefaultParserBuilder() {
        try {
            jaxbContext = JAXBContext.newInstance(ConfigFileBuilder.class);
        } catch (JAXBException e) {
            throw new InternalParsingException("Cannot instantiate JAXBContext", e);
        }
    }

    public Document readConfigDOM(Reader input) throws IOException, ParsingException {
        XmlResultBuilder builder = new XmlResultBuilder();
        EBNF_PARSER.parse(input, builder);
        return builder.getResult();
    }

    public ConfigFileBuilder createConfigFileBuilder(Document doc) throws ParsingException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Source source = new DOMSource(doc);
            return (ConfigFileBuilder) unmarshaller.unmarshal(source);
        } catch (JAXBException e) {
            throw new ParsingException(e);
        }
    }

    public Parser createParser(ConfigFileBuilder builder) {
        return new Parser(builder.buildRoot());
    }

    /**
     * Creates the parser reading EBNF from a reader.
     *
     * @param input reader with EBNF data. Is used as instructions to build new parser.
     * @return parser built according to provided EBNF.
     * @throws IOException      if something wrong reading the input.
     * @throws ParsingException if input cannot be parsed.
     */
    public Parser createParser(Reader input) throws IOException, ParsingException {
        try {
            XmlResultBuilder builder = new XmlResultBuilder();
            EBNF_PARSER.parse(input, builder);
            Document doc = builder.getResult();
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            Source source = new DOMSource(doc);
            ConfigFileBuilder configFileBuilder = (ConfigFileBuilder) unmarshaller.unmarshal(source);
            return new Parser(configFileBuilder.buildRoot());
        } catch (JAXBException e) {
            throw new ParsingException(e);
        }
    }
}
