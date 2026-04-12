package space.sunqian.fs.data;

import space.sunqian.annotation.Nullable;

/**
 * Base exception for data formatting errors.
 *
 * @author sunqian
 */
public class DataFormattingException extends DataException {

    /**
     * Empty constructor.
     */
    public DataFormattingException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DataFormattingException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DataFormattingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DataFormattingException(@Nullable Throwable cause) {
        super(cause);
    }
}
