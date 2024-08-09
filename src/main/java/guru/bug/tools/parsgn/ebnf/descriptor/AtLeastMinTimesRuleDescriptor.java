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
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.processing.Position;

import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class AtLeastMinTimesRuleDescriptor extends AbstractExpressionSuffixDescriptor {
    private final CalculationRuleDescriptor calculationDescriptor;

    protected AtLeastMinTimesRuleDescriptor(CalculationRuleDescriptor calculationDescriptor, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.calculationDescriptor = calculationDescriptor;
    }

    public static AtLeastMinTimesRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        if (children.size() != 1) {
            throw new ExpectationFailedException("Single Calculation", startPosition, endPosition);
        }
        if (children.getFirst() instanceof CalculationRuleDescriptor cd) {
            return new AtLeastMinTimesRuleDescriptor(cd, startPosition, endPosition);
        } else {
            throw new ExpectationFailedException("Calculation", startPosition, endPosition);
        }
    }

    @Override
    public Expression generate(RuleFactory rf, Expression expr) {
        return updatePosTo(rf.atLeastMinTimes(calculationDescriptor.generate(), expr));
    }

    @Override
    protected void print(StringBuilder sb) {
        sb.append("{");
        calculationDescriptor.print(sb);
        sb.append(",}");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtLeastMinTimesRuleDescriptor that)) return false;
        return Objects.equals(calculationDescriptor, that.calculationDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(calculationDescriptor);
    }
}
