package space.sunqian.common.base.exception;

import space.sunqian.annotations.Nullable;

/**
 * This exception will be thrown if, and only if, the execution reaches an unreachable point.
 *
 * @author sunqian
 */
public class UnreachablePointException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public UnreachablePointException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public UnreachablePointException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnreachablePointException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public UnreachablePointException(@Nullable Throwable cause) {
        super(cause);
    }
}
