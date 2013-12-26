package net.developithecus.parser;

import net.developithecus.parser.expr.CharType;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.26.12
 * @since 1.0
 */
public class ConfigParser extends Parser {
    public ConfigParser() {
        Rule configFile = rule("ConfigFile");
        Rule rule = rule("Rule");
        Rule i = rule("I");
        Rule whiteSpace = rule("WhiteSpace");
        Rule singleLineComment = rule("SingleLineComment");
        Rule multiLineComment = rule("MultiLineComment");
        Rule token = rule("Token");
        Rule expression = rule("Expression");
        Rule ruleRef = rule("RuleRef");
        Rule altGroup = rule("AltGroup");
        Rule altGroupItem = rule("AltGroupItem");
        Rule charType = rule("CharType");
        Rule string = rule("String");
        Rule group = rule("Group");
        Rule repeatGroup = rule("RepeatGroup");
        Rule optGroup = rule("OptGroup");
        Rule expressionList = rule("ExpressionList");

        configFile.addAll(
                ref(rule),
                repeat(ref(rule)),
                repeat(ref(i)));
        rule.addAll(
                repeat(ref(i)),
                ref(token),
                str(":"),
                ref(expressionList),
                str(";"));
        i.addAll(
                alt(
                        ref(whiteSpace),
                        ref(singleLineComment),
                        ref(multiLineComment)));
        whiteSpace.addAll(charType(CharType.WHITESPACE));
        singleLineComment.addAll(
                str("//"),
                repeat(charType(CharType.DEFINED)),
                charType(CharType.LINE_SEPARATOR));
        multiLineComment.addAll(
                str("/*"),
                repeat(charType(CharType.DEFINED)),
                str("*/"));
        token.addAll(
                charType(CharType.UNICODE_IDENTIFIER_START),
                repeat(charType(CharType.UNICODE_IDENTIFIER_PART)));
        expression.addAll(
                alt(
                        ref(ruleRef),
                        ref(altGroup),
                        ref(charType),
                        ref(string),
                        ref(group),
                        ref(repeatGroup),
                        ref(optGroup)));
        ruleRef.addAll(ref(token));
        altGroup.addAll(
                ref(altGroupItem),
                repeat(ref(i)),
                str("|"),
                repeat(ref(i)),
                ref(altGroupItem),
                repeat(
                        repeat(ref(i)),
                        str("|"),
                        repeat(ref(i)),
                        ref(altGroupItem)));
        altGroupItem.addAll(
                alt(
                        ref(ruleRef),
                        ref(charType),
                        ref(string),
                        ref(group)));
        charType.addAll(
                str("#"),
                ref(token));
        string.addAll(
                str("\""),
                repeat(alt(
                        str("\\\""),
                        str("\\"),
                        charType(CharType.DEFINED)
                )),
                str("\""));
        group.addAll(
                str("("),
                repeat(ref(expressionList)),
                str(")"));
        repeatGroup.addAll(
                str("{"),
                repeat(ref(expressionList)),
                str("}"));
        optGroup.addAll(
                str("["),
                repeat(ref(expressionList)),
                str("]"));
        expressionList.addAll(
                repeat(
                        ref(i),
                        ref(expression),
                        repeat(
                                repeat(ref(i)),
                                ref(expression)),
                        repeat(ref(i))));
        root(configFile);
    }
}
