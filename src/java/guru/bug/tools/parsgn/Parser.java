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

package guru.bug.tools.parsgn;

import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.ReferenceExpression;

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Dimitrijs Fedotovs <dima@fedoto.ws>
 * @version 1.0.0
 * @since 1.0.0
 */
public class Parser {
    private final ReferenceExpression root;

    public Parser(Rule root) {
        this.root = new ReferenceExpression();
        this.root.setReference(root);
    }

    public void parse(Reader input, NodeTreeVisitor visitor) throws ParsingException, IOException {
        ParseTreeResultBuilder builder = new ParseTreeResultBuilder();
        parse(input, builder);
        ParseNode root = builder.getRoot();
        class NodeHolder {
            final ParseNode node;
            final Iterator<ParseNode> iterator;

            NodeHolder(ParseNode node) {
                this.node = node;
                this.iterator = node.getChildren().iterator();
            }
        }
        Deque<NodeHolder> stack = new LinkedList<>();
        if (root.isLeaf()) {
            visitor.leafNode(root);
        } else {
            visitor.startNode(root);
            stack.push(new NodeHolder(root));
        }
        while (!stack.isEmpty()) {
            NodeHolder current = stack.peek();
            if (current.iterator.hasNext()) {
                ParseNode node = current.iterator.next();
                if (node.isLeaf()) {
                    visitor.leafNode(node);
                } else {
                    visitor.startNode(node);
                    stack.push(new NodeHolder(node));
                }
            } else {
                stack.pop();
                visitor.endNode(current.node);
            }
        }
    }

    public <T> void parse(Reader input, ResultBuilder<T> builder) throws ParsingException, IOException {
        CodePointSource src = new CodePointSource(input);
        ParsingContext<T> ctx = new ParsingContext<>(root, builder, src);
        ctx.parse();
    }

}
