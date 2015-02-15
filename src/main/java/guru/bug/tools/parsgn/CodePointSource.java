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

import guru.bug.tools.parsgn.expr.CharType;

import java.io.IOException;
import java.io.Reader;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CodePointSource {
    private final Deque<CodePoint> bookmarks = new LinkedList<>();
    private final Reader reader;
    private CodePoint next;
    private Position max;
    private CodePoint last;

    public CodePointSource(Reader reader) throws IOException {
        this.reader = reader;
        prepareNext();
    }

    private void prepareNext() throws IOException {
        if (this.next == null) {
            readNext();
        } else {
            if (this.next.next == null) {
                readNext();
            }
            this.next = this.next.next;
        }
    }

    private void readNext() throws IOException {
        int ch1 = reader.read();
        while (true) {
            if (ch1 != -1 && isHighSurrogate(ch1)) {
                int ch2 = reader.read();
                int cp = toCodePoint(ch1, ch2);
                if (cp == -1) {
                    addNext(ch1);
                    ch1 = ch2;
                    continue;
                } else {
                    addNext(cp);
                }
            } else {
                addNext(ch1);
            }
            break;
        }
    }

    private void addNext(int codePoint) {
        if (codePoint == '\r') {
            codePoint = '\n';
        }
        CodePoint tmp = new CodePoint(last, codePoint);
        if (next == null) {
            next = tmp;
            last = tmp;
        } else {
            last.next = tmp;
            last = tmp;
        }
    }

    private int toCodePoint(int ch1, int ch2) {
        if (ch1 == '\r' && ch2 == '\n') {
            return '\n';
        } else if (Character.isSurrogatePair((char) ch1, (char) ch2)) {
            return Character.toCodePoint((char) ch1, (char) ch2);
        } else {
            return -1;
        }
    }

    private boolean isHighSurrogate(int ch) {
        return ch == '\r' || Character.isHighSurrogate((char) ch);
    }

    public void mark() {
        bookmarks.push(next);
    }

    public void rewind() {
        next = bookmarks.pop();
    }

    public void removeMark() {
        bookmarks.pop();
    }

    public int getNext() throws IOException {
        int result = next.codePoint;
        updateMax();
        prepareNext();
        return result;
    }

    private void updateMax() {
        max = Position.max(max, next.pos);
    }

    public Position getMaxPos() {
        return max;
    }

    public Position getNextPos() {
        return next.pos;
    }

    public Position getLastPos() {
        return last == null ? null : last.pos;
    }

    private static class CodePoint {
        private final int codePoint;
        private final Position pos;
        private final boolean newLine;
        private CodePoint next;

        public CodePoint(CodePoint prev, int codePoint) {
            this.codePoint = codePoint;
            this.newLine = CharType.LINE_SEPARATOR.apply(codePoint);
            if (prev == null) {
                pos = new Position(1, 1);
            } else if (prev.newLine) {
                pos = Position.newRow(prev.pos);
            } else {
                pos = Position.newCol(prev.pos);
            }
        }

        @Override
        public String toString() {
            return "CodePoint{" +
                    "codePoint=" + codePoint +
                    ", pos=" + pos +
                    '}';
        }
    }
}
