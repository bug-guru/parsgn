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
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package guru.bug.tools.parsgn.utils;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public final class ParseTreeUtils {

    public static void walk(ParseNode root, boolean skipRoot, ParseNodeVisitor visitor) {
        Deque<NodeHolder> stack = new LinkedList<>();
        stack.push(new NodeHolder(root, skipRoot, true));
        boolean terminate = false;
        do {
            NodeHolder current = stack.peek();
            if (!terminate && current.iterator.hasNext()) {
                ParseNode node = current.iterator.next();
                ParseNodeVisitResult result = visitor.startNode(node);
                switch (result) {
                    case CONTINUE:
                        stack.push(new NodeHolder(node, false, false));
                        break;
                    case SKIP_SUBTREE:
                        visitor.endNode(node);
                        break;
                    case TERMINATE:
                        visitor.endNode(node);
                        terminate = true;
                        break;
                    default:
                        throw new IllegalStateException("ParserNodeVisitResult." + result + " isn't supported.");
                }
            } else if (stack.pop().node != null) {
                visitor.endNode(current.node);
            }
        } while (!stack.isEmpty());
    }

    public static void serialize(ParseNode root, final Writer out) throws IOException {
        walk(root, false, new ParseNodeVisitor() {
            int indent = 0;

            @Override
            public ParseNodeVisitResult startNode(ParseNode node) {
                try {
                    printNode(out, node, indent);
                    indent++;
                    return ParseNodeVisitResult.CONTINUE;
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }

            @Override
            public void endNode(ParseNode node) {
                indent--;
            }
        });
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

    private static class NodeHolder {
        final ParseNode node;
        final Iterator<ParseNode> iterator;

        NodeHolder(ParseNode node, boolean skipRoot, boolean isRoot) {
            if (isRoot) {
                this.node = null;
                this.iterator = skipRoot ? node.getChildren().iterator() : Collections.singleton(node).iterator();
            } else {
                this.node = skipRoot ? null : node;
                this.iterator = node.getChildren().iterator();
            }
        }
    }
}
