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
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.expr.Rule;
import guru.bug.tools.parsgn.processing.Position;

import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class RuleRuleDescriptor extends AbstractRuleDescriptor {
    private final NameRuleDescriptor name;
    private final HideFlagRuleDescriptor hideFlag;
    private final CompressFlagRuleDescriptor compressedFlag;
    private final RuleParamsRuleDescriptor ruleParams;
    private final ExpressionListRuleDescriptor expressionList;

    private RuleRuleDescriptor(NameRuleDescriptor name, HideFlagRuleDescriptor hideFlag, CompressFlagRuleDescriptor compressed, RuleParamsRuleDescriptor ruleParams, ExpressionListRuleDescriptor expressionList, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.name = name;
        this.hideFlag = hideFlag;
        this.compressedFlag = compressed;
        this.ruleParams = ruleParams;
        this.expressionList = expressionList;
    }

    public static RuleRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        NameRuleDescriptor name = null;
        HideFlagRuleDescriptor hideFlag = null;
        CompressFlagRuleDescriptor compressedFlag = null;
        RuleParamsRuleDescriptor ruleParams = null;
        ExpressionListRuleDescriptor expressionList = null;
        for (var child : children) {
            if (child instanceof NameRuleDescriptor nrb) {
                name = nrb;
            }
            if (child instanceof HideFlagRuleDescriptor hfd) {
                hideFlag = hfd;
            }
            if (child instanceof CompressFlagRuleDescriptor fcd) {
                compressedFlag = fcd;
            }
            if (child instanceof ExpressionListRuleDescriptor elb) {
                expressionList = elb;
            }
            if (child instanceof RuleParamsRuleDescriptor pb) {
                ruleParams = pb;
            }
        }
        if (name == null) {
            throw new ExpectationFailedException("Name", startPosition, endPosition);
        }
        if (expressionList == null) {
            throw new ExpectationFailedException("ExpressionList", startPosition, endPosition);
        }
        return new RuleRuleDescriptor(name, hideFlag, compressedFlag, ruleParams, expressionList, startPosition, endPosition);
    }

    public NameRuleDescriptor getName() {
        return name;
    }

    public void build(RuleFactory rf) throws ParsingException {
        String n = name.getValue();
        List<Expression> exprList = expressionList.generate(rf);
        Rule rule = rf.rule(n, exprList)
                .hidden(hideFlag != null)
                .compressed(compressedFlag != null)
                .params(ruleParams == null ? null : ruleParams.generate());
        updatePosTo(rule);
    }

    @Override
    protected void print(StringBuilder sb) {
        if (hideFlag != null) {
            hideFlag.print(sb);
        } else if (compressedFlag != null) {
            compressedFlag.print(sb);
        }
        name.print(sb);
        if (ruleParams != null) {
            ruleParams.print(sb);
        }
        sb.append(": ");
        expressionList.print(sb);
        sb.append(";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RuleRuleDescriptor that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(hideFlag, that.hideFlag) && Objects.equals(compressedFlag, that.compressedFlag) && Objects.equals(ruleParams, that.ruleParams) && Objects.equals(expressionList, that.expressionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, hideFlag, compressedFlag, ruleParams, expressionList);
    }
}
