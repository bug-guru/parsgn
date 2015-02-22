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
import guru.bug.tools.parsgn.exceptions.SyntaxErrorException;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.expr.calc.CalculationContext;
import guru.bug.tools.parsgn.processing.debug.Debugger;
import guru.bug.tools.parsgn.processing.debug.StackElement;
import guru.bug.tools.parsgn.processing.debug.State;
import guru.bug.tools.parsgn.utils.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class ParsingContext<T> implements CalculationContext {
    private static final Logger logger = Logger.getLogger(ParsingContext.class.getSimpleName());
    private final Deque<Holder> stack = new LinkedList<>();
    private final ResultBuilder<T> builder;
    private final CodePointSource source;
    private final List<Expression.ExpressionChecker> failedExpressions = new ArrayList<>();
    private Debugger debugger;

    public ParsingContext(Expression root, ResultBuilder<T> builder, CodePointSource source) {
        this.builder = builder;
        this.source = source;
        Holder holder = new ResultHolder();
        stack.push(holder);
        pushExpression(root);
    }

    public void parse(Debugger debugger) throws IOException, ParsingException {
        this.debugger = debugger;
        if (debugger != null) {
            debugger.onStart();
        }
        while (!builder.isFinished()) {
            next();
        }
        if (debugger != null) {
            debugger.onFinish();
        }
    }

    public void next() throws ParsingException, IOException {
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while (stack.peek().checker instanceof Expression.BranchExpressionChecker) {
            nextExpr = ((Expression.BranchExpressionChecker) stack.peek().checker).next();
            pushExpression(nextExpr);
        }
    }

    private void pushExpression(Expression nextExpr) {
        Holder holder = new Holder();
        stack.push(holder);
        holder.checker = nextExpr.checker(this);
        source.mark();
        holder.start = source.getNextPos();
    }

    private void process() throws ParsingException, IOException {
        Position lastPos = source.getNextPos();
        int codePoint = source.getNext();
        Holder leafHolder = stack.peek();
        Expression.LeafExpressionChecker leaf = ((Expression.LeafExpressionChecker) leafHolder.checker);
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "Expr stack: {0}", stack);
            logger.log(Level.FINER, "checking: {0}; codePoint {1} at: {2}", new Object[]{StringUtils.codePointToString(codePoint), codePoint, lastPos});
        }
        ResultType prevResult = leaf.check(codePoint);
        afterCheck(prevResult);
        Expression.BranchExpressionChecker branchChecker = null;
        loop:
        while (true) {
            logger.log(Level.FINER, "result: {0}", prevResult);
            switch (prevResult) {
                case CONTINUE:
                    break loop;
                case COMMIT:
                    failedExpressions.clear();
                    boolean ignoreFlag = isIgnoreFlagSet(leaf, branchChecker);
                    boolean hiddenFlag = isHiddenFlagSet(leaf, branchChecker);
                    if (ignoreFlag || hiddenFlag) {
                        if (hiddenFlag) {
                            source.removeMark();
                        } else {
                            source.rewind();
                        }
                        stack.pop();
                    } else {
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
                    }
                    break;
                case ROLLBACK:
                    failedExpressions.add(stack.peek().checker);
                case ROLLBACK_OPTIONAL:
                    stack.pop();
                    source.rewind();
                    if (stack.size() == 1) {
                        logger.log(Level.SEVERE, "Syntax error: {0}, {1}", new Object[]{source.getMaxPos(), failedExpressions});
                        throw new SyntaxErrorException(source.getMaxPos(), failedExpressions);
                    }
                    break;
                default:
                    throw new ParsingException("unknown result: " + prevResult);
            }
            if (builder.isFinished()) {
                break;
            }
            Holder holder = stack.peek();
            branchChecker = (Expression.BranchExpressionChecker) holder.checker;
            prevResult = branchChecker.check(prevResult);
            afterCheck(prevResult);
            logger.log(Level.FINER, "Expr stack: {0}", stack);
        }
    }

    private void afterCheck(ResultType prevResult) {
        if (debugger != null) {
            State state = buildPublicState(prevResult);
            debugger.afterCheck(state);
        }
    }

    private State buildPublicState(ResultType prevResult) {
        List<StackElement> result = stack.stream()
                .map(n -> new StackElement(n.start, n.toString()))
                .collect(Collectors.toList());
        return new State(result, source.getLastPos(), prevResult);
    }

    private boolean isIgnoreFlagSet(Expression.LeafExpressionChecker leaf, Expression.BranchExpressionChecker branchChecker) {
        return branchChecker == null && leaf.isIgnored() || branchChecker != null && branchChecker.isIgnored();
    }

    private boolean isHiddenFlagSet(Expression.LeafExpressionChecker leaf, Expression.BranchExpressionChecker branchChecker) {
        return branchChecker == null && leaf.isHidden() || branchChecker != null && branchChecker.isHidden();
    }

    @Override
    public Object getValue(String name) {
        Object result = null;
        for (Holder h : stack) {
            result = h.variables.get(name);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public void setValue(String name, Object value) {
        stack.peek().variables.put(name, value);
    }

    private class Holder {
        Map<String, Object> variables = new HashMap<>();
        Expression.ExpressionChecker checker;
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
