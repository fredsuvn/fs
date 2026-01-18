package space.sunqian.fs.object.create;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

/**
 * Exception for object operations.
 *
 * @author sunqian
 */
public class ObjectCreateException extends ObjectException {

    /**
     * Empty constructor.
     */
    public ObjectCreateException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectCreateException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectCreateException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectCreateException(@Nullable Throwable cause) {
        super(cause);
    }
}
