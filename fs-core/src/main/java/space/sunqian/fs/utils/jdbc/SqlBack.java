package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.Fs;
import space.sunqian.fs.collect.ListKit;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Implementation classes for JDBC interfaces.
 *
 * @author sunqian
 */
final class SqlBack {

    @SuppressWarnings("resource")
    static @Nonnull PreparedStatement createPreparedStatement(@Nonnull PreparedSql preparedSql) throws SQLException {
        Connection connection = preparedSql.connection();
        if (connection == null) {
            throw new SQLException("Connection is not set");
        }
        PreparedStatement statement =
            connection.prepareStatement(preparedSql.preparedSql(), Statement.RETURN_GENERATED_KEYS);
        List<Object> parameters = preparedSql.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
        return statement;
    }

    @SuppressWarnings("resource")
    static @Nonnull PreparedStatement createPreparedStatement(@Nonnull BatchSql batchSql) throws SQLException {
        Connection connection = batchSql.connection();
        if (connection == null) {
            throw new SQLException("Connection is not set");
        }
        PreparedStatement statement =
            connection.prepareStatement(batchSql.preparedSql(), Statement.RETURN_GENERATED_KEYS);
        statement.clearBatch();
        List<List<Object>> batchParameters = batchSql.batchParameters();
        for (List<Object> batchParameter : batchParameters) {
            for (int i = 0; i < batchParameter.size(); i++) {
                statement.setObject(i + 1, batchParameter.get(i));
            }
            statement.addBatch();
        }
        return statement;
    }

    static @Nonnull SqlBuilder newBuilder() {
        return new SqlBuilderImpl();
    }

    static <T> @Nonnull SqlQuery<T> newQuery(
        @Nonnull PreparedStatement statement,
        @Nonnull ResultSet resultSet,
        @Nonnull Type type
    ) {
        return new SqlQueryImpl<>(statement, resultSet, type);
    }

    static @Nonnull SqlUpdate newUpdate(@Nonnull PreparedStatement statement) {
        return new SqlUpdateImpl(statement);
    }

    static @Nonnull SqlInsert newInsert(
        @Nonnull PreparedStatement statement
    ) {
        return new SqlInsertImpl(statement);
    }

    static @Nonnull SqlBatchResult newBatchResult(
        @Nonnull PreparedStatement statement
    ) {
        return new SqlBatchResultImpl(statement);
    }

    private static final class SqlBuilderImpl implements SqlBuilder {

        private final @Nonnull StringBuilder sqlBuilder = new StringBuilder();
        private @Nullable List<Object> parameters;

        @Override
        public @Nonnull SqlBuilder append(@Nonnull String sql) {
            sqlBuilder.append(sql);
            return this;
        }

        @Override
        public @Nonnull SqlBuilder append(@Nonnull String sql, @Nullable Object param) {
            sqlBuilder.append(sql);
            if (param instanceof Collection<?>) {
                Collection<?> collection = (Collection<?>) param;
                parameters().addAll(collection);
                sqlBuilder.append(join(collection));
            } else if (param instanceof Iterable<?>) {
                Iterable<?> iterable = (Iterable<?>) param;
                Collection<?> collection = ListKit.toList(iterable);
                parameters().addAll(collection);
                sqlBuilder.append(join(collection));
            } else {
                // Handle single parameter
                sqlBuilder.append("?");
                parameters().add(param);
            }
            return this;
        }

        private @Nonnull List<Object> parameters() {
            if (parameters == null) {
                parameters = new ArrayList<>();
            }
            return parameters;
        }

        private @Nonnull String join(Collection<?> collection) {
            if (collection.isEmpty()) {
                return "";
            }
            int size = collection.size();
            char[] chars = new char[size * 2 - 1];
            chars[0] = '?';
            for (int i = 1; i < chars.length; i += 2) {
                chars[i] = ',';
                chars[i] = '?';
            }
            return new String(chars);
        }

        @Override
        public @Nonnull PreparedSql build() {
            return new PreparedSqlImpl(
                sqlBuilder.toString(),
                parameters == null ? Collections.emptyList() : parameters
            );
        }

        @Override
        public @Nonnull BatchSql buildBatch() {
            return new BatchSqlImpl(sqlBuilder.toString());
        }
    }

    private static final class PreparedSqlImpl implements PreparedSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<Object> parameters;

        private @Nullable Connection connection;

        private PreparedSqlImpl(@Nonnull String preparedSql, @Nonnull @RetainedParam List<Object> parameters) {
            this.preparedSql = preparedSql;
            this.parameters = parameters;
        }

        @Override
        public @Nonnull String preparedSql() {
            return preparedSql;
        }

        @Override
        public @Nonnull @Immutable List<Object> parameters() {
            return parameters;
        }

        @Override
        public @Nullable Connection connection() {
            return connection;
        }

        @Override
        public @Nonnull PreparedSql connection(@Nonnull Connection connection) throws JdbcException {
            this.connection = connection;
            return this;
        }
    }

    private static final class BatchSqlImpl implements BatchSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<List<Object>> batchedParameters = new ArrayList<>();

        private @Nullable Connection connection;

        private BatchSqlImpl(@Nonnull String preparedSql) {
            this.preparedSql = preparedSql;
        }

        @Override
        public @Nonnull String preparedSql() {
            return preparedSql;
        }

        @Override
        public @Nonnull @Immutable List<@Nonnull List<Object>> batchParameters() {
            return Collections.unmodifiableList(batchedParameters);
        }

        @Override
        public @Nullable Connection connection() {
            return connection;
        }

        @Override
        public @Nonnull BatchSql connection(@Nonnull Connection connection) throws JdbcException {
            this.connection = connection;
            return this;
        }

        @Override
        public @Nonnull BatchSql batchParameters(@Nonnull List<@Nonnull List<Object>> batchParameters) {
            batchedParameters.addAll(batchParameters);
            return this;
        }

        @Override
        public @Nonnull BatchSql parameters(@Nonnull List<Object> parameters) {
            batchedParameters.add(parameters);
            return this;
        }
    }

    private static final class SqlQueryImpl<T> implements SqlQuery<T> {

        private final @Nonnull PreparedStatement statement;
        private final @Nonnull ResultSet resultSet;
        private final @Nonnull Type type;

        private SqlQueryImpl(
            @Nonnull PreparedStatement statement,
            @Nonnull ResultSet resultSet,
            @Nonnull Type type
        ) {
            this.statement = statement;
            this.resultSet = resultSet;
            this.type = type;
        }

        @Override
        public @Nonnull ResultSet resultSet() {
            return resultSet;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull PreparedStatement preparedStatement() {
            return statement;
        }
    }

    private static final class SqlUpdateImpl implements SqlUpdate {

        private final @Nonnull PreparedStatement statement;

        private SqlUpdateImpl(@Nonnull PreparedStatement statement) {
            this.statement = statement;
        }

        @Override
        public long affectedRows() throws JdbcException {
            return Fs.uncheck(() -> {
                statement.executeUpdate();
                return statement.getUpdateCount();
            }, JdbcException::new);
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull PreparedStatement preparedStatement() {
            return statement;
        }
    }

    private static final class SqlInsertImpl implements SqlInsert {

        private final @Nonnull PreparedStatement statement;

        private SqlInsertImpl(@Nonnull PreparedStatement statement) {
            this.statement = statement;
        }

        @Override
        public long insertedRows() throws JdbcException {
            return Fs.uncheck(() -> {
                statement.executeUpdate();
                return statement.getUpdateCount();
            }, JdbcException::new);
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull PreparedStatement preparedStatement() {
            return statement;
        }
    }

    private static final class SqlBatchResultImpl implements SqlBatchResult {

        private final @Nonnull PreparedStatement statement;

        private SqlBatchResultImpl(@Nonnull PreparedStatement statement) {
            this.statement = statement;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull PreparedStatement preparedStatement() {
            return statement;
        }
    }

    private SqlBack() {
    }
}