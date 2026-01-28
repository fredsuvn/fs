package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.collect.ListKit;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class SqlBack {

    static @Nonnull PreparedStatement createPreparedStatement(
        @Nonnull PreparedSql preparedSql, @Nonnull Connection connection
    ) throws SQLException {
        PreparedStatement statement =
            connection.prepareStatement(preparedSql.preparedSql(), Statement.RETURN_GENERATED_KEYS);
        List<Object> parameters = preparedSql.parameters();
        for (int i = 0; i < parameters.size(); i++) {
            statement.setObject(i + 1, parameters.get(i));
        }
        return statement;
    }

    static @Nonnull PreparedStatement createPreparedStatement(
        @Nonnull PreparedBatchSql preparedBatchSql, @Nonnull Connection connection
    ) throws SQLException {
        PreparedStatement statement =
            connection.prepareStatement(preparedBatchSql.preparedSql(), Statement.RETURN_GENERATED_KEYS);
        statement.clearBatch();
        List<List<Object>> batchParameters = preparedBatchSql.batchParameters();
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
        @Nonnull Statement statement,
        @Nonnull Type type
    ) {
        return new SqlQueryImpl<>(statement, type);
    }

    static @Nonnull SqlUpdate newUpdate(@Nonnull Statement statement) {
        return new SqlUpdateImpl(statement);
    }

    static @Nonnull SqlInsert newInsert(
        @Nonnull Statement statement
    ) {
        return new SqlInsertImpl(statement);
    }

    static @Nonnull SqlBatch newBatchResult(
        @Nonnull Statement statement
    ) {
        return new SqlBatchImpl(statement);
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
                chars[i + 1] = '?';
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
        public @Nonnull PreparedBatchSql buildBatch() {
            return new PreparedBatchSqlImpl(sqlBuilder.toString());
        }
    }

    private static final class PreparedSqlImpl implements PreparedSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<Object> parameters;

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
    }

    private static final class PreparedBatchSqlImpl implements PreparedBatchSql {

        private final @Nonnull String preparedSql;
        private final @Nonnull List<List<Object>> batchedParameters = new ArrayList<>();

        private @Nullable Connection connection;

        private PreparedBatchSqlImpl(@Nonnull String preparedSql) {
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
        public @Nonnull PreparedBatchSql batchParameters(@Nonnull List<@Nonnull List<Object>> batchParameters) {
            batchedParameters.addAll(batchParameters);
            return this;
        }

        @Override
        public @Nonnull PreparedBatchSql parameters(@Nonnull List<Object> parameters) {
            batchedParameters.add(parameters);
            return this;
        }
    }

    private static final class SqlQueryImpl<T> implements SqlQuery<T> {

        private final @Nonnull Statement statement;
        private final @Nonnull Type type;

        private SqlQueryImpl(
            @Nonnull Statement statement,
            @Nonnull Type type
        ) {
            this.statement = statement;
            this.type = type;
        }

        @Override
        public @Nonnull Type type() {
            return type;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }
    }

    private static final class SqlUpdateImpl implements SqlUpdate {

        private final @Nonnull Statement statement;

        private SqlUpdateImpl(@Nonnull Statement statement) {
            this.statement = statement;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }
    }

    private static final class SqlInsertImpl implements SqlInsert {

        private final @Nonnull Statement statement;
        private volatile @Nullable List<@Nonnull Object> autoGeneratedKeys;

        private SqlInsertImpl(@Nonnull Statement statement) {
            this.statement = statement;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull List<@Nonnull Object> autoGeneratedKeys() throws SqlRuntimeException {
            List<@Nonnull Object> keys = autoGeneratedKeys;
            if (keys == null) {
                keys = SqlInsert.super.autoGeneratedKeys();
                autoGeneratedKeys = keys;
            }
            return keys;
        }
    }

    private static final class SqlBatchImpl implements SqlBatch {

        private final @Nonnull Statement statement;

        private SqlBatchImpl(@Nonnull Statement statement) {
            this.statement = statement;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }
    }

    private SqlBack() {
    }
}