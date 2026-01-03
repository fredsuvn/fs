package space.sunqian.fs.base.exception;

import space.sunqian.annotation.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown during the await operation. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class AwaitingException extends FsRuntimeException {

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
     * Returns {@code true} if this exception is caused by a {@link CancellationException}, and the {@link #getCause()}
     * will returns the {@link CancellationException}.
     *
     * @return {@code true} if this exception is caused by a {@link CancellationException}
     */
    public boolean isCausedByCancellation() {
        return getCause() instanceof CancellationException;
    }

    /**
     * Returns {@code true} if this exception is caused by a {@link ExecutionException}, and the {@link #getCause()}
     * will returns the {@link ExecutionException}.
     *
     * @return {@code true} if this exception is caused by a {@link ExecutionException}
     */
    public boolean isCausedByExecution() {
        return getCause() instanceof ExecutionException;
    }

    /**
     * Returns {@code true} if this exception is caused by a {@link InterruptedException}, and the {@link #getCause()}
     * will returns the {@link InterruptedException}.
     *
     * @return {@code true} if this exception is caused by a {@link InterruptedException}
     */
    public boolean isCausedByInterruption() {
        return getCause() instanceof InterruptedException;
    }

    /**
     * Returns {@code true} if this exception is caused by a {@link TimeoutException}, and the {@link #getCause()} will
     * returns the {@link TimeoutException}.
     *
     * @return {@code true} if this exception is caused by a {@link TimeoutException}
     */
    public boolean isCausedByTimeout() {
        return getCause() instanceof TimeoutException;
    }
}
