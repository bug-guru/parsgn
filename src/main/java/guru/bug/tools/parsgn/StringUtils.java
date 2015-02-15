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

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public final class StringUtils {
    public static int[] toCodePoints(String str) {
        int[] result = new int[str.length()];
        final int length = str.length();
        int codePoint;
        int idx = 0;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = str.codePointAt(offset);
            if (result.length <= idx) {
                result = Arrays.copyOf(result, idx * 2);
            }
            result[idx] = codePoint;
            idx++;
        }
        if (result.length > idx) {
            result = Arrays.copyOf(result, idx);
        }
        return result;
    }

    static void escape(Writer result, String value) throws IOException {
        int offset = 0;
        int len = value.length();
        while (offset < len) {
            int codePoint = value.codePointAt(offset);
            offset += Character.charCount(codePoint);
            switch (codePoint) {
                case '\\':
                    result.write("\\\\");
                    break;
                case '\t':
                    result.write("\\t");
                    break;
                case '\b':
                    result.write("\\b");
                    break;
                case '\n':
                    result.write("\\n");
                    break;
                case '\r':
                    result.write("\\r");
                    break;
                case '\f':
                    result.write("\\f");
                    break;
                case '\"':
                    result.write("\\\"");
                    break;
                default:
                    result.write((char) codePoint);
            }
        }
    }

    public static String codePointToString(int codePoint) {
        switch (codePoint) {
            case -1:
                return "#EOF";
            case '\\':
                return "\\\\";
            case '\t':
                return "\\t";
            case '\b':
                return "\\b";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\f':
                return "\\f";
            case '\"':
                return "\\\"";
            default:
                return new String(new char[]{(char) codePoint});
        }
    }

    public static String escape(String value) {
        int offset = 0;
        int len = value.length();
        StringBuilder result = new StringBuilder();
        while (offset < len) {
            int codePoint = value.codePointAt(offset);
            offset += Character.charCount(codePoint);
            switch (codePoint) {
                case '\\':
                    result.append("\\\\");
                    break;
                case '\t':
                    result.append("\\t");
                    break;
                case '\b':
                    result.append("\\b");
                    break;
                case '\n':
                    result.append("\\n");
                    break;
                case '\r':
                    result.append("\\r");
                    break;
                case '\f':
                    result.append("\\f");
                    break;
                case '\"':
                    result.append("\\\"");
                    break;
                default:
                    result.append((char) codePoint);
            }
        }
        return result.toString();
    }
}
