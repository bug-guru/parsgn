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

import net.developithecus.parser.*;
import net.developithecus.parser.exceptions.ParsingException;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */

public class ReferenceExpression extends Expression {
    private Rule reference;
    private String transform;

    public ReferenceExpression(Rule reference) {
        this.reference = reference;
    }

    public ReferenceExpression() {
    }

    public Rule getReference() {
        return reference;
    }

    public void setReference(Rule reference) {
        this.reference = reference;
    }

    @Override
    public boolean isHidden() {
        return super.isHidden() || reference.isHidden();
    }

    public String getTransform() {
        return transform;
    }

    public void setTransform(String transform) {
        this.transform = transform;
    }

    public ReferenceExpression transform(String transform) {
        setTransform(transform);
        return this;
    }

    @Override
    public ExpressionChecker checker() {
        if (reference.isTemplate()) {
            return new TemplateChecker();
        } else {
            return new GroupingChecker();
        }
    }

    class GroupingChecker extends GroupingExpressionChecker {

        @Override
        public Expression next() {
            return getReference().getExpression();
        }

        @Override
        public String getGroupName() {
            if (transform != null) {
                return transform;
            } else if (reference.getTransform() != null) {
                return reference.getTransform();
            } else {
                return reference.getName();
            }
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            return ResultType.COMMIT;
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return ResultType.ROLLBACK_OPTIONAL;
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return ResultType.ROLLBACK;
        }
    }

    class TemplateChecker extends TransparentExpressionChecker {

        @Override
        public Expression next() {
            return getReference().getExpression();
        }

        @Override
        public ResultType checkChildCommit() throws ParsingException {
            return ResultType.COMMIT;
        }

        @Override
        public ResultType checkChildOptionalRollback() throws ParsingException {
            return ResultType.ROLLBACK_OPTIONAL;
        }

        @Override
        public ResultType checkChildRollback() throws ParsingException {
            return ResultType.ROLLBACK;
        }
    }
}
