package space.sunqian.fs.base.lang;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * This runtime exception is typically used for cloning an object.
 *
 * @author sunqian
 */
public class CloneException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public CloneException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public CloneException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public CloneException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public CloneException(@Nullable Throwable cause) {
        super(cause);
    }
}
