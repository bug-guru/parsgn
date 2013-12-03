/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class ErrorMessage implements Cloneable {

    private Position position;
    private String message;
    private ErrorMessage cause;
    private String name;

    public ErrorMessage(String name, String msg, Position pos, ErrorMessage cause) {
        this.position = pos.clone();
        this.message = msg == null ? "Syntax error" : msg;
        this.cause = cause;
        this.name = name;
    }

    @Override
    public ErrorMessage clone() {
        try {
            ErrorMessage clone = (ErrorMessage) super.clone();
            clone.cause = cause == null ? null : cause.clone();
            clone.position = position.clone();
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public ErrorMessage getCause() {
        return cause;
    }

    public String getMessage() {
        return message;
    }

    public String getName() {
        return name;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        toString(result);
        return result.toString();
    }

    private void toString(StringBuilder result) {
        if (cause != null) {
            cause.toString(result);
            result.append(" -> ");
        }
        if (name != null) {
            result.append(name).append(": ");
        }
        result.append(message).append(" at ").append(position);
    }
}
