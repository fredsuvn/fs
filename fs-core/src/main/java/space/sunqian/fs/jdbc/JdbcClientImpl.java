// package space.sunqian.fs.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.annotation.Nullable;
// import space.sunqian.fs.object.pool.SimplePool;
// import space.sunqian.fs.object.pool.ObjectPoolException;
//
// import java.sql.Connection;
// import java.sql.DriverManager;
// import java.sql.SQLException;
// import java.util.function.Supplier;
//
// /**
//  * Implementation of {@link JdbcClient} using {@link SimplePool} for connection management.
//  */
// final class JdbcClientImpl implements JdbcClient {
//
//     private final @Nonnull SimplePool<Connection> connectionPool;
//     private volatile boolean closed = false;
//
//     JdbcClientImpl(@Nonnull SimplePool<Connection> connectionPool) {
//         this.connectionPool = connectionPool;
//     }
//
//     @Override
//     public @Nonnull Connection getConnection() throws JdbcException {
//         checkClosed();
//
//         try {
//             Connection connection = connectionPool.get();
//             if (connection == null) {
//                 throw new JdbcException("No available connection in pool");
//             }
//             return connection;
//         } catch (ObjectPoolException e) {
//             throw new JdbcException("Failed to acquire connection from pool", e);
//         }
//     }
//
//     @Override
//     public void close() throws JdbcException {
//         if (closed) {
//             return;
//         }
//
//         try {
//             connectionPool.close();
//             closed = true;
//         } catch (Exception e) {
//             throw new JdbcException("Failed to close JdbcClient", e);
//         }
//     }
//
//     private void checkClosed() throws JdbcException {
//         if (closed) {
//             throw new JdbcException("JdbcClient is closed");
//         }
//     }
//
//     /**
//      * Builder implementation for JdbcClient.
//      */
//     static final class BuilderImpl implements Builder {
//
//         private String url;
//         private String username;
//         private String password;
//         private int coreSize = 1;
//         private int maxSize = 10;
//         private long idleTimeoutMillis = 60000;
//
//         @Override
//         public @Nonnull Builder url(@Nonnull String url) {
//             this.url = url;
//             return this;
//         }
//
//         @Override
//         public @Nonnull Builder username(@Nonnull String username) {
//             this.username = username;
//             return this;
//         }
//
//         @Override
//         public @Nonnull Builder password(@Nonnull String password) {
//             this.password = password;
//             return this;
//         }
//
//         @Override
//         public @Nonnull Builder coreSize(int coreSize) {
//             this.coreSize = coreSize;
//             return this;
//         }
//
//         @Override
//         public @Nonnull Builder maxSize(int maxSize) {
//             this.maxSize = maxSize;
//             return this;
//         }
//
//         @Override
//         public @Nonnull Builder idleTimeout(long idleTimeoutMillis) {
//             this.idleTimeoutMillis = idleTimeoutMillis;
//             return this;
//         }
//
//         @Override
//         public @Nonnull JdbcClient build() throws JdbcException {
//             if (url == null) {
//                 throw new JdbcException("URL must be set");
//             }
//
//             Supplier<Connection> connectionSupplier = () -> {
//                 try {
//                     if (username != null && password != null) {
//                         return DriverManager.getConnection(url, username, password);
//                     } else {
//                         return DriverManager.getConnection(url);
//                     }
//                 } catch (SQLException e) {
//                     throw new RuntimeException("Failed to create connection", e);
//                 }
//             };
//
//             SimplePool<Connection> pool = SimplePool.newBuilder()
//                 .supplier(connectionSupplier)
//                 .coreSize(coreSize)
//                 .maxSize(maxSize)
//                 .idleTimeout(idleTimeoutMillis)
//                 .validator(connection -> {
//                     try {
//                         return connection != null && !connection.isClosed() && connection.isValid(5);
//                     } catch (SQLException e) {
//                         return false;
//                     }
//                 })
//                 .discarder(connection -> {
//                     if (connection != null) {
//                         try {
//                             connection.close();
//                         } catch (SQLException e) {
//                             // Ignore close errors
//                         }
//                     }
//                 })
//                 .build();
//
//             return new JdbcClientImpl(pool);
//         }
//     }
// }