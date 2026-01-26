package space.sunqian.fs.utils.jdbc;

import space.sunqian.fs.Fs;

/**
 * This interface represents the update result of a SQL execution.
 *
 * @author sunqian
 */
public interface SqlUpdate extends SqlResult {

    /**
     * Returns the number of rows affected by this SQL statement.
     *
     * @return the number of rows affected by this SQL statement
     * @throws JdbcException if any error occurs
     */
    @SuppressWarnings("resource")
    default long affectedRows() throws JdbcException {
        return Fs.uncheck(() -> preparedStatement().getUpdateCount(), JdbcException::new);
    }
}
