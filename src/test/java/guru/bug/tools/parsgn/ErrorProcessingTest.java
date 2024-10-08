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

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.ebnf.DefaultParserBuilder;
import guru.bug.tools.parsgn.ebnf.EBNFParser;
import guru.bug.tools.parsgn.exceptions.SyntaxErrorException;
import guru.bug.tools.parsgn.utils.ParseTreeResultBuilder;
import guru.bug.tools.parsgn.utils.ParseTreeUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 */
public class ErrorProcessingTest {

    @Test
    public void testEBNFError() throws Exception {
        try (
                InputStream input = requireNonNull(getClass().getResourceAsStream("ebnf_error01.rules"));
                BufferedInputStream bufInput = new BufferedInputStream(input);
                InputStreamReader reader = new InputStreamReader(bufInput)
        ) {
            DefaultParserBuilder builder = new DefaultParserBuilder();
            builder.createParser(reader);
            fail();
        } catch (SyntaxErrorException ex) {
            assertEquals(27, ex.getPosition().getRow());
            assertEquals(5, ex.getPosition().getCol());
        }
    }

    @Disabled
    @Test
    public void printParseTree() throws Exception {
        Parser parser = new EBNFParser();
        try (
                InputStream input = getClass().getResourceAsStream("ebnf_error01.rules");
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
