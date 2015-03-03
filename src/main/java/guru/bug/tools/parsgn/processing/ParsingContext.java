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
import guru.bug.tools.parsgn.exceptions.ParsingException;
import guru.bug.tools.parsgn.exceptions.SyntaxErrorException;
import guru.bug.tools.parsgn.expr.Expression;
import guru.bug.tools.parsgn.expr.calc.CalculationContext;
import guru.bug.tools.parsgn.processing.debug.DebugFrame;
import guru.bug.tools.parsgn.processing.debug.DebugInjection;
import guru.bug.tools.parsgn.processing.debug.StackElement;
import guru.bug.tools.parsgn.utils.StringUtils;

import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
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
    private final Deque<Holder<T>> stack = new LinkedList<>();
    private final ResultBuilder<T> builder;
    private final CodePointSource source;
    private DebugInjection debugInjection;

    public ParsingContext(Expression root, ResultBuilder<T> builder, CodePointSource source) {
        this.builder = builder;
        this.source = source;
        stack.push(new ResultHolder());
        pushExpression(root);
    }

    public void parse(DebugInjection debugInjection) throws IOException, ParsingException {
        this.debugInjection = debugInjection;
        if (debugInjection != null) {
            debugInjection.onStart();
        }
        while (!builder.isFinished()) {
            next();
        }
        if (debugInjection != null) {
            debugInjection.onFinish();
        }
    }

    public void next() throws ParsingException, IOException {
        completePath();
        process();
    }

    private void completePath() {
        Expression nextExpr;
        while (stack.peek().getChecker() instanceof Expression.BranchExpressionChecker) {
            nextExpr = stack.peek().getBranchExpressionChecker().next();
            pushExpression(nextExpr);
        }
    }

    private void pushExpression(Expression nextExpr) {
        Holder<T> holder = new Holder<>(builder);
        stack.push(holder);
        holder.setChecker(nextExpr.checker(this));
        source.mark();
        holder.setStart(source.getNextPos());
    }

    private void process() throws ParsingException, IOException {
        Position lastPos = source.getNextPos();
        int codePoint = source.getNext();
        Holder<T> leafHolder = stack.peek();
        Expression.LeafExpressionChecker leafChecker = leafHolder.getLeafExpressionChecker();
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, "checking: {0}; codePoint {1} at: {2}",
                    new Object[]{StringUtils.codePointToString(codePoint), codePoint, lastPos});
            logger.log(Level.FINER, "Expr stack: {0}", stackToString(stack));
        }

        Result result;
        if (codePoint == -1) {
            result = ResultType.MISMATCH.andRollback();
        } else {
            result = leafChecker.check(codePoint);
        }
        ResultType prevResult = result.getBasicResult();
        afterCheck(prevResult);
        result.apply(stack, source);
        logger.log(Level.FINER, "result: {0}\n", prevResult);

        while (prevResult != ResultType.CONTINUE) {
            if (stack.size() == 1 && !builder.isFinished()) {
                throw new SyntaxErrorException(source.getMaxPos());
            }
            if (builder.isFinished()) {
                break;
            }

            Holder holder = stack.peek();
            Expression.BranchExpressionChecker branchChecker = holder.getBranchExpressionChecker();

            if (logger.isLoggable(Level.FINER)) {
                logger.log(Level.FINER, "Expr stack: {0}", stackToString(stack));
            }
            result = branchChecker.check(prevResult);
            prevResult = result.getBasicResult();
            afterCheck(prevResult);
            result.apply(stack, source);

            logger.log(Level.FINER, "result: {0}\n", prevResult);
        }
    }

    private String stackToString(Deque<Holder<T>> stack) {
        StringBuilder result = new StringBuilder(1024);
        result.append('\n');
        stack.forEach(t -> result.append('\t').append(t).append('\n'));
        result.setLength(result.length() - 1);
        return result.toString();
    }

    private void afterCheck(ResultType prevResult) {
        if (debugInjection != null) {
            DebugFrame frame = buildDebugFrame(prevResult);
            debugInjection.afterCheck(frame);
        }
    }

    private DebugFrame buildDebugFrame(ResultType prevResult) {
        List<StackElement> result = stack.stream()
                .map(n -> new StackElement(n.getStart(), n.getChecker()))
                .collect(Collectors.toList());
        return new DebugFrame(result, source.getNextPos(), prevResult);
    }

    @Override
    public Object getValue(String name) {
        Object result = null;
        for (Holder h : stack) {
            result = h.getVariables().get(name);
            if (result != null) {
                break;
            }
        }
        return result;
    }

    @Override
    public void setValue(String name, Object value) {
        stack.peek().getVariables().put(name, value);
    }

    private class ResultHolder extends Holder<T> {

        private ResultHolder() {
            super(builder);
        }

        @Override
        public void commitNode(String nodeName, Holder<T> child) throws ParsingException {
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
