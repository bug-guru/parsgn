/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser.definitions;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public abstract class AbstractListDefinition extends AbstractDefinition {
    private final List<AbstractDefinition> entries = new LinkedList<AbstractDefinition>();

    public AbstractListDefinition add(AbstractDefinition definition) {
        entries.add(definition);
        return this;
    }

    protected List<AbstractDefinition> getEntries() {
        return entries;
    }
}
