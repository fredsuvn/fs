package space.sunqian.fs.net.http;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.net.NetException;

/**
 * Exception for http network.
 *
 * @author sunqian
 */
public class HttpNetException extends NetException {

    /**
     * Empty constructor.
     */
    public HttpNetException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public HttpNetException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public HttpNetException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public HttpNetException(@Nullable Throwable cause) {
        super(cause);
    }
}
