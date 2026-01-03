package space.sunqian.fs.dynamic;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Exception thrown when an error occurs during dynamic runtime metaprogramming.
 *
 * @author sunqian
 */
public class DynamicException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public DynamicException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DynamicException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DynamicException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DynamicException(@Nullable Throwable cause) {
        super(cause);
    }
}
