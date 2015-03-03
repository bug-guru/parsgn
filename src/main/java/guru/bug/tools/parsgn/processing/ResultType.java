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

import java.util.Deque;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
// TODO consider rename to Results
public enum ResultType {
    CONTINUE,
    MATCH,
    MISMATCH,
    MISMATCH_BUT_OPTIONAL;

    public Result and(ResultAction action) {
        return new Result(this, action);
    }

    public Result andRollback() {
        return and(ResultType::rollback);
    }

    public Result andSkip() {
        return and(ResultType::skip);
    }

    public Result andCommitGroup(String name) {
        return and(new ResultAction() {
            @Override
            public <T> void apply(Deque<Holder<T>> stack, CodePointSource src) {
                ResultType.commitGroup(stack, src, name);
            }
        });
    }

    public Result andCommitString(String value) {
        return and(new ResultAction() {
            @Override
            public <T> void apply(Deque<Holder<T>> stack, CodePointSource src) {
                ResultType.commitValue(stack, src, value);
            }
        });
    }

    public Result andCommitCodePoint(int codePoint) {
        return and(new ResultAction() {
            @Override
            public <T> void apply(Deque<Holder<T>> stack, CodePointSource src) {
                ResultType.commitCodePoint(stack, src, codePoint);
            }
        });
    }

    public Result andMerge() {
        return and(ResultType::merge);
    }

    public Result noAction() {
        return and(ResultType::empty);
    }

    public static <T> void rollback(Deque<Holder<T>> stack, CodePointSource source) {
        stack.pop();
        source.rewind();
    }

    public static <T> void skip(Deque<Holder<T>> stack, CodePointSource source) {
        stack.pop();
        source.removeMark();
    }

    public static <T> void commitGroup(Deque<Holder<T>> stack, CodePointSource source, String name) {
        Holder<T> top = stack.pop();
        source.removeMark();
        top.setEnd(source.getNextPos());
        stack.peek().commitNode(name, top);
    }

    public static <T> void commitValue(Deque<Holder<T>> stack, CodePointSource source, String value) {
        stack.pop();
        source.removeMark();
        stack.peek().getCommittedValue().append(value);
    }

    public static <T> void commitCodePoint(Deque<Holder<T>> stack, CodePointSource source, int codePoint) {
        stack.pop();
        source.removeMark();
        stack.peek().getCommittedValue().appendCodePoint(codePoint);
    }


    public static <T> void merge(Deque<Holder<T>> stack, CodePointSource source) {
        Holder<T> top = stack.pop();
        source.removeMark();
        stack.peek().merge(top);
    }

    public static <T> void empty(Deque<Holder<T>> stack, CodePointSource source) {

    }
}
