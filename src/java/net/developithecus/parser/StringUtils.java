package net.developithecus.parser;

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
}
