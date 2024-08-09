/*
 * Copyright (c) 2024 Dimitrijs Fedotovs http://www.bug.guru
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
import guru.bug.tools.parsgn.expr.calc.Term;
import guru.bug.tools.parsgn.processing.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReferenceParamsRuleDescriptor extends AbstractRuleDescriptor {
    private final List<CalculationRuleDescriptor> calculations;

    protected ReferenceParamsRuleDescriptor(List<CalculationRuleDescriptor> calculations, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.calculations = calculations;
    }

    public static ReferenceParamsRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        var calculations = new ArrayList<CalculationRuleDescriptor>(children.size());
        for (var child : children) {
            if (child instanceof CalculationRuleDescriptor cd) {
                calculations.add(cd);
            } else {
                throw new ExpectationFailedException("Calculation", startPosition, endPosition);
            }
        }
        return new ReferenceParamsRuleDescriptor(calculations, startPosition, endPosition);
    }

    public List<Term> generate() {
        return calculations.stream()
                .map(CalculationRuleDescriptor::generate)
                .toList();
    }

    @Override
    protected void print(StringBuilder sb) {
        var ci = calculations.iterator();
        sb.append("(");
        ci.next().print(sb);
        while (ci.hasNext()) {
            sb.append(", ");
            ci.next().print(sb);
        }
        sb.append(")");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceParamsRuleDescriptor that)) return false;
        return Objects.equals(calculations, that.calculations);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(calculations);
    }
}
