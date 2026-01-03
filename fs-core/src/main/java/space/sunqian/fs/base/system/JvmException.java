package space.sunqian.fs.base.system;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * This runtime exception is typically used for wrapping exceptions thrown during the JVM operation. The
 * {@link #getCause()} method returns the wrapped original cause (if any).
 *
 * @author sunqian
 */
public class JvmException extends FsRuntimeException {

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
