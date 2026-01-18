package space.sunqian.fs.object;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Base exception for object errors.
 *
 * @author sunqian
 */
public class ObjectException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public ObjectException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectException(@Nullable Throwable cause) {
        super(cause);
    }
}
