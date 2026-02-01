package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameFormatter;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.convert.PropertyNameMapper;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utilities for JDBC and SQL.
 *
 * @author sunqian
 */
public class JdbcKit {

    /**
     * Return the default name mapper to map the column name to the field name of the element type. The default name
     * mapper assume the column name is in underscore case (e.g. {@code user_id}) and the field name is in lower camel
     * case (e.g. {@code userId}).
     *
     * @return the default name mapper
     */
    public static @Nonnull PropertyNameMapper defaultNameMapper() {
        return DbNameMapper.INST;
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet the result set
     * @param javaType  the element java type of the returned list
     * @param converter the converter to convert the JDBC type to the java type
     * @param options   the options for converting, e.g. the
     *                  {@link ConvertOption#propertyNameMapper(PropertyNameMapper)} for mapping the column name to the
     *                  field name of the element type during converting
     * @param <T>       the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return toObject(resultSet, (Type) javaType, converter, options);
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet   the result set
     * @param javaTypeRef the element java type reference of the returned list
     * @param converter   the converter to convert the JDBC type to the java type
     * @param options     the options for converting, e.g. the
     *                    {@link ConvertOption#propertyNameMapper(PropertyNameMapper)} for mapping the column name to
     *                    the field name of the element type during converting
     * @param <T>         the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaTypeRef,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return toObject(resultSet, javaTypeRef.type(), converter, options);
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet the result set
     * @param javaType  the element java type of the returned list
     * @param converter the converter to convert the JDBC type to the java type
     * @param options   the options for converting, e.g. the
     *                  {@link ConvertOption#propertyNameMapper(PropertyNameMapper)} for mapping the column name to the
     *                  field name of the element type during converting
     * @param <T>       the element type of the returned list
     * @return the row list of the result set of which each row is mapped to the type {@link T}
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        return Fs.uncheck(() -> resultToObject(resultSet, javaType, converter, options), SqlRuntimeException::new);
    }

    private static <T> @Immutable @Nonnull List<@Nonnull T> resultToObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SQLException {

        // use RowMap to store the result set rows
        // to avoid the problem that the default converter will not convert Map<String, Object> to the same type
        // caused by AssignableConvertHandler
        class RowMap extends HashMap<Object, Object> {}

        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<T> objects = new ArrayList<>();
        RowMap rowMap = new RowMap();
        while (resultSet.next()) {
            rowMap.clear();
            for (int column = 0; column < metaData.getColumnCount(); column++) {
                String columnName = metaData.getColumnName(column + 1);
                Object jdbcObject = resultSet.getObject(column + 1);
                rowMap.put(columnName, jdbcObject);
            }
            Object element = converter.convert(rowMap, javaType, options);
            objects.add(Fs.as(element));
        }
        objects.trimToSize();
        return objects;
    }

    private enum DbNameMapper implements PropertyNameMapper {
        INST;

        private final @Nonnull NameFormatter from = NameFormatter.delimiter("_");
        private final @Nonnull NameFormatter to = NameFormatter.lowerCamel();

        @Override
        public @Nonnull String map(@Nonnull String name, @Nonnull Type srcType) {
            String lowerName = name.toLowerCase();
            return from.format(lowerName, to);
        }
    }

    private JdbcKit() {
    }
}
