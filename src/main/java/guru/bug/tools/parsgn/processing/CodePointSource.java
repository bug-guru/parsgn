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
    private final Deque<CodePointNode> bookmarks = new LinkedList<>();
    private final Reader reader;
    private CodePointNode next;
    private Position max;
    private CodePointNode last;

    public CodePointSource(Reader reader) throws IOException {
        this.reader = reader;
        prepareNext();
    }

    private void prepareNext() throws IOException {
        if (this.next == null) {
            readNext();
        } else {
            if (this.next.rightNode == null) {
                readNext();
            }
            this.next = this.next.rightNode;
        }
    }

    private void readNext() throws IOException {
        int ch1 = reader.read();
        while (true) {
            if (ch1 != -1 && Character.isHighSurrogate((char) ch1)) {
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
        CodePoint cp = new CodePoint(last == null ? null : last.cp, codePoint);
        CodePointNode tmp = new CodePointNode();
        tmp.cp = cp;
        if (next == null) {
            next = tmp;
        } else {
            last.rightNode = tmp;
        }
        last = tmp;
    }

    private int toCodePoint(int ch1, int ch2) {
        if (Character.isSurrogatePair((char) ch1, (char) ch2)) {
            return Character.toCodePoint((char) ch1, (char) ch2);
        } else {
            return -1;
        }
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
        return getNextCodePoint().getCodePoint();
    }

    public CodePoint getNextCodePoint() throws IOException {
        CodePoint result = next.cp;
        updateMax();
        prepareNext();
        return result;
    }

    private void updateMax() {
        max = Position.max(max, next.cp.getPosition());
    }

    public Position getMaxPos() {
        return max;
    }

    public Position getNextPos() {
        return next.cp.getPosition();
    }

    public Position getLastPos() {
        return last == null ? null : last.cp.getPosition();
    }

    private class CodePointNode {
        CodePoint cp;
        CodePointNode rightNode;
    }
}
