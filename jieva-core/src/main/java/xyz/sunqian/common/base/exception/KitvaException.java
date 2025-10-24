package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

/**
 * This is the root exception for this lib.
 *
 * @author sunqian
 */
public class KitvaException extends Exception {

    /**
     * Empty constructor.
     */
    public KitvaException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public KitvaException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public KitvaException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public KitvaException(@Nullable Throwable cause) {
        super(cause);
    }
}
