package xyz.sunqian.common.net;

/**
 * Exception for network.
 *
 * @author fredsuvn
 */
public class GekNetException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekNetException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekNetException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekNetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekNetException(Throwable cause) {
        super(cause);
    }
}
