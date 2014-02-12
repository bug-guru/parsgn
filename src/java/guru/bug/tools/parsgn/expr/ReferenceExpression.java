/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
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

package guru.bug.tools.parsgn.expr;

import guru.bug.tools.parsgn.Rule;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 * @version 1.0.0
 * @since 1.0.0
 */
public class ReferenceExpression extends Expression {
    private final String ruleName;
    private Rule reference;

    public ReferenceExpression(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) {
        this.reference = reference;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends BranchExpressionChecker {

        @Override
        public Expression next() {
            return getReference().getExpression();
        }

        @Override
        public String getGroupName() {
            return reference.getName();
        }

        @Override
        public String toString() {
            return "ref:" + reference.getName();
        }
    }
}
