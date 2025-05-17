package xyz.sunqian.common.work;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown for
 * {@linkplain xyz.sunqian.common.work work}. The {@link #getCause()} method returns the wrapped original cause (if
 * any).
 *
 * @author sunqian
 */
public class WorkException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public WorkException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public WorkException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public WorkException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public WorkException(@Nullable Throwable cause) {
        this(JieException.getMessage(cause), cause);
    }
}
