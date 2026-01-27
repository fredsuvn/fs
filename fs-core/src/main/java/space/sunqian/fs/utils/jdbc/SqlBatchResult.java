package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

/**
 * This interface represents the batch result of a list of SQL executions.
 *
 * @author sunqian
 */
public interface SqlBatchResult extends SqlResult {

    /**
     * Returns an array of which each element is the number of rows affected by this operation.
     *
     * @return an array of which each element is the number of rows affected by this operation
     * @throws JdbcException if any error occurs
     */
    @SuppressWarnings("resource")
    default int @Nonnull [] affectedRows() throws JdbcException {
        return Fs.uncheck(() -> preparedStatement().executeBatch(), JdbcException::new);
    }

    /**
     * Returns an array of which each element is the {@code long}number of rows affected by this operation.
     *
     * @return an array of which each element is the {@code long} number of rows affected by this operation
     * @throws JdbcException if any error occurs
     */
    @SuppressWarnings("resource")
    default long @Nonnull [] largeAffectedRows() throws JdbcException {
        return Fs.uncheck(() -> preparedStatement().executeLargeBatch(), JdbcException::new);
    }
}
