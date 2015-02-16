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

package guru.bug.tools.parsgn.ebnf.builder.calc;

import guru.bug.tools.parsgn.expr.calc.Operator;
import guru.bug.tools.parsgn.expr.calc.Term;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;
import java.util.stream.Collectors;

import static guru.bug.tools.parsgn.ebnf.RuleNames.OPERATOR;
import static guru.bug.tools.parsgn.ebnf.RuleNames.TERM;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@XmlType
public class CalcExpressionBuilder extends TermBuilder {
    @XmlElement(name = TERM)
    private List<TermParentBuilder> termParentBuilders;
    @XmlElement(name = OPERATOR)
    private List<OperatorParentBuilder> operatorParentBuilders;

    public Term build() {
        List<Term> terms = termParentBuilders.stream().map(TermParentBuilder::build).collect(Collectors.toList());
        if (operatorParentBuilders == null) {
            return terms.get(0);
        } else {
            List<Operator> operators = operatorParentBuilders.stream().map(OperatorParentBuilder::build).collect(Collectors.toList());
            return Term.expression(terms, operators);
        }
    }
}
