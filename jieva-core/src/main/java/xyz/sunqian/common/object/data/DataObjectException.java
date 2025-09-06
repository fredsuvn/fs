package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.lang.reflect.Type;

/**
 * Exception for data object.
 *
 * @author sunqian
 */
public class DataObjectException extends JieRuntimeException {

    private static String parsingSchemaMessage(@Nonnull Type type) {
        return "Parsing data schema failed: " + type.getTypeName() + "[" + type.getClass().getName() + "].";
    }

    /**
     * Empty constructor.
     */
    public DataObjectException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public DataObjectException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public DataObjectException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public DataObjectException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with specified data object type. This exception constructor is typically used when parsing data schema
     * fails.
     *
     * @param type specified data object type
     */
    public DataObjectException(@Nonnull Type type) {
        this(parsingSchemaMessage(type));
    }

    /**
     * Constructs with specified data object type and exception cause. This exception constructor is typically used when
     * parsing data schema fails.
     *
     * @param type  specified data object type
     * @param cause exception cause
     */
    public DataObjectException(@Nonnull Type type, @Nullable Throwable cause) {
        this(parsingSchemaMessage(type), cause);
    }
}
