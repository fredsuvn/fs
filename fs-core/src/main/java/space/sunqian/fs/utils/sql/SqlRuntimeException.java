package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IORuntimeException;

import java.sql.SQLException;

/**
 * This is the runtime version of {@link SQLException}.
 *
 * @author sunqian
 */
public class SqlRuntimeException extends IORuntimeException {

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