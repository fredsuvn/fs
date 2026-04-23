package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameMapper;
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
     * Executes this query and returns the result set of this query as a list of type {@link T}, using
     * {@link JdbcKit#defaultNameMapper()} and {@link ObjectConverter#defaultConverter()} to convert the object of the
     * JDBC type to the java type. If the result set is empty, returns {@code null}.
     *
     * @return the first row of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first() throws SqlRuntimeException {
        return list().stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the first row of the result set of this query as a list of type {@link T}, using
     * {@link ObjectConverter#defaultConverter()} to convert the object of the JDBC type to the java type. If the result
     * set is empty, returns {@code null}.
     *
     * @param nameMapper the mapper to map the column name to the field name of the element type
     * @return the first row of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first(@Nonnull NameMapper nameMapper) throws SqlRuntimeException {
        return list(nameMapper).stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the first row of the result set of this query as a list of type {@link T}. If the
     * result set is empty, returns {@code null}.
     *
     * @param nameMapper the mapper to map the column name to the field name of the element type
     * @param converter  the converter to convert the JDBC type to the java type
     * @param options    the options for converting
     * @return the first row of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nullable T first(
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return list(nameMapper, converter, options).stream().findFirst().orElse(null);
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}, using
     * {@link JdbcKit#defaultNameMapper()} and {@link ObjectConverter#defaultConverter()} to convert the object of the
     * JDBC type to the java type.
     *
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list() throws SqlRuntimeException {
        return Fs.as(JdbcKit.toObject(execute(), type()));
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}, using
     * {@link ObjectConverter#defaultConverter()} to convert the object of the JDBC type to the java type.
     *
     * @param nameMapper the mapper to map the column name to the field name of the element type
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(@Nonnull NameMapper nameMapper) throws SqlRuntimeException {
        return Fs.as(JdbcKit.toObject(
            execute(),
            type(),
            nameMapper
        ));
    }

    /**
     * Executes this query and returns the result set of this query as a list of type {@link T}.
     *
     * @param nameMapper the mapper to map the column name to the field name of the element type
     * @param converter  the converter to convert the JDBC type to the java type
     * @param options    the options for converting
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    default @Nonnull List<@Nonnull T> list(
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return Fs.as(JdbcKit.toObject(
            execute(),
            type(),
            nameMapper,
            converter,
            options
        ));
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
