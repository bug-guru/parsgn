package net.developithecus.parser;

import net.developithecus.parser.expr.ReferenceExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private final ReferenceExpression root;

    public Parser(Rule root) {
        this.root = new ReferenceExpression();
        this.root.setReference(root);
    }

    public Node parse(String input) throws ParsingException {
        List<Integer> log = new ArrayList<>(input.length() * 3 / 2);
        int length = input.length();
        ParsingContext ctx = new ParsingContext(root);
        for (int offset = 0; offset <= length; ) {
            int codePoint;
            if (offset == length) {
                codePoint = -1;
                offset++;
            } else {
                codePoint = input.codePointAt(offset);
                offset += Character.charCount(codePoint);
            }
            log.add(codePoint);
            do {
                codePoint = log.get(ctx.getNextIndex());
                ctx.next(codePoint);
                if (ctx.getResultTree() != null) {
                    return ctx.getResultTree();
                }
            } while (ctx.getNextIndex() < log.size());
        }
        throw new ParsingException("Parsing error");
    }

}
