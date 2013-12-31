package net.developithecus.parser.exceptions;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class InternalParsingException extends ParsingException {
    public InternalParsingException(String message) {
        super(message);
    }

    public InternalParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
