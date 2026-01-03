package space.sunqian.fs.base.exception;

import space.sunqian.annotation.Nullable;

/**
 * This is the root runtime exception for this lib.
 *
 * @author sunqian
 */
public class FsRuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsRuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public FsRuntimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public FsRuntimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public FsRuntimeException(@Nullable Throwable cause) {
        super(cause);
    }
}
