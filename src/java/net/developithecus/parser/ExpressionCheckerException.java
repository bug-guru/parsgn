package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.16.12
 * @since 1.0
 */
public class ExpressionCheckerException extends Exception {
    public ExpressionCheckerException() {
    }

    public ExpressionCheckerException(String message) {
        super(message);
    }

    public ExpressionCheckerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpressionCheckerException(Throwable cause) {
        super(cause);
    }

    protected ExpressionCheckerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
