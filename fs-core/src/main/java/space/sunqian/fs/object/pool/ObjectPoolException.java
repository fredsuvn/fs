package space.sunqian.fs.object.pool;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

/**
 * Exception for object pool.
 *
 * @author sunqian
 */
public class ObjectPoolException extends ObjectException {

    /**
     * Empty constructor.
     */
    public ObjectPoolException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectPoolException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectPoolException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectPoolException(@Nullable Throwable cause) {
        super(cause);
    }
}
