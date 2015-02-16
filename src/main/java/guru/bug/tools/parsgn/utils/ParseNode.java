/*
 * Copyright (c) 2015 Dimitrijs Fedotovs http://www.bug.guru
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.utils;

import guru.bug.tools.parsgn.processing.Position;

import java.util.Collections;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ParseNode {
    private final String name;
    private final List<ParseNode> children;
    private final String value;
    private final Position start;
    private final Position end;

    public ParseNode(String name, String value, List<ParseNode> children, Position start, Position end) {
        this.name = name;
        this.value = value;
        this.children = children == null ? Collections.<ParseNode>emptyList() : Collections.unmodifiableList(children);
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public List<ParseNode> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        toString("", result);
        return result.toString();
    }

    private void toString(String level, StringBuilder result) {
        result.append(level).append(name);
        if (value != null) {
            result.append('=').append(value);
        }
        result.append('\n');
        if (!children.isEmpty()) {
            children.forEach(n -> n.toString(level + "   ", result));
        }
    }

    public Position getStart() {
        return start;
    }

    public Position getEnd() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ParseNode parseNode = (ParseNode) o;

        return children.equals(parseNode.children)
                && end.equals(parseNode.end)
                && name.equals(parseNode.name)
                && start.equals(parseNode.start)
                && !(value != null
                ? !value.equals(parseNode.value)
                : parseNode.value != null);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + children.hashCode();
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + start.hashCode();
        result = 31 * result + end.hashCode();
        return result;
    }
}
