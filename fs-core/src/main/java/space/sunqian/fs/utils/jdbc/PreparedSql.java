package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * This interface represents a prepared SQL string with the specified parameters to be bound. It can be built from
 * {@link SqlBuilder}.
 *
 * @author sunqian
 */
public interface PreparedSql {

    /**
     * Sets the connection for this prepared SQL, then returns this prepared SQL itself.
     *
     * @param connection the connection to be set
     * @return this prepared SQL itself
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    PreparedSql connection(@Nonnull Connection connection) throws JdbcException;

    /**
     * Returns this prepared SQL string.
     *
     * @return this prepared SQL string
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
     * Returns the connection to be used to execute this prepared SQL, may be {@code null} if not set.
     *
     * @return the connection to be used to execute this prepared SQL, may be {@code null} if not set
     */
    @Nullable
    Connection connection();

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type the type mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(Class<T> type) throws JdbcException {
        return query((Type) type);
    }

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type the type reference mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(TypeRef<T> type) throws JdbcException {
        return query(type.type());
    }

    /**
     * Executes the SQL query and returns the query result set as a {@link SqlQuery}.
     *
     * @param type the type mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query result set as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(Type type) throws JdbcException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = SqlBack.createPreparedStatement(this);
            ResultSet resultSet = statement.getResultSet();
            return SqlBack.newQuery(statement, resultSet, type);
        }, JdbcException::new);
    }

    /**
     * Executes the SQL update and returns the update result as a {@link SqlUpdate}.
     *
     * @return the update result as a {@link SqlUpdate}
     * @throws JdbcException if any error occurs
     */
    default @Nonnull SqlUpdate update() throws JdbcException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = SqlBack.createPreparedStatement(this);
            return SqlBack.newUpdate(statement);
        }, JdbcException::new);
    }

    /**
     * Executes the SQL insert and returns the insert result as a {@link SqlInsert}.
     *
     * @return the insert result as a {@link SqlInsert}
     * @throws JdbcException if any error occurs
     */
    default @Nonnull SqlInsert insert() throws JdbcException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = SqlBack.createPreparedStatement(this);
            return SqlBack.newInsert(statement);
        }, JdbcException::new);
    }
}
