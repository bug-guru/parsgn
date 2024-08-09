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

package guru.bug.tools.parsgn.ebnf.descriptor;

import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.exceptions.ExpectationFailedException;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.processing.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ExpressionListRuleDescriptor extends AbstractRuleDescriptor {
    private final List<ExpressionRuleDescriptor> expressions;

    private ExpressionListRuleDescriptor(List<ExpressionRuleDescriptor> expressions, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.expressions = expressions;
    }

    public static ExpressionListRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        var expressions = new ArrayList<ExpressionRuleDescriptor>(children.size());
        for (var child : children) {
            if (child instanceof ExpressionRuleDescriptor ed) {
                expressions.add(ed);
            } else {
                throw new ExpectationFailedException("Expression", startPosition, endPosition);
            }
        }
        return new ExpressionListRuleDescriptor(expressions, startPosition, endPosition);
    }

    public List<Expression> generate(RuleFactory rf) {
        return expressions.stream()
                .map(m -> m.generate(rf))
                .collect(Collectors.toList());
    }


    @Override
    protected void print(StringBuilder sb) {
        var ei = expressions.iterator();
        ei.next().print(sb);
        while (ei.hasNext()) {
            sb.append(" ");
            ei.next().print(sb);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpressionListRuleDescriptor that)) return false;
        return Objects.equals(expressions, that.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(expressions);
    }
}
