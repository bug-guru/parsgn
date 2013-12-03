/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import net.developithecus.parser.definitions.AbstractDefinition;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ConfigRulesBuilder extends Builder {
    private static final String CONFIG_FILE = "ConfigFile";
    private static final String RULE = "Rule";
    private static final String EOF = "EOF";
    private static final String RULE_NAME = "RuleName";
    private static final String RULE_NAME_SEPARATOR = "RuleNameSeparator";
    private static final String RULE_TERMINATOR = "RuleTerminator";
    private static final String EXPRESSION = "Expression";
    private static final String TOKEN = "Token";
    private static final String IGNORABLE = "Ignorable";
    private static final String INPUT_CHAR = "InputChar";
    private static final String SEPARATOR = "Separator";
    private static final String WHITE_SPACES = "WhiteSpaces";
    private static final String WHITE_SPACE = "WhiteSpace";
    private static final String COMMENT = "Comment";
    private static final String COMMENT_START = "CommentStart";
    private static final String COMMENT_END = "CommentEnd";
    private static final String EXPRESSION_SEPARATOR = "ExpressionSeparator";
    private static final String OPEN_EXPRESSION = "OpenExpression";
    private static final String CLOSE_EXPRESSION = "CloseExpression";
    private static final String EXPRESSION_LIST = "ExpressionList";
    private static final String OF_EXPRESSION = "OfExpression";
    private static final String PREFIX = "Prefix";
    private static final String STRING_LITERAL = "StringLiteral";
    private static final String STRING_LITERAL_CHAR = "StringLiteralChar";
    private static final String QUOTATION_MARK = "QuotationMark";
    private static final String REFERENCE_EXPRESSION = "ReferenceExpression";
    private static final String VALUE_EXPRESSION = "ValueExpression";
    private static final String SET_OF_EXPRESSION = "SetOfExpression";
    private static final String SEQUENCE_EXPRESSION = "SequenceExpression";
    private static final String REPEAT_WHILE = "RepeatWhile";
    private static final String REPEAT_UNTIL = "RepeatUntil";
    private static final String REPEAT_FROM = "RepeatFrom";
    private static final String REPEAT_EXPRESSION = "RepeatExpression";
    private static final String ONE_OF_EXPRESSION = "OneOfExpression";
    private static final String CONVERT_EXPRESSION = "ConvertExpression";
    private static final String CHAR_EXPRESSION = "CharExpression";


    public AbstractDefinition main;

    public Parser newParser() throws ParserException {
        if (main == null) {
            build();
        }
        Parser result = new Parser();
        result.setMainRule(main);
        return result;
    }

    private void build() throws RuleExistsException {
        main = ref(CONFIG_FILE);
        /*
         * ConfigFile: repeat Rule until ignore sequence Ignorable, EOF;
         */
        rule(CONFIG_FILE,
             repeat(ref(RULE))
                     .until(
                             ignore(sequence(ref(IGNORABLE), ref(EOF)))));
        /*
         * Rule: sequence
         *          RuleName,
         *          ignore RuleNameSeparator,
         *          Expression,
         *          ignore RuleTerminator;
         */
        rule(RULE,
             sequence(
                     ref(RULE_NAME),
                     ignore(ref(RULE_NAME_SEPARATOR)),
                     ref(EXPRESSION),
                     ignore(ref(RULE_TERMINATOR))));
        /*
         * RuleName: compact Token;
         */
        rule(RULE_NAME, compact(ref(TOKEN)));
        /*
         * Token: sequence
         *       Ignorable,
         *       compact repeat InputChar until ignore reapply Separator;
         */
        rule(TOKEN,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     compact(repeat(
                             ref(INPUT_CHAR))
                                     .until(
                                             ignore(reapply(ref(SEPARATOR)))))));
        /*
         * Ignorable: optional ignore repeat one of WhiteSpaces, Comment;
         */
        rule(IGNORABLE,
             repeat(
                     oneof(
                             ref(WHITE_SPACES),
                             ref(COMMENT))));
        /*
         * Separator: one of
         *              char ":;,#\"() \n\r\f\t" of InputChar,
         *              EOF;
         */
        rule(SEPARATOR,
             oneof(
                     charof(":;,#\"() \n\r\f\t", ref(INPUT_CHAR)),
                     ref(EOF)));
        /*
         * RuleNameSeparator: sequence
         *                          Ignorable,
         *                          value ":" of InputChar;
         */
        rule(RULE_NAME_SEPARATOR,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     charof(":", ref(INPUT_CHAR))));
        /*
         * RuleTerminator: sequence
         *                      Ignorable,
         *                      value ";" of InputChar;
         */
        rule(RULE_TERMINATOR,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     charof(";", ref(INPUT_CHAR))));
        /*
         * ExpressionSeparator: sequence
         *                          Ignorable,
         *                          value "," of InputChar;
         */
        rule(EXPRESSION_SEPARATOR,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     charof(",", ref(INPUT_CHAR))));
        /*
         * OpenExpression: sequence
         *        Ignorable,
         *        value "(" of InputChar;
         */
        rule(OPEN_EXPRESSION,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     charof("(", ref(INPUT_CHAR))));
        /*
         * CloseExpression: sequence
         *        Ignorable,
         *        value ")" of InputChar;
         */
        rule(CLOSE_EXPRESSION,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     charof(")", ref(INPUT_CHAR))));
        /*
         * WhiteSpace: char "\n\r\t\f " of InputChar;
         */
        rule(WHITE_SPACE,
             charof("\n\r\t\f ", ref(INPUT_CHAR)));
        /*
         * WhiteSpaces: compact repeat WhiteSpace;
         */
        rule(WHITE_SPACES,
             compact(repeat(ref(WHITE_SPACE))));
        /*
         * Comment: repeat InputChar from CommentStart until CommentEnd;
         */
        rule(COMMENT,
             compact(
                     repeat(ref(WHITE_SPACE))
                             .from(ref(COMMENT_START))
                             .until(ref(COMMENT_END))));
        /*
         * CommentStart: ignore value "#" of InputChar;
         */
        rule(COMMENT_START,
             ignore(charof("#", ref(INPUT_CHAR))));
        /*
         * CommentEnd: ignore reapply char "\n\r" of InputChar;
         */
        rule(COMMENT_END,
             ignore(reapply(charof("\n\r", ref(INPUT_CHAR)))));
        /*
         * EOF
         */
        rule(EOF, eof());
        /*
         * StringLiteralChar:
         *     one of
         *         convert value "\\\\" to "\\",
         *         convert value "\\\"" to "\"",
         *         convert value "\\n" to "\n",
         *         convert value "\\r" to "\r",
         *         convert value "\\t" to "\t",
         *         convert value "\\f" to "\f",
         *         char;
         */
        rule(STRING_LITERAL_CHAR,
             compact(
                     oneof(
                             convertto(value("\\\\"), "\\"),
                             convertto(value("\\\""), "\""),
                             convertto(value("\\t"), "\t"),
                             convertto(value("\\n"), "\n"),
                             convertto(value("\\r"), "\r"),
                             convertto(value("\\f"), "\f"),
                             chars())));
        rule(INPUT_CHAR, chars());
        /*
         * QuotationMark:
         *     compact char "\"" of InputChar;
         */
        rule(QUOTATION_MARK,
             compact(charof("\"", ref(INPUT_CHAR))));
        /*
         * StringLiteral:
         *     sequence
         *         optional ignore Ignorable,
         *         compact repeat InputChar
         *                   from ignore QuotationMark
         *                  until ignore QuotationMark;
         */
        rule(STRING_LITERAL,
             sequence(
                     optional(ignore(ref(IGNORABLE))),
                     compact(repeat(ref(STRING_LITERAL_CHAR))
                                     .from(ref(QUOTATION_MARK))
                                     .until(ref(QUOTATION_MARK)))));
        /*
         * Prefix:
         *     set of
         *         value "compact" of Token,
         *         value "ignore" of Token,
         *         value "optional" of Token,
         *         value "reapply" of Token;
         */
        rule(PREFIX,
             setof(
                     valueof("compact", ref(TOKEN)),
                     valueof("ignore", ref(TOKEN)),
                     valueof("optional", ref(TOKEN)),
                     valueof("reapply", ref(TOKEN))));
        /*
         * OfExpression:
         *     sequence
         *         ignore value "of" of Token,
         *         Expression;
         */
        rule(OF_EXPRESSION,
             sequence(
                     ignore(valueof("of", ref(TOKEN))),
                     ref(EXPRESSION)));
        /*
         * ExpressionList:
         *     repeat Expression from Expression while ignore ExpressionSeparator;
         */
        rule(EXPRESSION_LIST,
             repeat(ref(EXPRESSION))
                     .from(ref(EXPRESSION))
                     .setWhile(ignore(ref(EXPRESSION_SEPARATOR))));
        /*
         * Expression:
         *     one of
         *         CharExpression,
         *         ConvertExpression,
         *         OneOfExpression,
         *         RepeatExpression,
         *         SequenceExpression,
         *         SetOfExpression,
         *         ValueExpression,
         *         ReferenceExpression;
         */
        rule(EXPRESSION,
             oneof(
                     ref(CHAR_EXPRESSION),
                     ref(CONVERT_EXPRESSION),
                     ref(ONE_OF_EXPRESSION),
                     ref(REPEAT_EXPRESSION),
                     ref(SEQUENCE_EXPRESSION),
                     ref(SET_OF_EXPRESSION),
                     ref(VALUE_EXPRESSION),
                     ref(REFERENCE_EXPRESSION)));
        /*
         * CharExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "char" of Token,
         *         optional StringLiteral,
         *         optional OfExpression;
         */
        rule(CHAR_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("char", ref(TOKEN))),
                     optional(ref(STRING_LITERAL)),
                     optional(ref(OF_EXPRESSION))));
        /*
         * ConvertExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "convert" of Token,
         *         Expression,
         *         ignore value "to" of Token,
         *         StringLiteral;
         */
        rule(CONVERT_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("convert", ref(TOKEN))),
                     ref(EXPRESSION),
                     ignore(valueof("to", ref(TOKEN))),
                     ref(STRING_LITERAL)));
        /*
         * OneOfExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "one" of Token,
         *         ignore value "of" of Token,
         *         ExpressionList;
         */
        rule(ONE_OF_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("one", ref(TOKEN))),
                     ignore(valueof("of", ref(TOKEN))),
                     ref(EXPRESSION_LIST)));
        /*
         * RepeatExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "repeat" of Token,
         *         Expression,
         *         optional RepeatFrom,
         *         optional one of RepeatUntil, RepeatWhile;
         */
        rule(REPEAT_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("repeat", ref(TOKEN))),
                     ref(EXPRESSION),
                     optional(ref(REPEAT_FROM)),
                     optional(oneof(
                             ref(REPEAT_UNTIL),
                             ref(REPEAT_WHILE)))));
        /*
         * RepeatFrom:
         *     sequence
         *         ignore value "from" of Token,
         *         Expression;
         */
        rule(REPEAT_FROM,
             sequence(
                     ignore(valueof("from", ref(TOKEN))),
                     ref(EXPRESSION)));
        /*
         * RepeatUntil:
         *     sequence
         *         ignore value "until" of Token,
         *         Expression;
         */
        rule(REPEAT_UNTIL,
             sequence(
                     ignore(valueof("until", ref(TOKEN))),
                     ref(EXPRESSION)));
        /*
         * RepeatWhile:
         *     sequence
         *         ignore value "while" of Token,
         *         Expression;
         */
        rule(REPEAT_WHILE,
             sequence(
                     ignore(valueof("while", ref(TOKEN))),
                     ref(EXPRESSION)));
        /*
         * SequenceExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "sequence" of Token,
         *         ExpressionList;
         */
        rule(SEQUENCE_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("sequence", ref(TOKEN))),
                     ref(EXPRESSION_LIST)));
        /*
         * SetOfExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "set" of Token,
         *         ignore value "of" of Token,
         *         ExpressionList;
         */
        rule(SET_OF_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("set", ref(TOKEN))),
                     ignore(valueof("of", ref(TOKEN))),
                     ref(EXPRESSION_LIST)));
        /*
         * ValueExpression:
         *     sequence
         *         optional Prefix,
         *         ignore value "value" of Token,
         *         optional StringLiteral,
         *         optional OfExpression;
         */
        rule(VALUE_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ignore(valueof("value", ref(TOKEN))),
                     optional(ref(STRING_LITERAL)),
                     optional(ref(OF_EXPRESSION))));
        /*
         * ReferenceExpression:
         *     sequence
         *         optional Prefix,
         *         RuleName;
         */
        rule(REFERENCE_EXPRESSION,
             sequence(
                     optional(ref(PREFIX)),
                     ref(RULE_NAME)));
    }
}
