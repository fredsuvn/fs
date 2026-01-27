package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

/**
 * This interface represents a prepared SQL string for batch execution with the specified batch parameters to be bound.
 * It can be built from {@link SqlBuilder}.
 *
 * @author sunqian
 */
public interface BatchSql {

    /**
     * Sets the connection for this prepared SQL for batch execution, then returns this prepared SQL for batch execution
     * itself.
     *
     * @param connection the connection to be set
     * @return this prepared SQL for batch execution itself
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    BatchSql connection(@Nonnull Connection connection) throws JdbcException;

    /**
     * Adds the given batch parameters to be bound to this prepared SQL for batch execution, then returns this prepared
     * SQL for batch execution itself.
     *
     * @param batchParameters the batch parameters to be bound to this prepared SQL for batch execution
     * @return this prepared SQL for batch execution itself
     */
    @Nonnull
    BatchSql batchParameters(@Nonnull List<@Nonnull List<Object>> batchParameters);

    /**
     * Adds the given parameters to be bound to this prepared SQL for batch execution, then returns this prepared SQL
     * for batch execution itself.
     *
     * @param parameters the parameters to be bound to this prepared SQL for batch execution
     * @return this prepared SQL for batch execution itself
     */
    @Nonnull
    BatchSql parameters(@Nonnull List<Object> parameters);

    /**
     * Returns this prepared SQL string for batch execution.
     *
     * @return this prepared SQL string for batch execution
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
     * Returns the connection to be used to execute this prepared SQL for batch execution, may be {@code null} if not
     * set.
     *
     * @return the connection to be used to execute this prepared SQL for batch execution, may be {@code null} if not
     * set
     */
    @Nullable
    Connection connection();

    /**
     * Executes this prepared SQL for batch execution and returns the batch result as a {@link SqlBatchResult}.
     *
     * @return the batch result as a {@link SqlBatchResult}
     * @throws JdbcException if any error occurs
     */
    default @Nonnull SqlBatchResult execute() throws JdbcException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = SqlBack.createPreparedStatement(this);
            return SqlBack.newBatchResult(statement);
        }, JdbcException::new);
    }
}
