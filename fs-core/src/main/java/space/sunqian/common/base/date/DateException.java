package space.sunqian.common.base.date;

import space.sunqian.annotations.Nullable;

import java.time.DateTimeException;

/**
 * Extension of {@link DateTimeException}, used for time and date.
 *
 * @author sunqian
 */
public class DateException extends DateTimeException {

    /**
     * Empty constructor.
     */
    public DateException() {
        super(null);
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DateException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DateException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DateException(@Nullable Throwable cause) {
        super(null, cause);
    }
}
