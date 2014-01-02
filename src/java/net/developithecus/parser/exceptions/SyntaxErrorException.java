package net.developithecus.parser.exceptions;

import net.developithecus.parser.Position;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class SyntaxErrorException extends ParsingException {
    private final Position position;

    public SyntaxErrorException(Position position) {
        super(String.format("Line %d: syntax error at or near col %d", position.getRow(), position.getCol()));
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
