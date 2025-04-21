package xyz.sunqian.test;

/**
 * Exception for I/O tests.
 *
 * @author sunqian
 */
public class TestIOException extends RuntimeException {

    /**
     * Constructs with no detail message.
     */
    public TestIOException() {
    }

    /**
     * Constructs with the specified detail message.
     *
     * @param message the specified detail message.
     */
    public TestIOException(String message) {
        super(message);
    }

    /**
     * Constructs with the specified detail message and cause.
     *
     * @param message the specified detail message.
     * @param cause   the specified cause.
     */
    public TestIOException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the specified cause.
     *
     * @param cause the specified cause.
     */
    public TestIOException(Throwable cause) {
        super(cause);
    }
}
