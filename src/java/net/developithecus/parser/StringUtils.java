package net.developithecus.parser;

import java.io.StringWriter;
import java.util.Arrays;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.6.12
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

    static void escape(StringWriter result, String value) {
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

}
