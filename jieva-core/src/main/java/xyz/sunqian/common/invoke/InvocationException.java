package xyz.sunqian.common.invoke;

import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * Invocation exception.
 *
 * @author sunqian
 */
public class InvocationException extends JieRuntimeException {

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public InvocationException(String message) {
        super(message);
    }

    /**
     * Constructs with the original cause.
     *
     * @param cause the original cause
     */
    public InvocationException(Throwable cause) {
        super(cause);
    }
}
