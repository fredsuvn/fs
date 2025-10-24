package xyz.sunqian.test;

import xyz.sunqian.annotations.Nullable;

/**
 * Exception for tests.
 *
 * @author sunqian
 */
public class KitvaTestException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public KitvaTestException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public KitvaTestException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public KitvaTestException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public KitvaTestException(@Nullable Throwable cause) {
        super(cause);
    }
}
