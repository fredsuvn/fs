package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.sql.Connection;
import java.util.List;

/**
 * This interface represents a prepared SQL string for batch execution with the specified batch parameters to be bound.
 * It can be built from {@link SqlBuilder}.
 *
 * @author sunqian
 */
public interface PreparedBatchSql {

    /**
     * Adds the given batch parameters to be bound to this prepared SQL for batch execution, then returns this prepared
     * SQL for batch execution itself.
     *
     * @param batchParameters the batch parameters to be bound to this prepared SQL for batch execution
     * @return this prepared SQL for batch execution itself
     */
    @Nonnull
    PreparedBatchSql batchParameters(@Nonnull List<@Nonnull List<Object>> batchParameters);

    /**
     * Adds the given parameters to be bound to this prepared SQL for batch execution, then returns this prepared SQL
     * for batch execution itself.
     *
     * @param parameters the parameters to be bound to this prepared SQL for batch execution
     * @return this prepared SQL for batch execution itself
     */
    @Nonnull
    PreparedBatchSql parameters(@Nonnull List<Object> parameters);

    /**
     * Returns the prepared SQL string for batch execution.
     *
     * @return the prepared SQL string for batch execution
     */
    @Nonnull
    String preparedSql();

    /**
     * Returns the batch parameters to be bound to this prepared SQL for batch execution.
     *
     * @return the batch parameters to be bound to this prepared SQL for batch execution
     */
    @Nonnull
    @Immutable
    List<@Nonnull List<Object>> batchParameters();

    /**
     * Executes this prepared SQL for batch execution and returns the batch result as a {@link SqlBatch}.
     *
     * @param connection the connection to create the statement to execute this batch execution
     * @return the batch result as a {@link SqlBatch}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull SqlBatch execute(@Nonnull Connection connection) throws SqlRuntimeException {
        return Fs.uncheck(() ->
                SqlBack.newBatchResult(SqlBack.createPreparedStatement(this, connection)),
            SqlRuntimeException::new
        );
    }
}
