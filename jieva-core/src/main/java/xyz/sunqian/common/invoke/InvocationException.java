package xyz.sunqian.common.invoke;

import xyz.sunqian.common.base.JieException;

/**
 * Invocation exception.
 *
 * @author sunqian
 */
public class InvocationException extends JieException {

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
