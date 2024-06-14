package xyz.fslabo.common.bean;

/**
 * Exception for bean.
 *
 * @author fredsuvn
 */
public class BeanException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public BeanException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public BeanException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public BeanException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public BeanException(Throwable cause) {
        super(cause);
    }
}
