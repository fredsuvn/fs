package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.List;

/**
 * This interface represents a prepared SQL string with the specified parameters to be bound. It can be built from
 * {@link SqlBuilder}.
 *
 * @author sunqian
 */
public interface PreparedSql {

    /**
     * Returns the prepared SQL string.
     *
     * @return the prepared SQL string
     */
    @Nonnull
    String preparedSql();

    /**
     * Returns the parameters to be bound to this prepared SQL.
     *
     * @return the parameters to be bound to this prepared SQL
     */
    @Nonnull
    @Immutable
    List<Object> parameters();

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type       the type mapped to the data row of the result set
     * @param connection the connection to create the statement to execute the query
     * @param <T>        the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws SqlRuntimeException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(
        @Nonnull Class<T> type, @Nonnull Connection connection
    ) throws SqlRuntimeException {
        return query((Type) type, connection);
    }

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type       the type reference mapped to the data row of the result set
     * @param connection the connection to create the statement to execute the query
     * @param <T>        the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws SqlRuntimeException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(
        @Nonnull TypeRef<T> type, @Nonnull Connection connection
    ) throws SqlRuntimeException {
        return query(type.type(), connection);
    }

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type       the type mapped to the data row of the result set
     * @param connection the connection to create the statement to execute the query
     * @param <T>        the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws SqlRuntimeException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(
        @Nonnull Type type, @Nonnull Connection connection
    ) throws SqlRuntimeException {
        return Fs.uncheck(() ->
                SqlBack.newQuery(SqlBack.createPreparedStatement(this, connection), type),
            SqlRuntimeException::new
        );
    }

    /**
     * Executes the SQL update and returns the update result as a {@link SqlUpdate}.
     *
     * @param connection the connection to create the statement to execute the query
     * @return the update result as a {@link SqlUpdate}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull SqlUpdate update(@Nonnull Connection connection) throws SqlRuntimeException {
        return Fs.uncheck(() ->
                SqlBack.newUpdate(SqlBack.createPreparedStatement(this, connection)),
            SqlRuntimeException::new
        );
    }

    /**
     * Executes the SQL insert and returns the insert result as a {@link SqlInsert}.
     *
     * @param connection the connection to create the statement to execute the query
     * @return the insert result as a {@link SqlInsert}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull SqlInsert insert(@Nonnull Connection connection) throws SqlRuntimeException {
        return Fs.uncheck(() ->
                SqlBack.newInsert(SqlBack.createPreparedStatement(this, connection)),
            SqlRuntimeException::new
        );
    }
}