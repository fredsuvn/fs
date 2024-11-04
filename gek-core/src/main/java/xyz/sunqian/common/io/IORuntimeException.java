package xyz.sunqian.common.io;

import java.io.IOException;

/**
 * Runtime version of {@link IOException}.
 *
 * @author fredsuvn
 */
public class IORuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public IORuntimeException() {
    }

    /**
     * Constructs with exception message.
     *
     * @param message exception message
     */
    public IORuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs with exception message and exception cause.
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with exception cause.
     *
     * @param cause exception cause
     */
    public IORuntimeException(Throwable cause) {
        super(cause);
    }
}
