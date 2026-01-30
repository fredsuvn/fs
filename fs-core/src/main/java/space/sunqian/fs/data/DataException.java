package space.sunqian.fs.data;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Base exception for data errors.
 *
 * @author sunqian
 */
public class DataException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public DataException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DataException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DataException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DataException(@Nullable Throwable cause) {
        super(cause);
    }
}
