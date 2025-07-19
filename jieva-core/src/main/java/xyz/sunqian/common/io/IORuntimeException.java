package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.io.IOException;

/**
 * This runtime class is used to wrap {@link IOException}, using {@link #getCause()} to get the cause. It can be used as
 * an unchecked version of {@link IOException}.
 *
 * @author sunqian
 */
public class IORuntimeException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public IORuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public IORuntimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public IORuntimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public IORuntimeException(@Nullable Throwable cause) {
        super(cause);
    }

    /**
     * Constructs with the {@link IOException}.
     *
     * @param cause the {@link IOException}
     */
    public IORuntimeException(@Nonnull IOException cause) {
        this(cause.getMessage(), cause.getCause());
    }
}
