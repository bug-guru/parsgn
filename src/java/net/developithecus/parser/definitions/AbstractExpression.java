/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.developithecus.parser.definitions;

import net.developithecus.parser.ParserException;
import net.developithecus.parser.Turn;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public abstract class AbstractExpression {
    private boolean ignore;
    private boolean compact;
    private boolean optional;
    private boolean reapply;

    public void init(Turn turn) throws ParserException {
    }

    public void turn(Turn turn, int ch) throws ParserException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void afterCommit(Turn turn) throws ParserException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void afterRollback(Turn turn) throws ParserException {
        throw new UnsupportedOperationException("Not implemented");
    }

    public abstract AbstractDefinition getDefinition();

    public boolean isCompact() {
        return compact;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isReapply() {
        return reapply;
    }

    public void setReapply(boolean reapply) {
        this.reapply = reapply;
    }

    @Override
    public String toString() {
        return getDefinition().toString();
    }

}
