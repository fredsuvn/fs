package xyz.sunqian.common.reflect;

/**
 * Jvm exception.
 *
 * @author sunqian
 */
public class JvmException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public JvmException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public JvmException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public JvmException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public JvmException(Throwable cause) {
        super(cause);
    }
}
