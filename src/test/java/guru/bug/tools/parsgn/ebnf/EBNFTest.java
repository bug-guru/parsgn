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
import guru.bug.tools.parsgn.ebnf.descriptor.ConfigFileRuleDescriptor;
import guru.bug.tools.parsgn.utils.EBNFResultBuilder;
import guru.bug.tools.parsgn.utils.ParseNode;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;
import guru.bug.tools.parsgn.utils.ParseTreeUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class EBNFTest {

    private Parser generateDefaultParser() throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = requireNonNull(getClass().getResourceAsStream("config.rules"));
                BufferedInputStream buf = new BufferedInputStream(ebnfInput);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            return builder.createParser(reader);
        }
    }

    private ParseNode parseToTree(Parser parser, String resName) throws Exception {
        try (
                InputStream input = requireNonNull(getClass().getResourceAsStream(resName));
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
        doTest(generated, builtIn, "config.rules");
        doTest(generated, builtIn, "tree_structure.rules");
    }

    private void doTest(Parser generated, Parser builtIn, String resName) throws Exception {
        ParseNode generatedTree = parseToTree(generated, resName);
        ParseNode builtInTree = parseToTree(builtIn, resName);
        assertEquals(builtInTree, generatedTree);
    }


    @Test
    public void ebnfRecursionParsingTest() throws Exception {
        Parser parser = new EBNFParser();
        try (
                InputStream input = requireNonNull(getClass().getResourceAsStream("config.rules"));
                BufferedInputStream buf = new BufferedInputStream(input);
                InputStreamReader reader1 = new InputStreamReader(buf)
        ) {
            var resultBuilder1 = new EBNFResultBuilder();
            parser.parse(reader1, resultBuilder1);
            ConfigFileRuleDescriptor root1 = resultBuilder1.getRoot();
            String strRoot1 = root1.toString();
            System.out.println(strRoot1);
            var reader2 = new StringReader(strRoot1);
            var resultBuilder2 = new EBNFResultBuilder();
            parser.parse(reader2, resultBuilder2);
            ConfigFileRuleDescriptor root2 = resultBuilder2.getRoot();
            assertEquals(root1, root2);
        }
    }

    @Ignore
    @Test
    public void printParseTree() throws Exception {
        Parser parser = new EBNFParser();
        try (
                InputStream input = requireNonNull(getClass().getResourceAsStream("config.rules"));
                BufferedInputStream buf = new BufferedInputStream(input);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            ParseTreeResultBuilder resultBuilder = new ParseTreeResultBuilder();
            parser.parse(reader, resultBuilder);
            StringWriter writer = new StringWriter(2048);
            ParseTreeUtils.serialize(resultBuilder.getRoot(), writer);
            System.out.println(writer);
        }
    }
}
