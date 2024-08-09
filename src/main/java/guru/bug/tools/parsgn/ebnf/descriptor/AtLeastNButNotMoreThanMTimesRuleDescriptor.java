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
import guru.bug.tools.parsgn.expr.calc.Term;
import guru.bug.tools.parsgn.processing.Position;

import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class AtLeastNButNotMoreThanMTimesRuleDescriptor extends AbstractExpressionSuffixDescriptor {
    private final CalculationRuleDescriptor minCalculation;
    private final CalculationRuleDescriptor maxCalculation;

    protected AtLeastNButNotMoreThanMTimesRuleDescriptor(CalculationRuleDescriptor minCalculation, CalculationRuleDescriptor maxCalculation, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.minCalculation = minCalculation;
        this.maxCalculation = maxCalculation;
    }

    public static AtLeastNButNotMoreThanMTimesRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        if (children.size() != 2) {
            throw new ExpectationFailedException("Min and Max Calculations", startPosition, endPosition);
        }
        CalculationRuleDescriptor minCalculation = null;
        CalculationRuleDescriptor maxCalculation = null;
        if (children.getFirst() instanceof CalculationRuleDescriptor d) {
            minCalculation = d;
        }
        if (children.getLast() instanceof CalculationRuleDescriptor d) {
            maxCalculation = d;
        }
        return new AtLeastNButNotMoreThanMTimesRuleDescriptor(minCalculation, maxCalculation, startPosition, endPosition);
    }

    @Override
    public Expression generate(RuleFactory rf, Expression expr) {
        Term min = minCalculation.generate();
        Term max = maxCalculation.generate();
        return updatePosTo(rf.atLeastMinButNotMoreThanMaxTimes(min, max, expr));
    }

    @Override
    protected void print(StringBuilder sb) {
        sb.append("{");
        minCalculation.print(sb);
        sb.append(",");
        maxCalculation.print(sb);
        sb.append("}");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AtLeastNButNotMoreThanMTimesRuleDescriptor that)) return false;
        return Objects.equals(minCalculation, that.minCalculation) && Objects.equals(maxCalculation, that.maxCalculation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(minCalculation, maxCalculation);
    }
}
