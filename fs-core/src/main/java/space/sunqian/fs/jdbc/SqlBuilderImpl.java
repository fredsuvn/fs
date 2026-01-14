package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

final class SqlBuilderImpl implements SqlBuilder {

    private final @Nonnull StringBuilder sqlBuilder = new StringBuilder();
    private  List<Object> paramList;

    @Override
    public @Nonnull SqlBuilder append(@Nonnull String sql) {
        sqlBuilder.append(sql);
        return this;
    }

    @Override
    public @Nonnull SqlBuilder append(@Nonnull String sql, @Nonnull Object @Nonnull ... params) {
        sqlBuilder.append(sql);
        if (paramList == null) {
            paramList = new ArrayList<>();
        }
        //paramList.addAll(List.of(params));
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
    public @Nonnull SqlBuilder appendIf(boolean condition, @Nonnull String sql, @Nonnull Object @Nonnull ... params) {
        if (condition) {
            sqlBuilder.append(sql);
        }
        return this;
    }

    @Override
    public @Nonnull PreparedSql build() {
       // return new PreparedSqlImpl(sqlBuilder.toString());
        return null;
    }
}
