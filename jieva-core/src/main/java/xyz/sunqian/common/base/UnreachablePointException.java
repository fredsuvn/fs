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
    protected UnreachablePointException() {
        super();
    }

    /**
     * Constructs with message.
     *
     * @param message the message
     */
    protected UnreachablePointException(String message) {
        super(message);
    }

    /**
     * Constructs with message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    protected UnreachablePointException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with cause.
     *
     * @param cause the cause
     */
    protected UnreachablePointException(Throwable cause) {
        super(cause);
    }
}
