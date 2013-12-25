package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.25.12
 * @since 1.0
 */
public class SourceEvent {
    private final SourceEventType type;
    private final int offset;
    private final int codePoint;

    public SourceEvent(SourceEventType type, int offset, int codePoint) {
        this.type = type;
        this.offset = offset;
        this.codePoint = codePoint;
    }

    public SourceEvent(SourceEventType type, int offset) {
        this(type, offset, 0);
    }

    public SourceEventType getType() {
        return type;
    }

    public int getOffset() {
        return offset;
    }

    public int getCodePoint() {
        return codePoint;
    }
}
