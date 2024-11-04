package xyz.sunqian.common.reflect;

/**
 * Reflection exception.
 *
 * @author sunqian
 */
public class ReflectionException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public ReflectionException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ReflectionException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ReflectionException(Throwable cause) {
        super(cause);
    }
}
