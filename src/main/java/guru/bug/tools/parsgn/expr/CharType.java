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

package guru.bug.tools.parsgn.expr;

/**
 * @author Dimitrijs Fedotovs <a href="http://www.bug.guru">www.bug.guru</a>
 * @version 1.0
 * @since 1.0
 */
public enum CharType {
    ALPHABETIC {
        @Override
        public boolean apply(int codePoint) {
            return Character.isAlphabetic(codePoint);
        }
    },
    BASIC_MULTILINGUAL_PLANE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isBmpCodePoint(codePoint);
        }
    },
    DEFINED {
        @Override
        public boolean apply(int codePoint) {
            return Character.isDefined(codePoint);
        }
    },
    DIGIT {
        @Override
        public boolean apply(int codePoint) {
            return Character.isDigit(codePoint);
        }
    },
    IDENTIFIER_IGNORABLE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isIdentifierIgnorable(codePoint);
        }
    },
    IDEOGRAPHIC {
        @Override
        public boolean apply(int codePoint) {
            return Character.isIdeographic(codePoint);
        }
    },
    ISO_CONTROL {
        @Override
        public boolean apply(int codePoint) {
            return Character.isISOControl(codePoint);
        }
    },
    JAVA_IDENTIFIER_PART {
        @Override
        public boolean apply(int codePoint) {
            return Character.isJavaIdentifierPart(codePoint);
        }
    },
    JAVA_IDENTIFIER_START {
        @Override
        public boolean apply(int codePoint) {
            return Character.isJavaIdentifierStart(codePoint);
        }
    },
    LETTER {
        @Override
        public boolean apply(int codePoint) {
            return Character.isLetter(codePoint);
        }
    },
    LETTER_OR_DIGIT {
        @Override
        public boolean apply(int codePoint) {
            return Character.isLetterOrDigit(codePoint);
        }
    },
    LOWER_CASE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isLowerCase(codePoint);
        }
    },
    MIRRORED {
        @Override
        public boolean apply(int codePoint) {
            return Character.isMirrored(codePoint);
        }
    },
    SPACE_CHAR {
        @Override
        public boolean apply(int codePoint) {
            return Character.isSpaceChar(codePoint);
        }
    },
    SUPPLEMENTARY {
        @Override
        public boolean apply(int codePoint) {
            return Character.isSupplementaryCodePoint(codePoint);
        }
    },
    TITLE_CASE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isTitleCase(codePoint);
        }
    },
    UNICODE_IDENTIFIER_PART {
        @Override
        public boolean apply(int codePoint) {
            return Character.isUnicodeIdentifierPart(codePoint);
        }
    },
    UNICODE_IDENTIFIER_START {
        @Override
        public boolean apply(int codePoint) {
            return Character.isUnicodeIdentifierStart(codePoint);
        }
    },
    UPPER_CASE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isUpperCase(codePoint);
        }
    },
    VALID {
        @Override
        public boolean apply(int codePoint) {
            return Character.isValidCodePoint(codePoint);
        }
    },
    WHITESPACE {
        @Override
        public boolean apply(int codePoint) {
            return Character.isWhitespace(codePoint);
        }
    },
    LINE_SEPARATOR {
        @Override
        public boolean apply(int codePoint) {
            return codePoint == '\n' || codePoint == '\r';
        }
    },
    EOF {
        @Override
        public boolean apply(int codePoint) {
            return codePoint == -1;
        }
    };

    public abstract boolean apply(int codePoint);
}
