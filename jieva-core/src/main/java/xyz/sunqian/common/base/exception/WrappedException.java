package xyz.sunqian.common.base.exception;

import xyz.sunqian.annotations.Nonnull;

/**
 * This runtime exception is used to wrap a non-runtime exception. The {@link #getCause()} method returns the wrapped
 * original cause.
 *
 * @author sunqian
 */
public class WrappedException extends JieRuntimeException {

    /**
     * Constructs with the original cause.
     *
     * @param cause the original cause
     */
    public WrappedException(@Nonnull Throwable cause) {
        super(ExceptionKit.getMessage(cause), cause);
    }
}
