package internal.test;

import space.sunqian.annotations.Nullable;

/**
 * Exception for tests.
 *
 * @author sunqian
 */
public class FsTestException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public FsTestException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public FsTestException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public FsTestException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public FsTestException(@Nullable Throwable cause) {
        super(cause);
    }
}
