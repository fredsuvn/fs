package xyz.sunqian.common.invoke;

import xyz.sunqian.common.base.JieException;

/**
 * Invocation exception.
 *
 * @author sunqian
 */
public class InvocationException extends JieException {

    /**
     * Empty constructor.
     */
    public InvocationException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public InvocationException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public InvocationException(Throwable cause) {
        super(cause);
    }
}
