package xyz.sunqian.common.app.di;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.app.SimpleAppException;

/**
 * Exception for {@link InjectedApp}.
 *
 * @author sunqian
 */
public class InjectedAppException extends SimpleAppException {

    /**
     * Empty constructor.
     */
    public InjectedAppException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public InjectedAppException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public InjectedAppException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public InjectedAppException(@Nullable Throwable cause) {
        super(cause);
    }
}
