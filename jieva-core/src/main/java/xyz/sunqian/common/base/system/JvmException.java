package xyz.sunqian.common.base.system;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown during the JVM operation. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class JvmException extends JieRuntimeException {

    /**
     * Empty constructor.
     */
    public JvmException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JvmException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JvmException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JvmException(@Nullable Throwable cause) {
        super(cause);
    }
}
