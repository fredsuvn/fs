package xyz.sunqian.common.base;

/**
 * This exception should be thrown if and only if execution reaches a logically unreachable point.
 *
 * @author sunqian
 */
public class UnreachablePointException extends JieException {

    /**
     * Empty constructor.
     */
    public UnreachablePointException() {
        super();
    }

    /**
     * Constructs with message.
     *
     * @param message the message
     */
    public UnreachablePointException(String message) {
        super(message);
    }

    /**
     * Constructs with message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnreachablePointException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with cause.
     *
     * @param cause the cause
     */
    public UnreachablePointException(Throwable cause) {
        super(cause);
    }
}
