package xyz.sunqian.common.runtime.invoke;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.lang.reflect.InvocationTargetException;

/**
 * This runtime exception is used for wrapping exceptions thrown during the invocation of {@link Invocable}. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class InvocationException extends JieRuntimeException {

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public InvocationException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the original cause.
     *
     * @param cause the original cause
     */
    public InvocationException(@Nullable Throwable cause) {
        super(cause instanceof InvocationTargetException ? cause.getCause() : cause);
    }
}
