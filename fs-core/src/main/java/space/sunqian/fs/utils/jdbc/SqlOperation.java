package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * This interface represents the operation of a SQL statement. It is the base interface for {@link SqlQuery},
 * {@link SqlUpdate} and {@link SqlInsert}.
 *
 * @author sunqian
 */
public interface SqlOperation {

    /**
     * Returns the statement of this operation.
     *
     * @return the statement of this operation
     */
    @Nonnull
    Statement statement();

    /**
     * Returns the prepared statement of this operation.
     *
     * @return the prepared statement of this operation
     * @implNote The default implementation directly returns the {@link #statement()} cast to
     * {@link PreparedStatement}.
     */
    default @Nonnull PreparedStatement preparedStatement() {
        return (PreparedStatement) statement();
    }

    /**
     * Releases the {@link Statement}/{@link PreparedStatement} and {@link ResultSet} (if it has) resources of this
     * operation.
     *
     * @throws SqlRuntimeException if any error occurs
     */
    @SuppressWarnings("resource")
    default void close() throws SqlRuntimeException {
        Statement statement = statement();
        Fs.uncheck(statement::close, SqlRuntimeException::new);
    }
}
