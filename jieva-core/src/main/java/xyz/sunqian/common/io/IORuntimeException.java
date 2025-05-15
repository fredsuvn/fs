package xyz.sunqian.common.io;

import java.io.IOException;

/**
 * Runtime exception for {@link IOException}.
 *
 * @author sunqian
 */
public class IORuntimeException extends RuntimeException {

    /**
     * Empty constructor.
     */
    public IORuntimeException() {
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public IORuntimeException(String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public IORuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public IORuntimeException(Throwable cause) {
        super(cause instanceof IOException ? cause.getCause() : cause);
    }

    /**
     * Constructs with the {@link IOException}.
     *
     * @param cause the {@link IOException}
     */
    public IORuntimeException(IOException cause) {
        this(cause.getMessage(), cause.getCause());
    }
}
