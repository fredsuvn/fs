package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Extension for number operations.
 *
 * @author sunqian
 */
public class NumberException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public NumberException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public NumberException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NumberException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public NumberException(@Nullable Throwable cause) {
        super(null, cause);
    }
}
