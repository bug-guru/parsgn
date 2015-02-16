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

package guru.bug.tools.parsgn.model;

import guru.bug.tools.parsgn.RuleBuilder;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.model.expr.*;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import static guru.bug.tools.parsgn.model.RuleNames.*;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ExpressionParentModel {
    @XmlElements({
            @XmlElement(name = ONE_OF, type = OneOfExpressionModel.class),
            @XmlElement(name = REFERENCE, type = ReferenceExpressionModel.class),
            @XmlElement(name = CHAR_TYPE, type = CharTypeExpressionModel.class),
            @XmlElement(name = STRING, type = StringExpressionModel.class),
            @XmlElement(name = SEQUENCE, type = SequenceExpressionModel.class)
    })
    private ExpressionModel expression;
    @XmlElement(name = EXPRESSION_SUFFIX)
    private ExpressionSuffixParentModel suffix;


    public Expression generate(RuleBuilder builder) {
        Expression expr = expression.generate(builder);
        if (suffix == null) {
            return expr;
        }
        return suffix.generate(builder, expr);
    }
}
