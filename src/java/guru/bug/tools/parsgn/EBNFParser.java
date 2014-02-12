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

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.exceptions.UnresolvedRuleException;
import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.expr.ReferenceExpression;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 * @version 1.0.0
 * @since 1.0.0
 */
public class EBNFParser extends Parser {
    public static final String CONFIG_FILE = "ConfigFile";
    public static final String RULE = "Rule";
    public static final String I = "I";
    public static final String SINGLE_LINE_COMMENT = "SingleLineComment";
    public static final String MULTI_LINE_COMMENT = "MultiLineComment";
    public static final String NAME = "Name";
    public static final String EXPRESSION = "Expression";
    public static final String EXPRESSION_LIST = "ExpressionList";
    public static final String EXPRESSION_SUFFIX = "ExpressionSuffix";
    public static final String QUANTIFIER = "Quantifier";
    public static final String ZERO_OR_ONE = "ZeroOrOne";
    public static final String ONE_OR_MORE = "OneOrMore";
    public static final String ZERO_OR_MORE = "ZeroOrMore";
    public static final String UNTIL = "Until";
    public static final String EXACTLY_N_TIMES = "ExactlyNTimes";
    public static final String AT_LEAST_MIN_TIMES = "AtLeastMinTimes";
    public static final String AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES = "AtLeastNButNotMoreThanMTimes";
    public static final String NUMBER = "Number";
    public static final String MIN = "Min";
    public static final String MAX = "Max";
    public static final String ONE_OF = "OneOf";
    public static final String ONE_OF_VARIANT1 = "OneOfVariant1";
    public static final String ONE_OF_VARIANT2 = "OneOfVariant2";
    public static final String ONE_OF_EXPRESSION = "OneOfExpression";
    public static final String REFERENCE = "Reference";
    public static final String CHAR_TYPE = "CharType";
    public static final String STRING = "String";
    public static final String TRANSFORM = "Transform";
    public static final String SEQUENCE = "Sequence";
    private static final ReferenceExpression root;

    static {
        try {
            root = generateRules();
        } catch (UnresolvedRuleException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ReferenceExpression generateRules() throws UnresolvedRuleException {
        RuleBuilder rb = new RuleBuilder();
        rb.rule(CONFIG_FILE,
                rb.oneOrMore(
                        rb.ref(I),
                        rb.ref(RULE)
                ),
                rb.ref(I),
                rb.charType(CharType.EOF));
        rb.rule(RULE,
                rb.ref(NAME),
                rb.ref(I),
                rb.str(":"),
                rb.ref(EXPRESSION_LIST),
                rb.str(";")
        );
        rb.rule(I,
                rb.zeroOrMore(
                        rb.oneOf(
                                rb.charType(CharType.WHITESPACE),
                                rb.ref(SINGLE_LINE_COMMENT),
                                rb.ref(MULTI_LINE_COMMENT)
                        )
                )
        );
        rb.rule(SINGLE_LINE_COMMENT,
                rb.str("//"),
                rb.repeatUntil(
                        rb.charType(CharType.LINE_SEPARATOR),
                        rb.charType(CharType.VALID)
                )
        );
        rb.rule(MULTI_LINE_COMMENT,
                rb.str("/*"),
                rb.repeatUntil(
                        rb.str("*/"),
                        rb.charType(CharType.VALID)
                )
        );
        rb.rule(NAME,
                rb.sequence(
                        rb.charType(CharType.UNICODE_IDENTIFIER_START),
                        rb.zeroOrMore(
                                rb.charType(CharType.UNICODE_IDENTIFIER_PART)
                        )
                )
        );
        rb.rule(EXPRESSION_LIST,
                rb.oneOrMore(
                        rb.ref(I),
                        rb.ref(EXPRESSION)
                )
        );
        rb.rule(EXPRESSION,
                rb.oneOf(
                        rb.ref(ONE_OF),
                        rb.ref(REFERENCE),
                        rb.ref(CHAR_TYPE),
                        rb.ref(STRING),
                        rb.ref(SEQUENCE)
                ),
                rb.zeroOrOne(
                        rb.ref(EXPRESSION_SUFFIX)
                )
        );
        rb.rule(EXPRESSION_SUFFIX,
                rb.ref(I),
                rb.oneOf(
                        rb.ref(QUANTIFIER),
                        rb.ref(UNTIL)
                ),
                rb.ref(I)
        );
        rb.rule(QUANTIFIER,
                rb.oneOf(
                        rb.ref(ZERO_OR_ONE),
                        rb.ref(ZERO_OR_MORE),
                        rb.ref(ONE_OR_MORE),
                        rb.ref(EXACTLY_N_TIMES),
                        rb.ref(AT_LEAST_MIN_TIMES),
                        rb.ref(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES)
                )
        );
        rb.rule(ZERO_OR_ONE,
                rb.str("?")
        );
        rb.rule(ONE_OR_MORE,
                rb.str("+")
        );
        rb.rule(ZERO_OR_MORE,
                rb.str("*")
        );
        rb.rule(UNTIL,
                rb.str("{"),
                rb.ref(I),
                rb.ref(EXPRESSION),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(EXACTLY_N_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(NUMBER),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(AT_LEAST_MIN_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(NUMBER),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(MIN),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.ref(MAX),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(NUMBER,
                rb.oneOrMore(
                        rb.charType(CharType.DIGIT)
                )
        );
        rb.rule(MIN,
                rb.ref(NUMBER)
        );
        rb.rule(MAX,
                rb.ref(NUMBER)
        );
        rb.rule(ONE_OF,
                rb.oneOf(
                        rb.ref(ONE_OF_VARIANT1),
                        rb.ref(ONE_OF_VARIANT2)
                )
        );
        rb.rule(ONE_OF_VARIANT1,
                rb.str("("),
                rb.ref(I),
                rb.ref(ONE_OF_VARIANT2),
                rb.ref(I),
                rb.str(")")
        );
        rb.rule(ONE_OF_VARIANT2,
                rb.ref(ONE_OF_EXPRESSION),
                rb.oneOrMore(
                        rb.ref(I),
                        rb.str("|"),
                        rb.ref(I),
                        rb.ref(ONE_OF_EXPRESSION)
                )
        );
        rb.rule(ONE_OF_EXPRESSION,
                rb.oneOf(
                        rb.ref(REFERENCE),
                        rb.ref(CHAR_TYPE),
                        rb.ref(STRING),
                        rb.ref(SEQUENCE)
                ),
                rb.zeroOrOne(
                        rb.ref(EXPRESSION_SUFFIX)
                )
        );
        rb.rule(REFERENCE,
                rb.ref(NAME)
        );
        rb.rule(CHAR_TYPE,
                rb.str("#"),
                rb.ref(NAME)
        );
        rb.rule(STRING,
                rb.str("\""),
                rb.repeatUntil(
                        rb.str("\""),
                        rb.oneOf(
                                rb.str("\\\"").transform("\""),
                                rb.str("\\\\").transform("\\"),
                                rb.charType(CharType.VALID)
                        )
                ),
                rb.zeroOrOne(
                        rb.ref(TRANSFORM)
                )
        );
        rb.rule(TRANSFORM,
                rb.ref(I),
                rb.str("->"),
                rb.ref(I),
                rb.ref(STRING)
        );
        rb.rule(SEQUENCE,
                rb.str("("),
                rb.ref(EXPRESSION_LIST),
                rb.str(")")
        );
        return rb.build(CONFIG_FILE);
    }

    public EBNFParser() {
        super(root);
    }
}
