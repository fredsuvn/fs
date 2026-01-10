package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC utilities.
 *
 * @author sunqian
 */
public class JdbcKit {

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type, may be
     *                   {@code null} if the column name is the same as the field name
     * @param converter  the converter to convert the JDBC type to the java type
     * @param options    the options for converting, e.g. the {@link ConvertOption#} for the converter
     * @param <T>        the element type of the returned list
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nullable NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws JdbcException {
        return toObject(resultSet, (Type) javaType, nameMapper, converter, options);
    }

    /**
     * Convert the result set to a list of objects.
     *
     * @param resultSet  the result set
     * @param javaType   the element java type of the returned list
     * @param nameMapper the name mapper to map the column name to the field name of the element type, may be
     *                   {@code null} if the column name is the same as the field name
     * @param converter  the converter to convert the JDBC type to the java type
     * @param options    the options for converting, e.g. the {@link ConvertOption#} for the converter
     * @param <T>        the element type of the returned list
     * @return the list of objects
     * @throws JdbcException if any error occurs
     */
    public static <T> @Immutable @Nonnull List<@Nonnull T> toObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nullable NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws JdbcException {
        return Fs.uncheck(() -> resultToObject(resultSet, javaType, nameMapper, converter, options), JdbcException::new);
    }

    private static <T> @Immutable @Nonnull List<@Nonnull T> resultToObject(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nullable NameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        ArrayList<T> objects = new ArrayList<>();
        Map<String, Object> rowMap = new HashMap<>();
        NameMapper toName = Fs.nonnull(nameMapper, n -> n);
        while (resultSet.next()) {
            rowMap.clear();
            for (int column = 0; column < metaData.getColumnCount(); column++) {
                String columnName = metaData.getColumnName(column + 1);
                Object jdbcObject = resultSet.getObject(column + 1);
                rowMap.put(toName.map(columnName), jdbcObject);
            }
            Object element = converter.convertMap(rowMap, javaType, options);
            objects.add(Fs.as(element));
        }
        objects.trimToSize();
        return objects;
    }

    private JdbcKit() {
    }
}
