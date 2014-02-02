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

package net.developithecus.parser;

import net.developithecus.parser.exceptions.SyntaxErrorException;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.2.2
 * @since 1.0
 */
public class ErrorProcessingTest {

    private Parser createParser(String fileName) throws Exception {
        try (
                InputStream input = getClass().getResourceAsStream(fileName);
                BufferedInputStream bufInput = new BufferedInputStream(input);
                InputStreamReader reader = new InputStreamReader(bufInput)) {
            DefaultParserBuilder builder = new DefaultParserBuilder();
            return builder.createParser(reader);
        }
    }

    @Test
    public void testEBNFError() throws Exception {
        try {
            createParser("ebnf_error01.rules");
        } catch (SyntaxErrorException ex) {
            Assert.assertEquals(5, ex.getPosition().getRow());
            Assert.assertEquals(5, ex.getPosition().getCol());
        }
    }
}
