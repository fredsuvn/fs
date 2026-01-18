package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.object.ObjectException;

import java.lang.reflect.Type;

/**
 * Exception for object schema, including map schema and non-map object schema.
 *
 * @author sunqian
 */
public class SchemaException extends ObjectException {

    private static String toMessage(@Nonnull Type type) {
        return "Parsing data schema failed: " + type.getTypeName() + "[" + type.getClass().getName() + "].";
    }

    /**
     * Empty constructor.
     */
    public SchemaException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public SchemaException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SchemaException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public SchemaException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with the type of specified data object.
     *
     * @param type the type of specified data object
     */
    public SchemaException(@Nonnull Type type) {
        this(toMessage(type));
    }

    /**
     * Constructs with the type of specified data object and cause.
     *
     * @param type  the type of specified data object
     * @param cause the cause
     */
    public SchemaException(@Nonnull Type type, @Nullable Throwable cause) {
        this(toMessage(type), cause);
    }
}
