package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.IOException;

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

    /**
     * Constructs with the {@link IOException}.
     *
     * @param cause the {@link IOException}
     */
    public NetException(@Nonnull IOException cause) {
        this(cause.getMessage(), cause.getCause());
    }
}
