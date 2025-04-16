package xyz.sunqian.test;

/**
 * Exception for tests.
 *
 * @author sunqian
 */
public class JieTestException extends RuntimeException {

    /**
     * Constructs with no detail message.
     */
    public JieTestException() {
    }

    /**
     * Constructs with the specified detail message.
     *
     * @param message the specified detail message.
     */
    public JieTestException(String message) {
        super(message);
    }

    /**
     * Constructs with the specified detail message and cause.
     *
     * @param message the specified detail message.
     * @param cause   the specified cause.
     */
    public JieTestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the specified cause.
     *
     * @param cause the specified cause.
     */
    public JieTestException(Throwable cause) {
        super(cause);
    }
}
