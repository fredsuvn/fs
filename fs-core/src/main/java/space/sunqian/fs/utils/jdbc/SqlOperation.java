package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;

import java.sql.PreparedStatement;
import java.sql.Statement;

/**
 * This interface represents the operation for a SQL statement, it is the base interface for {@link SqlQuery},
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
     */
    @Nonnull
    PreparedStatement preparedStatement();

    /**
     * Releases this operation object's database and JDBC resources immediately instead of waiting for this to happen
     * when it is automatically closed.
     *
     * @throws JdbcException if any error occurs
     */
    void close() throws JdbcException;
}
