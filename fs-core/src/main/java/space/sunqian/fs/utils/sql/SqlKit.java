package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.lang.Tuple2;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    public static <T> @Nonnull T nextRow(
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
    public static <T> @Nonnull T nextRow(
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

    private static @Nonnull Object nextRow0(
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
    public static <T> @Nonnull List<@Nonnull T> readRows(
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
    public static <T> @Nonnull List<@Nonnull T> readRows(
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

    private static @Nonnull List<@Nonnull Object> readRows0(
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
     * Inserts the given value as one row into the specified connection. The table info comes from the class of the
     * value by {@link Object#getClass()}: the table name should be specified by {@link SqlTable} on the class, and the
     * columns should be specified by {@link SqlColumn} on the properties of the class. Only columns whose
     * {@link SqlColumn#autoGenerated()} is false are included.
     * <p>
     * If there exists an auto-generated key column in the table, then this operation will enable the
     * {@link Statement#RETURN_GENERATED_KEYS} feature to get the auto-generated key from the first column of the
     * generated keys result set, and the auto-generated key will be converted to the target type using the given
     * converter.
     *
     * @param <I>          the type to which the auto-generated key is converted
     * @param connection   the specified connection to use for the insert operation
     * @param value        the value to be inserted as one row
     * @param targetType   the target type for the auto-generated key
     * @param introspector the introspector used to introspect the type of the value
     * @param nameMapper   the name mapper used to map names between java and SQL
     * @param converter    the given converter
     * @param options      the options to be used for the conversion process
     * @return the result of the insert operation, including the number of rows inserted and the auto-generated keys if
     * any
     * @throws SqlRuntimeException if any error occurs
     */
    public static <I> @Nonnull SqlInsertResult<I> insertRow(
        @Nonnull Connection connection,
        @Nonnull Object value,
        @Nonnull Class<I> targetType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        Type tableType = value.getClass();
        InsertInfo insertInfo = buildPreparedInsertSql(tableType, introspector, nameMapper);
        try (
            PreparedStatement statement = connection.prepareStatement(
                insertInfo.preparedSql,
                insertInfo.hasAutoGeneratedKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS
            )
        ) {
            int index = 1;
            for (Tuple2<SqlColumn, PropertyMeta> column : insertInfo.columns) {
                Object propertyValue = column.get1().getValue(value);
                setParameter(statement, index++, propertyValue);
            }
            int insertedRows = statement.executeUpdate();
            if (!insertInfo.hasAutoGeneratedKey) {
                return new SqlInsertResult<>(insertedRows, Collections.emptyList());
            }
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                resultSet.next();
                Object generatedKey = resultSet.getObject(1);
                Class<?> generatedKeyType = Fs.nonnull(generatedKey.getClass(), Object.class);
                I autoGeneratedKey = converter.convert(generatedKey, generatedKeyType, targetType, options);
                return new SqlInsertResult<>(insertedRows, Collections.singletonList(autoGeneratedKey));
            }
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Inserts the given values as rows into the specified connection. The table info comes from the class of the first
     * value ({@code values.get(0)}) by {@link Object#getClass()}: the table name should be specified by
     * {@link SqlTable} on the class, and the columns should be specified by {@link SqlColumn} on the properties of the
     * class. Only columns whose {@link SqlColumn#autoGenerated()} is false are included.
     * <p>
     * If there exists an auto-generated key column in the table, then this operation will enable the
     * {@link Statement#RETURN_GENERATED_KEYS} feature to get the auto-generated key from the first column of the
     * generated keys result set, and the auto-generated key will be converted to the target type using the given
     * converter.
     *
     * @param <I>          the type to which the auto-generated key is converted
     * @param connection   the specified connection to use for the insert operation
     * @param values       the values to be inserted as rows
     * @param targetType   the target type for the auto-generated key
     * @param introspector the introspector used to introspect the type of the value
     * @param nameMapper   the name mapper used to map names between java and SQL
     * @param converter    the given converter
     * @param options      the options to be used for the conversion process
     * @return the result of the insert operation, including the number of rows inserted and the auto-generated keys if
     * any
     * @throws SqlRuntimeException if any error occurs
     */
    public static <I> @Nonnull SqlInsertResult<I> insertRows(
        @Nonnull Connection connection,
        @Nonnull List<?> values,
        @Nonnull Class<I> targetType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) throws SqlRuntimeException {
        if (values.isEmpty()) {
            return SqlInsertResult.empty();
        }
        Type tableType = values.get(0).getClass();
        InsertInfo insertInfo = buildPreparedInsertSql(tableType, introspector, nameMapper);
        try (
            PreparedStatement statement = connection.prepareStatement(
                insertInfo.preparedSql,
                insertInfo.hasAutoGeneratedKey ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS
            )
        ) {
            for (Object value : values) {
                int index = 1;
                for (Tuple2<SqlColumn, PropertyMeta> column : insertInfo.columns) {
                    Object propertyValue = column.get1().getValue(value);
                    setParameter(statement, index++, propertyValue);
                }
                statement.addBatch();
            }
            int[] affectedRows = statement.executeBatch();
            int insertedRows = Arrays.stream(affectedRows).sum();
            if (!insertInfo.hasAutoGeneratedKey) {
                return new SqlInsertResult<>(insertedRows, Collections.emptyList());
            }
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                List<I> result = new ArrayList<>(values.size());
                while (resultSet.next()) {
                    Object generatedKey = resultSet.getObject(1);
                    Class<?> generatedKeyType = Fs.nonnull(generatedKey.getClass(), Object.class);
                    result.add(converter.convert(generatedKey, generatedKeyType, targetType, options));
                }
                return new SqlInsertResult<>(insertedRows, result);
            }
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull InsertInfo buildPreparedInsertSql(
        @Nonnull Type tableType,
        @Nonnull ObjectMetaIntrospector introspector,
        @Nonnull SqlNameMapper nameMapper
    ) throws SqlRuntimeException {
        ObjectMeta beanMeta = introspector.introspect(tableType);
        SqlTable sqlTable = beanMeta.annotations().annotation(SqlTable.class);
        if (sqlTable == null) {
            throw new SqlRuntimeException(
                "No SQL table annotation found on " + tableType.getTypeName() + ": " + SqlTable.class.getName() + "."
            );
        }
        String tableName = SqlKit.toTableName(tableType, sqlTable, nameMapper);
        List<Tuple2<SqlColumn, PropertyMeta>> columns = new ArrayList<>();
        boolean hasAutoGeneratedKey = false;
        for (PropertyMeta propertyMeta : beanMeta.properties().values()) {
            SqlColumn sqlColumn = propertyMeta.annotations().annotation(SqlColumn.class);
            if (sqlColumn == null) {
                continue;
            }
            if (sqlColumn.autoGenerated()) {
                hasAutoGeneratedKey = true;
                continue;
            }
            columns.add(Tuple2.of(sqlColumn, propertyMeta));
        }
        if (columns.isEmpty()) {
            throw new SqlRuntimeException("No non-auto-generated SQL column found on " + tableType.getTypeName() + ".");
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        int columnCount = columns.size();
        for (int i = 0; i < columnCount; i++) {
            Tuple2<SqlColumn, PropertyMeta> column = columns.get(i);
            SqlColumn sqlColumn = column.get0();
            PropertyMeta propertyMeta = column.get1();
            sql.append(SqlKit.toColumnName(propertyMeta.name(), sqlColumn, nameMapper));
            if (i < columnCount - 1) {
                sql.append(", ");
            }
        }
        sql.append(") VALUES (");
        for (int i = 0; i < columnCount; i++) {
            sql.append("?");
            if (i < columnCount - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        return new InsertInfo(sql.toString(), columns, hasAutoGeneratedKey);
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

    @SuppressWarnings("ClassCanBeRecord")
    private static final class InsertInfo {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<@Nonnull Tuple2<@Nonnull SqlColumn, @Nonnull PropertyMeta>> columns;
        private final boolean hasAutoGeneratedKey;

        private InsertInfo(
            @Nonnull String preparedSql,
            @Nonnull List<@Nonnull Tuple2<@Nonnull SqlColumn, @Nonnull PropertyMeta>> columns,
            boolean hasAutoGeneratedKey
        ) {
            this.preparedSql = preparedSql;
            this.columns = columns;
            this.hasAutoGeneratedKey = hasAutoGeneratedKey;
        }
    }

    private SqlKit() {
    }
}
