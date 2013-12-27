package net.developithecus.parser;

import net.developithecus.parser.expr.*;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class Parser {
    private static final Logger logger = Logger.getLogger(Parser.class.getName());
    private final ReferenceExpression root;
    private final Deque<ExpressionChecker> stack = new LinkedList<>();
    private final ParsingContext ctx = new ParsingContext();

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

    public RepeatGroupExpression repeat(Expression[] expressions, Expression endCondition) {
        RepeatGroupExpression result = new RepeatGroupExpression();
        result.addAll(expressions);
        result.setEndCondition(endCondition);
        return result;
    }

    public RepeatGroupExpression repeat(Expression... expressions) {
        return repeat(expressions, null);
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
        try {
            List<Integer> log = new ArrayList<>(input.length() * 3 / 2);
            int length = input.length();
            ctx.reset(0, -1);
            ctx.setNextIndex(0);
            int index = 0;
            pushExpression(root);
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
                    codePoint = log.get(index);
                    ctx.reset(index, codePoint);
                    finishPath();
                    do {
                        ExpressionChecker currentChecker = stack.peek();
                        currentChecker.check();
                        index = ctx.getNextIndex();
                        switch (ctx.getResult()) {
                            case CONTINUE:
                                break;
                            case COMMIT:
                                stack.pop();
                                if (stack.isEmpty()) {
                                    return ctx.getSingleCommittedNode();
                                }
                                break;
                            case ROLLBACK:
                                stack.pop();
                                if (stack.isEmpty()) {
                                    throw new ParsingException("Syntax error");
                                }
                                break;
                            default:
                                throw new ParsingException("unknown result: " + ctx.getResult());
                        }
                    } while (ctx.getResult() != ResultType.CONTINUE);
                } while (index < log.size());
            }
            throw new ParsingException("Parsing error");
        } catch (ParsingException e) {
            logger.log(Level.SEVERE, "Exception when processing " + stack, e);
            throw e;
        }
    }

    private void finishPath() {
        Expression nextExpr;
        logger.log(Level.FINER, "updating stack {0}", stack);
        while ((nextExpr = stack.peek().next()) != null) {
            pushExpression(nextExpr);
        }
        logger.log(Level.FINER, "Stack updated {0}", stack);
    }

    private void pushExpression(Expression nextExpr) {
        ExpressionChecker nextChecker = nextExpr.checker();
        nextChecker.init(ctx);
        stack.push(nextChecker);
    }
}
