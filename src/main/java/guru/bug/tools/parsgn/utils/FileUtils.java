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

package guru.bug.tools.parsgn.utils;

import guru.bug.tools.parsgn.ebnf.EBNFParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;

public class FileUtils {
    public static String readFileContent(String fileName) throws IOException {
        try (
                InputStream inputStream = new FileInputStream(new File(fileName))
        ) {
            return readContent(inputStream);
        }
    }

    public static String readContent(InputStream inputStream) throws IOException {
        try (
                Reader rulesReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            return readContent(rulesReader);
        }
    }

    public static String readContent(Reader reader) throws IOException {
        StringBuilder text = new StringBuilder(2048);
        CharBuffer buf = CharBuffer.allocate(512);
        while (reader.read(buf) != -1) {
            buf.flip();
            text.append(buf.array(), 0, buf.limit());
            buf.clear();
        }
        buf.flip();
        text.append(buf.array(), 0, buf.limit());
        return text.toString();
    }

}
