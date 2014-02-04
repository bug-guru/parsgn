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

package net.developithecus.tools.spargn;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 14.29.1
 * @since 1.0
 */
public class ParseTreeSerialization {

    public static void serialize(ParseNode root, final Writer out) throws IOException {
        Deque<Iterator<ParseNode>> stack = new LinkedList<>();
        stack.push(Collections.singletonList(root).iterator());
        while (!stack.isEmpty()) {
            Iterator<ParseNode> current = stack.peek();
            if (current.hasNext()) {
                ParseNode node = current.next();
                printNode(out, node, stack.size() - 1);
                stack.push(node.getChildren().iterator());
            } else {
                stack.pop();
            }
        }
    }

    private static void printNode(Writer out, ParseNode node, int indent) throws IOException {
        out.append(String.format("%08x %08x %08x %08x %08x ", indent,
                node.getStart().getRow(),
                node.getStart().getCol(),
                node.getEnd().getRow(),
                node.getEnd().getCol()));
        for (int i = 0; i < indent; i++) {
            out.append("  ");
        }
        out.append('"');
        StringUtils.escape(out, node.getName());
        out.append('"');
        if (node.getValue() != null) {
            out.append(" \"");
            StringUtils.escape(out, node.getValue());
            out.append('"');
        }
        out.write('\n');
    }
}
