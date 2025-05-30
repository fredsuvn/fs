package xyz.sunqian.common.reflect.proxy.jdk;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieException;
import xyz.sunqian.common.reflect.proxy.ProxyException;

/**
 * This exception is the sub-exception of {@link ProxyException} for JDK dynamic proxy implementation.
 *
 * @author sunqian
 */
public class JdkProxyException extends ProxyException {

    /**
     * Empty constructor.
     */
    public JdkProxyException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JdkProxyException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JdkProxyException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JdkProxyException(@Nullable Throwable cause) {
        this(JieException.getMessage(cause), cause);
    }
}
