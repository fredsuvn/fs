// package space.sunqian.fs.utils.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.annotation.Nullable;
// import space.sunqian.fs.object.pool.SimplePool;
//
// import java.sql.Connection;
// import java.sql.SQLException;
// import java.util.List;
// import java.util.function.Predicate;
//
// /**
//  * Implementation of {@link SimpleJdbcPool} that wraps a {@link SimplePool} of {@link Connection} objects.
//  *
//  * @author sunqian
//  */
// final class SimpleJdbcPoolImpl implements SimpleJdbcPool {
//
//     private @Nonnull SimplePool<PooledConnection> pool;
//
//     // Default constructor for use by Builder
//     SimpleJdbcPoolImpl() {
//     }
//
//     // Constructor for direct initialization (not used by Builder)
//     SimpleJdbcPoolImpl(@Nonnull SimplePool<PooledConnection> pool) {
//         this.pool = pool;
//     }
//
//     // Initialize method called by Builder
//     void init(@Nonnull SimplePool<PooledConnection> pool) {
//         this.pool = pool;
//     }
//
//     @Override
//     public @Nullable Connection getConnection() throws SqlRuntimeException {
//         try {
//             return pool.get();
//         } catch (Exception e) {
//             throw new SqlRuntimeException("Failed to get connection from pool", e);
//         }
//     }
//
//     @Override
//     public boolean releaseConnection(@Nonnull Connection connection) throws SqlRuntimeException {
//         try {
//             if (connection instanceof PooledConnection) {
//                 return pool.release((PooledConnection) connection);
//             }
//             return false;
//         } catch (Exception e) {
//             throw new SqlRuntimeException("Failed to release connection to pool", e);
//         }
//     }
//
//     /**
//      * Internal method to release a pooled connection back to the pool.
//      * This method is called by PooledConnection.close().
//      */
//     boolean releaseConnectionInternal(@Nonnull PooledConnection connection) {
//         try {
//             return pool.release(connection);
//         } catch (Exception e) {
//             // If we can't release the connection back to the pool, we should close it
//             try {
//                 connection.realClose();
//             } catch (SQLException ex) {
//                 // Ignore exception during close
//             }
//             return false;
//         }
//     }
//
//     @Override
//     public void clean() throws SqlRuntimeException {
//         try {
//             pool.clean();
//         } catch (Exception e) {
//             throw new SqlRuntimeException("Failed to clean connection pool", e);
//         }
//     }
//
//     @Override
//     public void close() {
//         pool.close();
//     }
//
//     @Override
//     public boolean isClosed() {
//         return pool.isClosed();
//     }
//
//     @Override
//     public int size() {
//         return pool.size();
//     }
//
//     @Override
//     public int idleSize() {
//         return pool.idleSize();
//     }
//
//     @Override
//     public int activeSize() {
//         return pool.activeSize();
//     }
// }