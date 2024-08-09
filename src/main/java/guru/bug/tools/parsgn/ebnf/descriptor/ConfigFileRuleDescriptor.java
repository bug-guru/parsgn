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
import guru.bug.tools.parsgn.exceptions.NumberOfParametersException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.ReferenceExpression;
import guru.bug.tools.parsgn.processing.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */

public class ConfigFileRuleDescriptor extends AbstractRuleDescriptor {
    private final List<RuleRuleDescriptor> ruleList;

    private ConfigFileRuleDescriptor(List<RuleRuleDescriptor> ruleList, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.ruleList = ruleList;
    }

    public static ConfigFileRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        var ruleList = new ArrayList<RuleRuleDescriptor>(children.size());
        for (var child : children) {
            if (child instanceof RuleRuleDescriptor rd) {
                ruleList.add(rd);
            } else {
                throw new ExpectationFailedException("Rule", startPosition, endPosition);
            }
        }
        return new ConfigFileRuleDescriptor(ruleList, startPosition, endPosition);
    }

    public ReferenceExpression buildRoot() throws ParsingException, NumberOfParametersException {
        RuleFactory builder = new RuleFactory();
        ruleList.forEach(m -> m.build(builder));
        String rootName = ruleList.getFirst().getName().getValue();
        return updatePosTo(builder.resolveAllFromRoot(rootName));
    }


    @Override
    protected void print(StringBuilder sb) {
        ruleList.forEach(m -> {
            m.print(sb);
            sb.append(System.lineSeparator());
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigFileRuleDescriptor that)) return false;
        return Objects.equals(ruleList, that.ruleList);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ruleList);
    }
}
