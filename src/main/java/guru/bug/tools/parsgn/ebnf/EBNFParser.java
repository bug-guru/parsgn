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

import guru.bug.tools.parsgn.Parser;
import guru.bug.tools.parsgn.RuleFactory;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.expr.ReferenceExpression;

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
        RuleFactory rb = new RuleFactory();
        // ConfigFile:
        //    [I Rule]+ I #EOF;
        rb.rule(CONFIG_FILE,
                rb.oneOrMore(
                        rb.ref(I),
                        rb.ref(RULE)
                ),
                rb.ref(I)
        );
        // Rule:
        //     HideFlag? I Name I RuleParams? I ":" ExpressionList I ";";
        rb.rule(RULE,
                rb.zeroOrOne(rb.ref(HIDE_FLAG)),
                rb.ref(I),
                rb.ref(NAME),
                rb.ref(I),
                rb.zeroOrOne(rb.ref(RULE_PARAMS)),
                rb.ref(I),
                rb.str(":"),
                rb.ref(EXPRESSION_LIST),
                rb.ref(I),
                rb.str(";")
        );
        // HideFlag: "." I;
        rb.rule(HIDE_FLAG,
                rb.str("."),
                rb.ref(I)
        );
        // RuleParams: "(" I Name [I "," I Name]* I ")";
        rb.rule(RULE_PARAMS,
                rb.str("("),
                rb.ref(I),
                rb.ref(NAME),
                rb.zeroOrMore(rb.sequence(
                        rb.ref(I),
                        rb.str(","),
                        rb.ref(I),
                        rb.ref(NAME)
                )),
                rb.ref(I),
                rb.str(")")
        );
        // I: [WS | LT | SingleLineComment | MultiLineComment]*;
        rb.rule(I,
                rb.zeroOrMore(
                        rb.oneOf(
                                rb.ref(WS),
                                rb.ref(LT),
                                rb.ref(SINGLE_LINE_COMMENT),
                                rb.ref(MULTI_LINE_COMMENT)
                        )
                )
        );
        // SingleLineComment: "//" [!EOL #VALID]* EOL;
        rb.rule(SINGLE_LINE_COMMENT,
                rb.str("//"),
                rb.zeroOrMore(
                        rb.not(rb.ref(EOL)),
                        rb.charType(CharType.VALID)
                ),
                rb.ref(EOL)
        );
        // .EOL: WS LT;
        rb.rule(EOL,
                rb.ref(WS),
                rb.ref(LT)
        ).hide();
        // .LT: ["\r\n" | "\r" | "\n"];
        rb.rule(LT,
                rb.oneOf(
                        rb.str("\r\n"),
                        rb.str("\r"),
                        rb.str("\n")
                )
        ).hide();
        // .WS: [" " | "\t"]*;
        rb.rule(WS,
                rb.zeroOrMore(rb.oneOf(
                        rb.str(" "),
                        rb.str("\t")
                ))
        ).hide();
        // MultiLineComment: "/*" [!"*/" #VALID] "*/";
        rb.rule(MULTI_LINE_COMMENT,
                rb.str("/*"),
                rb.zeroOrMore(
                        rb.not(rb.str("*/")),
                        rb.charType(CharType.VALID)
                ),
                rb.str("*/")
        );
        // Name: #UNICODE_IDENTIFIER_START #UNICODE_IDENTIFIER_PART*;
        rb.rule(NAME,
                rb.sequence(
                        rb.charType(CharType.UNICODE_IDENTIFIER_START),
                        rb.zeroOrMore(
                                rb.charType(CharType.UNICODE_IDENTIFIER_PART)))
        );
        // ExpressionList: [I Expression]+;
        rb.rule(EXPRESSION_LIST,
                rb.oneOrMore(
                        rb.ref(I),
                        rb.ref(EXPRESSION)
                )
        );
        // Expression:
        //     NegativeFlag?
        //     OneOf | Reference | CharType | String | Sequence
        //     ExpressionSuffix?;
        rb.rule(EXPRESSION,
                rb.zeroOrOne(rb.ref(NEGATIVE_FLAG)),
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
        // NegativeFlag: "!" I ;
        rb.rule(NEGATIVE_FLAG,
                rb.str("!"),
                rb.ref(I)
        );
        // ExpressionSuffix: I [ZeroOrOne
        //                    | OneOrMore
        //                    | ZeroOrMore
        //                    | ExactlyNTimes
        //                    | AtLeastMinTimes
        //                    | AtLeastMinButNotMoreThanMaxTimes
        //                    | LookAhead] I;
        rb.rule(EXPRESSION_SUFFIX,
                rb.ref(I),
                rb.oneOf(
                        rb.ref(ZERO_OR_ONE),
                        rb.ref(ONE_OR_MORE),
                        rb.ref(ZERO_OR_MORE),
                        rb.ref(EXACTLY_N_TIMES),
                        rb.ref(AT_LEAST_MIN_TIMES),
                        rb.ref(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES)
                ),
                rb.ref(I)
        );
        // ZeroOrOne: "?";
        rb.rule(ZERO_OR_ONE,
                rb.str("?")
        );
        // OneOrMore: "+";
        rb.rule(ONE_OR_MORE,
                rb.str("+")
        );
        // ZeroOrMore: "*";
        rb.rule(ZERO_OR_MORE,
                rb.str("*")
        );
        // ExactlyNTimes: "{" I CalcExpression I "}";
        rb.rule(EXACTLY_N_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALCULATION),
                rb.ref(I),
                rb.str("}")
        );
        // AtLeastMinTimes: "{" I CalcExpression I "," I "}";
        rb.rule(AT_LEAST_MIN_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALCULATION),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.str("}")
        );
        // AtLeastMinButNotMoreThanMaxTimes: "{" I CalcExpression I "," I CalcExpression I "}";
        rb.rule(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES,
                rb.str("{"),
                rb.ref(I),
                rb.ref(CALCULATION),
                rb.ref(I),
                rb.str(","),
                rb.ref(I),
                rb.ref(CALCULATION),
                rb.ref(I),
                rb.str("}")
        );
        // OneOf: ["[" I OneOfExpression [I "|" I OneOfExpression]+ I "]"]
        //      | [OneOfExpression [I "|" I OneOfExpression]+];
        rb.rule(ONE_OF,
                rb.oneOf(
                        rb.sequence(
                                rb.str("["),
                                rb.ref(I),
                                rb.ref(ONE_OF_EXPRESSION),
                                rb.oneOrMore(
                                        rb.ref(I),
                                        rb.str("|"),
                                        rb.ref(I),
                                        rb.ref(ONE_OF_EXPRESSION)
                                ),
                                rb.ref(I),
                                rb.str("]")
                        ),
                        rb.sequence(
                                rb.ref(ONE_OF_EXPRESSION),
                                rb.oneOrMore(
                                        rb.ref(I),
                                        rb.str("|"),
                                        rb.ref(I),
                                        rb.ref(ONE_OF_EXPRESSION)
                                )
                        )
                )
        );
        // OneOfExpression:
        //     Reference | CharType | String | Sequence
        //     ExpressionSuffix?;
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
        // Reference: Name ReferenceParams?;
        rb.rule(REFERENCE,
                rb.ref(NAME),
                rb.zeroOrOne(rb.ref(REFERENCE_PARAMS))
        );
        // ReferenceParams: "(" I CalcExpression [I "," I CalcExpression]* I ")";
        rb.rule(REFERENCE_PARAMS,
                rb.str("("),
                rb.ref(I),
                rb.ref(CALCULATION),
                rb.zeroOrMore(
                        rb.ref(I),
                        rb.str(","),
                        rb.ref(I),
                        rb.ref(CALCULATION)
                ),
                rb.ref(I),
                rb.str(")")
        );
        // CharType: "#" Name; //TODO: list all possible char types
        rb.rule(CHAR_TYPE,
                rb.str("#"),
                rb.ref(NAME)
        );
        // String: StringConstant [I "->" I StringConstant]?;
        rb.rule(STRING,
                rb.ref(STRING_CONSTANT),
                rb.zeroOrOne(
                        rb.ref(I),
                        rb.str("->"),
                        rb.ref(I),
                        rb.ref(STRING_CONSTANT)
                )
        );
        // StringConstant: "\"" [!"\""
        //                             [   "\\\"" -> "\""
        //                               | "\\\\" -> "\\"
        //                               | "\\n" -> "\n"
        //                               | "\\r" -> "\r"
        //                               | "\\t" -> "\t"
        //                               | "\\f" -> "\f"
        //                               | "\\b" -> "\b"
        //                               | #VALID
        //                             ]
        //                 ]* "\"";
        rb.rule(STRING_CONSTANT,
                rb.str("\""),
                rb.zeroOrMore(
                        rb.not(rb.str("\"")),
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
                rb.str("\"")
        );
        // Sequence: "[" ExpressionList I "]";
        rb.rule(SEQUENCE,
                rb.str("["),
                rb.ref(EXPRESSION_LIST),
                rb.ref(I),
                rb.str("]")
        );

        // CALCULATION EXPRESSION

        // CalcExpression: I Term [I Operator I Term]* I;
        rb.rule(CALCULATION,
                rb.ref(I),
                rb.ref(TERM),
                rb.zeroOrMore(
                        rb.ref(I),
                        rb.ref(OPERATOR),
                        rb.ref(I),
                        rb.ref(TERM)),
                rb.ref(I)
        );
        // Term: ["(" CalcExpression ")"] | Variable | Constant;
        rb.rule(TERM,
                rb.oneOf(
                        rb.sequence(
                                rb.str("("),
                                rb.ref(CALCULATION),
                                rb.str(")")),
                        rb.ref(VARIABLE),
                        rb.ref(CONSTANT))
        );
        // Constant: Integer;
        rb.rule(CONSTANT,
                rb.ref(INTEGER)
        );
        //Variable: Name;
        rb.rule(VARIABLE, rb.ref(NAME));
        // Integer: #DIGIT+;
        rb.rule(INTEGER, rb.oneOrMore(rb.charType(CharType.DIGIT)));
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
