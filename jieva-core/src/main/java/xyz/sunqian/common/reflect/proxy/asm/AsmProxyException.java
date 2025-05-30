package xyz.sunqian.common.reflect.proxy.asm;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.reflect.proxy.ProxyException;

/**
 * This exception is the sub-exception of {@link ProxyException} for <a href="https://asm.ow2.io/">ASM</a> proxy
 * implementation.
 *
 * @author sunqian
 */
public class AsmProxyException extends ProxyException {

    /**
     * Empty constructor.
     */
    public AsmProxyException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public AsmProxyException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AsmProxyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public AsmProxyException(@Nullable Throwable cause) {
        this(JieException.getMessage(cause), cause);
    }
}
