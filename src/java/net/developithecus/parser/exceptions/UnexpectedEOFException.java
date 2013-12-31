package net.developithecus.parser.exceptions;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.31.12
 * @since 1.0
 */
public class UnexpectedEOFException extends ParsingException {
    public UnexpectedEOFException() {
        this("Unexpected End-of-File");
    }

    public UnexpectedEOFException(String message) {
        super(message);
    }

    public UnexpectedEOFException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedEOFException(Throwable cause) {
        super(cause);
    }
}
