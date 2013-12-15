package net.developithecus.parser;

/**
 * @author <a href="mailto:dima@fedoto.ws">Dimitrijs Fedotovs</a>
 * @version 13.16.12
 * @since 1.0
 */
public class FlowCompleteException extends ExpressionCheckerException {
    public FlowCompleteException() {
    }

    public FlowCompleteException(String message) {
        super(message);
    }

    public FlowCompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public FlowCompleteException(Throwable cause) {
        super(cause);
    }

    protected FlowCompleteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
