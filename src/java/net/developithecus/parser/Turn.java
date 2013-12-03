/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser;

import net.developithecus.parser.definitions.AbstractExpression;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public interface Turn {

    public void accept();

    public void commit() throws ParserException;

    public void commit(Object result) throws ParserException;

    public void exception(String msg);

    public void push(AbstractExpression expr) throws ParserException;

    public Group result();

    public void rollback() throws ParserException;

    public void rollback(String errorMessage) throws ParserException;
}
