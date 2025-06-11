package xyz.sunqian.common.net.data;

/**
 * Exception for data.
 *
 * @author fredsuvn
 */
public class GekDataException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public GekDataException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public GekDataException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public GekDataException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public GekDataException(Throwable cause) {
        super(cause);
    }
}
