/**
 * This package provides SQL and JDBC utilities for database operations, for example:
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
 *     <li>{@link space.sunqian.fs.utils.sql.SqlBuilder}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.PreparedSql}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.PreparedBatchSql}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlQuery}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlInsert}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlUpdate}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlOperation}</li>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlBatch}</li>
 * </ul>
 * Utility classes:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlKit}</li>
 * </ul>
 * Runtime exceptions for SQL operations:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.sql.SqlRuntimeException}</li>
 * </ul>
 */
package space.sunqian.fs.utils.sql;