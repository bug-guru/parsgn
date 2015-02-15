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

import guru.bug.tools.parsgn.annotations.RootRule;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
@RootRule("ExpressionSuffix.Quantifier")
public class ExpressionSuffixQuantifierModel extends ExpressionSuffixModel {
    private int min;
    private int max;

    public ExpressionSuffixQuantifierModel(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @RootRule("ZeroOrOne")
    public static ExpressionSuffixQuantifierModel createZeroOrOne() {
        return new ExpressionSuffixQuantifierModel(0, 1);
    }

    @RootRule("OneOrMore")
    public static ExpressionSuffixQuantifierModel createOneOrMore() {
        return new ExpressionSuffixQuantifierModel(1, Integer.MAX_VALUE);
    }

    @RootRule("ZeroOrMore")
    public static ExpressionSuffixQuantifierModel createZeroOrMore() {
        return new ExpressionSuffixQuantifierModel(0, Integer.MAX_VALUE);
    }

    @RootRule("ExactlyNTimes")
    public static ExpressionSuffixQuantifierModel createExactlyNTimes(@RootRule("Number") int num) {
        return new ExpressionSuffixQuantifierModel(num, num);
    }

    @RootRule("AtLeastMinTimes")
    public static ExpressionSuffixQuantifierModel createAtLeastNTimes(@RootRule("Number") int num) {
        return new ExpressionSuffixQuantifierModel(num, Integer.MAX_VALUE);
    }

    @RootRule("AtLeastMinButNorMoreThanMaxTimes")
    public static ExpressionSuffixQuantifierModel createAtLeastButNotMoreThanMaxTimes(
            @RootRule("Min.Number") int min,
            @RootRule("Max.Number") int max) {
        return new ExpressionSuffixQuantifierModel(min, max);
    }

}
