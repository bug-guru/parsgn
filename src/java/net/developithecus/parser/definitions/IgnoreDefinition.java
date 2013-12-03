/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class IgnoreDefinition extends AbstractSingleRefDefinition {

    @Override
    public AbstractExpression buildExpression() throws ParserException {
        AbstractExpression result = buildNestedExpression();
        result.setIgnore(true);
        return result;
    }

}

