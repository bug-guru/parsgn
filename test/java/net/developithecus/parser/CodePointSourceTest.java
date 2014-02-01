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

import org.junit.Test;

import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * @author dima
 */
public class CodePointSourceTest {

    @Test
    public void testHappyPath() throws Exception {
        CodePointSource src = new CodePointSource(new StringReader("0123456789ABCDEFGH"));
        src.mark();
        assertEquals('0', src.getNext());
        src.mark();
        assertEquals('1', src.getNext());
        assertEquals('2', src.getNext());
        assertEquals('3', src.getNext());
        src.mark();
        assertEquals('4', src.getNext());
        assertEquals('5', src.getNext());
        src.mark();
        assertEquals('6', src.getNext());
        src.rewind();
        assertEquals('6', src.getNext());
        src.removeMark();
        assertEquals('7', src.getNext());
        assertEquals('8', src.getNext());
        src.rewind();
        assertEquals('1', src.getNext());
        assertEquals('2', src.getNext());
        assertEquals('3', src.getNext());
        assertEquals('4', src.getNext());
        assertEquals('5', src.getNext());
        assertEquals('6', src.getNext());
        src.mark();
        assertEquals('7', src.getNext());
        assertEquals('8', src.getNext());
        assertEquals('9', src.getNext());
        assertEquals('A', src.getNext());
        src.mark();
        assertEquals('B', src.getNext());
        src.removeMark();
        assertEquals('C', src.getNext());
        assertEquals('D', src.getNext());
        assertEquals('E', src.getNext());
        assertEquals('F', src.getNext());
        assertEquals('G', src.getNext());
        assertEquals('H', src.getNext());
        assertEquals(-1, src.getNext());
        src.rewind();
        assertEquals('7', src.getNext());
        assertEquals('8', src.getNext());
        assertEquals('9', src.getNext());
        assertEquals('A', src.getNext());
        assertEquals('B', src.getNext());
        assertEquals('C', src.getNext());
        assertEquals('D', src.getNext());
        assertEquals('E', src.getNext());
        assertEquals('F', src.getNext());
        assertEquals('G', src.getNext());
        src.mark();
        src.mark();
        src.mark();
        src.mark();
        assertEquals('H', src.getNext());
        assertEquals(-1, src.getNext());
        assertEquals(-1, src.getNext());
        assertEquals(-1, src.getNext());
        src.rewind();
        assertEquals('H', src.getNext());
        src.rewind();
        assertEquals('H', src.getNext());
        src.rewind();
        src.removeMark();
        assertEquals('H', src.getNext());
        assertEquals(-1, src.getNext());
        src.rewind();
        assertEquals('0', src.getNext());
    }

    @Test
    public void testMultiline() throws Exception {
        CodePointSource src = new CodePointSource(new StringReader("0123\n456\r7\r\n\r\r\n\n89AB\r\n\r\nCDEFGH"));
        assertEquals('0', src.getNext());
        assertEquals('1', src.getNext());
        assertEquals('2', src.getNext());
        assertEquals('3', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('4', src.getNext());
        assertEquals('5', src.getNext());
        assertEquals('6', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('7', src.getNext());
        assertEquals('\n', src.getNext());
        src.mark();
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('8', src.getNext());
        src.rewind();
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('8', src.getNext());
        assertEquals('9', src.getNext());
        assertEquals('A', src.getNext());
        src.mark();
        assertEquals('B', src.getNext());
        src.mark();
        assertEquals('\n', src.getNext());
        src.mark();
        assertEquals('\n', src.getNext());
        src.mark();
        src.rewind();
        assertEquals('C', src.getNext());
        src.rewind();
        assertEquals('\n', src.getNext());
        assertEquals('C', src.getNext());
        assertEquals('D', src.getNext());
        src.rewind();
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('C', src.getNext());
        assertEquals('D', src.getNext());
        assertEquals('E', src.getNext());
        assertEquals('F', src.getNext());
        src.rewind();
        assertEquals('B', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('\n', src.getNext());
        assertEquals('C', src.getNext());
        assertEquals('D', src.getNext());
        assertEquals('E', src.getNext());
        assertEquals('F', src.getNext());
        assertEquals('G', src.getNext());
        assertEquals('H', src.getNext());
        assertEquals(-1, src.getNext());
    }

}
