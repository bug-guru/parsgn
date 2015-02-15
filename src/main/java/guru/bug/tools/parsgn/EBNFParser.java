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

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.expr.ReferenceExpression;
import guru.bug.tools.parsgn.model.RuleNames;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class EBNFParser extends Parser implements RuleNames {
    private static final ReferenceExpression root;

    static {
        try {
            root = generateRules();
        } catch (ParsingException e) {
            throw new IllegalStateException(e);
        }
    }

    public EBNFParser() {
        super(root);
    }

    private static ReferenceExpression generateRules() throws ParsingException {
        RuleBuilder rb = new RuleBuilder();
        rb.rule(CONFIG_FILE,
                rb.oneOrMore(
                        rb.ref(I),
                        rb.ref(RULE)
                ),
                rb.ref(I),
                rb.charType(CharType.EOF)
        );
        rb.rule(RULE,
                rb.zeroOrOne(rb.ref(HIDE_FLAG)),
                rb.ref(NAME),
                rb.zeroOrOne(rb.ref(RULE_PARAMS)),
                rb.ref(I),
                rb.str(":"),
                rb.ref(EXPRESSION_LIST),
                rb.str(";")
        );
        rb.rule(HIDE_FLAG,
                rb.str(".")
        );
        rb.rule(RULE_PARAMS,
                rb.str("("),
                rb.ref(I),
                rb.ref(NAME),
                rb.ref(I),
                rb.str(")")
        );
        rb.rule(I,
                rb.zeroOrMore(
                        rb.oneOf(
                                rb.charType(CharType.WHITESPACE),
                                rb.ref(SINGLE_LINE_COMMENT),
                                rb.ref(MULTI_LINE_COMMENT)
                        )
                )
        ).hide();
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
                ),
                rb.str("*/")
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
                rb.str(">"),
                rb.ref(I),
                rb.ref(EXPRESSION),
                rb.ref(I)
        );
        rb.rule(EXACTLY_N_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALC_EXPRESSION),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(AT_LEAST_MIN_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALC_EXPRESSION),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALC_EXPRESSION),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.ref(CALC_EXPRESSION),
                rb.ref(I),
                rb.str("}")
        );
        rb.rule(ONE_OF,
                rb.oneOf(
                        rb.ref(ONE_OF_VARIANT1),
                        rb.ref(ONE_OF_VARIANT2)
                )
        );
        rb.rule(ONE_OF_VARIANT1,
                rb.str("["),
                rb.ref(I),
                rb.ref(ONE_OF_VARIANT2),
                rb.ref(I),
                rb.str("]")
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
                rb.ref(NAME),
                rb.zeroOrOne(rb.ref(REFERENCE_PARAMS))
        );
        rb.rule(REFERENCE_PARAMS,
                rb.str("("),
                rb.ref(I),
                rb.ref(CALC_EXPRESSION),
                rb.ref(I),
                rb.str(")")
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
                                rb.str("\\n").transform("\n"),
                                rb.str("\\r").transform("\r"),
                                rb.str("\\t").transform("\t"),
                                rb.str("\\f").transform("\f"),
                                rb.str("\\b").transform("\b"),
                                rb.charType(CharType.VALID)
                        )
                ),
                rb.str("\""),
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
                rb.str("["),
                rb.ref(EXPRESSION_LIST),
                rb.ref(I),
                rb.str("]")
        );

        // CALCULATION EXPRESSION

        // CalcExpression: I Term I Operator I Term [I Operator I Term]* I;
        rb.rule(CALC_EXPRESSION,
                rb.ref(I),
                rb.ref(TERM),
                rb.zeroOrMore(
                        rb.ref(I),
                        rb.ref(OPERATOR),
                        rb.ref(I),
                        rb.ref(TERM)),
                rb.ref(I)
        );
        // Term: ["(" CalcExpression ")"] | Name | Constant;
        rb.rule(TERM,
                rb.oneOf(
                        rb.sequence(
                                rb.str("("),
                                rb.ref(CALC_EXPRESSION),
                                rb.str(")")),
                        rb.ref(NAME),
                        rb.ref(CONSTANT)
                ));
        // Constant: Integer;
        rb.rule(CONSTANT,
                rb.ref(INTEGER)
        );
        // Integer: #DIGIT+;
        rb.rule(INTEGER,
                rb.oneOrMore(rb.charType(CharType.DIGIT))
        );
        // Operator: Addition | Subtraction | Multiplication | Division;
        rb.rule(OPERATOR,
                rb.oneOf(
                        rb.ref(ADDITION),
                        rb.ref(SUBTRACTION),
                        rb.ref(MULTIPLICATION),
                        rb.ref(DIVISION))
        );
        // Addition: "+";
        rb.rule(ADDITION, rb.str("+"));
        // Subtraction: "-";
        rb.rule(SUBTRACTION, rb.str("-"));
        // Multiplication: "*";
        rb.rule(MULTIPLICATION, rb.str("*"));
        // Division: "/";
        rb.rule(DIVISION, rb.str("/"));
        return rb.build(CONFIG_FILE);
    }
}
