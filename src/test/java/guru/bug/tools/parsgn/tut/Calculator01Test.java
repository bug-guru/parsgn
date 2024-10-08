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

package guru.bug.tools.parsgn.tut;

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;
import guru.bug.tools.parsgn.utils.ParseTreeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static java.util.Objects.requireNonNull;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class Calculator01Test {
    protected static Parser parser;

    public static Parser createParser(String fileName) throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = Calculator01Test.class.getResourceAsStream(fileName);
                BufferedInputStream buf = new BufferedInputStream(ebnfInput);
                InputStreamReader reader = new InputStreamReader(buf)
        ) {
            return builder.createParser(reader);
        }
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        parser = createParser("calculator01.rules");
    }

    @Test
    public void test01() throws Exception {
        doTest("test01");
    }

    private void doTest(String name) throws Exception {
        // TODO need to complete this code
        String sampleFileName = "calculator01" + name + "_sample.txt";
        String expectedFileName = "calculator01" + name + "_tree.xml";
        try (InputStream sample = requireNonNull(Calculator01Test.class.getResourceAsStream(sampleFileName));
             BufferedInputStream bufSample = new BufferedInputStream(sample);
             InputStreamReader bufReader = new InputStreamReader(bufSample);
             InputStream expected = Calculator01Test.class.getResourceAsStream(expectedFileName)) {
            ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
            parser.parse(bufReader, builder);
            StringWriter out = new StringWriter(2048);
            ParseTreeUtils.serialize(builder.getRoot(), out);
            System.out.println(out);
        }
    }

}
