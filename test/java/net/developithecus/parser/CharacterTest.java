package net.developithecus.parser;

import net.developithecus.parser2.CharType;
import org.junit.Test;

/**
 *
 */
public class CharacterTest {

    @Test
    public void testIgnorable() {
        isIdentifierIgnorable(';');
        isIdentifierIgnorable(' ');
        isIdentifierIgnorable('*');
        isIdentifierIgnorable('\t');
        isIdentifierIgnorable('\n');
        isIdentifierIgnorable('b');
        isIdentifierIgnorable('\u0000');
        isIdentifierIgnorable('\u0001');
        isIdentifierIgnorable('\u0002');
        isIdentifierIgnorable('\u0003');
        isIdentifierIgnorable('\u0004');
        isIdentifierIgnorable('\u0004');
        isIdentifierIgnorable('\u003b');
        isIdentifierIgnorable('1');
        isIdentifierIgnorable('z');
        isIdentifierIgnorable('+');
        isIdentifierIgnorable('$');
        isIdentifierIgnorable('\u001E');
        isIdentifierIgnorable('\n');
        isIdentifierIgnorable('\r');
    }

    private void isIdentifierIgnorable(char c) {
        System.out.println("char [" + c + "] [" + Integer.toHexString(c) + "]");
        for (CharType t : CharType.values()) {
            System.out.println("\t" + t + " " + t.apply(c));
        }
    }

}
