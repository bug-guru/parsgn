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

import guru.bug.tools.parsgn.exceptions.ExpectationFailedException;
import guru.bug.tools.parsgn.exceptions.UnexpectedItemException;
import guru.bug.tools.parsgn.expr.calc.Operator;
import guru.bug.tools.parsgn.expr.calc.Term;
import guru.bug.tools.parsgn.processing.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CalculationRuleDescriptor extends AbstractTermDescriptor {
    private final List<TermRuleDescriptor> terms;
    private final List<OperatorRuleDescriptor> operators;

    protected CalculationRuleDescriptor(List<TermRuleDescriptor> terms, List<OperatorRuleDescriptor> operators, Position startPosition, Position endPosition) {
        super(startPosition, endPosition);
        this.terms = terms;
        this.operators = operators;
    }

    public static CalculationRuleDescriptor create(List<AbstractRuleDescriptor> children, Position startPosition, Position endPosition) {
        var odd = true;
        var terms = new ArrayList<TermRuleDescriptor>(children.size() / 2 + 1);
        var operators = new ArrayList<OperatorRuleDescriptor>(children.size() / 2);
        for (var child : children) {
            if (odd) {
                if (child instanceof TermRuleDescriptor d) {
                    terms.add(d);
                } else {
                    throw new UnexpectedItemException(String.valueOf(child), startPosition, endPosition);
                }
            } else {
                if (child instanceof OperatorRuleDescriptor descriptor) {
                    operators.add(descriptor);
                } else {
                    throw new UnexpectedItemException(String.valueOf(child), startPosition, endPosition);
                }
            }
            odd = !odd;
        }
        if (terms.size() != operators.size() + 1) {
            throw new ExpectationFailedException("Term [Operator Term]*", startPosition, endPosition);
        }
        return new CalculationRuleDescriptor(List.copyOf(terms), List.copyOf(operators), startPosition, endPosition);
    }

    @Override
    public Term generate() {
        List<Term> trms = terms.stream()
                .map(TermRuleDescriptor::generate)
                .toList();
        if (operators == null || operators.isEmpty()) {
            return trms.getFirst();
        } else {
            List<Operator> ops = operators.stream()
                    .map(OperatorRuleDescriptor::generate)
                    .toList();
            return updatePosTo(Term.expression(trms, ops));
        }
    }

    @Override
    protected void print(StringBuilder sb) {
        var ti = terms.iterator();
        var oi = operators.iterator();
        ti.next().print(sb);
        while (oi.hasNext()) {
            sb.append(' ');
            oi.next().print(sb);
            sb.append(' ');
            ti.next().print(sb);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CalculationRuleDescriptor that)) return false;
        return Objects.equals(terms, that.terms) && Objects.equals(operators, that.operators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(terms, operators);
    }
}
