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

package guru.bug.tools.parsgn.calc;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public abstract class Term {
    public static Term variable(String name) {
        return new Variable(name);
    }

    public static Term constant(Object value) {
        return new Constant(value);
    }

    public static Term expression(List<Term> terms, List<Operator> operators) {
        CalcExpression expr = new CalcExpression(terms, operators);
        return expr.isConstant() ? constant(expr.evaluate(null)) : expr;
    }

    public abstract Object evaluate(CalcExpressionContext ctx);

    public abstract boolean isConstant();

    static class Variable extends Term {
        private String name;

        public Variable(String name) {
            this.name = name;
        }

        @Override
        public Object evaluate(CalcExpressionContext ctx) {
            return ctx.getValue(name);
        }

        @Override
        public boolean isConstant() {
            return false;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    static class Constant extends Term {

        private final Object value;

        public Constant(Object value) {
            this.value = value;
        }

        @Override
        public Object evaluate(CalcExpressionContext ctx) {
            return value;
        }

        @Override
        public boolean isConstant() {
            return true;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    static class CalcExpression extends Term {
        private final List<Term> terms;
        private final List<Operator> operators;

        public CalcExpression(List<Term> terms, List<Operator> operators) {
            this.terms = terms;
            this.operators = operators;
        }

        public boolean hasVariables() {
            return terms.stream().filter(t -> !(t instanceof Constant)).findFirst().isPresent();
        }

        @Override
        public Object evaluate(CalcExpressionContext ctx) {
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

        private Term doAddAndSub(CalcExpressionContext ctx, ListIterator<Term> terms, ListIterator<Operator> opts) {
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

        private void doMulAndDiv(CalcExpressionContext ctx, ListIterator<Term> terms, ListIterator<Operator> opts) {
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
}
