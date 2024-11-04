package xyz.sunqian.common.bean;

import java.lang.reflect.Type;

/**
 * Exception for bean resolving.
 *
 * @author fredsuvn
 */
public class BeanResolvingException extends BeanException {

    /**
     * Empty constructor.
     */
    public BeanResolvingException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public BeanResolvingException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public BeanResolvingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public BeanResolvingException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with bean type.
     *
     * @param type bean type
     */
    public BeanResolvingException(Type type) {
        this("Failed to resolve type to bean: " + type.getTypeName() + ".");
    }

    /**
     * Constructs with bean type and exception cause.
     *
     * @param type  bean type
     * @param cause exception cause
     */
    public BeanResolvingException(Type type, Throwable cause) {
        this("Failed to resolve type to bean: " + type.getTypeName() + "[" + type.getClass().getName() + "].", cause);
    }
}
