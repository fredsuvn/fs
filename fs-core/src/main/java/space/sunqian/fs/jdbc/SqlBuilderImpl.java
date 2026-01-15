package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class SqlBuilderImpl implements SqlBuilder {

    private final @Nonnull StringBuilder sqlBuilder = new StringBuilder();
    private @Nullable List<Object> paramList;
    private @Nullable Connection connection;

    @Override
    public @Nonnull SqlBuilder connection(@Nonnull Connection connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public @Nonnull SqlBuilder append(@Nonnull String sql) {
        sqlBuilder.append(sql);
        return this;
    }

    @Override
    public @Nonnull SqlBuilder append(@Nonnull String sql, @Nullable Object param) {
        sqlBuilder.append(sql);
        addParameter(param);
        return this;
    }

    @Override
    public @Nonnull SqlBuilder append(@Nonnull String sql, @Nullable Object @Nonnull ... params) {
        sqlBuilder.append(sql);
        addParameters(params);
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
            sqlBuilder.append(sql);
            addParameter(param);
        }
        return this;
    }

    @Override
    public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nullable Object @Nonnull ... params) {
        if (condition) {
            sqlBuilder.append(sql);
            addParameters(params);
        }
        return this;
    }

    private void addParameter(@Nullable Object param) {
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        paramList.add(param);
    }

    private void addParameters(@Nullable Object @Nonnull ... params) {
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        paramList.addAll(Arrays.asList(params));
    }

    @Override
    public @Nonnull PreparedSql build() {
        if (connection == null) {
            throw new IllegalArgumentException("Connection is not set.");
        }
        String sql = sqlBuilder.toString();
        return new PreparedSqlImpl(
            connection,
            sql,
            paramList == null ? Collections.emptyList() : Collections.unmodifiableList(paramList)
        );
    }
}