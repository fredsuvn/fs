package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;

/**
 * This interface represents the operation result of a SQL execution. It is the base interface for {@link SqlQuery},
 * {@link SqlUpdate} and {@link SqlInsert}.
 *
 * @author sunqian
 */
public interface SqlResult {

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
     */
    @Nonnull
    PreparedStatement preparedStatement();

    /**
     * Releases the JDBC resources of this operation including the connection, statement and result set (if it has
     * one).
     *
     * @throws JdbcException if any error occurs
     */
    @SuppressWarnings("resource")
    default void close() throws JdbcException {
        Statement statement = statement();
        Fs.uncheck(Arrays.asList(
                () -> statement.getConnection().close(),
                statement::close
            ),
            JdbcException::new
        );
    }
}
