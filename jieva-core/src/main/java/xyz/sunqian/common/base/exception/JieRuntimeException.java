package xyz.sunqian.common.base.exception;

/**
 * Root runtime exception for Jieva.
 *
 * @author sunqian
 */
public class JieRuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public JieRuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JieRuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JieRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JieRuntimeException(Throwable cause) {
        super(cause);
    }
}
