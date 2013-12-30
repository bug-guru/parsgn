package net.developithecus.parser;

import net.developithecus.parser.expr.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public abstract class ParserBuilder {

    private Map<String, RuleDef> ruleDefinitions = new HashMap<>();

    private Expression wrap(Expression... expressions) {
        if (expressions.length > 1) {
            return sequence(expressions);
        } else {
            return expressions[0];
        }
    }

    protected final RuleDef rule(String name, Expression... expressions) {
        RuleDef result = new RuleDef(name);
        result.setExpression(wrap(expressions));
        ruleDefinitions.put(name, result);
        return result;
    }

    protected final OneOfExpression oneOf(Expression... expressions) {
        return new OneOfExpression()
                .expressions(expressions);
    }

    protected final CharacterExpression charType(CharType charType) {
        CharacterExpression result = new CharacterExpression();
        result.setCharType(charType);
        return result;
    }

    protected final ReferenceExpression ref(String ruleName) {
        RuleRef ref = new RuleRef(ruleName, ruleDefinitions);
        return ref(ref);
    }

    protected final ReferenceExpression ref(Rule rule) {
        ReferenceExpression result = new ReferenceExpression();
        result.setReference(rule);
        return result;
    }

    protected final QuantityExpression zeroOrOne(Expression... expressions) {
        return repeat(0, 1, expressions);
    }

    protected final QuantityExpression oneOrMore(Expression... expressions) {
        return repeat(1, Integer.MAX_VALUE, expressions);
    }

    protected final QuantityExpression zeroOrMore(Expression... expressions) {
        return repeat(0, Integer.MAX_VALUE, expressions);
    }

    protected final QuantityExpression exactlyNTimes(int n, Expression... expressions) {
        return repeat(n, n, expressions);
    }

    protected final QuantityExpression atLeastMinTimes(int min, Expression... expressions) {
        return repeat(min, Integer.MAX_VALUE, expressions);
    }

    protected final QuantityExpression atLeastMinButNotMoreThanMaxTimes(int min, int max, Expression... expressions) {
        return repeat(min, max, expressions);
    }

    protected final QuantityExpression repeat(int min, int max, Expression... expressions) {
        return new QuantityExpression()
                .loop(wrap(expressions))
                .minOccurrences(min)
                .maxOccurrences(max);
    }

    protected final UntilExpression repeatUntil(Expression until, Expression... expressions) {
        return new UntilExpression()
                .loop(wrap(expressions))
                .condition(until);
    }

    protected final SequentialExpression sequence(Expression... expressions) {
        return new SequentialExpression()
                .expressions(expressions);
    }

    protected final StringExpression str(String str) {
        StringExpression result = new StringExpression();
        result.setValue(str);
        return result;
    }

    protected final Parser createParser(Rule root) {
        Parser result = new Parser(root);
        ruleDefinitions = new HashMap<>();
        return result;
    }

    protected final Parser createParser(String rootRuleName) {
        RuleRef ref = new RuleRef(rootRuleName, ruleDefinitions);
        return createParser(ref);
    }
}
