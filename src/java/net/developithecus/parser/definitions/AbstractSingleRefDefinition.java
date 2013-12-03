/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public abstract class AbstractSingleRefDefinition extends AbstractDefinition {
    private AbstractDefinition definition;

    public AbstractSingleRefDefinition definition(AbstractDefinition definition) {
        this.definition = definition;
        return this;
    }

    protected AbstractDefinition definition() {
        return definition;
    }

    protected AbstractExpression buildNestedExpression() throws ParserException {
        return definition.buildExpression();
    }
}
