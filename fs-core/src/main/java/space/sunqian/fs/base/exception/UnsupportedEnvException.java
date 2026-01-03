package space.sunqian.fs.base.exception;

import space.sunqian.annotation.Nullable;

/**
 * This runtime exception is typically used for checking whether the current environment is supported.
 *
 * @author sunqian
 */
public class UnsupportedEnvException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public UnsupportedEnvException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public UnsupportedEnvException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnsupportedEnvException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public UnsupportedEnvException(@Nullable Throwable cause) {
        super(cause);
    }
}
