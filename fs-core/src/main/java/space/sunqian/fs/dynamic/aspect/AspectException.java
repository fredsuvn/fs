package space.sunqian.fs.dynamic.aspect;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * This runtime exception is typically used for aspect-oriented programming.
 *
 * @author sunqian
 */
public class AspectException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public AspectException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public AspectException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AspectException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public AspectException(@Nullable Throwable cause) {
        super(cause);
    }
}
