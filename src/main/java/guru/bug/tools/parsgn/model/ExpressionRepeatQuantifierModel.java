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

import guru.bug.tools.parsgn.annotations.RuleValue;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@RuleValue("ExpressionSuffix.Quantifier")
public class ExpressionRepeatQuantifierModel extends ExpressionRepeatModel {
    private int min;
    private int max;

    public ExpressionRepeatQuantifierModel(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @RuleValue("ZeroOrOne")
    public static ExpressionRepeatQuantifierModel createZeroOrOne() {
        return new ExpressionRepeatQuantifierModel(0, 1);
    }

    @RuleValue("OneOrMore")
    public static ExpressionRepeatQuantifierModel createOneOrMore() {
        return new ExpressionRepeatQuantifierModel(1, Integer.MAX_VALUE);
    }

    @RuleValue("ZeroOrMore")
    public static ExpressionRepeatQuantifierModel createZeroOrMore() {
        return new ExpressionRepeatQuantifierModel(0, Integer.MAX_VALUE);
    }

    @RuleValue("ExactlyNTimes")
    public static ExpressionRepeatQuantifierModel createExactlyNTimes(@RuleValue("Number") int num) {
        return new ExpressionRepeatQuantifierModel(num, num);
    }

    @RuleValue("AtLeastMinTimes")
    public static ExpressionRepeatQuantifierModel createAtLeastNTimes(@RuleValue("Number") int num) {
        return new ExpressionRepeatQuantifierModel(num, Integer.MAX_VALUE);
    }

    @RuleValue("AtLeastMinButNorMoreThanMaxTimes")
    public static ExpressionRepeatQuantifierModel createAtLeastButNotMoreThanMaxTimes(
            @RuleValue("Min.Number") int min,
            @RuleValue("Max.Number") int max) {
        return new ExpressionRepeatQuantifierModel(min, max);
    }

}
