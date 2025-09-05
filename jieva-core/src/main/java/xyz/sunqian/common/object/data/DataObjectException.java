package xyz.sunqian.common.object.data;

import java.lang.reflect.Type;

/**
 * Exception for data object.
 *
 * @author sunqian
 */
public class DataObjectException extends BeanException {

    private static String parsingSchemaMessage(Type type) {
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
    public DataObjectException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public DataObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public DataObjectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with specified data object type. This exception constructor is typically used when parsing data schema
     * fails.
     *
     * @param type specified data object type
     */
    public DataObjectException(Type type) {
        this(parsingSchemaMessage(type));
    }

    /**
     * Constructs with specified data object type and exception cause. This exception constructor is typically used when
     * parsing data schema fails.
     *
     * @param type  specified data object type
     * @param cause exception cause
     */
    public DataObjectException(Type type, Throwable cause) {
        this(parsingSchemaMessage(type), cause);
    }
}
