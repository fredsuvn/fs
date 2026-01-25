package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Immutable;
import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameMapper;
import space.sunqian.fs.object.convert.ConvertOption;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.reflect.TypeRef;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Implementation classes for JDBC interfaces.
 *
 * @author sunqian
 */
final class SqlBack {

    private SqlBack() {
    }

    /**
     * Implementation of SqlBuilder interface.
     */
    static final class SqlBuilderImpl implements SqlBuilder {
        private final StringBuilder sqlBuilder = new StringBuilder();
        private final List<Object> parameters = new ArrayList<>();

        @Override
        public @Nonnull SqlBuilder append(@Nonnull String sql) {
            sqlBuilder.append(sql);
            return this;
        }

        @Override
        public @Nonnull SqlBuilder append(@Nonnull String sql, @Nullable Object param) {
            sqlBuilder.append(sql);
            if (param instanceof Iterable && !(param instanceof String)) {
                // Handle iterable parameters (like lists)
                Iterable<?> iterable = (Iterable<?>) param;
                boolean first = true;
                for (Object ignored : iterable) {
                    if (!first) {
                        sqlBuilder.append(",");
                    }
                    sqlBuilder.append("?");
                    first = false;
                }
                // Add all iterable elements as parameters
                for (Object item : iterable) {
                    parameters.add(item);
                }
            } else {
                // Handle single parameter
                sqlBuilder.append("?");
                parameters.add(param);
            }
            return this;
        }

        @Override
        public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql) {
            if (condition) {
                sqlBuilder.append(sql);
            }
            return this;
        }

        @Override
        public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nullable Object param) {
            if (condition) {
                return append(sql, param);
            }
            return this;
        }

        @Override
        public @Nonnull PreparedSql build() {
            return new PreparedSqlImpl(sqlBuilder.toString(), Collections.unmodifiableList(new ArrayList<>(parameters)));
        }
    }

    /**
     * Implementation of PreparedSql interface.
     */
    static final class PreparedSqlImpl implements PreparedSql {
        private final String preparedSql;
        private final List<Object> parameters;
        private Connection connection;

        PreparedSqlImpl(@Nonnull String preparedSql, @Nonnull List<Object> parameters) {
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
        public <T> @Nonnull SqlQuery<T> query(Type type) throws JdbcException {
            return Fs.uncheck(() -> {
                PreparedStatement statement = createPreparedStatement();
                boolean isResultSet = statement.execute();

                if (!isResultSet) {
                    throw new SQLException("Expected a result set, but got an update count");
                }

                ResultSet resultSet = statement.getResultSet();
                return new SqlQueryImpl<>(statement, resultSet, type);
            }, JdbcException::new);
        }

        @Override
        public @Nonnull ResultSet query() throws JdbcException {
            return Fs.uncheck(() -> {
                PreparedStatement statement = createPreparedStatement();
                boolean isResultSet = statement.execute();

                if (!isResultSet) {
                    throw new SQLException("Expected a result set, but got an update count");
                }

                return statement.getResultSet();
            }, JdbcException::new);
        }

        @Override
        public @Nonnull SqlUpdate update() throws JdbcException {
            return Fs.uncheck(() -> {
                PreparedStatement statement = createPreparedStatement();
                boolean isResultSet = statement.execute();

                if (isResultSet) {
                    throw new SQLException("Expected an update count, but got a result set");
                }

                return new SqlUpdateImpl(statement);
            }, JdbcException::new);
        }

        @Override
        public @Nonnull SqlInsert insert() throws JdbcException {
            return Fs.uncheck(() -> {
                PreparedStatement statement = createPreparedStatement();
                boolean isResultSet = statement.execute();

                if (isResultSet) {
                    throw new SQLException("Expected an update count, but got a result set");
                }

                // Get auto-generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();
                List<Object> keys = new ArrayList<>();
                while (generatedKeys.next()) {
                    keys.add(generatedKeys.getObject(1));
                }
                generatedKeys.close();

                return new SqlInsertImpl(statement, keys);
            }, JdbcException::new);
        }

        @Override
        public @Nonnull PreparedSql connection(@Nonnull Connection connection) throws JdbcException {
            this.connection = connection;
            return this;
        }

        private PreparedStatement createPreparedStatement() throws SQLException {
            if (connection == null) {
                throw new SQLException("Connection is not set");
            }

            PreparedStatement statement = connection.prepareStatement(preparedSql, Statement.RETURN_GENERATED_KEYS);

            // Bind parameters
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            return statement;
        }
    }

    /**
     * Implementation of SqlQuery interface.
     */
    static final class SqlQueryImpl<T> implements SqlQuery<T> {
        private final PreparedStatement statement;
        private final ResultSet resultSet;
        private final Type type;

        SqlQueryImpl(@Nonnull PreparedStatement statement, @Nonnull ResultSet resultSet, @Nonnull Type type) {
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

        @Override
        public void close() throws JdbcException {
            Fs.uncheck(() -> {
                try {
                    if (resultSet != null && !resultSet.isClosed()) {
                        resultSet.close();
                    }
                } finally {
                    if (statement != null && !statement.isClosed()) {
                        statement.close();
                    }
                }
            }, JdbcException::new);
        }

        @Override
        public @Nonnull List<@Nonnull T> resultList(
            @Nullable NameMapper columnMapper,
            @Nullable ObjectConverter converter,
            @Nonnull Option<?, ?> @Nonnull ... options
        ) throws JdbcException {
            return JdbcKit.toObject(
                resultSet(),
                type(),
                columnMapper,
                Fs.nonnull(converter, ObjectConverter.defaultConverter()),
                options
            );
        }
    }

    /**
     * Implementation of SqlUpdate interface.
     */
    static final class SqlUpdateImpl implements SqlUpdate {
        private final PreparedStatement statement;

        SqlUpdateImpl(@Nonnull PreparedStatement statement) {
            this.statement = statement;
        }

        @Override
        public long affectedRows() throws JdbcException {
            return Fs.uncheck(() -> {
                try {
                    return statement.getUpdateCount();
                } catch (SQLException e) {
                    throw new JdbcException("Failed to get update count", e);
                }
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

        @Override
        public void close() throws JdbcException {
            Fs.uncheck(() -> {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            }, JdbcException::new);
        }
    }

    /**
     * Implementation of SqlInsert interface.
     */
    static final class SqlInsertImpl implements SqlInsert {
        private final PreparedStatement statement;
        private final List<Object> autoGeneratedKeys;

        SqlInsertImpl(@Nonnull PreparedStatement statement, @Nonnull List<Object> autoGeneratedKeys) {
            this.statement = statement;
            this.autoGeneratedKeys = Collections.unmodifiableList(autoGeneratedKeys);
        }

        @Override
        public long insertedRows() throws JdbcException {
            return Fs.uncheck(() -> {
                try {
                    return statement.getUpdateCount();
                } catch (SQLException e) {
                    throw new JdbcException("Failed to get update count", e);
                }
            }, JdbcException::new);
        }

        @Override
        public @Immutable @Nonnull List<@Nonnull Object> autoGeneratedKeys() {
            return autoGeneratedKeys;
        }

        @Override
        public @Nonnull Statement statement() {
            return statement;
        }

        @Override
        public @Nonnull PreparedStatement preparedStatement() {
            return statement;
        }

        @Override
        public void close() throws JdbcException {
            Fs.uncheck(() -> {
                if (statement != null && !statement.isClosed()) {
                    statement.close();
                }
            }, JdbcException::new);
        }
    }
}