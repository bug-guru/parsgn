/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.Builder;
import net.developithecus.parser.ParserException;
import net.developithecus.parser.RuleNotFoundException;

/**
 * @author Dimitrijs
 */
public class RuleRefDefinition extends AbstractDefinition {
    private final String refRuleName;
    private final Builder builder;
    private Rule ref;

    public RuleRefDefinition(String refRuleName, Builder builder) {
        this.refRuleName = refRuleName;
        this.builder = builder;
    }

    @Override
    public AbstractExpression buildExpression() throws ParserException {
        if (ref == null) {
            resolve();
        }
        return ref.buildExpression();
    }

    private void resolve() throws RuleNotFoundException {
        ref = builder.getRule(refRuleName);
    }
}
