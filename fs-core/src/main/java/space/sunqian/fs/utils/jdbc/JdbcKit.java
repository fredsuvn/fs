package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameFormatter;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for JDBC and SQL.
 *
 * @author sunqian
 */
public class JdbcKit {

    /**
     * Return the default name mapper to map the column name to the field name of the element type. The default name
     * mapper assume the column name is in underscore case (e.g. {@code USER_ID}) and the field name is in lower camel
     * case (e.g. {@code userId}).
     *
     * @return the default name mapper
     */
    public static @Nonnull NameMapper defaultNameMapper() {
        return DbNameMapper.INST;
    }

    /**
     * Convert the result set to a list of objects, using {@link #defaultNameMapper()} and
     * }{@link ObjectConverter#defaultConverter()} to convert the object of the JDBC type to the java type.
     *
     * @param resultSet the result set
     * @param javaType  the element java type of the returned list
     * @param <T>       the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaType,
            defaultNameMapper()
        );
    }

    /**
     * Convert the result set to a list of objects, using {@link #defaultNameMapper()} and
     * {@link ObjectConverter#defaultConverter()} to convert the object of the JDBC type to the java type.
     *
     * @param resultSet the result set
     * @param javaType  the element java type reference of the returned list
     * @param <T>       the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaType
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaType,
            defaultNameMapper()
        );
    }

    /**
     * Convert the result set to a list of objects, using {@link #defaultNameMapper()} and
     * {@link ObjectConverter#defaultConverter()} to convert the object of the JDBC type to the java type.
     *
     * @param resultSet the result set
     * @param javaType  the element java type of the returned list
     * @return the row list of the result set of which each row is mapped to the specified java type
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Immutable @Nonnull List<@Nonnull Object> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaType,
            defaultNameMapper()
        );
    }

    /**
     * Convert the result set to a list of objects, using {@link ObjectConverter#defaultConverter()} to convert the
     * object of the JDBC type to the java type.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type
     * @param <T>        the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nonnull NameMapper nameMapper
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaType,
            nameMapper,
            ObjectConverter.defaultConverter()
        );
    }

    /**
     * Convert the result set to a list of objects, using {@link ObjectConverter#defaultConverter()} to convert the
     * object of the JDBC type to the java type.
     *
     * @param resultSet   the result set
     * @param javaTypeRef the element java type reference of the returned list
     * @param nameMapper  the name mapper to map the column name to the field name of the element type
     * @param <T>         the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaTypeRef,
        @Nonnull NameMapper nameMapper
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaTypeRef,
            nameMapper,
            ObjectConverter.defaultConverter()
        );
    }

    /**
     * Convert the result set to a list of objects, using {@link ObjectConverter#defaultConverter()} to convert the
     * object of the JDBC type to the java type.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type
     * @return the row list of the result set of which each row is mapped to the specified java type
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Immutable @Nonnull List<@Nonnull Object> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull NameMapper nameMapper
    ) throws SqlRuntimeException {
        return toObject(
            resultSet,
            javaType,
            nameMapper,
            ObjectConverter.defaultConverter()
        );
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type
     * @param converter  the converter to convert the object of the JDBC type to the java type
     * @param options    the options for converting
     * @param <T>        the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return Fs.as(toObject(resultSet, (Type) javaType, nameMapper, converter, options));
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet   the result set
     * @param javaTypeRef the element java type reference of the returned list
     * @param nameMapper  the name mapper to map the column name to the field name of the element type
     * @param converter   the converter to convert the object of the JDBC type to the java type
     * @param options     the options for converting
     * @param <T>         the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaTypeRef,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return Fs.as(toObject(resultSet, javaTypeRef.type(), nameMapper, converter, options));
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type
     * @param converter  the converter to convert the object of the JDBC type to the java type
     * @param options    the options for converting
     * @return the row list of the result set of which each row is mapped to the specified java type
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Immutable @Nonnull List<@Nonnull Object> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return Fs.uncheck(() -> resultToObject(resultSet, javaType, nameMapper, converter, options), SqlRuntimeException::new);
    }

    private static <T> @Immutable @Nonnull List<@Nonnull T> resultToObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<T> objects = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> rowMap = new HashMap<>();
            for (int column = 0; column < metaData.getColumnCount(); column++) {
                String columnName = metaData.getColumnName(column + 1);
                Object jdbcObject = resultSet.getObject(column + 1);
                String fieldName = nameMapper.map(columnName);
                rowMap.put(fieldName, jdbcObject);
            }
            Object element = converter.convert(rowMap, javaType, options);
            objects.add(Fs.as(element));
        }
        objects.trimToSize();
        return objects;
    }

    private enum DbNameMapper implements NameMapper {
        INST;

        private final @Nonnull NameFormatter from = NameFormatter.delimiter("_");
        private final @Nonnull NameFormatter to = NameFormatter.lowerCamel();

        @Override
        public @Nonnull String map(@Nonnull String name) {
            String lowerName = name.toLowerCase();
            return from.format(lowerName, to);
        }
    }

    private JdbcKit() {
    }
}
