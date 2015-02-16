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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.ebnf;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.XmlResultBuilder;
import guru.bug.tools.parsgn.ebnf.builder.ConfigFileBuilder;
import guru.bug.tools.parsgn.exceptions.InternalParsingException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class DefaultParserBuilder {
    private final Parser EBNF_PARSER = new EBNFParser();
    private final JAXBContext jaxbContext;

    public DefaultParserBuilder() {
        try {
            jaxbContext = JAXBContext.newInstance(ConfigFileBuilder.class);
        } catch (JAXBException e) {
            throw new InternalParsingException("Cannot instantiate JAXBContext", e);
        }
    }

    public Parser createParser(Reader input) throws IOException, ParsingException, ParserConfigurationException {
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
