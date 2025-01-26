package xyz.sunqian.common.objects;

import java.lang.reflect.Type;

/**
 * Exception for object introspection.
 *
 * @author fredsuvn
 */
public class ObjectIntrospectionException extends BeanException {

    private static String buildTypeMessage(Type type) {
        return "Introspection failed: " + type.getTypeName() + "[" + type.getClass().getName() + "].";
    }

    /**
     * Empty constructor.
     */
    public ObjectIntrospectionException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ObjectIntrospectionException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ObjectIntrospectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ObjectIntrospectionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with specified type.
     *
     * @param type specified type
     */
    public ObjectIntrospectionException(Type type) {
        this(buildTypeMessage(type));
    }

    /**
     * Constructs with specified type and exception cause.
     *
     * @param type  specified type
     * @param cause exception cause
     */
    public ObjectIntrospectionException(Type type, Throwable cause) {
        this(buildTypeMessage(type), cause);
    }
}
