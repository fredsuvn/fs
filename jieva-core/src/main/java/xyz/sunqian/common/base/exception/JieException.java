package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

/**
 * This is the root exception for this lib.
 *
 * @author sunqian
 */
public class JieException extends Exception {

    /**
     * Empty constructor.
     */
    public JieException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JieException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JieException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JieException(@Nullable Throwable cause) {
        super(cause);
    }
}
