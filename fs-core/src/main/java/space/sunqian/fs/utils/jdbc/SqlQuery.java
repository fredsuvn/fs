package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.List;

/**
 * This interface represents the query result of a SQL execution.
 *
 * @param <T> the type mapped to the data row of the query result set
 * @author sunqian
 */
public interface SqlQuery<T> extends SqlResult {

    /**
     * Executes the result set of this query.
     *
     * @return the result set of this query
     */
    @Nonnull
    ResultSet resultSet();

    /**
     * Returns the type mapped to the data row of the query result set.
     *
     * @return the type mapped to the data row of the query result set
     */
    @Nonnull
    Type type();

    /**
     * Returns the result set of this query as a list.
     *
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    default @Nullable T first(
    ) throws JdbcException {
        List<T> result = resultList(null, null);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns the result set of this query as a list.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    default @Nullable T first(
        @Nullable NameMapper columnMapper
    ) throws JdbcException {
        List<T> result = resultList(columnMapper, null);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Returns the result set of this query as a list.
     *
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    default @Nonnull List<@Nonnull T> resultList(
    ) throws JdbcException {
        return resultList(null, null);
    }

    /**
     * Returns the result set of this query as a list.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    default @Nonnull List<@Nonnull T> resultList(
        @Nullable NameMapper columnMapper
    ) throws JdbcException {
        return resultList(columnMapper, null);
    }

    /**
     * Returns the result set of this query as a list.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @param converter    the converter to convert the JDBC type to the java type, may be {@code null} if it uses the
     *                     default converter
     * @param options      the options for converting, e.g. the {@link ConvertOption} for the converter
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    default @Nonnull List<@Nonnull T> resultList(
        @Nullable NameMapper columnMapper,
        @Nullable ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws JdbcException {
        return JdbcKit.toObject(
            resultSet(),
            type(),
            columnMapper,
            Fs.nonnull(converter, ObjectConverter.defaultConverter()),
            options
        );
    }
}
