package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

/**
 * This is the root runtime exception for this lib.
 *
 * @author sunqian
 */
public class KitvaRuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public KitvaRuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public KitvaRuntimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public KitvaRuntimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public KitvaRuntimeException(@Nullable Throwable cause) {
        super(cause);
    }
}
