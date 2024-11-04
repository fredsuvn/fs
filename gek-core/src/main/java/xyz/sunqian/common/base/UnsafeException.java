package xyz.sunqian.common.base;

/**
 * Super exception for unsafe.
 *
 * @author sunq62
 */
public class UnsafeException extends JieException {

    /**
     * Empty constructor.
     */
    protected UnsafeException() {
        super();
    }

    /**
     * Constructs with message.
     *
     * @param message the message
     */
    protected UnsafeException(String message) {
        super(message);
    }

    /**
     * Constructs with message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    protected UnsafeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with cause.
     *
     * @param cause the cause
     */
    protected UnsafeException(Throwable cause) {
        super(cause);
    }
}
