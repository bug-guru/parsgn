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

package guru.bug.tools.parsgn.processing;

import guru.bug.tools.parsgn.ResultBuilder;
import guru.bug.tools.parsgn.exceptions.InternalParsingException;
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.expr.Expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class Holder<T> {
    private final Map<String, Object> variables = new HashMap<>();
    private Expression.ExpressionChecker checker;
    private List<T> committedNodes;
    private StringBuilder committedValue;
    private Position start;
    private Position end;
    private final ResultBuilder<T> builder;

    protected Holder(ResultBuilder<T> builder) {
        this.builder = builder;
    }

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

    public void commitNode(String nodeName, Holder<T> child) throws ParsingException {
        T node = wrapNode(nodeName, child);
        addNode(node);
    }

    protected T wrapNode(String nodeName, Holder<T> child) {
        if (child == null) {
            throw new IllegalArgumentException("child cannot be null");
        }
        boolean emptyNodes = child.committedNodes == null || child.committedNodes.isEmpty();
        boolean emptyValue = child.committedValue == null || child.committedValue.isEmpty();
        String value = emptyValue ? null : child.committedValue.toString();
        List<T> nodes = emptyNodes ? null : child.committedNodes;
        return builder.createNode(nodeName, value, nodes, child.start, child.end);
    }

    public void merge(Holder<T> another) throws ParsingException {
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

    public Position getStart() {
        return start;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getEnd() {
        return end;
    }

    public void setEnd(Position end) {
        this.end = end;
    }

    public Expression.ExpressionChecker getChecker() {
        return checker;
    }

    public void setChecker(Expression.ExpressionChecker checker) {
        this.checker = checker;
    }

    public Expression.LeafExpressionChecker getLeafExpressionChecker() {
        return (Expression.LeafExpressionChecker) checker;
    }

    public Expression.BranchExpressionChecker getBranchExpressionChecker() {
        return (Expression.BranchExpressionChecker) checker;
    }

    public Expression.ExpressionChecker getExpressionChecker() {
        return checker;
    }
}
