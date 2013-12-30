package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public class ParsingEntry {
    private final int row;
    private final int col;
    private final int codePoint;

    public ParsingEntry(int row, int col, int codePoint) {
        this.row = row;
        this.col = col;
        this.codePoint = codePoint;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getCodePoint() {
        return codePoint;
    }

    @Override
    public String toString() {
        return "ParsingEntry{" +
                "row=" + row +
                ", col=" + col +
                ", codePoint=" + codePoint +
                (codePoint > 0 ? ", char=" + (char) codePoint : "") +
                '}';
    }
}
