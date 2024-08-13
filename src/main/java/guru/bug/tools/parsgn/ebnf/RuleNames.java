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

package guru.bug.tools.parsgn.ebnf;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public interface RuleNames {
    String CONFIG_FILE = "ConfigFile";
    String RULE = "Rule";
    String FLAG = "Flag";
    String HIDE_FLAG = "HideFlag";
    String COMPRESS_FLAG = "CompressFlag";
    String RULE_PARAMS = "RuleParams";
    String I = "I";
    String SINGLE_LINE_COMMENT = "SingleLineComment";
    String EOL = "EOL";
    String LT = "LT";
    String WS = "WS";
    String MULTI_LINE_COMMENT = "MultiLineComment";
    String NAME = "Name";
    String EXPRESSION = "Expression";
    String NEGATIVE_FLAG = "NegativeFlag";
    String EXPRESSION_LIST = "ExpressionList";
    String EXPRESSION_SUFFIX = "ExpressionSuffix";
    String ZERO_OR_ONE = "ZeroOrOne";
    String ONE_OR_MORE = "OneOrMore";
    String ZERO_OR_MORE = "ZeroOrMore";
    String EXACTLY_N_TIMES = "ExactlyNTimes";
    String AT_LEAST_MIN_TIMES = "AtLeastMinTimes";
    String AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES = "AtLeastNButNotMoreThanMTimes";
    String ONE_OF = "OneOf";
    String ONE_OF_EXPRESSION = "OneOfExpression";
    String REFERENCE = "Reference";
    String REFERENCE_PARAMS = "ReferenceParams";
    String CHAR_TYPE = "CharType";
    String STRING = "String";
    String SEQUENCE = "Sequence";
    String CALCULATION = "Calculation";
    String TERM = "Term";
    String OPERATOR = "Operator";
    String CONSTANT = "Constant";
    String INTEGER = "Integer";
    String ADDITION = "Addition";
    String SUBTRACTION = "Subtraction";
    String MULTIPLICATION = "Multiplication";
    String DIVISION = "Division";
    String STRING_CONSTANT = "StringConstant";
    String CASE_SENSITIVE_STRING_CONSTANT = "CaseSensitiveStringConstant";
    String CASE_INSENSITIVE_STRING_CONSTANT = "CaseInsensitiveStringConstant";
    String VARIABLE = "Variable";
}
