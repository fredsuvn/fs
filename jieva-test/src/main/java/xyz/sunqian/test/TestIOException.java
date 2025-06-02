package xyz.sunqian.test;

import xyz.sunqian.annotations.Nullable;

/**
 * Exception for I/O tests.
 *
 * @author sunqian
 */
public class TestIOException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public TestIOException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public TestIOException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TestIOException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public TestIOException(@Nullable Throwable cause) {
        super(cause);
    }
}
