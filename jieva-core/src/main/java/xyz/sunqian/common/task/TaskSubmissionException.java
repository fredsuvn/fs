package xyz.sunqian.common.task;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.util.concurrent.RejectedExecutionException;

/**
 * This runtime class is typically used for wrapping original exceptions that occur when submitting a task to an
 * executor. The {@link #getCause()} method returns the wrapped original cause such as
 * {@link RejectedExecutionException} (if any).
 *
 * @author sunqian
 */
public class TaskSubmissionException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public TaskSubmissionException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public TaskSubmissionException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TaskSubmissionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public TaskSubmissionException(@Nullable Throwable cause) {
        super(cause);
    }
}
