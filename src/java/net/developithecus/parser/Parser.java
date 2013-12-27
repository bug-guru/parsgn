package net.developithecus.parser;

import net.developithecus.parser.expr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private final ReferenceExpression root;

    public Parser() {
        this.root = new ReferenceExpression();
    }

    public void root(Rule rule) {
        root.setReference(rule);
    }

    public Rule rule(String name, Expression... expressions) {
        Rule result = new Rule();
        result.setName(name);
        result.addAll(expressions);
        return result;
    }

    public AltGroupExpression alt(Expression... expressions) {
        AltGroupExpression result = new AltGroupExpression();
        result.addAll(expressions);
        return result;
    }

    public CharacterExpression charType(CharType charType) {
        CharacterExpression result = new CharacterExpression();
        result.setCharType(charType);
        return result;
    }

    public OptionalGroupExpression opt(Expression... expressions) {
        OptionalGroupExpression result = new OptionalGroupExpression();
        result.addAll(expressions);
        return result;
    }

    public ReferenceExpression ref(Rule rule) {
        ReferenceExpression result = new ReferenceExpression();
        result.setReference(rule);
        return result;
    }

    public RepeatGroupExpression repeat(Expression... expressions) {
        RepeatGroupExpression result = new RepeatGroupExpression();
        result.addAll(expressions);
        return result;
    }

    public SequentialGroupExpression group(Expression... expressions) {
        SequentialGroupExpression result = new SequentialGroupExpression();
        result.addAll(expressions);
        return result;
    }

    public StringExpression str(String str) {
        StringExpression result = new StringExpression();
        result.setValue(str);
        return result;
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
