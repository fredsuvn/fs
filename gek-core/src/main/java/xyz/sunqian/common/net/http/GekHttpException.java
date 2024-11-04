package xyz.sunqian.common.net.http;

/**
 * Exception for http.
 *
 * @author fredsuvn
 */
public class GekHttpException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekHttpException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekHttpException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekHttpException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekHttpException(Throwable cause) {
        super(cause);
    }
}
