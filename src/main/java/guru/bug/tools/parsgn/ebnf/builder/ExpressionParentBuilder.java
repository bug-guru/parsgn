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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.ebnf.builder;

import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.ebnf.RuleNames;
import guru.bug.tools.parsgn.ebnf.builder.expr.*;
import guru.bug.tools.parsgn.expr.Expression;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@XmlType
public class ExpressionParentBuilder {
    @XmlElements({
            @XmlElement(name = RuleNames.ONE_OF, type = OneOfExpressionBuilder.class),
            @XmlElement(name = RuleNames.REFERENCE, type = ReferenceExpressionBuilder.class),
            @XmlElement(name = RuleNames.CHAR_TYPE, type = CharTypeExpressionBuilder.class),
            @XmlElement(name = RuleNames.STRING, type = StringExpressionBuilder.class),
            @XmlElement(name = RuleNames.SEQUENCE, type = SequenceExpressionBuilder.class)
    })
    private ExpressionBuilder expression;
    @XmlElement(name = RuleNames.EXPRESSION_SUFFIX)
    private SuffixParentBuilder suffix;


    public Expression build(RuleFactory builder) {
        Expression expr = expression.build(builder);
        if (suffix == null) {
            return expr;
        }
        return suffix.generate(builder, expr);
    }
}
