package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.lang.Tuple2;
import space.sunqian.fs.base.lang.Tuple3;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.meta.ObjectMeta;
import space.sunqian.fs.object.meta.PropertyMeta;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * Utilities for SQL operations.
 *
 * @author sunqian
 */
public class SqlKit {

    /**
     * Sets the given parameter on the specified statement.
     * <p>
     * If the statement is a {@link CallableStatement} and the parameter is a {@link SqlParameter}, its
     * {@link SqlParameter.Mode mode} determines how it will be set:
     * <ul>
     *     <li>
     *         if the mode is {@link SqlParameter.Mode#OUT}, it will be registered as an output parameter by
     *         {@link CallableStatement#registerOutParameter(int, SQLType)};
     *     </li>
     *     <li>
     *         if the mode is {@link SqlParameter.Mode#IN_OUT}, it will be registered as an output parameter by
     *         {@link CallableStatement#registerOutParameter(int, SQLType)} and then its value will be set by
     *         {@link CallableStatement#setObject(int, Object, SQLType)};
     *     </li>
     *     <li>
     *         otherwise, its value will be set by {@link CallableStatement#setObject(int, Object, SQLType)}.
     *     </li>
     * </ul>
     * <p>
     * If the statement is a {@link CallableStatement} but the parameter is not an instance of {@link SqlParameter}, the
     * mode will be treated as {@link SqlParameter.Mode#IN} and its value will be set by
     * {@link CallableStatement#setObject(int, Object, SQLType)}.
     * <p>
     * In other cases, if the parameter is an instance of {@link SqlParameter}, its value will be set by
     * {@link PreparedStatement#setObject(int, Object, SQLType)}; if the parameter is {@code null}, it will be
     * set with {@link Types#NULL}; otherwise, its value will be set by
     * {@link PreparedStatement#setObject(int, Object)}.
     *
     * @param statement the specified statement, which may be a {@link CallableStatement}
     * @param index     the parameter index of the statement, starting from 1
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
        if (parameter instanceof SqlParameter) {
            try {
                @SuppressWarnings({"PatternVariableCanBeUsed"})
                SqlParameter sqlParameter = (SqlParameter) parameter;
                SQLType type = sqlParameter.sqlType();
                SqlParameter.Mode mode = sqlParameter.mode();
                if (SqlParameter.Mode.OUT.equals(mode)) {
                    statement.registerOutParameter(index, type);
                    return;
                }
                if (SqlParameter.Mode.IN_OUT.equals(mode)) {
                    statement.registerOutParameter(index, type);
                }
                statement.setObject(index, sqlParameter.value(), type);
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
     * @param index      the start parameter index in the statement, must be {@code >= 1}
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
     * Reads the next row from the given {@link ResultSet} and converts it to a bean. If no more rows are available,
     * returns {@code null}.
     *
     * @param resultSet  the given {@link ResultSet} to read from
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper that maps column names to bean property names
     * @param converter  the converter that converts objects from the {@link ResultSet} into the bean
     * @return the converted bean, or {@code null} if no more rows are available
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Nullable Object nextRow(
        @Nonnull ResultSet resultSet,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter
    ) throws SqlRuntimeException {
        try {
            if (!resultSet.next()) {
                return null;
            }
            ResultSetMetaData sqlMeta = resultSet.getMetaData();
            return nextRow0(resultSet, sqlMeta, beanMeta, nameMapper, converter);
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Reads all rows from the given {@link ResultSet} and converts them into a list of beans. If no rows are available,
     * returns an empty list.
     *
     * @param resultSet  the given {@link ResultSet} to read from
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper that maps column names to bean property names
     * @param converter  the converter that converts objects from the {@link ResultSet} into the list of beans
     * @return a list of the converted beans, or an empty list if no rows are available
     * @throws SqlRuntimeException if any error occurs
     */
    public static @Nonnull List<@Nonnull Object> readRows(
        @Nonnull ResultSet resultSet,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter
    ) throws SqlRuntimeException {
        try {
            ArrayList<Object> rows = new ArrayList<>();
            ResultSetMetaData sqlMeta = resultSet.getMetaData();
            while (resultSet.next()) {
                rows.add(nextRow0(resultSet, sqlMeta, beanMeta, nameMapper, converter));
            }
            return rows;
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull Object nextRow0(
        @Nonnull ResultSet resultSet,
        @Nonnull ResultSetMetaData sqlMeta,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull NameMapper nameMapper,
        @Nonnull ObjectConverter converter
    ) throws SQLException {
        Map<String, Object> sqlData = new HashMap<>();
        int columnCount = sqlMeta.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = sqlMeta.getColumnLabel(i);
            String propertyName = nameMapper.map(columnName);
            PropertyMeta propertyMeta = beanMeta.getProperty(propertyName);
            if (propertyMeta == null) {
                continue;
            }
            Object jdbcObject = resultSet.getObject(i);
            sqlData.put(propertyName, jdbcObject);
        }
        return converter.convert(sqlData, beanMeta.type());
    }

    /**
     * Inserts the given bean as a row using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties.
     * <p>
     * Auto-generated columns are not set in this insert operation. If any exist, the statement is prepared with
     * {@link Statement#RETURN_GENERATED_KEYS}; after execution, the key is read from the first column of the result set
     * returned by {@link PreparedStatement#getGeneratedKeys()} and converted into the Java type of one of the
     * auto-generated properties (specified by {@link SqlColumn#autoGenerated()}). If there are multiple auto-generated
     * columns, only one is used, typically the first one found, though the order is not guaranteed.
     * <p>
     * The inserted-row count is the value returned by {@link PreparedStatement#executeUpdate()}.
     *
     * @param connection the given connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param converter  the converter that converts the generated key into the corresponding Java type
     * @param bean       the bean to insert
     * @return the insert result, including the inserted-row count and the generated key, if any
     * @throws SqlRuntimeException if an error occurs
     */
    public static SqlResult.@Nonnull OfInsert insertRow(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Object bean
    ) throws SqlRuntimeException {
        InsertInfo insertInfo = buildPreparedInsertSql(beanMeta, nameMapper);
        PropertyMeta autoGeneratedKeyProperty = insertInfo.autoGeneratedKeyProperty;
        try (
            PreparedStatement statement = autoGeneratedKeyProperty != null ?
                connection.prepareStatement(insertInfo.preparedSql, Statement.RETURN_GENERATED_KEYS)
                :
                connection.prepareStatement(insertInfo.preparedSql)
        ) {
            int index = 1;
            for (Tuple2<SqlColumn, PropertyMeta> column : insertInfo.columns) {
                Object propertyValue = column.get1().getValue(bean);
                setParameter(statement, index++, propertyValue);
            }
            int insertedRows = statement.executeUpdate();
            if (autoGeneratedKeyProperty == null) {
                return SqlResult.ofInsert(insertedRows, Collections.emptyList());
            }
            return nextAutoGeneratedKeys(statement, insertedRows, autoGeneratedKeyProperty, converter);
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Inserts the given beans as rows using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties.
     * <p>
     * Auto-generated columns are not set in this insert operation. If any exist, the statement is prepared with
     * {@link Statement#RETURN_GENERATED_KEYS}; after execution, the keys are read from the first column of the result
     * set returned by {@link PreparedStatement#getGeneratedKeys()} and converted into the Java type of one of the
     * auto-generated properties (specified by {@link SqlColumn#autoGenerated()}). If there are multiple auto-generated
     * columns, only one is used, typically the first one found, though the order is not guaranteed.
     * <p>
     * The inserted-row count is the sum of the values returned by {@link PreparedStatement#executeBatch()}, added
     * directly with no special handling for special update counts such as {@link Statement#SUCCESS_NO_INFO} or
     * {@link Statement#EXECUTE_FAILED}.
     *
     * @param connection the given connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param converter  the converter that converts generated keys into the corresponding Java type
     * @param beans      the beans to insert
     * @return the insert result, including the inserted-row count and the generated keys, if any
     * @throws SqlRuntimeException if an error occurs
     */
    public static SqlResult.@Nonnull OfInsert insertRows(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull Object @Nonnull ... beans
    ) throws SqlRuntimeException {
        if (beans.length == 0) {
            return SqlResult.emptyInsert();
        }
        return insertRows0(connection, beanMeta, nameMapper, converter, Fs.list(beans));
    }

    /**
     * Inserts the given beans as rows using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties.
     * <p>
     * Auto-generated columns are not set in this insert operation. If any exist, the statement is prepared with
     * {@link Statement#RETURN_GENERATED_KEYS}; after execution, the keys are read from the first column of the result
     * set returned by {@link PreparedStatement#getGeneratedKeys()} and converted into the Java type of one of the
     * auto-generated properties (specified by {@link SqlColumn#autoGenerated()}). If there are multiple auto-generated
     * columns, only one is used, typically the first one found, though the order is not guaranteed.
     * <p>
     * The inserted-row count is the sum of the values returned by {@link PreparedStatement#executeBatch()}, added
     * directly with no special handling for special update counts such as {@link Statement#SUCCESS_NO_INFO} or
     * {@link Statement#EXECUTE_FAILED}.
     *
     * @param connection the given connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param converter  the converter that converts generated keys into the corresponding Java type
     * @param beans      the beans to insert
     * @return the insert result, including the inserted-row count and the generated keys, if any
     * @throws SqlRuntimeException if an error occurs
     */
    public static SqlResult.@Nonnull OfInsert insertRows(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull List<@Nonnull ?> beans
    ) throws SqlRuntimeException {
        if (beans.isEmpty()) {
            return SqlResult.emptyInsert();
        }
        return insertRows0(connection, beanMeta, nameMapper, converter, beans);
    }

    private static SqlResult.@Nonnull OfInsert insertRows0(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull ObjectConverter converter,
        @Nonnull List<@Nonnull ?> beans
    ) throws SqlRuntimeException {
        InsertInfo insertInfo = buildPreparedInsertSql(beanMeta, nameMapper);
        PropertyMeta autoGeneratedKeyProperty = insertInfo.autoGeneratedKeyProperty;
        try (
            PreparedStatement statement = autoGeneratedKeyProperty != null ?
                connection.prepareStatement(insertInfo.preparedSql, Statement.RETURN_GENERATED_KEYS)
                :
                connection.prepareStatement(insertInfo.preparedSql)
        ) {
            for (Object value : beans) {
                int index = 1;
                for (Tuple2<SqlColumn, PropertyMeta> column : insertInfo.columns) {
                    Object propertyValue = column.get1().getValue(value);
                    setParameter(statement, index++, propertyValue);
                }
                statement.addBatch();
            }
            int insertedRows = IntStream.of(statement.executeBatch()).sum();
            if (autoGeneratedKeyProperty == null) {
                return SqlResult.ofInsert(insertedRows, Collections.emptyList());
            }
            return nextAutoGeneratedKeys(statement, insertedRows, autoGeneratedKeyProperty, converter);
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull InsertInfo buildPreparedInsertSql(
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper
    ) throws SqlRuntimeException {
        Type tableType = beanMeta.type();
        SqlTable sqlTable = beanMeta.annotations().annotation(SqlTable.class);
        if (sqlTable == null) {
            throw new SqlRuntimeException(
                "No SQL table annotation found on " + tableType.getTypeName() + ": " + SqlTable.class.getName() + "."
            );
        }
        String tableName = SqlKit.toTableName(tableType, sqlTable, nameMapper);
        List<Tuple2<SqlColumn, PropertyMeta>> columns = new ArrayList<>();
        PropertyMeta autoGeneratedKeyProperty = null;
        for (PropertyMeta propertyMeta : beanMeta.properties().values()) {
            SqlColumn sqlColumn = propertyMeta.annotations().annotation(SqlColumn.class);
            if (sqlColumn == null) {
                continue;
            }
            if (sqlColumn.autoGenerated()) {
                if (autoGeneratedKeyProperty == null) {
                    autoGeneratedKeyProperty = propertyMeta;
                }
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
        return new InsertInfo(sql.toString(), columns, autoGeneratedKeyProperty);
    }

    private static SqlResult.@Nonnull OfInsert nextAutoGeneratedKeys(
        @Nonnull PreparedStatement statement,
        int insertedRows,
        @Nonnull PropertyMeta autoGeneratedKeyProperty,
        @Nonnull ObjectConverter converter
    ) throws SQLException {
        try (
            ResultSet resultSet = statement.getGeneratedKeys();
        ) {
            List<Object> result = new ArrayList<>(insertedRows);
            while (resultSet.next()) {
                Object generatedKey = resultSet.getObject(1);
                result.add(converter.convert(generatedKey, autoGeneratedKeyProperty.type()));
            }
            return SqlResult.ofInsert(insertedRows, result);
        }
    }

    /**
     * Updates one row identified by the given primary-key value using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code SET} clause contains every property annotated with {@link SqlColumn} whose value read from
     * {@code bean} is non-null, including the primary-key property when its value is non-null. Therefore, the
     * primary-key value in {@code bean} is not used as the {@code WHERE} condition and may itself be updated by this
     * operation.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code id} only.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param bean       the bean providing the values to update
     * @param id         the primary-key value to match
     * @return the update result, including the number of rows updated
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfUpdate updateById(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object bean,
        @Nonnull Object id
    ) throws SqlRuntimeException {
        return updateByIds0(connection, beanMeta, nameMapper, bean, Fs.list(id));
    }

    /**
     * Updates rows identified by the given primary-key values using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code SET} clause contains every property annotated with {@link SqlColumn} whose value read from
     * {@code bean} is non-null, including the primary-key property when its value is non-null. Therefore, the
     * primary-key value in {@code bean} is not used as the {@code WHERE} condition and may itself be updated by this
     * operation.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code ids} only: a single id uses {@code = ?};
     * multiple ids use {@code IN (?, ...)}.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param bean       the bean providing the values to update
     * @param ids        the primary-key values to match; must contain at least one value
     * @return the update result, including the number of rows updated
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfUpdate updateByIds(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object bean,
        @Nonnull Object @Nonnull ... ids
    ) throws SqlRuntimeException {
        if (ids.length == 0) {
            throw new SqlRuntimeException("Primary keys are empty.");
        }
        return updateByIds0(connection, beanMeta, nameMapper, bean, Fs.list(ids));
    }

    /**
     * Updates rows identified by the given primary-key values using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code SET} clause contains every property annotated with {@link SqlColumn} whose value read from
     * {@code bean} is non-null, including the primary-key property when its value is non-null. Therefore, the
     * primary-key value in {@code bean} is not used as the {@code WHERE} condition and may itself be updated by this
     * operation.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code ids} only: a single id uses {@code = ?};
     * multiple ids use {@code IN (?, ...)}.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param bean       the bean providing the values to update
     * @param ids        the primary-key values to match; must contain at least one value
     * @return the update result, including the number of rows updated
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfUpdate updateByIds(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object bean,
        @Nonnull List<@Nonnull ?> ids
    ) throws SqlRuntimeException {
        if (ids.isEmpty()) {
            throw new SqlRuntimeException("Primary keys are empty.");
        }
        return updateByIds0(connection, beanMeta, nameMapper, bean, ids);
    }

    private static SqlResult.@Nonnull OfUpdate updateByIds0(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object bean,
        @Nonnull List<@Nonnull ?> ids
    ) throws SqlRuntimeException {
        Type tableType = beanMeta.type();
        SqlTable sqlTable = beanMeta.annotations().annotation(SqlTable.class);
        if (sqlTable == null) {
            throw new SqlRuntimeException(
                "No SQL table annotation found on " + tableType.getTypeName() + ": " + SqlTable.class.getName() + "."
            );
        }
        String tableName = SqlKit.toTableName(tableType, sqlTable, nameMapper);
        List<Tuple3<SqlColumn, PropertyMeta, Object>> columns = new ArrayList<>();
        Tuple3<SqlColumn, PropertyMeta, Object> primaryKey = null;
        for (PropertyMeta propertyMeta : beanMeta.properties().values()) {
            SqlColumn sqlColumn = propertyMeta.annotations().annotation(SqlColumn.class);
            if (sqlColumn == null) {
                continue;
            }
            Object propertyValue = propertyMeta.getValue(bean);
            if (sqlColumn.primary()) {
                if (primaryKey != null) {
                    throw new SqlRuntimeException("Multiple primary keys found on " + tableType.getTypeName() + ".");
                }
                primaryKey = Tuple3.of(sqlColumn, propertyMeta, propertyValue);
            }
            if (propertyValue == null) {
                continue;
            }
            columns.add(Tuple3.of(sqlColumn, propertyMeta, propertyValue));
        }
        if (primaryKey == null) {
            throw new SqlRuntimeException("No primary key found on " + tableType.getTypeName() + ".");
        }
        if (columns.isEmpty()) {
            throw new SqlRuntimeException("No updatable columns found on " + tableType.getTypeName() + ".");
        }
        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        int columnCount = columns.size();
        for (int i = 0; i < columnCount; i++) {
            Tuple3<SqlColumn, PropertyMeta, Object> column = columns.get(i);
            SqlColumn sqlColumn = column.get0();
            PropertyMeta propertyMeta = column.get1();
            sql.append(SqlKit.toColumnName(propertyMeta.name(), sqlColumn, nameMapper));
            sql.append(" = ?");
            if (i < columnCount - 1) {
                sql.append(", ");
            }
        }
        sql.append(" WHERE ");
        SqlColumn primaryColumn = primaryKey.get0();
        PropertyMeta primaryMeta = primaryKey.get1();
        sql.append(SqlKit.toColumnName(primaryMeta.name(), primaryColumn, nameMapper));
        if (ids.size() == 1) {
            sql.append(" = ?");
        } else {
            sql.append(" IN (");
            for (int i = 0; i < ids.size(); i++) {
                sql.append("?");
                if (i < ids.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
        try (
            PreparedStatement statement = connection.prepareStatement(sql.toString())
        ) {
            int index = 1;
            for (Tuple3<SqlColumn, PropertyMeta, Object> column : columns) {
                setParameter(statement, index++, column.get2());
            }
            if (ids.size() == 1) {
                setParameter(statement, index, ids.get(0));
            } else {
                for (Object id : ids) {
                    setParameter(statement, index++, id);
                }
            }
            return SqlResult.ofUpdate(statement.executeUpdate());
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    /**
     * Deletes the row or rows identified by the given primary-key value using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code id} only.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param id         the primary-key value to match
     * @return the delete result, including the number of rows deleted
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfDelete deleteById(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object id
    ) throws SqlRuntimeException {
        Tuple2<SqlColumn, PropertyMeta> primaryKey = findPrimaryKey(beanMeta);
        return deleteByIds0(connection, beanMeta, primaryKey, nameMapper, Fs.list(id));
    }

    /**
     * Deletes rows identified by the given primary-key values using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code ids} only: a single id uses {@code = ?};
     * multiple ids use {@code IN (?, ...)}.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param ids        the primary-key values to match; must contain at least one value
     * @return the delete result, including the number of rows deleted
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfDelete deleteByIds(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull Object @Nonnull ... ids
    ) throws SqlRuntimeException {
        if (ids.length == 0) {
            throw new SqlRuntimeException("Primary keys are empty.");
        }
        Tuple2<SqlColumn, PropertyMeta> primaryKey = findPrimaryKey(beanMeta);
        return deleteByIds0(connection, beanMeta, primaryKey, nameMapper, Fs.list(ids));
    }

    /**
     * Deletes rows identified by the given primary-key values using the given connection.
     * <p>
     * The table info is resolved from {@code beanMeta}: the table name comes from the {@link SqlTable} annotation on
     * the bean type, and columns come from the {@link SqlColumn} annotations on the bean properties. Exactly one of
     * those columns is the primary key column: its {@link SqlColumn#primary()} attribute is {@code true}.
     * <p>
     * The {@code WHERE} clause matches the primary-key column by {@code ids} only: a single id uses {@code = ?};
     * multiple ids use {@code IN (?, ...)}.
     *
     * @param connection the connection to use
     * @param beanMeta   the metadata of the bean
     * @param nameMapper the mapper between Java and JDBC names
     * @param ids        the primary-key values to match; must contain at least one value
     * @return the delete result, including the number of rows deleted
     * @throws SqlRuntimeException if any error occurs
     */
    public static SqlResult.@Nonnull OfDelete deleteByIds(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull List<@Nonnull ?> ids
    ) throws SqlRuntimeException {
        if (ids.isEmpty()) {
            throw new SqlRuntimeException("Primary keys are empty.");
        }
        Tuple2<SqlColumn, PropertyMeta> primaryKey = findPrimaryKey(beanMeta);
        return deleteByIds0(connection, beanMeta, primaryKey, nameMapper, ids);
    }

    private static SqlResult.@Nonnull OfDelete deleteByIds0(
        @Nonnull Connection connection,
        @Nonnull ObjectMeta beanMeta,
        @Nonnull Tuple2<@Nonnull SqlColumn, @Nonnull PropertyMeta> primaryKey,
        @Nonnull SqlNameMapper nameMapper,
        @Nonnull List<@Nonnull ?> ids
    ) throws SqlRuntimeException {
        Type tableType = beanMeta.type();
        SqlTable sqlTable = beanMeta.annotations().annotation(SqlTable.class);
        if (sqlTable == null) {
            throw new SqlRuntimeException(
                "No SQL table annotation found on " + tableType.getTypeName() + ": " + SqlTable.class.getName() + "."
            );
        }
        String tableName = SqlKit.toTableName(tableType, sqlTable, nameMapper);
        StringBuilder sql = new StringBuilder("DELETE FROM ").append(tableName).append(" WHERE ");
        SqlColumn primaryColumn = primaryKey.get0();
        PropertyMeta primaryMeta = primaryKey.get1();
        sql.append(SqlKit.toColumnName(primaryMeta.name(), primaryColumn, nameMapper));
        if (ids.size() == 1) {
            sql.append(" = ?");
        } else {
            sql.append(" IN (");
            for (int i = 0; i < ids.size(); i++) {
                sql.append("?");
                if (i < ids.size() - 1) {
                    sql.append(", ");
                }
            }
            sql.append(")");
        }
        try (
            PreparedStatement statement = connection.prepareStatement(sql.toString())
        ) {
            int index = 1;
            for (Object id : ids) {
                setParameter(statement, index++, id);
            }
            return SqlResult.ofDelete(statement.executeUpdate());
        } catch (Exception e) {
            throw new SqlRuntimeException(e);
        }
    }

    private static @Nonnull Tuple2<@Nonnull SqlColumn, @Nonnull PropertyMeta> findPrimaryKey(
        @Nonnull ObjectMeta beanMeta
    ) throws SqlRuntimeException {
        Type tableType = beanMeta.type();
        Tuple2<SqlColumn, PropertyMeta> primaryKey = null;
        for (PropertyMeta propertyMeta : beanMeta.properties().values()) {
            SqlColumn sqlColumn = propertyMeta.annotations().annotation(SqlColumn.class);
            if (sqlColumn == null) {
                continue;
            }
            if (sqlColumn.primary()) {
                if (primaryKey != null) {
                    throw new SqlRuntimeException("Multiple primary keys found on " + tableType.getTypeName() + ".");
                }
                primaryKey = Tuple2.of(sqlColumn, propertyMeta);
            }
        }
        if (primaryKey == null) {
            throw new SqlRuntimeException("No primary key found on " + tableType.getTypeName() + ".");
        }
        return primaryKey;
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
        private final @Nullable PropertyMeta autoGeneratedKeyProperty;

        private InsertInfo(
            @Nonnull String preparedSql,
            @Nonnull List<@Nonnull Tuple2<@Nonnull SqlColumn, @Nonnull PropertyMeta>> columns,
            @Nullable PropertyMeta autoGeneratedKeyProperty
        ) {
            this.preparedSql = preparedSql;
            this.columns = columns;
            this.autoGeneratedKeyProperty = autoGeneratedKeyProperty;
        }
    }

    private SqlKit() {
    }
}
