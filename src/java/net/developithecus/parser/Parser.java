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


    public Node parse(String input) {
        ParsingContext ctx = new ParsingContext();
        List<Integer> log = new ArrayList<>(input.length() * 3 / 2);
        final int length = input.length();
        ctx.reset(0, -1);
        ctx.setNextIndex(0);
        ExpressionChecker rootChecker = root.checker(ctx);
        int index = 0;
        for (int offset = 0; offset < length; ) {
            int codePoint = input.codePointAt(offset);
            offset += Character.charCount(codePoint);
            log.add(codePoint);
            do {
                codePoint = log.get(index);
                ctx.reset(index, codePoint);
                rootChecker.check();
                index = ctx.getNextIndex();
                switch (ctx.getResult()) {
                    case CONTINUE:
                        break;
                    case COMMIT:
                        return ctx.getSingleCommittedNode();
                    case ROLLBACK:
                        throw new IllegalStateException("Syntax error");
                    default:
                        throw new IllegalStateException("unknown result: " + ctx.getResult());
                }
            } while (index < log.size());
        }
        throw new IllegalStateException("Parsing error");
    }
}
