package space.sunqian.fs.object.builder;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

/**
 * Exception for object creation and building.
 *
 * @author sunqian
 */
public class ObjectBuilderException extends ObjectException {

    /**
     * Empty constructor.
     */
    public ObjectBuilderException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectBuilderException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectBuilderException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectBuilderException(@Nullable Throwable cause) {
        super(cause);
    }
}
