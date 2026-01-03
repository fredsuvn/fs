package space.sunqian.fs.base.exception;

import space.sunqian.annotation.Nonnull;

/**
 * This runtime exception is used to wrap a non-runtime exception. The {@link #getCause()} method returns the wrapped
 * original cause.
 *
 * @author sunqian
 */
public class WrappedException extends FsRuntimeException {

    /**
     * Constructs with the original cause.
     *
     * @param cause the original cause
     */
    public WrappedException(@Nonnull Throwable cause) {
        super(cause);
    }
}
