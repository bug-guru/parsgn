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

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public interface RuleNames {
    public static final String CONFIG_FILE = "ConfigFile";
    public static final String RULE = "Rule";
    public static final String HIDE_FLAG = "HideFlag";
    public static final String RULE_PARAMS = "RuleParams";
    public static final String I = "I";
    public static final String SINGLE_LINE_COMMENT = "SingleLineComment";
    public static final String MULTI_LINE_COMMENT = "MultiLineComment";
    public static final String NAME = "Name";
    public static final String EXPRESSION = "Expression";
    public static final String EXPRESSION_LIST = "ExpressionList";
    public static final String EXPRESSION_SUFFIX = "ExpressionSuffix";
    public static final String ZERO_OR_ONE = "ZeroOrOne";
    public static final String ONE_OR_MORE = "OneOrMore";
    public static final String ZERO_OR_MORE = "ZeroOrMore";
    public static final String UNTIL = "Until";
    public static final String EXACTLY_N_TIMES = "ExactlyNTimes";
    public static final String AT_LEAST_MIN_TIMES = "AtLeastMinTimes";
    public static final String AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES = "AtLeastNButNotMoreThanMTimes";
    public static final String ONE_OF = "OneOf";
    public static final String ONE_OF_EXPRESSION = "OneOfExpression";
    public static final String REFERENCE = "Reference";
    public static final String REFERENCE_PARAMS = "ReferenceParams";
    public static final String CHAR_TYPE = "CharType";
    public static final String STRING = "String";
    public static final String SEQUENCE = "Sequence";
    public static final String CALC_EXPRESSION = "CalcExpression";
    public static final String TERM = "Term";
    public static final String OPERATOR = "Operator";
    public static final String CONSTANT = "Constant";
    public static final String INTEGER = "Integer";
    public static final String ADDITION = "Addition";
    public static final String SUBTRACTION = "Subtraction";
    public static final String MULTIPLICATION = "Multiplication";
    public static final String DIVISION = "Division";
    public static final String STRING_CONSTANT = "StringConstant";
    public static final String VARIABLE = "Variable";
}
