package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;

interface ConnectionProvider {

    Connection newConnection(
        @Nonnull Connection delegate, @Nonnull SimplePool<Connection> pool
    ) throws SqlRuntimeException;
}
