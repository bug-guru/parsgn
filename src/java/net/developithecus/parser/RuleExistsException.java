/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

/**
 * @author Dimitrijs
 */
public class RuleExistsException extends ParserException {

    public RuleExistsException(String msg) {
        super(msg);
    }

    public RuleExistsException() {
        super();
    }

}
