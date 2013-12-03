/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ParserClosedException extends ParserException {

    /**
     * Creates a new instance of <code>ParserClosedException</code> without detail message.
     */
    public ParserClosedException() {
    }


    /**
     * Constructs an instance of <code>ParserClosedException</code> with the specified detail message.
     *
     * @param msg the detail message.
     */
    public ParserClosedException(String msg) {
        super(msg);
    }
}
