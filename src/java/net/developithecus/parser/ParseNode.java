/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.developithecus.net
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

package net.developithecus.parser;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.5.12
 * @since 1.0
 */
public class ParseNode {
    private final String name;
    private final Position beginPosition;
    private final int length;
    private final List<ParseNode> children;
    private final String value;

    public ParseNode(String name, Position beginPosition, int length, String value, List<ParseNode> children) {
        this.name = name;
        this.beginPosition = beginPosition;
        this.length = length;
        this.value = value;
        this.children = children == null ? null : Collections.unmodifiableList(children);
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

    public Position getBeginPosition() {
        return beginPosition;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(name);
        if (value != null) {
            result.append('=').append(value);
        }
        if (children != null && !children.isEmpty()) {
            result.append(children);
        }
        return result.toString();
    }

    public boolean isLeaf() {
        return children == null || children.isEmpty();
    }
}
