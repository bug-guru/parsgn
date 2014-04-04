/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
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

package guru.bug.tools.parsgn;

import org.junit.Test;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 */
public class EBNFTest {

    private Parser generateDefaultParser() throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = getClass().getResourceAsStream("config.rules");
                BufferedInputStream buf = new BufferedInputStream(ebnfInput);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            return builder.createParser(reader);
        }
    }

    private ParseNode parseToTree(Parser parser) throws Exception {
        try (
                InputStream input = getClass().getResourceAsStream("config.rules");
                BufferedInputStream buf = new BufferedInputStream(input);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
            parser.parse(reader, builder);
            return builder.getRoot();
        }
    }

    @Test
    public void compareGeneratedAndBuiltIn() throws Exception {
        Parser generated = generateDefaultParser();
        Parser builtIn = new EBNFParser();
        ParseNode generatedTree = parseToTree(generated);
        ParseNode builtInTree = parseToTree(builtIn);
        assertEquals(builtInTree, generatedTree);
    }


    //    @Test
    public void printXml() throws Exception {
        Parser parser = new EBNFParser();
        try (
                InputStream input = getClass().getResourceAsStream("config.rules");
                BufferedInputStream buf = new BufferedInputStream(input);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            XmlResultBuilder resultBuilder = new XmlResultBuilder();
            parser.parse(reader, resultBuilder);
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
