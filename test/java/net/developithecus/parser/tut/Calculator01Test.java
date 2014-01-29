/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
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

package net.developithecus.parser.tut;

import net.developithecus.parser.DefaultParserBuilder;
import net.developithecus.parser.ParseTreeResultBuilder;
import net.developithecus.parser.ParseTreeSerialization;
import net.developithecus.parser.Parser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.27.1
 * @since 1.0
 */
public class Calculator01Test {
    protected static Parser parser;

    public static Parser createParser(String fileName) throws Exception {
        DefaultParserBuilder builder = new DefaultParserBuilder();
        try (
                InputStream ebnfInput = Calculator01Test.class.getResourceAsStream(fileName)
        ) {
            return builder.createParser(ebnfInput);
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        parser = createParser("calculator01.rules");
    }

    @Test
    public void test01() throws Exception {
        doTest("test01");
    }

    private void doTest(String name) throws Exception {
        String sampleFileName = "calculator01" + name + "_sample.txt";
        String expectedFileName = "calculator01" + name + "_tree.xml";
        try (InputStream sample = Calculator01Test.class.getResourceAsStream(sampleFileName);
             InputStream expected = Calculator01Test.class.getResourceAsStream(expectedFileName)) {
            ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
            parser.parse(sample, builder);
            StringWriter out = new StringWriter(2048);
            ParseTreeSerialization.serialize(builder.getRoot(), out);
            System.out.println(out.toString());
        }
    }

}
