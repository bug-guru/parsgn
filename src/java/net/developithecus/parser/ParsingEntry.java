package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.30.12
 * @since 1.0
 */
public class ParsingEntry {
    private Position position;
    private final int codePoint;

    public ParsingEntry(Position position, int codePoint) {
        this.position = position;
        this.codePoint = codePoint;
    }

    public Position getPosition() {
        return position;
    }

    public int getCodePoint() {
        return codePoint;
    }

    @Override
    public String toString() {
        return "ParsingEntry{" +
                "pos=" + position +
                ", codePoint=" + codePoint +
                (codePoint > 0 ? ", char=" + (char) codePoint : "") +
                '}';
    }
}
