package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.data.DataException;

/**
 * Base exception for {@code Properties} errors.
 *
 * @author sunqian
 */
public class PropertiesException extends DataException {

    /**
     * Empty constructor.
     */
    public PropertiesException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public PropertiesException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public PropertiesException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public PropertiesException(@Nullable Throwable cause) {
        super(cause);
    }
}
