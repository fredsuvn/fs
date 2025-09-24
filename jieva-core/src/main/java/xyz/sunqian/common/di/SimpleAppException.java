package xyz.sunqian.common.di;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * Exception for {@link SimpleApp}.
 *
 * @author sunqian
 */
public class SimpleAppException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public SimpleAppException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public SimpleAppException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SimpleAppException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public SimpleAppException(@Nullable Throwable cause) {
        super(cause);
    }
}
