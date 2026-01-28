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
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

/**
 * This interface represents the query operation of a SQL statement.
 *
 * @param <T> the type mapped to the data row of the query result set
 * @author sunqian
 */
public interface SqlQuery<T> extends SqlOperation {

    /**
     * Executes this query and returns the result set.
     *
     * @return the result set of this query
     */
    @SuppressWarnings("resource")
    default @Nonnull ResultSet execute() {
        return Fs.uncheck(() -> preparedStatement().executeQuery(), SqlRuntimeException::new);
    }

    /**
     * Returns the type mapped to the data row of the query result set.
     *
     * @return the type mapped to the data row of the query result set
     */
    @Nonnull
    Type type();

    /**
     * Executes this query and returns the first row maps to the type {@link T} of the result set of this query. The
     * name mapper to map the column name to the field name of the element type is {@link JdbcKit#defaultNameMapper()}.
     *
     * @return the list of objects
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first(
    ) throws SqlRuntimeException {
        List<T> result = list(JdbcKit.defaultNameMapper(), null);
        return result.stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the first row maps to the type {@link T} of the result set of this query.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @return the list of objects
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first(
        @Nullable NameMapper columnMapper
    ) throws SqlRuntimeException {
        List<T> result = list(columnMapper, null);
        return result.stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}. The name mapper to map
     * the column name to the field name of the element type is {@link JdbcKit#defaultNameMapper()}.
     *
     * @return the list of objects
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(
    ) throws SqlRuntimeException {
        return list(JdbcKit.defaultNameMapper(), null);
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @return the list of objects
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(
        @Nullable NameMapper columnMapper
    ) throws SqlRuntimeException {
        return list(columnMapper, null);
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}.
     *
     * @param columnMapper the name mapper to map the column name to the field name of the element type, may be
     *                     {@code null} if the column name is the same as the field name
     * @param converter    the converter to convert the JDBC type to the java type, may be {@code null} if it uses the
     *                     default converter
     * @param options      the options for converting, e.g. the {@link ConvertOption} for the converter
     * @return the list of objects
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(
        @Nullable NameMapper columnMapper,
        @Nullable ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return JdbcKit.toObject(
            execute(),
            type(),
            columnMapper,
            Fs.nonnull(converter, ObjectConverter.defaultConverter()),
            options
        );
    }

    @Override
    @SuppressWarnings("resource")
    default void close() throws SqlRuntimeException {
        Statement statement = statement();
        ResultSet resultSet = execute();
        Fs.uncheck(Arrays.asList(
                resultSet::close,
                statement::close
            ),
            SqlRuntimeException::new
        );
    }
}
