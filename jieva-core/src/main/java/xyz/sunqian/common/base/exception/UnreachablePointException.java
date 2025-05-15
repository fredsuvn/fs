package xyz.sunqian.common.base.exception;

/**
 * This exception should be thrown if and only if execution reaches a logically unreachable point.
 *
 * @author sunqian
 */
public class UnreachablePointException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public UnreachablePointException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public UnreachablePointException(String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnreachablePointException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public UnreachablePointException(Throwable cause) {
        super(cause);
    }
}
