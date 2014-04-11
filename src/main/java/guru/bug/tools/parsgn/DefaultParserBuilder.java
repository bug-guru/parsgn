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

import guru.bug.tools.parsgn.exceptions.InternalParsingException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.expr.OneOfExpression;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static guru.bug.tools.parsgn.EBNFParser.*;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 * @version 1.0.0
 * @since 1.0.0
 */
public class DefaultParserBuilder {
    private static final Parser EBNF_PARSER = new EBNFParser();

    public Parser createParser(Reader input) throws IOException, ParsingException {
        RuleBuilder rb = new RuleBuilder();
        ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
        EBNF_PARSER.parse(input, builder);
        String rootName = null;
        for (ParseNode ruleNode : builder.getRoot().getChildren()) {
            String nodeName = ruleNode.getName();
            switch (nodeName) {
                case RULE:
                    Rule rule = createRule(rb, ruleNode);
                    if (rootName == null) {
                        rootName = rule.getName();
                    }
                    break;
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return new Parser(rb.build(rootName));
    }

    private Rule createRule(RuleBuilder rb, ParseNode ruleNode) throws ParsingException {
        String name = null;
        List<Expression> expressions = null;
        for (ParseNode node : ruleNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case NAME:
                    name = node.getValue();
                    break;
                case EXPRESSION_LIST:
                    expressions = parseExpressionList(rb, node);
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return rb.rule(name, expressions);
    }

    private List<Expression> parseExpressionList(RuleBuilder rb, ParseNode listNode) throws ParsingException {
        List<Expression> expressions = new ArrayList<>();
        for (ParseNode node : listNode.getChildren()) {
            switch (node.getName()) {
                case EXPRESSION:
                case ONE_OF_EXPRESSION:
                    Expression expr = createExpression(rb, node);
                    expressions.add(expr);
                    break;
                case I:
                    break;
                default:
                    throw new InternalParsingException(node.getName());
            }
        }
        return expressions;
    }

    private Expression createExpression(RuleBuilder rb, ParseNode exprNode) throws ParsingException {
        Expression expression = null;
        for (ParseNode node : exprNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case ONE_OF:
                    expression = createOneOfExpression(rb, node);
                    break;
                case REFERENCE:
                    expression = createReferenceExpression(rb, node);
                    break;
                case CHAR_TYPE:
                    expression = createCharTypeExpression(rb, node);
                    break;
                case STRING:
                    expression = createStringExpression(rb, node);
                    break;
                case SEQUENCE:
                    expression = createSequenceExpression(rb, node);
                    break;
                case EXPRESSION_SUFFIX:
                    expression = wrapRepeatExpression(rb, node, expression);
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return expression;
    }

    private Expression wrapRepeatExpression(RuleBuilder rb, ParseNode suffixNode, Expression expression) throws ParsingException {
        for (ParseNode node : suffixNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case QUANTIFIER:
                    return createQuantityExpression(rb, node, expression);
                case UNTIL:
                    return createUntilExpression(rb, node, expression);
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("Quantifier or Until were expected");
    }

    private Expression createUntilExpression(RuleBuilder rb, ParseNode root, Expression expression) throws ParsingException {
        for (ParseNode node : root.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case EXPRESSION:
                    Expression until = createExpression(rb, node);
                    return rb.repeatUntil(until, expression);
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("Until-expression was expected");
    }

    private Expression createQuantityExpression(RuleBuilder rb, ParseNode root, Expression expression) throws ParsingException {
        int minOccurrences = 1;
        int maxOccurrences = 1;
        for (ParseNode node : root.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
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
                    minOccurrences = findNumber(node);
                    maxOccurrences = minOccurrences;
                    break;
                case AT_LEAST_MIN_TIMES:
                    minOccurrences = findNumber(node);
                    maxOccurrences = Integer.MAX_VALUE;
                    break;
                case AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES:
                    Bounds bounds = findBounds(node);
                    minOccurrences = bounds.min;
                    maxOccurrences = bounds.max;
                    break;
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return rb.repeat(minOccurrences, maxOccurrences, expression);
    }

    private Bounds findBounds(ParseNode root) throws InternalParsingException {
        Bounds result = new Bounds();
        for (ParseNode node : root.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case MIN:
                    result.min = findNumber(node);
                    break;
                case MAX:
                    result.max = findNumber(node);
                    break;
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return result;
    }

    private int findNumber(ParseNode root) throws InternalParsingException {
        for (ParseNode node : root.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case NUMBER:
                    return Integer.parseInt(node.getValue());
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("Number was expected");
    }

    private Expression createSequenceExpression(RuleBuilder rb, ParseNode seqNode) throws ParsingException {
        for (ParseNode node : seqNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case EXPRESSION_LIST:
                    List<Expression> expressions = parseExpressionList(rb, node);
                    return rb.sequence(expressions);
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("ExpressionList haven't been found");
    }

    private Expression createStringExpression(RuleBuilder rb, ParseNode strNode) throws InternalParsingException {
        String str = strNode.getValue();
        String transform = null;
        if (strNode.getChildren() != null && !strNode.getChildren().isEmpty()) {
            transform = readTransform(strNode.getChildren().get(0));
        }
        return rb.str(str).transform(transform);
    }

    private String readTransform(ParseNode transformNode) throws InternalParsingException {
        for (ParseNode node : transformNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case STRING:
                    return node.getValue();
                case I:
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("String was expected");
    }

    private Expression createCharTypeExpression(RuleBuilder rb, ParseNode charNode) {
        String strType = charNode.getChildren().get(0).getValue();
        CharType type = CharType.valueOf(strType);
        return rb.charType(type);
    }

    private Expression createReferenceExpression(RuleBuilder rb, ParseNode refNode) throws ParsingException {
        String ruleName = null;
        for (ParseNode node : refNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case NAME:
                    ruleName = node.getValue();
                    break;
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        return rb.ref(ruleName);
    }

    private OneOfExpression createOneOfExpression(RuleBuilder rb, ParseNode oneOfNode) throws ParsingException {
        for (ParseNode node : oneOfNode.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case ONE_OF_VARIANT1:
                    return createOneOfVariant1Expression(rb, node);
                case ONE_OF_VARIANT2:
                    List<Expression> expressions = parseExpressionList(rb, node);
                    return rb.oneOf(expressions);
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("OneOfVariant1 or OneOfVariant2 were expected");
    }

    private OneOfExpression createOneOfVariant1Expression(RuleBuilder rb, ParseNode root) throws ParsingException {
        for (ParseNode node : root.getChildren()) {
            String nodeName = node.getName();
            switch (nodeName) {
                case ONE_OF_VARIANT2:
                    List<Expression> expressions = parseExpressionList(rb, node);
                    return rb.oneOf(expressions);
                default:
                    throw new InternalParsingException(nodeName);
            }
        }
        throw new InternalParsingException("OneOfVariant2 was expected");
    }

    private static class Bounds {
        int min;
        int max;
    }
}
