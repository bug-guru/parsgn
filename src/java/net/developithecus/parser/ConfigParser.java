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
        Rule name = rule("Name");
        Rule expression = rule("Expression");
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
                repeat(ref(i)).silent());
        rule.addAll(
                repeat(ref(i)).silent(),
                ref(name),
                str(":").silent(),
                ref(expressionList),
                str(";").silent());
        i.addAll(
                alt(
                        ref(whiteSpace),
                        ref(singleLineComment),
                        ref(multiLineComment)));
        whiteSpace.addAll(
                charType(CharType.WHITESPACE),
                repeat(charType(CharType.WHITESPACE)));
        singleLineComment.addAll(
                str("//"),
                repeat(charType(CharType.DEFINED))
                        .endCondition(charType(CharType.LINE_SEPARATOR)));
        multiLineComment.addAll(
                str("/*"),
                repeat(charType(CharType.DEFINED)).endCondition(str("*/")));
        name.addAll(group(
                charType(CharType.UNICODE_IDENTIFIER_START),
                repeat(charType(CharType.UNICODE_IDENTIFIER_PART)))
                .compact());
        expression.addAll(
                alt(
                        ref(altGroup),
                        ref(name),
                        ref(charType),
                        ref(string),
                        ref(group),
                        ref(repeatGroup),
                        ref(optGroup)));
        altGroup.addAll(
                ref(altGroupItem),
                repeat(ref(i)).silent(),
                str("|").silent(),
                repeat(ref(i)).silent(),
                ref(altGroupItem),
                repeat(
                        repeat(ref(i)).silent(),
                        str("|").silent(),
                        repeat(ref(i)).silent(),
                        ref(altGroupItem)));
        altGroupItem.addAll(
                alt(
                        ref(name),
                        ref(charType),
                        ref(string),
                        ref(group)));
        charType.addAll(
                str("#").silent(),
                ref(name));
        string.addAll(
                str("\"").silent(),
                repeat(
                        alt(
                                str("\\\""),
                                str("\\\\"),
                                charType(CharType.DEFINED)))
                        .endCondition(str("\"").silent())
                        .compact());
        group.addAll(
                str("(").silent(),
                repeat(ref(expressionList)),
                str(")").silent());
        repeatGroup.addAll(
                str("{").silent(),
                repeat(ref(expressionList)),
                str("}").silent());
        optGroup.addAll(
                str("[").silent(),
                repeat(ref(expressionList)),
                str("]").silent());
        expressionList.addAll(
                repeat(ref(i)).silent(),
                ref(expression),
                repeat(
                        repeat(ref(i)).silent(),
                        ref(expression)),
                repeat(ref(i)).silent());
        root(configFile);
    }
}
