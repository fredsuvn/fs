package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.data.DataException;

/**
 * Base exception for {@code JSON} errors.
 *
 * @author sunqian
 */
public class JsonDataException extends DataException {

    /**
     * Empty constructor.
     */
    public JsonDataException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JsonDataException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JsonDataException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JsonDataException(@Nullable Throwable cause) {
        super(cause);
    }
}
