/**
 * This package provides JDBC and SQL utilities for database operations, for example:
 * <pre>{@code
 * // Quickly create a connection pool to get a connection:
 * SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
 *     .driverClassName(className)
 *     .url(url)
 *     .username(username)
 *     .password(password)
 *     .build();
 * Connection connection = pool.getConnection();
 * // Using SqlBuilder for fluent SQL construction:
 * List<User> users = SqlBuilder.newBuilder()
 *     .append("SELECT * FROM `users` WHERE 1=1")
 *     .appendIf(searchEnabled, " AND name LIKE ", "%" + searchTerm + "%")
 *     .append(" ORDER BY created_at DESC")
 *     .build()
 *     .query(User.class, connection)
 *     .list();
 * // Release (but not actually close) the connection pool:
 * connection.close();
 * // Close the connection pool, all connections in the pool will be closed:
 * pool.close();
 * }</pre>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.utils.jdbc.SimpleJdbcPool}</li>
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