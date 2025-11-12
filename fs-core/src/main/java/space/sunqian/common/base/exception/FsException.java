package space.sunqian.common.base.exception;

import space.sunqian.annotations.Nullable;

/**
 * This is the root exception for this lib.
 *
 * @author sunqian
 */
public class FsException extends Exception {

    /**
     * Empty constructor.
     */
    public FsException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public FsException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public FsException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public FsException(@Nullable Throwable cause) {
        super(cause);
    }
}
