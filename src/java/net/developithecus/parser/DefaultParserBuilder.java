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

package net.developithecus.parser;

import net.developithecus.parser.exceptions.InternalParsingException;
import net.developithecus.parser.exceptions.ParsingException;
import net.developithecus.parser.expr.CharType;
import net.developithecus.parser.expr.OneOfExpression;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static net.developithecus.parser.EBNFParserBuilder.*;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class DefaultParserBuilder extends AbstractParserBuilder {
    private static final Parser EBNF_PARSER = new EBNFParserBuilder().createParser();

    public Parser createParser(InputStream input) throws IOException, ParsingException {
        ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
        EBNF_PARSER.parse(input, builder);
        Rule root = null;
        for (ParseNode ruleNode : builder.getRoot().getChildren()) {
            Rule rule = createRule(ruleNode);
            if (root == null) {
                root = rule;
            }
        }
        return createParser(root);
    }

    private Rule createRule(ParseNode ruleNode) throws ParsingException {
        boolean isHidden = false;
        boolean isTemplate = false;
        String name = null;
        String transform = null;
        List<Expression> expressions = new ArrayList<>();
        for (ParseNode node : ruleNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case HIDDEN_FLAG:
                    isHidden = true;
                    break;
                case TEMPLATE_FLAG:
                    isTemplate = true;
                    break;
                case NAME:
                    name = node.getValue();
                    break;
                case TRANSFORM:
                    transform = node.getChildren().get(0).getValue();
                    break;
                case EXPRESSION:
                    Expression expr = createExpression(node);
                    expressions.add(expr);
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        RuleDef rule = rule(name);
        rule.setHidden(isHidden);
        rule.setTemplate(isTemplate);
        rule.setTransform(transform);
        if (expressions.size() > 1) {
            rule.setExpression(sequence(expressions));
        } else {
            rule.setExpression(expressions.get(0));
        }
        return rule;
    }

    private Expression createExpression(ParseNode exprNode) throws ParsingException {
        Expression expression = null;
        int minOccurrences = 1;
        int maxOccurrences = 1;
        Expression until = null;
        for (ParseNode node : exprNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case ONE_OF:
                    expression = createOneOfExpression(node);
                    break;
                case REFERENCE:
                    expression = createReferenceExpression(node);
                    break;
                case CHAR_TYPE:
                    expression = createCharTypeExpression(node);
                    break;
                case STRING:
                    expression = createStringExpression(node);
                    break;
                case SEQUENCE:
                    expression = createSequenceExpression(node);
                    break;
                case ZERO_OR_ONE:
                    minOccurrences = 0;
                    maxOccurrences = 1;
                    break;
                case ZERO_OR_MORE:
                    minOccurrences = 0;
                    maxOccurrences = Integer.MAX_VALUE;
                    break;
                case ONE_OR_MORE:
                    minOccurrences = 1;
                    maxOccurrences = Integer.MAX_VALUE;
                    break;
                case EXACTLY_N_TIMES:
                    minOccurrences = readNumber(node.getValue());
                    maxOccurrences = minOccurrences;
                    break;
                case AT_LEAST_MIN_TIMES:
                    minOccurrences = readNumber(node.getValue());
                    maxOccurrences = Integer.MAX_VALUE;
                    break;
                case AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES:
                    minOccurrences = readNumber(node.getChildren().get(0).getValue());
                    maxOccurrences = readNumber(node.getChildren().get(1).getValue());
                    break;
                case UNTIL:
                    until = createExpression(node.getChildren().get(0));
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        if (until != null) {
            return repeatUntil(until, expression);
        } else if (minOccurrences == 1 && maxOccurrences == 1) {
            return expression;
        } else {
            return repeat(minOccurrences, maxOccurrences, expression);
        }
    }

    private int readNumber(String value) {
        return Integer.valueOf(value);
    }

    private Expression createSequenceExpression(ParseNode seqNode) throws ParsingException {
        List<Expression> expressions = readExpressionList(seqNode);
        return sequence(expressions);
    }

    private Expression createStringExpression(ParseNode strNode) {
        String str = strNode.getValue();
        String transform = null;
        if (strNode.getChildren() != null && !strNode.getChildren().isEmpty()) {
            transform = readTransform(strNode.getChildren().get(0));
        }
        return str(str).transform(transform);
    }

    private Expression createCharTypeExpression(ParseNode charNode) {
        String strType = charNode.getChildren().get(0).getValue();
        CharType type = CharType.valueOf(strType);
        return charType(type);
    }

    private Expression createReferenceExpression(ParseNode refNode) throws ParsingException {
        String ruleName = null;
        String transform = null;
        for (ParseNode node : refNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case NAME:
                    ruleName = node.getValue();
                    break;
                case TRANSFORM:
                    transform = readTransform(node);
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return ref(ruleName).transform(transform);
    }

    private String readTransform(ParseNode transformNode) {
        return transformNode.getChildren().get(0).getValue();
    }

    private OneOfExpression createOneOfExpression(ParseNode oneOfNode) throws ParsingException {
        List<Expression> expressions = readExpressionList(oneOfNode);
        return oneOf(expressions);
    }

    private List<Expression> readExpressionList(ParseNode parentNode) throws ParsingException {
        List<Expression> expressions = new ArrayList<>();
        for (ParseNode node : parentNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case EXPRESSION:
                    Expression expr = createExpression(node);
                    expressions.add(expr);
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return expressions;
    }

}
