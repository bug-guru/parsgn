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

import guru.bug.tools.parsgn.expr.CharType;
import guru.bug.tools.parsgn.utils.StringUtils;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public class CodePoint {
    private final int codePoint;
    private final Position position;
    private boolean newLine;

    public CodePoint(CodePoint prev, int codePoint) {
        this.codePoint = codePoint;
        if (prev != null && prev.codePoint == '\r' && codePoint == '\n') {
            prev.newLine = false;
        }
        this.newLine = CharType.LINE_SEPARATOR.apply(codePoint);
        if (prev == null) {
            position = new Position(1, 1);
        } else if (prev.newLine) {
            position = Position.newRow(prev.position);
        } else {
            position = Position.newCol(prev.position);
        }
    }

    public int getCodePoint() {
        return codePoint;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isNewLine() {
        return newLine;
    }

    @Override
    public String toString() {
        switch (codePoint) {
            case '\\':
                return "\\";
            case '\'':
                return "\'";
            case '\"':
                return "\"";
            case '\t':
                return "\\t       ";
            default:
                return StringUtils.codePointToString(codePoint);
        }
    }
}
