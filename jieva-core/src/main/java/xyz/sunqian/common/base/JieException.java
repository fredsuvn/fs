package xyz.sunqian.common.base;

/**
 * Super exception for Jieva.
 *
 * @author sunq62
 */
public class JieException extends RuntimeException {

    /**
     * Empty constructor.
     */
    protected JieException() {
        super();
    }

    /**
     * Constructs with message.
     *
     * @param message the message
     */
    protected JieException(String message) {
        super(message);
    }

    /**
     * Constructs with message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    protected JieException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with cause.
     *
     * @param cause the cause
     */
    protected JieException(Throwable cause) {
        super(cause);
    }
}
