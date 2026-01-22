package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nullable;

/**
 * Exception for object copy operations.
 *
 * @author sunqian
 */
public class ObjectCopyException extends ObjectConvertException {

    /**
     * Empty constructor.
     */
    public ObjectCopyException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectCopyException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectCopyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectCopyException(@Nullable Throwable cause) {
        super(cause);
    }
}
