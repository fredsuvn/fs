// package space.sunqian.fs.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.collect.ListKit;
//
// import java.util.ArrayList;
// import java.util.List;
//
// final class SqlMakerImpl implements SqlMaker {
//
//     private StringBuilder sqlBuilder;
//     private List<@Nonnull Object> sqlParams;
//
//
//     @Override
//     public @Nonnull SqlMaker sql(@Nonnull String sql) {
//         sqlBuilder().append(sql);
//         return this;
//     }
//
//     @Override
//     public @Nonnull SqlMaker sql(@Nonnull String sql, @Nonnull Object @Nonnull ... params) {
//         sqlBuilder().append(sql);
//         sqlParams().addAll(ListKit.list(params));
//         return this;
//     }
//
//     @Override
//     public @Nonnull SqlMaker condition(boolean condition, @Nonnull String sql) {
//         if (condition) {
//             return sql(sql);
//         }
//         return this;
//     }
//
//     @Override
//     public @Nonnull SqlMaker condition(boolean condition, @Nonnull String sql, @Nonnull Object @Nonnull ... params) {
//         if (condition) {
//             return sql(sql, params);
//         }
//         return this;
//     }
//
//     private @Nonnull StringBuilder sqlBuilder() {
//         if (sqlBuilder == null) {
//             sqlBuilder = new StringBuilder();
//         }
//         return sqlBuilder;
//     }
//
//     private @Nonnull List<@Nonnull Object> sqlParams() {
//         if (sqlParams == null) {
//             sqlParams = new ArrayList<>();
//         }
//         return sqlParams;
//     }
// }
