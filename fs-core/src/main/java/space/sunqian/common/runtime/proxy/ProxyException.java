package space.sunqian.common.runtime.proxy;

import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.exception.FsRuntimeException;

/**
 * This runtime exception is typically used for proxy.
 *
 * @author sunqian
 */
public class ProxyException extends FsRuntimeException {

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
