package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * This runtime exception is typically used for proxy.
 *
 * @author sunqian
 */
public class ProxyException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public ProxyException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ProxyException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ProxyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ProxyException(@Nullable Throwable cause) {
        super(cause);
    }
}
