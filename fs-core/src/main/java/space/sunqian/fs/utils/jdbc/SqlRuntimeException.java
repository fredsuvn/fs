package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

import java.sql.SQLException;

/**
 * This is the runtime version of {@link SQLException}.
 *
 * @author sunqian
 */
public class SqlRuntimeException extends FsRuntimeException {

    /**
     * Empty constructor.
     */
    public SqlRuntimeException() {
        super();
    }

    /**
     * Constructs with the message.
     *
     * @param message the message
     */
    public SqlRuntimeException(@Nullable String message) {
        super(message);
    }

    /**
     * Constructs with the message and cause.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SqlRuntimeException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs with the cause.
     *
     * @param cause the cause
     */
    public SqlRuntimeException(@Nullable Throwable cause) {
        super(cause);
    }
}