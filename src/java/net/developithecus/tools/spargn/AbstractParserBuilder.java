/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
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

package net.developithecus.tools.spargn;

import net.developithecus.tools.spargn.expr.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public abstract class AbstractParserBuilder {

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
        if (expressions.length > 0) {
            result.setExpression(wrap(expressions));
        }
        ruleDefinitions.put(name, result);
        return result;
    }

    protected final OneOfExpression oneOf(Expression... expressions) {
        return new OneOfExpression()
                .expressions(expressions);
    }

    protected final OneOfExpression oneOf(List<Expression> expressions) {
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

    protected final SequentialExpression sequence(List<Expression> expressions) {
        SequentialExpression expr = new SequentialExpression();
        expr.setExpressions(expressions);
        return expr;
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
