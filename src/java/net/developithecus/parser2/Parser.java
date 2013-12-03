package net.developithecus.parser2;

/**
 *
 */
public class Parser {
    private final Rule rootRule;

    public Parser(Rule rootRule) {
        this.rootRule = rootRule;
        init();
    }

    private void init() {

    }


    public void parse(String input) {
        final int length = input.length();
        int codePoint;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = input.codePointAt(offset);

        }
    }
}
