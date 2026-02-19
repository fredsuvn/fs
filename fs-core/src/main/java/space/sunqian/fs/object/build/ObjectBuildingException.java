package space.sunqian.fs.object.build;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

/**
 * Exception for object creation and building.
 *
 * @author sunqian
 */
public class ObjectBuildingException extends ObjectException {

    /**
     * Empty constructor.
     */
    public ObjectBuildingException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ObjectBuildingException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ObjectBuildingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ObjectBuildingException(@Nullable Throwable cause) {
        super(cause);
    }
}
