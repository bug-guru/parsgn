/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
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


import guru.bug.tools.parsgn.CalcExpressionContext;
import guru.bug.tools.parsgn.ResultType;
import guru.bug.tools.parsgn.exceptions.ParsingException;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public abstract class Expression {
    private boolean hidden;

    public abstract ExpressionChecker checker(CalcExpressionContext cCtx);

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Expression hide() {
        setHidden(true);
        return this;
    }

    public abstract class ExpressionChecker {

        public boolean isIgnored() {
            return false;
        }

        public boolean isHidden() {
            return Expression.this.isHidden();
        }
    }

    public abstract class LeafExpressionChecker extends ExpressionChecker {
        public abstract void commitResult(StringBuilder result);

        public abstract ResultType check(int codePoint) throws ParsingException;
    }

    public abstract class BranchExpressionChecker extends ExpressionChecker {
        public abstract Expression next();

        public String getGroupName() {
            return null;
        }

        public ResultType check(ResultType childResult) throws ParsingException {
            return childResult;
        }
    }
}
