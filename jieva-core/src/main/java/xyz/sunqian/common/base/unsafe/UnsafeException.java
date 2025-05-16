package xyz.sunqian.common.base.unsafe;

import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * Runtime exception for unsafe.
 *
 * @author sunqian
 */
public class UnsafeException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public UnsafeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public UnsafeException(String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnsafeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public UnsafeException(Throwable cause) {
        super(cause);
    }
}
