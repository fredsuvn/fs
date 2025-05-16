package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

/**
 * This is the root runtime exception for jieva.
 *
 * @author sunqian
 */
public class JieRuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public JieRuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JieRuntimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JieRuntimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JieRuntimeException(@Nullable Throwable cause) {
        super(cause);
    }
}
