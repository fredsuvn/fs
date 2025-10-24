package xyz.sunqian.common.runtime.reflect;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.KitvaRuntimeException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown during the reflection operation. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class ReflectionException extends KitvaRuntimeException {

    /**
     * Empty constructor.
     */
    public ReflectionException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public ReflectionException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public ReflectionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public ReflectionException(@Nullable Throwable cause) {
        super(cause);
    }
}
