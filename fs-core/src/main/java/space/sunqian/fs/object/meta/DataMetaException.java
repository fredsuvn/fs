package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

import java.lang.reflect.Type;

/**
 * Exception for object meta, including map meta and non-map object meta.
 *
 * @author sunqian
 */
public class DataMetaException extends ObjectException {

    private static String toMessage(@Nonnull Type type) {
        return "Introspection data meta failed: " + type.getTypeName() + "[" + type.getClass().getName() + "].";
    }

    /**
     * Empty constructor.
     */
    public DataMetaException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DataMetaException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DataMetaException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DataMetaException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with the type to be introspected.
     *
     * @param type the type to be introspected
     */
    public DataMetaException(@Nonnull Type type) {
        this(toMessage(type));
    }

    /**
     * Constructs with the type to be introspected and cause.
     *
     * @param type  the type to be introspected
     * @param cause the cause
     */
    public DataMetaException(@Nonnull Type type, @Nullable Throwable cause) {
        this(toMessage(type), cause);
    }
}
