package space.sunqian.common.base.string;

import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.exception.KitvaRuntimeException;

/**
 * Extension for naming format, typically used for {@link NameFormatter}.
 *
 * @author sunqian
 */
public class NameFormatException extends KitvaRuntimeException {

    /**
     * Empty constructor.
     */
    public NameFormatException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public NameFormatException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NameFormatException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public NameFormatException(@Nullable Throwable cause) {
        super(cause);
    }
}
