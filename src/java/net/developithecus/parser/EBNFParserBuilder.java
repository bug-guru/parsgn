package net.developithecus.parser;

import net.developithecus.parser.expr.CharType;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class EBNFParserBuilder extends ParserBuilder {
    public static final String CONFIG_FILE = "ConfigFile";
    public static final String RULE = "RULE";
    public static final String I = "I";
    public static final String WHITE_SPACE = "WhiteSpace";
    public static final String COMMENT = "Comment";
    public static final String SINGLE_LINE_COMMENT = "SingleLineComment";
    public static final String MULTI_LINE_COMMENT = "MultiLineComment";
    public static final String NAME = "Name";
    public static final String EXPRESSION = "Expression";
    public static final String EXPRESSION_LIST = "ExpressionList";
    public static final String PREFIX = "Prefix";
    public static final String MAKE_VALUE = "MakeValue";
    public static final String SILENT = "Silent";
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
    public static final String ONE_OF = "oneOf";
    public static final String ONE_OF_ITEM = "OneOfItem";
    public static final String REFERENCE = "Reference";
    public static final String CHAR_TYPE = "CharType";
    public static final String STRING = "String";
    public static final String SEQUENCE = "Sequence";

    private final Rule root;

    public EBNFParserBuilder() {
        this.root = generateRules();
    }

    private Rule generateRules() {
        Expression ignorable = zeroOrMore(ref(I)).silent();
        Rule result = rule(CONFIG_FILE,
                oneOrMore(
                        ignorable,
                        ref(RULE)
                ),
                ignorable
        );
        rule(RULE,
                zeroOrMore(
                        ref(PREFIX),
                        ignorable
                ),
                ref(NAME),
                ignorable,
                str(":").silent(),
                ref(EXPRESSION_LIST),
                str(";").silent()
        );
        rule(I,
                oneOf(
                        ref(WHITE_SPACE).silent(),
                        ref(COMMENT)
                )
        );
        rule(WHITE_SPACE,
                oneOrMore(charType(CharType.VALID)).silent()
        );
        rule(COMMENT,
                oneOf(
                        ref(SINGLE_LINE_COMMENT),
                        ref(MULTI_LINE_COMMENT)
                ).makeValue()
        );
        rule(SINGLE_LINE_COMMENT,
                str("//").silent(),
                repeatUntil(
                        charType(CharType.LINE_SEPARATOR).silent(),
                        charType(CharType.VALID)
                )
        );
        rule(MULTI_LINE_COMMENT,
                str("/*").silent(),
                repeatUntil(
                        str("*/").silent(),
                        charType(CharType.VALID)
                )
        );
        rule(NAME,
                sequence(
                        charType(CharType.UNICODE_IDENTIFIER_START),
                        zeroOrMore(
                                charType(CharType.UNICODE_IDENTIFIER_PART)
                        )
                ).makeValue()
        );
        rule(EXPRESSION,
                zeroOrMore(
                        ref(PREFIX),
                        ignorable
                ),
                oneOf(
                        ref(ONE_OF),
                        ref(REFERENCE),
                        ref(CHAR_TYPE),
                        ref(STRING),
                        ref(SEQUENCE)
                ),
                ignorable,
                zeroOrOne(
                        oneOf(
                                ref(QUANTIFIER),
                                ref(UNTIL)
                        ),
                        ignorable
                )
        );
        rule(EXPRESSION_LIST,
                oneOrMore(
                        ignorable,
                        ref(EXPRESSION)
                )
        );
        rule(PREFIX,
                oneOf(
                        ref(MAKE_VALUE),
                        ref(SILENT)
                )
        );
        rule(MAKE_VALUE,
                str("=").silent()
        );
        rule(SILENT,
                str("^").silent()
        );
        return result;
    }
}
