/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

/**
 * @author Dimitrijs
 */
public class RuleNotFoundException extends ParserException {

    public RuleNotFoundException(String msg) {
        super(msg);
    }

    public RuleNotFoundException() {
        super();
    }

}
