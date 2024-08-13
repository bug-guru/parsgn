/*
 * Copyright (c) 2024 Dimitrijs Fedotovs http://www.bug.guru
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

package guru.bug.tools.parsgn.utils;

import guru.bug.tools.parsgn.ResultBuilder;
import guru.bug.tools.parsgn.ebnf.RuleNames;
import guru.bug.tools.parsgn.ebnf.descriptor.*;
import guru.bug.tools.parsgn.exceptions.SyntaxErrorException;
import guru.bug.tools.parsgn.processing.Position;

import java.util.List;

public class EBNFResultBuilder extends ResultBuilder<AbstractRuleDescriptor> {
    private ConfigFileRuleDescriptor root;

    public ConfigFileRuleDescriptor getRoot() {
        return root;
    }

    @Override
    public AbstractRuleDescriptor createNode(String name, String value, List<AbstractRuleDescriptor> children, Position startPos, Position endPos) {
        return switch (name) {
            case RuleNames.CONFIG_FILE -> ConfigFileRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.RULE -> RuleRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.HIDE_FLAG -> HideFlagRuleDescriptor.create(startPos, endPos);
            case RuleNames.COMPRESS_FLAG -> CompressFlagRuleDescriptor.create(startPos, endPos);
            case RuleNames.RULE_PARAMS -> RuleParamsRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.NAME -> NameRuleDescriptor.create(value, startPos, endPos);
            case RuleNames.EXPRESSION -> ExpressionRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.NEGATIVE_FLAG -> NegativeFlagRuleDescriptor.create(startPos, endPos);
            case RuleNames.EXPRESSION_LIST -> ExpressionListRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.EXPRESSION_SUFFIX -> ExpressionSuffixRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.ZERO_OR_ONE -> ZeroOrOneRuleDescriptor.create(startPos, endPos);
            case RuleNames.ONE_OR_MORE -> OneOrMoreRuleDescriptor.create(startPos, endPos);
            case RuleNames.ZERO_OR_MORE -> ZeroOrMoreRuleDescriptor.create(startPos, endPos);
            case RuleNames.EXACTLY_N_TIMES -> ExactlyNTimesRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.AT_LEAST_MIN_TIMES -> AtLeastMinTimesRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.AT_LEAST_MIN_BUT_NOT_MORE_THAN_MAX_TIMES ->
                    AtLeastNButNotMoreThanMTimesRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.ONE_OF -> OneOfRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.ONE_OF_EXPRESSION -> OneOfExpressionRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.REFERENCE -> ReferenceRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.REFERENCE_PARAMS -> ReferenceParamsRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.CHAR_TYPE -> CharTypeRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.STRING -> StringRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.SEQUENCE -> SequenceRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.CALCULATION -> CalculationRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.TERM -> TermRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.OPERATOR -> OperatorRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.CONSTANT -> ConstantRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.INTEGER -> IntegerRuleDescriptor.create(value, startPos, endPos);
            case RuleNames.ADDITION -> AdditionRuleDescriptor.create(startPos, endPos);
            case RuleNames.SUBTRACTION -> SubtractionRuleDescriptor.create(startPos, endPos);
            case RuleNames.MULTIPLICATION -> MultiplicationRuleDescriptor.create(startPos, endPos);
            case RuleNames.DIVISION -> DivisionRuleDescriptor.create(startPos, endPos);
            case RuleNames.STRING_CONSTANT -> StringConstantRuleDescriptor.create(children, startPos, endPos);
            case RuleNames.CASE_SENSITIVE_STRING_CONSTANT ->
                    CaseSensitiveStringConstantRuleDescriptor.create(value, startPos, endPos);
            case RuleNames.CASE_INSENSITIVE_STRING_CONSTANT ->
                    CaseInsensitiveStringConstantRuleDescriptor.create(value, startPos, endPos);
            case RuleNames.VARIABLE -> VariableRuleDescriptor.create(children, startPos, endPos);
            default -> throw new SyntaxErrorException(startPos);
        };
    }

    @Override
    public void committedRoot(AbstractRuleDescriptor root) {
        if (root instanceof ConfigFileRuleDescriptor cfb) {
            this.root = cfb;
        }
    }
}
