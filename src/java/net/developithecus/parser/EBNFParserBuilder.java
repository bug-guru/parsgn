package net.developithecus.parser;

import net.developithecus.parser.expr.CharType;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class EBNFParserBuilder extends AbstractParserBuilder {
    public static final String CONFIG_FILE = "ConfigFile";
    public static final String RULE = "Rule";
    public static final String RULE_PREFIX = "RulePrefix";
    public static final String HIDDEN_FLAG = "HiddenFlag";
    public static final String TEMPLATE_FLAG = "TemplateFlag";
    public static final String I = "I";
    public static final String SINGLE_LINE_COMMENT = "SingleLineComment";
    public static final String MULTI_LINE_COMMENT = "MultiLineComment";
    public static final String NAME = "Name";
    public static final String EXPRESSION = "Expression";
    public static final String TRANSFORM = "Transform";
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
    public static final String ONE_OF_EXPRESSION = "OneOfItem";
    public static final String REFERENCE = "Reference";
    public static final String CHAR_TYPE = "CharType";
    public static final String STRING = "String";
    public static final String SEQUENCE = "Sequence";
    private final Rule root;

    public EBNFParserBuilder() {
        this.root = generateRules();
    }

    private Rule generateRules() {
        Rule result = rule(CONFIG_FILE,
                oneOrMore(
                        ref(I),
                        ref(RULE)
                ),
                ref(I),
                charType(CharType.EOF)
        );
        rule(RULE,
                zeroOrOne(
                        ref(RULE_PREFIX),
                        ref(I)
                ),
                ref(NAME),
                zeroOrOne(
                        ref(TRANSFORM)
                ),
                ref(I),
                str(":"),
                ref(EXPRESSION_LIST),
                str(";")
        );
        rule(RULE_PREFIX,
                oneOf(
                        ref(HIDDEN_FLAG),
                        ref(TEMPLATE_FLAG)
                )
        ).template();
        rule(HIDDEN_FLAG,
                str(".")
        );
        rule(TEMPLATE_FLAG,
                str("^")
        );
        rule(I,
                zeroOrMore(
                        oneOf(
                                charType(CharType.WHITESPACE),
                                ref(SINGLE_LINE_COMMENT),
                                ref(MULTI_LINE_COMMENT)
                        )
                )
        ).hide();
        rule(SINGLE_LINE_COMMENT,
                str("//"),
                repeatUntil(
                        charType(CharType.LINE_SEPARATOR),
                        charType(CharType.VALID)
                )
        );
        rule(MULTI_LINE_COMMENT,
                str("/*"),
                repeatUntil(
                        str("*/"),
                        charType(CharType.VALID)
                )
        );
        rule(NAME,
                sequence(
                        charType(CharType.UNICODE_IDENTIFIER_START),
                        zeroOrMore(
                                charType(CharType.UNICODE_IDENTIFIER_PART)
                        )
                )
        );
        rule(EXPRESSION_LIST,
                oneOrMore(
                        ref(I),
                        ref(EXPRESSION)
                )
        ).template();
        rule(EXPRESSION,
                oneOf(
                        ref(ONE_OF),
                        ref(REFERENCE),
                        ref(CHAR_TYPE),
                        ref(STRING),
                        ref(SEQUENCE)
                ),
                ref(EXPRESSION_SUFFIX)
        );
        rule(EXPRESSION_SUFFIX,
                ref(I),
                zeroOrOne(
                        oneOf(
                                ref(QUANTIFIER),
                                ref(UNTIL)
                        ),
                        ref(I)
                )
        ).template();
        rule(TRANSFORM,
                ref(I),
                str("->"),
                ref(I),
                oneOf(
                        ref(STRING),
                        ref(NAME).transform(STRING)
                )
        );
        rule(QUANTIFIER,
                oneOf(
                        ref(ZERO_OR_ONE),
                        ref(ZERO_OR_MORE),
                        ref(ONE_OR_MORE),
                        ref(EXACTLY_N_TIMES),
                        ref(AT_LEAST_MIN_TIMES),
                        ref(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES)
                )
        ).template();
        rule(ZERO_OR_ONE,
                str("?")
        );
        rule(ONE_OR_MORE,
                str("+")
        );
        rule(ZERO_OR_MORE,
                str("*")
        );
        rule(UNTIL,
                str("{"),
                ref(I),
                ref(EXPRESSION),
                ref(I),
                str("}")
        );
        rule(EXACTLY_N_TIMES,
                str("{"),
                ref(I),
                ref(NUMBER),
                ref(I),
                str("}")
        );
        rule(AT_LEAST_MIN_TIMES,
                str("{"),
                ref(I),
                ref(NUMBER),
                ref(I),
                str(","),
                ref(I),
                str("}")
        );
        rule(AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES,
                str("{"),
                ref(I),
                ref(MIN),
                ref(I),
                str(","),
                ref(I),
                ref(MAX),
                ref(I),
                str("}")
        );
        rule(NUMBER,
                oneOrMore(
                        charType(CharType.DIGIT)
                )
        ).template();
        rule(MIN,
                ref(NUMBER)
        );
        rule(MAX,
                ref(NUMBER)
        );
        rule(ONE_OF,
                oneOf(
                        ref(ONE_OF_VARIANT1),
                        ref(ONE_OF_VARIANT2)
                )
        );
        rule(ONE_OF_VARIANT1,
                str("("),
                ref(I),
                ref(ONE_OF_VARIANT2),
                ref(I),
                str(")")
        ).template();
        rule(ONE_OF_VARIANT2,
                ref(ONE_OF_EXPRESSION),
                oneOrMore(
                        ref(I),
                        str("|"),
                        ref(I),
                        ref(ONE_OF_EXPRESSION)
                )
        ).template();
        rule(ONE_OF_EXPRESSION,
                oneOf(
                        ref(REFERENCE),
                        ref(CHAR_TYPE),
                        ref(STRING),
                        ref(SEQUENCE)
                ),
                ref(EXPRESSION_SUFFIX)
        ).transform(EXPRESSION);
        rule(REFERENCE,
                ref(NAME),
                zeroOrOne(
                        ref(TRANSFORM)
                )
        );
        rule(CHAR_TYPE,
                str("#"),
                ref(NAME)
        );
        rule(STRING,
                str("\""),
                repeatUntil(
                        str("\""),
                        oneOf(
                                str("\\\"").transform("\""),
                                str("\\\\").transform("\\"),
                                charType(CharType.VALID)
                        )
                ),
                zeroOrOne(
                        ref(TRANSFORM)
                )
        );
        rule(SEQUENCE,
                str("("),
                ref(EXPRESSION_LIST),
                str(")")
        );
        return result;
    }

    public Parser createParser() {
        return createParser(root);
    }
}
