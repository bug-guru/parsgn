/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.ebnf.builder.expr;

import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.ebnf.RuleNames;
import guru.bug.tools.parsgn.ebnf.builder.ExpressionParentBuilder;
import guru.bug.tools.parsgn.expr.Expression;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@XmlType
public class OneOfExpressionBuilder extends ExpressionBuilder {
    @XmlElement(name = RuleNames.ONE_OF_EXPRESSION)
    private List<ExpressionParentBuilder> expressionParentBuilders;

    @Override
    public Expression build(RuleFactory rf) {
        List<Expression> expressions = expressionParentBuilders.stream()
                .map(b -> b.build(rf))
                .collect(Collectors.toList());
        return update(rf.oneOf(expressions));
    }
}
