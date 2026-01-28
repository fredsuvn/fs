// package space.sunqian.fs.utils.jdbc;
//
// import space.sunqian.annotation.Nonnull;
// import space.sunqian.fs.object.pool.SimplePool;
//
// import java.sql.Array;
// import java.sql.Blob;
// import java.sql.CallableStatement;
// import java.sql.Clob;
// import java.sql.Connection;
// import java.sql.DatabaseMetaData;
// import java.sql.NClob;
// import java.sql.PreparedStatement;
// import java.sql.SQLClientInfoException;
// import java.sql.SQLException;
// import java.sql.SQLWarning;
// import java.sql.SQLXML;
// import java.sql.Savepoint;
// import java.sql.Statement;
// import java.sql.Struct;
// import java.util.Map;
// import java.util.Properties;
// import java.util.concurrent.Executor;
//
// final class PoolBack {
//
//     private static final class PoolConnection implements Connection {
//
//         private final @Nonnull Connection delegate;
//         private final @Nonnull SimplePool<PoolConnection> pool;
//         private boolean closed = false;
//
//         private PoolConnection(@Nonnull Connection delegate, @Nonnull SimplePool<PoolConnection> pool) {
//             this.delegate = delegate;
//             this.pool = pool;
//         }
//     }
//
//     private PoolBack() {
//     }
// }
