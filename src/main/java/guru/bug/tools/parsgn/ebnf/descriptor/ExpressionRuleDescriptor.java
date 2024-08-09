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

import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ExpressionRuleDescriptor extends AbstractRuleDescriptor {
    private final NegativeFlagRuleDescriptor negativeFlag;
    private final AbstractExpressionTypeDescriptor expressionType;
    private final ExpressionSuffixRuleDescriptor suffix;

    private ExpressionRuleDescriptor(NegativeFlagRuleDescriptor negativeFlag, AbstractExpressionTypeDescriptor expressionType, ExpressionSuffixRuleDescriptor suffix, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.negativeFlag = negativeFlag;
        this.expressionType = expressionType;
        this.suffix = suffix;
    }

    public static ExpressionRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        NegativeFlagRuleDescriptor negativeFlag = null;
        AbstractExpressionTypeDescriptor expressionType = null;
        ExpressionSuffixRuleDescriptor suffix = null;
        for (var child : children) {
            if (child instanceof NegativeFlagRuleDescriptor nfd) {
                negativeFlag = nfd;
            }
            if (child instanceof AbstractExpressionTypeDescriptor etd) {
                expressionType = etd;
            }
            if (child instanceof ExpressionSuffixRuleDescriptor esd) {
                suffix = esd;
            }
        }
        if (expressionType == null) {
            throw new ExpectationFailedException("OneOf | Reference | CharType | String | Sequence", startPosition, endPosition);
        }
        return new ExpressionRuleDescriptor(negativeFlag, expressionType, suffix, startPosition, endPosition);
    }

    public Expression generate(RuleFactory rf) {
        var result = expressionType.generate(rf);
        if (suffix != null) {
            result = suffix.generate(rf, result);
        }
        if (negativeFlag != null) {
            result = negativeFlag.generate(rf, result);
        }
        return result;
    }

    @Override
    protected void print(StringBuilder sb) {
        if (negativeFlag != null) {
            negativeFlag.print(sb);
        }
        expressionType.print(sb);
        if (suffix != null) {
            suffix.print(sb);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpressionRuleDescriptor that)) return false;
        return Objects.equals(negativeFlag, that.negativeFlag) && Objects.equals(expressionType, that.expressionType) && Objects.equals(suffix, that.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(negativeFlag, expressionType, suffix);
    }
}
