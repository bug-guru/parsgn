/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ReapplyDefinition extends AbstractSingleRefDefinition {

    @Override
    public AbstractExpression buildExpression() throws ParserException {
        AbstractExpression result = buildNestedExpression();
        result.setReapply(true);
        return result;
    }

}
