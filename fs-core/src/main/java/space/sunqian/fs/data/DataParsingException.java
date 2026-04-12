package space.sunqian.fs.data;

import space.sunqian.annotation.Nullable;

/**
 * Base exception for data parsing errors.
 *
 * @author sunqian
 */
public class DataParsingException extends DataException {

    /**
     * Empty constructor.
     */
    public DataParsingException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public DataParsingException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public DataParsingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public DataParsingException(@Nullable Throwable cause) {
        super(cause);
    }
}
