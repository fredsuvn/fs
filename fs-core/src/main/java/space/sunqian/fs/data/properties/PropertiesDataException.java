package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.data.DataException;

/**
 * Base exception for {@code Properties} errors.
 *
 * @author sunqian
 */
public class PropertiesDataException extends DataException {

    /**
     * Empty constructor.
     */
    public PropertiesDataException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public PropertiesDataException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PropertiesDataException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public PropertiesDataException(@Nullable Throwable cause) {
        super(cause);
    }
}
