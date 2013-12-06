package net.developithecus.parser2;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private final Expression rootExpression;

    public Parser(Expression rootExpression) {
        this.rootExpression = rootExpression;
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
