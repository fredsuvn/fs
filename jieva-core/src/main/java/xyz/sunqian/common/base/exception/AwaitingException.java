package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nullable;

import java.io.InterruptedIOException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown during the await operation. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class AwaitingException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public AwaitingException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public AwaitingException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AwaitingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public AwaitingException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Returns whether the cause is an {@link InterruptedException}, {@link InterruptedIOException}.
     *
     * @return whether the cause is an {@link InterruptedException}
     */
    public boolean isInterrupted() {
        return getCause() instanceof InterruptedException;
    }
}
