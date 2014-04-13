/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.exceptions.DuplicateRuleNameException;
import guru.bug.tools.parsgn.exceptions.EmptyExpressionListException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.exceptions.UnresolvedRuleException;
import guru.bug.tools.parsgn.expr.*;

import java.util.*;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class RuleBuilder {
    private Map<String, Rule> ruleDefinitions = new HashMap<>();
    private Map<String, ReferenceExpression> unresolved = new HashMap<>();
    private Map<String, ReferenceExpression> resolved = new HashMap<>();

    private Expression wrap(Expression... expressions) throws ParsingException {
        if (expressions == null || expressions.length == 0) {
            throw new EmptyExpressionListException();
        }
        if (expressions.length > 1) {
            return sequence(expressions);
        } else {
            return expressions[0];
        }
    }

    private Expression wrap(List<Expression> expressions) throws ParsingException {
        if (expressions == null || expressions.isEmpty()) {
            throw new EmptyExpressionListException();
        }
        if (expressions.size() > 1) {
            return sequence(expressions);
        } else {
            return expressions.get(0);
        }
    }

    private Rule rule(String name, Expression expression) throws ParsingException {
        if (ruleDefinitions.containsKey(name)) {
            throw new DuplicateRuleNameException(name);
        }
        Rule result = new Rule(name);
        result.setExpression(expression);
        ruleDefinitions.put(name, result);
        return result;
    }

    public final Rule rule(String name, List<Expression> expressions) throws ParsingException {
        Expression expr = wrap(expressions);
        return rule(name, expr);
    }

    public final Rule rule(String name, Expression... expressions) throws ParsingException {
        Expression expr = wrap(expressions);
        return rule(name, expr);
    }

    public final OneOfExpression oneOf(Expression... expressions) {
        return new OneOfExpression()
                .expressions(expressions);
    }

    public final OneOfExpression oneOf(List<Expression> expressions) {
        return new OneOfExpression()
                .expressions(expressions);
    }

    public final CharacterExpression charType(CharType charType) {
        CharacterExpression result = new CharacterExpression();
        result.setCharType(charType);
        return result;
    }

    public final ReferenceExpression ref(String ruleName) {
        ReferenceExpression result = unresolved.get(ruleName);
        if (result == null) {
            result = new ReferenceExpression(ruleName);
            unresolved.put(ruleName, result);
        }
        return result;
    }

    public final QuantityExpression zeroOrOne(Expression... expressions) throws ParsingException {
        return repeat(0, 1, expressions);
    }

    public final QuantityExpression oneOrMore(Expression... expressions) throws ParsingException {
        return repeat(1, Integer.MAX_VALUE, expressions);
    }

    public final QuantityExpression zeroOrMore(Expression... expressions) throws ParsingException {
        return repeat(0, Integer.MAX_VALUE, expressions);
    }

    public final QuantityExpression exactlyNTimes(int n, Expression... expressions) throws ParsingException {
        return repeat(n, n, expressions);
    }

    public final QuantityExpression atLeastMinTimes(int min, Expression... expressions) throws ParsingException {
        return repeat(min, Integer.MAX_VALUE, expressions);
    }

    public final QuantityExpression atLeastMinButNotMoreThanMaxTimes(int min, int max, Expression... expressions) throws ParsingException {
        return repeat(min, max, expressions);
    }

    public final QuantityExpression repeat(int min, int max, Expression... expressions) throws ParsingException {
        return new QuantityExpression()
                .loop(wrap(expressions))
                .minOccurrences(min)
                .maxOccurrences(max);
    }

    public final UntilExpression repeatUntil(Expression until, Expression... expressions) throws ParsingException {
        return new UntilExpression()
                .loop(wrap(expressions))
                .condition(until);
    }

    public final SequentialExpression sequence(Expression... expressions) {
        return new SequentialExpression()
                .expressions(expressions);
    }

    public final SequentialExpression sequence(List<Expression> expressions) {
        SequentialExpression expr = new SequentialExpression();
        expr.setExpressions(expressions);
        return expr;
    }

    public final StringExpression str(String str) {
        StringExpression result = new StringExpression();
        result.setValue(str);
        return result;
    }

    public final ReferenceExpression build(String name) throws UnresolvedRuleException {
        resolveReferences();
        ReferenceExpression result = resolved.get(name);
        if (result == null) {
            Rule rule = ruleDefinitions.get(name);
            if (rule == null) {
                throw new UnresolvedRuleException(name);
            }
            result = new ReferenceExpression(name);
            result.setReference(rule);
            resolved.put(name, result);
        }
        return result;
    }

    private void resolveReferences() throws UnresolvedRuleException {
        Iterator<ReferenceExpression> iterator = unresolved.values().iterator();
        List<String> unresolvedNames = new ArrayList<>();
        while (iterator.hasNext()) {
            ReferenceExpression ref = iterator.next();
            String ruleName = ref.getRuleName();
            Rule rule = ruleDefinitions.get(ruleName);
            if (rule == null) {
                unresolvedNames.add(ruleName);
                continue;
            }
            ref.setReference(rule);
            resolved.put(ruleName, ref);
            iterator.remove();
        }
        if (!unresolvedNames.isEmpty()) {
            throw new UnresolvedRuleException(unresolvedNames);
        }
    }
}
