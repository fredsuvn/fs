package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * Exception for proxy.
 *
 * @author fredsuvn
 */
public class ProxyException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public ProxyException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public ProxyException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ProxyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public ProxyException(Throwable cause) {
        super(cause);
    }
}
