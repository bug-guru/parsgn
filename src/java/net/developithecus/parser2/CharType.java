package net.developithecus.parser2;

/**
 *
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
    };

    public abstract boolean apply(int codePoint);
}
