package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

/**
 * Exception for jdbc.
 *
 * @author sunqian
 */
public class JdbcException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public JdbcException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public JdbcException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public JdbcException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public JdbcException(@Nullable Throwable cause) {
        super(cause);
    }
}