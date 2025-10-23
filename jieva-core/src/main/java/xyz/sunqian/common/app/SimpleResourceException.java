package xyz.sunqian.common.app;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * Exception for {@link SimpleResource}.
 *
 * @author sunqian
 */
public class SimpleResourceException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public SimpleResourceException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public SimpleResourceException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SimpleResourceException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public SimpleResourceException(@Nullable Throwable cause) {
        super(cause);
    }
}
