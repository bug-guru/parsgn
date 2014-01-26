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

package net.developithecus.parser.expr;

import net.developithecus.parser.Expression;
import net.developithecus.parser.ExpressionChecker;
import net.developithecus.parser.ResultType;
import net.developithecus.parser.TransparentExpressionChecker;
import net.developithecus.parser.exceptions.ParsingException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class SequentialExpression extends Expression {
    private List<Expression> expressions;

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(Expression... expressions) {
        this.expressions = Arrays.asList(expressions);
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = new ArrayList<>(expressions);
    }

    public SequentialExpression expressions(Expression... expressions) {
        setExpressions(expressions);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        return new Checker();
    }

    class Checker extends TransparentExpressionChecker {
        private final Iterator<Expression> expressions = getExpressions().iterator();
        private Expression curExpr;
        private boolean hasCommitted;

        @Override
        public Expression next() {
            curExpr = expressions.next();
            return curExpr;
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            hasCommitted = true;
            return doCommitOrContinue();
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return doCommitOrContinue();
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return ResultType.ROLLBACK;
        }

        private ResultType doCommitOrContinue() throws ParsingException {
            if (expressions.hasNext()) {
                return ResultType.CONTINUE;
            } else if (!hasCommitted) {
                return ResultType.ROLLBACK_OPTIONAL;
            } else {
                return ResultType.COMMIT;
            }
        }
    }
}
