/*
 * Copyright (c) 2014 Dimitrijs Fedotovs http://www.bug.guru
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

import guru.bug.tools.parsgn.exceptions.InternalParsingException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.exceptions.SyntaxErrorException;
import guru.bug.tools.parsgn.expr.BranchExpressionChecker;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.expr.ExpressionChecker;
import guru.bug.tools.parsgn.expr.LeafExpressionChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ParsingContext<T> {
    private final Deque<Holder> stack = new LinkedList<>();
    private final ResultBuilder<T> builder;
    private final CodePointSource source;

    public ParsingContext(Expression root, ResultBuilder<T> builder, CodePointSource source) {
        this.builder = builder;
        this.source = source;
        Holder holder = new ResultHolder();
        stack.push(holder);
        pushExpression(root);
    }

    public void parse() throws IOException, ParsingException {
        while (!builder.isFinished()) {
            next();
        }
    }

    public void next() throws ParsingException, IOException {
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while (stack.peek().checker instanceof BranchExpressionChecker) {
            nextExpr = ((BranchExpressionChecker) stack.peek().checker).next();
            pushExpression(nextExpr);
        }
    }

    private void pushExpression(Expression nextExpr) {
        ExpressionChecker nextChecker = nextExpr.checker();
        Holder holder = new Holder();
        holder.checker = nextChecker;
        stack.push(holder);
        source.mark();
        holder.start = source.getNextPos();
    }

    private void process() throws ParsingException, IOException {
        int codePoint = source.getNext();
        Holder leafHolder = stack.peek();
        LeafExpressionChecker leaf = ((LeafExpressionChecker) leafHolder.checker);
        ResultType prevResult = leaf.check(codePoint);
        BranchExpressionChecker branchChecker = null;
        loop:
        while (true) {
            switch (prevResult) {
                case CONTINUE:
                    break loop;
                case COMMIT:
                    source.removeMark();
                    if (branchChecker == null) {
                        stack.pop();
                        leaf.commitResult(stack.peek().getCommittedValue());
                    } else if (branchChecker.getGroupName() == null) {
                        ParsingContext<T>.Holder top = stack.pop();
                        stack.peek().merge(top);
                    } else {
                        ParsingContext<T>.Holder top = stack.pop();
                        top.end = source.getNextPos();
                        stack.peek().commitNode(branchChecker.getGroupName(), top);
                    }
                    break;
                case ROLLBACK:
                case ROLLBACK_OPTIONAL:
                    stack.pop();
                    source.rewind();
                    if (stack.size() == 1) {
                        throw new SyntaxErrorException(source.getMaxPos());
                    }
                    break;
                default:
                    throw new ParsingException("unknown result: " + prevResult);
            }
            if (builder.isFinished()) {
                break;
            }
            Holder holder = stack.peek();
            branchChecker = (BranchExpressionChecker) holder.checker;
            prevResult = branchChecker.check(prevResult);
        }
    }

    private class Holder {
        ExpressionChecker checker;
        List<T> committedNodes;
        StringBuilder committedValue;
        Position start;
        Position end;

        protected void initValue() throws InternalParsingException {
            if (committedValue == null) {
                committedValue = new StringBuilder();
            }
        }

        protected void initNodes() throws InternalParsingException {
            if (committedNodes == null) {
                committedNodes = new ArrayList<>();
            }
        }

        protected void addNode(T node) throws InternalParsingException {
            initNodes();
            committedNodes.add(node);
        }

        protected void addNodes(List<T> nodes) {
            if (nodes == null || nodes.isEmpty()) {
                return;
            }
            if (committedNodes == null || committedNodes.isEmpty()) {
                committedNodes = nodes;
            } else {
                committedNodes.addAll(nodes);
            }
        }

        public void commitValue(StringBuilder builder) throws InternalParsingException {
            if (builder == null || builder.length() == 0) {
                return;
            }
            initValue();
            committedValue.append(builder);
        }

        public void commitNode(String nodeName, Holder child) throws ParsingException {
            T node = wrapNode(nodeName, child);
            addNode(node);
        }

        protected T wrapNode(String nodeName, Holder child) {
            if (child == null) {
                throw new IllegalArgumentException("child cannot be null");
            }
            boolean emptyNodes = child.committedNodes == null || child.committedNodes.isEmpty();
            boolean emptyValue = child.committedValue == null || child.committedValue.length() == 0;
            String value = emptyValue ? null : child.committedValue.toString();
            List<T> nodes = emptyNodes ? null : child.committedNodes;
            return builder.createNode(nodeName, value, nodes, child.start, child.end);
        }

        public void merge(Holder another) throws ParsingException {
            commitValue(another.committedValue);
            addNodes(another.committedNodes);
        }

        @Override
        public String toString() {
            return checker.toString();
        }

        public StringBuilder getCommittedValue() throws InternalParsingException {
            initValue();
            return committedValue;
        }
    }

    private class ResultHolder extends Holder {

        @Override
        public void commitNode(String nodeName, Holder child) throws ParsingException {
            T node = wrapNode(nodeName, child);
            builder.committedRoot(node);
            builder.setFinished(true);
        }

        @Override
        public String toString() {
            return "ROOT";
        }
    }
}
