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

    private static String toMessage(@Nonnull Type type) {
        return "Parsing data schema failed: " + type.getTypeName() + "[" + type.getClass().getName() + "].";
    }

    /**
     * Empty constructor.
     */
    public DataObjectException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DataObjectException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DataObjectException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DataObjectException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with the type of specified data object.
     *
     * @param type the type of specified data object
     */
    public DataObjectException(@Nonnull Type type) {
        this(toMessage(type));
    }

    /**
     * Constructs with the type of specified data object and cause.
     *
     * @param type  the type of specified data object
     * @param cause the cause
     */
    public DataObjectException(@Nonnull Type type, @Nullable Throwable cause) {
        this(toMessage(type), cause);
    }
}
