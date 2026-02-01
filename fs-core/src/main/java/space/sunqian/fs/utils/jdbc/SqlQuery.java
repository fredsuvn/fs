package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.PropertyNameMapper;

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
     * Executes this query and returns the first row maps to the type {@link T} of the result set of this query.
     * <p>
     * This method uses {@link ObjectConverter#defaultConverter()} with the option {@link JdbcKit#defaultNameMapper()}
     * to convert the result set. It is equivalent to:
     * <pre>{@code
     * first(ObjectConverter.defaultConverter(), ConvertOption.propertyNameMapper(JdbcKit.defaultNameMapper()))
     * }</pre>
     *
     * @return the first row of the result set of which row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first() throws SqlRuntimeException {
        return first(ObjectConverter.defaultConverter(), ConvertOption.propertyNameMapper(JdbcKit.defaultNameMapper()));
    }

    /**
     * Executes this query and returns the first row maps to the type {@link T} of the result set of this query.
     *
     * @param converter the converter to convert the JDBC type to the java type
     * @param options   the options for converting, e.g. the
     *                  {@link ConvertOption#propertyNameMapper(PropertyNameMapper)} for mapping the column name to the
     *                  field name of the element type during converting
     * @return the first row of the result set of which row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first(
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        List<T> result = list(converter, options);
        return result.stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}.
     * <p>
     * This method uses {@link ObjectConverter#defaultConverter()} with the option {@link JdbcKit#defaultNameMapper()}
     * to convert the result set. It is equivalent to:
     * <pre>{@code
     * list(ObjectConverter.defaultConverter(), ConvertOption.propertyNameMapper(JdbcKit.defaultNameMapper()))
     * }</pre>
     *
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list() throws SqlRuntimeException {
        return list(ObjectConverter.defaultConverter(), ConvertOption.propertyNameMapper(JdbcKit.defaultNameMapper()));
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}.
     *
     * @param converter the converter to convert the JDBC type to the java type
     * @param options   the options for converting, e.g. the
     *                  {@link ConvertOption#propertyNameMapper(PropertyNameMapper)} for mapping the column name to the
     *                  field name of the element type during converting
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return JdbcKit.toObject(
            execute(),
            type(),
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
