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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.expr.calc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
class CalculationTerm extends Term {
    private final List<Term> terms;
    private final List<Operator> operators;

    public CalculationTerm(List<Term> terms, List<Operator> operators) {
        this.terms = terms;
        this.operators = operators;
    }

    public boolean hasVariables() {
        return terms.stream().filter(t -> !(t instanceof ConstantTerm)).findFirst().isPresent();
    }

    @Override
    public Object evaluate(CalculationContext ctx) {
        List<Term> lTerms = new LinkedList<>(terms);
        List<Operator> lOps = new LinkedList<>(operators);
        doMulAndDiv(ctx, lTerms.listIterator(), lOps.listIterator());
        Term last = doAddAndSub(ctx, lTerms.listIterator(), lOps.listIterator());
        return last.evaluate(ctx);
    }

    @Override
    public boolean isConstant() {
        return !hasVariables();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Iterator<Term> ti = terms.iterator();
        for (Operator operator : operators) {
            result.append(ti.next())
                    .append(' ')
                    .append(operator.opSign())
                    .append(' ');
        }
        result.append(ti.next());
        return result.toString();
    }

    private Term doAddAndSub(CalculationContext ctx, ListIterator<Term> terms, ListIterator<Operator> opts) {
        Term rightTerm = terms.next();
        while (opts.hasNext()) {
            Operator curOp = opts.next();
            terms.remove();
            Term leftTerm = terms.next();
            opts.remove();
            Object result;
            if (curOp == Operator.ADD) {
                result = (Integer) rightTerm.evaluate(ctx) + (Integer) leftTerm.evaluate(ctx);
            } else {
                result = (Integer) rightTerm.evaluate(ctx) - (Integer) leftTerm.evaluate(ctx);
            }
            rightTerm = Term.constant(result);
            terms.set(rightTerm);
        }
        return rightTerm;
    }

    private void doMulAndDiv(CalculationContext ctx, ListIterator<Term> terms, ListIterator<Operator> opts) {
        Term rightTerm = terms.next();
        while (opts.hasNext()) {
            Operator curOp = opts.next();
            if (curOp == Operator.MUL || curOp == Operator.DIV) {
                terms.remove();
                Term leftTerm = terms.next();
                opts.remove();
                Object result;
                if (curOp == Operator.MUL) {
                    result = (Integer) rightTerm.evaluate(ctx) * (Integer) leftTerm.evaluate(ctx);
                } else {
                    result = (Integer) rightTerm.evaluate(ctx) / (Integer) leftTerm.evaluate(ctx);
                }
                rightTerm = Term.constant(result);
                terms.set(rightTerm);
            } else {
                rightTerm = terms.next();
            }
        }
    }
}
