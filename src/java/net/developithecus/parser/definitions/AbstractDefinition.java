/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public abstract class AbstractDefinition {

    public abstract AbstractExpression buildExpression() throws ParserException;

    @Override
    public String toString() {
        String result = getClass().getSimpleName();
        if (result.endsWith("Definition")) {
            result = result.substring(0, result.length() - 10);
        }
        return result;
    }


}
