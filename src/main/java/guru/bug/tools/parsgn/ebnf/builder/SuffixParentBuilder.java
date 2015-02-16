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

package guru.bug.tools.parsgn.ebnf.builder;

import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.ebnf.builder.suffixes.*;
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
public class SuffixParentBuilder {
    @XmlElements({
            @XmlElement(name = RuleNames.ZERO_OR_ONE, type = ZeroOrOneSuffixBuilder.class),
            @XmlElement(name = RuleNames.ONE_OR_MORE, type = OneOrMoreSuffixBuilder.class),
            @XmlElement(name = RuleNames.ZERO_OR_MORE, type = ZeroOrMoreSuffixBuilder.class),
            @XmlElement(name = RuleNames.EXACTLY_N_TIMES, type = ExactlyNTimesSuffixBuilder.class),
            @XmlElement(name = RuleNames.AT_LEAST_MIN_TIMES, type = AtLeastMinTimesSuffixBuilder.class),
            @XmlElement(name = RuleNames.AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES, type = AtLeastNButNotMoreThanMTimesSuffixBuilder.class),
            @XmlElement(name = RuleNames.UNTIL, type = UntilSuffixBuilder.class)
    })
    private SuffixBuilder suffix;

    public Expression generate(RuleFactory rb, Expression expr) {
        return suffix.build(rb, expr);
    }
}
