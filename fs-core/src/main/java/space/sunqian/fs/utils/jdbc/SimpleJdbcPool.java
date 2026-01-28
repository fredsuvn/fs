// package space.sunqian.fs.utils.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.annotation.Nullable;
// import space.sunqian.fs.object.pool.SimplePool;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.time.Duration;
// import java.util.function.Consumer;
// import java.util.function.Predicate;
//
// /**
//  * Simple JDBC connection pool interface that provides methods for acquiring and releasing database connections. This
//  * pool is built on top of {@link SimplePool} and does not introduce any third-party dependencies.
//  * <p>
//  * Example usage:
//  * <pre>{@code
//  * SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
//  *     .url("jdbc:mysql://localhost:3306/test")
//  *     .user("username")
//  *     .password("password")
//  *     .coreSize(5)
//  *     .maxSize(20)
//  *     .idleTimeout(Duration.ofMinutes(5))
//  *     .build();
//  *
//  * try {
//  *     Connection connection = pool.getConnection();
//  *     // Use the connection for database operations
//  * } finally {
//  *     pool.releaseConnection(connection);
//  * }
//  * }</pre>
//  *
//  * @author sunqian
//  */
// public interface SimpleJdbcPool {
//
//     /**
//      * Returns a builder for {@link SimpleJdbcPool}.
//      *
//      * @return a builder for {@link SimpleJdbcPool}
//      */
//     static @Nonnull Builder newBuilder() {
//         return new Builder();
//     }
//
//     /**
//      * Acquires a database connection from the pool, or {@code null} if no connection is available.
//      * <p>
//      * If any exception occurs during the acquisition process, {@link #close()} will be invoked to close this pool.
//      *
//      * @return the acquired connection, or {@code null} if no connection is available
//      * @throws SqlRuntimeException if failed to acquire connection
//      */
//     @Nullable
//     Connection getConnection() throws SqlRuntimeException;
//
//     /**
//      * Cleans the pool, removing idle connections that have timed out or been invalidated, or over the core size, adding
//      * new connections up to the core size if necessary. The active connections will not be cleaned.
//      * <p>
//      * If any exception occurs during the clean process, {@link #close()} will be invoked to close this pool.
//      *
//      * @throws SqlRuntimeException if any exception occurs during the clean process
//      */
//     void clean() throws SqlRuntimeException;
//
//     /**
//      * Closes the pool and releases all resources. After calling this method, the pool cannot be used anymore. This
//      * method will close all connections in the pool, including idle and active connections.
//      *
//      * @throws SqlRuntimeException if any exception occurs during the close process
//      */
//     void close() throws SqlRuntimeException;
//
//     /**
//      * Returns {@code true} if this pool is closed, {@code false} otherwise.
//      *
//      * @return {@code true} if this pool is closed, {@code false} otherwise
//      */
//     boolean isClosed();
//
//     /**
//      * Returns the total number of connections in this pool, including both idle and active connections.
//      *
//      * @return the total number of connections in this pool
//      */
//     int size();
//
//     /**
//      * Returns the number of idle connections in this pool.
//      *
//      * @return the number of idle connections in this pool
//      */
//     int idleSize();
//
//     /**
//      * Returns the number of active connections in this pool.
//      *
//      * @return the number of active connections in this pool
//      */
//     int activeSize();
//
//     /**
//      * Builder class for {@link SimpleJdbcPool}.
//      */
//     class Builder {
//
//         // Connection validation
//         private static @Nonnull Predicate<@Nonnull Connection> VALIDATOR = connection -> {
//             try {
//                 return !connection.isClosed() && connection.isValid(0);
//             } catch (SQLException e) {
//                 return false;
//             }
//         };
//
//         // JDBC configuration
//         private @Nullable String url;
//         private @Nullable String username;
//         private @Nullable String password;
//         private @Nullable String driver;
//         private @Nonnull Duration connectionTimeout = Duration.ofSeconds(30);
//
//         // Pool configuration
//         private int coreSize = 2;
//         private int maxSize = 10;
//         private @Nonnull Duration idleTimeout = Duration.ofMinutes(5);
//         private @Nullable Predicate<@Nonnull Connection> validator = VALIDATOR;
//
//         /**
//          * Sets the JDBC URL for the database connection.
//          *
//          * @param url the JDBC URL
//          * @return this builder
//          */
//         public @Nonnull Builder url(@Nonnull String url) {
//             this.url = url;
//             return this;
//         }
//
//         /**
//          * Sets the username for the database connection, default is {@code null}.
//          *
//          * @param username the username
//          * @return this builder
//          */
//         public @Nonnull Builder username(@Nullable String username) {
//             this.username = username;
//             return this;
//         }
//
//         /**
//          * Sets the password for the database connection, default is {@code null}.
//          *
//          * @param password the password
//          * @return this builder
//          */
//         public @Nonnull Builder password(@Nullable String password) {
//             this.password = password;
//             return this;
//         }
//
//         /**
//          * Sets the JDBC driver class name.
//          *
//          * @param driver the JDBC driver class name
//          * @return this builder
//          */
//         public @Nonnull Builder driverClassName(@Nullable String driver) {
//             this.driver = driver;
//             return this;
//         }
//
//         /**
//          * Sets the core size of the connection pool. This is the minimum number of connections that will be maintained
//          * in the pool. Default is {@code 2}.
//          *
//          * @param coreSize the core size of the connection pool
//          * @return this builder
//          */
//         public @Nonnull Builder coreSize(int coreSize) {
//             this.coreSize = coreSize;
//             return this;
//         }
//
//         /**
//          * Sets the maximum size of the connection pool. This is the maximum number of connections that can be created
//          * in the pool. Default is {@code 10}.
//          *
//          * @param maxSize the maximum size of the connection pool
//          * @return this builder
//          */
//         public @Nonnull Builder maxSize(int maxSize) {
//             this.maxSize = maxSize;
//             return this;
//         }
//
//         /**
//          * Sets the idle timeout for connections in the pool. Connections that have been idle for longer than this
//          * duration will be eligible for removal during cleanup. Default is {@code 5 minutes}.
//          *
//          * @param idleTimeout the idle timeout for connections
//          * @return this builder
//          */
//         public @Nonnull Builder idleTimeout(@Nonnull Duration idleTimeout) {
//             this.idleTimeout = idleTimeout;
//             return this;
//         }
//
//         /**
//          * Sets the validator for checking connection validity. By default, connections are validated if
//          * <pre>{@code
//          * !connection.isClosed() && connection.isValid(0);
//          * }</pre>.
//          *
//          * @param validator the validator for checking connection validity
//          * @return this builder
//          */
//         public @Nonnull Builder validator(@Nonnull Predicate<@Nonnull Connection> validator) {
//             this.validator = validator;
//             return this;
//         }
//
//         /**
//          * Builds and returns a new {@link SimpleJdbcPool}.
//          *
//          * @return the built new {@link SimpleJdbcPool}
//          * @throws IllegalArgumentException if some configuration is invalid
//          * @throws SqlRuntimeException      if failed to build the pool
//          */
//         public @Nonnull SimpleJdbcPool build() throws IllegalArgumentException, SqlRuntimeException {
//             if (url == null) {
//                 throw new IllegalArgumentException("The url for JDBC connection must be set.");
//             }
//             if (driver == null) {
//                 throw new IllegalArgumentException("The driver class name for JDBC connection must be set.");
//             }
//             SimplePool<PooledConnection> pool = SimplePool.<Connection>newBuilder()
//                 .coreSize(coreSize)
//                 .maxSize(maxSize)
//                 .idleTimeout(idleTimeout)
//                 .validator(validator)
//                 .supplier(supplier)
//                 .validator(pooledValidator)
//                 .discarder(discarder)
//                 .build();
//
//
//             try {
//                 // Load driver if specified
//                 if (driver != null && !driver.isEmpty()) {
//                     Class.forName(driver);
//                 }
//
//                 // Create the pool implementation first
//                 SimpleJdbcPoolImpl poolImpl = new SimpleJdbcPoolImpl();
//
//                 // Create connection supplier
//                 java.util.function.Supplier<PooledConnection> supplier = () -> {
//                     try {
//                         Connection realConnection;
//                         if (username != null && password != null) {
//                             realConnection = DriverManager.getConnection(url, username, password);
//                         } else if (username != null) {
//                             realConnection = DriverManager.getConnection(url, username, "");
//                         } else {
//                             realConnection = DriverManager.getConnection(url);
//                         }
//                         return new PooledConnection(realConnection, poolImpl);
//                     } catch (SQLException e) {
//                         throw new SqlRuntimeException("Failed to create connection", e);
//                     }
//                 };
//
//                 // Create connection discarder
//                 Consumer<PooledConnection> discarder = pooledConnection -> {
//                     try {
//                         if (pooledConnection != null) {
//                             pooledConnection.realClose();
//                         }
//                     } catch (SQLException e) {
//                         // Ignore exceptions during close
//                     }
//                 };
//
//                 // Create validator for pooled connections
//                 Predicate<PooledConnection> pooledValidator = pooledConnection -> {
//                     try {
//                         if (pooledConnection == null || pooledConnection.isPooledConnectionClosed()) {
//                             return false;
//                         }
//                         return validator.test(pooledConnection.getRealConnection());
//                     } catch (Exception e) {
//                         return false;
//                     }
//                 };
//
//
//                 // Initialize the pool implementation with the underlying pool
//                 poolImpl.init(pool);
//
//                 return poolImpl;
//             } catch (ClassNotFoundException e) {
//                 throw new SqlRuntimeException("Failed to load JDBC driver", e);
//             } catch (Exception e) {
//                 throw new SqlRuntimeException("Failed to build connection pool", e);
//             }
//         }
//     }
// }