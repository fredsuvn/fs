// package space.sunqian.fs.utils.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.object.pool.SimplePool;
//
// import java.sql.Connection;
//
// final class ConnectionProviderImpl implements ConnectionProvider {
//
//     static ConnectionProvider getInstance() {
//         return new ConnectionProviderImpl();
//     }
//
//     @Override
//     public Connection newConnection(
//         @Nonnull Connection delegate,
//         @Nonnull SimplePool<Connection> pool
//     ) throws SqlRuntimeException {
//         return new PooledConnection(delegate, pool);
//     }
// }
