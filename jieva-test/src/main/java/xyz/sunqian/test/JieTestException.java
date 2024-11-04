package xyz.sunqian.test;

/**
 * Exception for testing.
 *
 * @author fredsuvn
 */
public class JieTestException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public JieTestException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public JieTestException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public JieTestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public JieTestException(Throwable cause) {
        super(cause);
    }
}
