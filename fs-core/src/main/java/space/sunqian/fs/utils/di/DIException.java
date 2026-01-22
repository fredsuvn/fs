package space.sunqian.fs.utils.di;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Exception for dependency injection.
 *
 * @author sunqian
 */
public class DIException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public DIException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DIException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DIException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DIException(@Nullable Throwable cause) {
        super(cause);
    }
}
