/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.developithecus.parser;

import java.util.*;

/**
 * @author Dimitrijs Fedotovs <dimitrijs.fedotovs@developithecus.net>
 */
public class Group implements Cloneable {
    private List<Group> children;
    private Object value;
    private boolean rule;
    private boolean dumping;

    public Group(boolean isRule) {
        this.rule = isRule;
    }

    @Override
    public Group clone() {
        try {
            Group clone = (Group) super.clone();
            if (this.children != null) {
                clone.children = new LinkedList<Group>();
                for (Group c : this.children) {
                    clone.children.add(c.clone());
                }
            }
            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public void clear() {
        if (children != null) {
            children.clear();
        }
    }

    public List<Group> getChildren() {
        if (children == null) {
            return Collections.emptyList();
        }
        return children;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isRule() {
        return rule;
    }

    public void add(Group group) {
        if (children == null) {
            children = new LinkedList<Group>();
        }
        children.add(group);
    }

    public String dump() {
        StringBuilder result = new StringBuilder();
        dump(result, "", false);
        return result.toString();
    }

    private void dump(StringBuilder result, String ident, boolean lastChild) {
        if (dumping) {
            throw new IllegalStateException("Cycling");
        }
        dumping = true;

        try {
            StringTokenizer lines = new StringTokenizer(String.valueOf(value), "\n\r");
            boolean first = true;
            while (lines.hasMoreTokens()) {
                result.append(ident);
                result.append(lines.nextToken().trim());
                result.append("\n");
                if (first && lines.hasMoreTokens()) {
                    first = false;
                    ident = ident.replace('-', ' ');
                    if (lastChild) {
                        ident = ident.substring(0, ident.length() - 4) + "    ";
                    } else {
                        ident = ident.substring(0, ident.length() - 1) + " ";
                    }
                }
            }
            if (first) {
                ident = ident.replace('-', ' ');
                if (lastChild) {
                    ident = ident.substring(0, ident.length() - 4) + "    ";
                }
            }
            ident += "  |-";
            Iterator<Group> i = getChildren().iterator();
            while (i.hasNext()) {
                Group e = i.next();
                e.dump(result, ident, !i.hasNext());
            }
        } finally {
            dumping = false;
        }
    }

    @Override
    public String toString() {
        if (getChildren().isEmpty()) {
            return String.valueOf(value);
        } else {
            StringBuilder result = new StringBuilder();
            result.append(value);
            result.append("[");
            Iterator<Group> i = getChildren().iterator();
            while (i.hasNext()) {
                Group e = i.next();
                result.append(e.toString());
                if (i.hasNext()) {
                    result.append(",");
                }
            }
            result.append("]");
            return result.toString();
        }
    }

}
