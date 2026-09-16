package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.ObjectMetaIntrospector;
import space.sunqian.fs.object.meta.PropertyMeta;
import space.sunqian.fs.reflect.TypeRef;
import space.sunqian.fs.utils.sql.annotation.SqlColumn;
import space.sunqian.fs.utils.sql.annotation.SqlTable;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for SQL operations.
 *
 * @author sunqian
 */
public class SqlKit {

    /**
     * Sets the given parameter on the specified statement.
     * <p>
     * If the statement is a {@link CallableStatement} and the parameter is a {@link SqlCallableParameter}, its
     * {@link SqlCallableParameter.Mode mode} determines how it will be set:
     * <ul>
     *     <li>
     *         if the mode is {@link SqlCallableParameter.Mode#OUT}, it will be registered as an output parameter by
     *         {@link CallableStatement#registerOutParameter(int, SQLType)};
     *     </li>
     *     <li>
     *         if the mode is {@link SqlCallableParameter.Mode#IN_OUT}, it will be registered as an output parameter by
     *         {@link CallableStatement#registerOutParameter(int, SQLType)} and then set by
     *         {@link CallableStatement#setObject(int, Object, SQLType)};
     *     </li>
     *     <li>
     *         otherwise, it will be set by {@link CallableStatement#setObject(int, Object, SQLType)}.
     *     </li>
     * </ul>
     * <p>
     * Otherwise, if the parameter is an instance of {@link SqlParameter}, its value will be set as the specified SQL
     * type by {@link PreparedStatement#setObject(int, Object, SQLType)}; if the parameter is {@code null}, it will be
     * set as {@link Types#NULL}; otherwise, the parameter will be set by
     * {@link PreparedStatement#setObject(int, Object)}. Note if the statement is a {@link CallableStatement} but the
     * parameter is not a {@link SqlCallableParameter}, the mode will be considered as
     * {@link SqlCallableParameter.Mode#IN}.
     *
     * @param statement the specified statement, which may be a {@link CallableStatement}
     * @param index     the parameter index of the statement
     * @param parameter the given parameter
     * @throws SqlRuntimeException if any error occurs
     */
    public static void setParameter(
        @Nonnull PreparedStatement statement,
        int index,
        @Nullable Object parameter
    ) throws SqlRuntimeException {
        if (statement instanceof CallableStatement) {
            setCallableStatement((CallableStatement) statement, index, parameter);
            return;
        }
        setPreparedStatement(statement, index, parameter);
    }

    private static void setPreparedStatement(
        @Nonnull PreparedStatement statement,
        int index,
        @Nullable Object parameter
    ) throws SqlRuntimeException {
        try {
            if (parameter == null) {
                statement.setNull(index, Types.NULL);
            } else if (parameter instanceof SqlParameter) {
                @SuppressWarnings({"PatternVariableCanBeUsed"})
                SqlParameter sqlParameter = (SqlParameter) parameter;
                SQLType type = sqlParameter.sqlType();
                statement.setObject(index, sqlParameter.value(), type);
            } else {
                statement.setObject(index, parameter);
            }
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static void setCallableStatement(
        @Nonnull CallableStatement statement,
        int index,
        @Nullable Object parameter
    ) throws SqlRuntimeException {
        if (parameter instanceof SqlCallableParameter) {
            try {
                @SuppressWarnings({"PatternVariableCanBeUsed"})
                SqlCallableParameter callableParameter = (SqlCallableParameter) parameter;
                SQLType type = callableParameter.sqlType();
                SqlCallableParameter.Mode mode = callableParameter.mode();
                if (SqlCallableParameter.Mode.OUT.equals(mode)) {
                    statement.registerOutParameter(index, type);
                    return;
                }
                if (SqlCallableParameter.Mode.IN_OUT.equals(mode)) {
                    statement.registerOutParameter(index, type);
                }
                statement.setObject(index, callableParameter.value(), type);
            } catch (Exception e) {
                throw new SqlRuntimeException(e);
            }
        } else {
            setPreparedStatement(statement, index, parameter);
        }
    }

    /**
     * Sets the given parameters on the specified statement starting from index 1. Each parameter will be set by
     * {@link #setParameter(PreparedStatement, int, Object)} in the order of the list.
     *
     * @param statement  the specified statement, which may be a {@link CallableStatement}
     * @param parameters the given parameters
     * @throws SqlRuntimeException if any error occurs
     */
    public static void setParameters(
        @Nonnull PreparedStatement statement,
        @Nonnull List<?> parameters
    ) throws SqlRuntimeException {
        setParameters(statement, 1, parameters);
    }

    /**
     * Sets the given parameters on the specified statement starting from the given index. Each parameter will be set by
     * {@link #setParameter(PreparedStatement, int, Object)} in the order of the list.
     *
     * @param statement  the specified statement, which may be a {@link CallableStatement}
     * @param index      the start index, must be {@code >= 1}
     * @param parameters the given parameters
     * @throws SqlRuntimeException if any error occurs
     */
    public static void setParameters(
        @Nonnull PreparedStatement statement,
        int index,
        @Nonnull List<?> parameters
    ) throws SqlRuntimeException {
        try {
            // preparedStatement.clearParameters();
            int i = index;
            for (Object parameter : parameters) {
                setParameter(statement, i++, parameter);
            }
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Sets the given parameter batches on the specified statement. Each batch will be set by
     * {@link #setParameters(PreparedStatement, List)}.
     *
     * @param statement the specified statement, which may be a {@link CallableStatement}
     * @param batches   the given parameter batches
     * @throws SqlRuntimeException if any error occurs
     */
    public static void setParameterBatches(
        @Nonnull PreparedStatement statement,
        @Nonnull List<@Nonnull List<?>> batches
    ) throws SqlRuntimeException {
        try {
            // preparedStatement.clearBatch();
            for (List<?> parameters : batches) {
                setParameters(statement, parameters);
                statement.addBatch();
            }
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Reads the next row from the given {@link ResultSet} and converts it to the specified Java type.
     *
     * @param <T>              the specified Java type
     * @param resultSet        the given {@link ResultSet}
     * @param javaType         the specified Java type
     * @param introspector     the introspector used to introspect the specified Java type
     * @param columnNameMapper the name mapper used to map the column name to the property name of the specified Java
     *                         type
     * @param converter        the converter used to convert the JDBC type to the Java type
     * @param options          the options for the converter
     * @return the converted Java object
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> T nextRow(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        try {
            return Fs.as(nextRow0(resultSet, javaType, introspector, columnNameMapper, converter, options));
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Reads the next row from the given {@link ResultSet} and converts it to the specified Java type.
     *
     * @param <T>              the specified Java type
     * @param resultSet        the given {@link ResultSet}
     * @param javaTypeRef      the reference to the specified Java type
     * @param introspector     the introspector used to introspect the specified Java type
     * @param columnNameMapper the name mapper used to map the column name to the property name of the specified Java
     *                         type
     * @param converter        the converter used to convert the JDBC type to the Java type
     * @param options          the options for the converter
     * @return the converted Java object
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> T nextRow(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaTypeRef,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        try {
            return Fs.as(nextRow0(resultSet, javaTypeRef.type(), introspector, columnNameMapper, converter, options));
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static Object nextRow0(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SQLException {
        ObjectMeta javaMeta = introspector.introspect(javaType);
        Map<String, Object> sqlData = new HashMap<>();
        ResultSetMetaData sqlMeta = resultSet.getMetaData();
        resultSet.next();
        int columnCount = sqlMeta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = sqlMeta.getColumnName(i);
            String propertyName = columnNameMapper.map(columnName);
            PropertyMeta propertyMeta = javaMeta.getProperty(propertyName);
            if (propertyMeta == null) {
                continue;
            }
            Object jdbcObject = resultSet.getObject(i);
            sqlData.put(propertyName, jdbcObject);
        }
        return converter.convert(sqlData, javaType, options);
    }

    /**
     * Reads the next row from the given {@link ResultSet} and converts it to a {@link Map}.
     *
     * @param resultSet        the given {@link ResultSet}
     * @param columnNameMapper the name mapper used to map the column name to the key of the returned map
     * @return the converted {@link Map}
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Nonnull Map<@Nonnull String, Object> nextRow(
        @Nonnull ResultSet resultSet,
        @Nonnull NameMapper columnNameMapper
    ) throws SqlRuntimeException {
        try {
            return nextRow0(resultSet, columnNameMapper);
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull Map<@Nonnull String, Object> nextRow0(
        @Nonnull ResultSet sqlResult,
        @Nonnull NameMapper columnNameMapper
    ) throws SQLException {
        Map<String, Object> sqlData = new HashMap<>();
        ResultSetMetaData sqlMeta = sqlResult.getMetaData();
        sqlResult.next();
        int columnCount = sqlMeta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = sqlMeta.getColumnName(i);
            String key = columnNameMapper.map(columnName);
            Object jdbcObject = sqlResult.getObject(i);
            sqlData.put(key, jdbcObject);
        }
        return sqlData;
    }

    /**
     * Reads all rows from the given {@link ResultSet} and converts them to the specified Java type.
     *
     * @param <T>              the specified Java type
     * @param resultSet        the given {@link ResultSet}
     * @param javaType         the specified Java type
     * @param introspector     the introspector used to introspect the specified Java type
     * @param columnNameMapper the name mapper used to map the column name to the property name of the specified Java
     *                         type
     * @param converter        the converter used to convert the JDBC type to the Java type
     * @param options          the options for the converter
     * @return a list of the converted Java objects
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Nonnull List<T> readRows(
        @Nonnull ResultSet resultSet,
        @Nonnull Class<T> javaType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        try {
            return Fs.as(readRows0(resultSet, javaType, introspector, columnNameMapper, converter, options));
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Reads all rows from the given {@link ResultSet} and converts them to the specified Java type.
     *
     * @param <T>              the specified Java type
     * @param resultSet        the given {@link ResultSet}
     * @param javaTypeRef      the type reference to the specified Java type
     * @param introspector     the introspector used to introspect the specified Java type
     * @param columnNameMapper the name mapper used to map the column name to the property name of the specified Java
     *                         type
     * @param converter        the converter used to convert the JDBC type to the Java type
     * @param options          the options for the converter
     * @return a list of the converted Java objects
     * @throws SqlRuntimeException if any error occurs
     */
    public static <T> @Nonnull List<T> readRows(
        @Nonnull ResultSet resultSet,
        @Nonnull TypeRef<T> javaTypeRef,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        try {
            return Fs.as(readRows0(resultSet, javaTypeRef.type(), introspector, columnNameMapper, converter, options));
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull List<Object> readRows0(
        @Nonnull ResultSet resultSet,
        @Nonnull Type javaType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull NameMapper columnNameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SQLException {
        ObjectMeta javaMeta = introspector.introspect(javaType);
        Map<String, Object> sqlData = new HashMap<>();
        ResultSetMetaData sqlMeta = resultSet.getMetaData();
        int columnCount = sqlMeta.getColumnCount();
        List<String> propertyNames = resolveSqlMeta(sqlMeta, columnNameMapper);
        ArrayList<Object> objects = new ArrayList<>();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                Object jdbcObject = resultSet.getObject(i);
                String propertyName = propertyNames.get(i - 1);
                PropertyMeta propertyMeta = javaMeta.getProperty(propertyName);
                if (propertyMeta == null) {
                    continue;
                }
                sqlData.put(propertyName, jdbcObject);
            }
            Object javaData = converter.convert(sqlData, javaType, options);
            objects.add(javaData);
            sqlData.clear();
        }
        objects.trimToSize();
        return objects;
    }

    /**
     * Reads all rows from the given {@link ResultSet} and converts them to a list of {@link Map}.
     *
     * @param resultSet        the given {@link ResultSet}
     * @param columnNameMapper the name mapper used to map the column name to the key of the {@link Map}s
     * @return a list of the converted {@link Map}s
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Nonnull List<@Nonnull Map<@Nonnull String, Object>> readRows(
        @Nonnull ResultSet resultSet,
        @Nonnull NameMapper columnNameMapper
    ) throws SqlRuntimeException {
        try {
            return readRows0(resultSet, columnNameMapper);
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull List<@Nonnull Map<@Nonnull String, Object>> readRows0(
        @Nonnull ResultSet resultSet,
        @Nonnull NameMapper columnNameMapper
    ) throws SQLException {
        ResultSetMetaData sqlMeta = resultSet.getMetaData();
        int columnCount = sqlMeta.getColumnCount();
        List<String> keys = resolveSqlMeta(sqlMeta, columnNameMapper);
        List<Map<String, Object>> objects = new ArrayList<>();
        while (resultSet.next()) {
            Map<String, Object> sqlData = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                Object jdbcObject = resultSet.getObject(i);
                String key = keys.get(i - 1);
                sqlData.put(key, jdbcObject);
            }
            objects.add(sqlData);
        }
        return objects;
    }

    private static @Nonnull List<@Nonnull String> resolveSqlMeta(
        @Nonnull ResultSetMetaData sqlMeta,
        @Nonnull NameMapper columnNameMapper
    ) throws SQLException {
        List<String> columnNames = new ArrayList<>(sqlMeta.getColumnCount());
        for (int i = 1; i <= sqlMeta.getColumnCount(); i++) {
            String columnName = sqlMeta.getColumnName(i);
            String propertyName = columnNameMapper.map(columnName);
            columnNames.add(propertyName);
        }
        return columnNames;
    }

    /**
     * Returns the column name mapped from the specified property which is annotated by {@link SqlColumn}.
     *
     * @param propertyName     the name of the specified property
     * @param sqlColumn        the {@link SqlColumn} annotation on the specified property
     * @param columnNameMapper the default mapping policy if the {@link SqlColumn#value()} of the {@link SqlColumn} is
     *                         empty
     * @return the mapped column name
     */
    public static @Nonnull String toColumnName(
        @Nonnull String propertyName,
        @Nonnull SqlColumn sqlColumn,
        @Nonnull SqlNameMapper columnNameMapper
    ) {
        String value = sqlColumn.value();
        if (value.isEmpty()) {
            return columnNameMapper.toColumnName(propertyName);
        }
        return value;
    }

    /**
     * Returns the table name mapped from the specified java type which is annotated by {@link SqlTable}.
     *
     * @param typeName        the type name of the specified java type
     * @param sqlTable        the {@link SqlTable} annotation on the specified java type
     * @param tableNameMapper the default mapping policy if the {@link SqlTable#value()} of the {@link SqlTable} is
     *                        empty
     * @return the mapped table name
     */
    public static @Nonnull String toTableName(
        @Nonnull String typeName,
        @Nonnull SqlTable sqlTable,
        @Nonnull SqlNameMapper tableNameMapper
    ) {
        String value = sqlTable.value();
        if (value.isEmpty()) {
            return tableNameMapper.toTableName(typeName);
        }
        return value;
    }

    /**
     * Returns the table name mapped from the specified java type which is annotated by {@link SqlTable}.
     *
     * @param javaType        the specified java type
     * @param sqlTable        the {@link SqlTable} annotation on the specified java type
     * @param tableNameMapper the default mapping policy if the {@link SqlTable#value()} of the {@link SqlTable} is
     *                        empty
     * @return the mapped table name
     */
    public static @Nonnull String toTableName(
        @Nonnull Type javaType,
        @Nonnull SqlTable sqlTable,
        @Nonnull SqlNameMapper tableNameMapper
    ) {
        String value = sqlTable.value();
        if (value.isEmpty()) {
            return tableNameMapper.toTableName(javaType);
        }
        return value;
    }

    private SqlKit() {
    }
}
