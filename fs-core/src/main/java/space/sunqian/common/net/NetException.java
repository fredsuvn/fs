package space.sunqian.common.net;

import space.sunqian.annotations.Nullable;
import space.sunqian.common.io.IORuntimeException;

/**
 * Exception for network.
 *
 * @author sunqian
 */
public class NetException extends IORuntimeException {

    /**
     * Empty constructor.
     */
    public NetException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public NetException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NetException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public NetException(@Nullable Throwable cause) {
        super(cause);
    }
}