/**
 * This package provides JDBC and SQL utilities for database operations, for example:
 * <pre>{@code
 * // Using SqlBuilder for fluent SQL construction
 * List<User> users = SqlBuilder.newBuilder()
 *     .append("SELECT * FROM `users` WHERE 1=1")
 *     .appendIf(searchEnabled, " AND name LIKE ", "%" + searchTerm + "%")
 *     .append(" ORDER BY created_at DESC")
 *     .build()
 *     .query(User.class, connection)
 *     .list();
 * }</pre>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlBuilder}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.PreparedSql}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.PreparedBatchSql}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlQuery}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlInsert}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlUpdate}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlOperation}</li>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlBatch}</li>
 * </ul>
 * Utility classes:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.jdbc.JdbcKit}</li>
 * </ul>
 * Runtime exceptions for SQL operations:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SqlRuntimeException}</li>
 * </ul>
 */
package space.sunqian.fs.utils.jdbc;