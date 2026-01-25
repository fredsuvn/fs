package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

/**
 * This interface represents a prepared SQL string with the specified parameters to be bound.
 *
 * @author sunqian
 */
public interface PreparedSql {

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
     * Executes the SQL query and returns the query object as a {@link SqlQuery}.
     *
     * @param type the type mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query object as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(Class<T> type) throws JdbcException {
        return query((Type) type);
    }

    /**
     * Executes the SQL query and returns the query object as a {@link SqlQuery}.
     *
     * @param type the type reference mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query object as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    default <T> @Nonnull SqlQuery<T> query(TypeRef<T> type) throws JdbcException {
        return query(type.type());
    }

    /**
     * Executes the SQL query and returns the query object as a {@link SqlQuery}.
     *
     * @param type the type mapped to the data row of the result set
     * @param <T>  the type mapped to the data row of the result set
     * @return the query object as a {@link SqlQuery}
     * @throws JdbcException if any error occurs
     */
    <T> @Nonnull SqlQuery<T> query(Type type) throws JdbcException;

    /**
     * Executes the SQL query and returns the query result set.
     *
     * @return the query result set
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    ResultSet query() throws JdbcException;

    /**
     * Executes the SQL update and returns the update object as a {@link SqlUpdate}.
     *
     * @return the update object as a {@link SqlUpdate}
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    SqlUpdate update() throws JdbcException;

    /**
     * Executes the SQL insert and returns the insert object as a {@link SqlInsert}.
     *
     * @return the insert object as a {@link SqlInsert}
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    SqlInsert insert() throws JdbcException;

    /**
     * Sets the connection for this prepared SQL, then returns this prepared SQL itself.
     *
     * @param connection the connection to be set
     * @return this prepared SQL itself
     * @throws JdbcException if any error occurs
     */
    @Nonnull
    PreparedSql connection(@Nonnull Connection connection) throws JdbcException;
}
