package xyz.sunqian.common.base.time;

import xyz.sunqian.annotations.Nullable;

import java.time.DateTimeException;

/**
 * Extension of {@link DateTimeException}, used for time and date.
 *
 * @author sunqian
 */
public class TimeException extends DateTimeException {

    /**
     * Empty constructor.
     */
    public TimeException() {
        super(null);
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public TimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public TimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public TimeException(@Nullable Throwable cause) {
        super(null, cause);
    }
}
