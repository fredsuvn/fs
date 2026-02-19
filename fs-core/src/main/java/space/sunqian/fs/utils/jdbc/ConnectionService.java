package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.lang.FsLoader;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;

interface ConnectionService {

    @Nonnull
    ConnectionService INST = FsLoader.loadImplByJvm(ConnectionService.class, 9);

    Connection newConnection(
        @Nonnull Connection delegate, @Nonnull SimplePool<Connection> pool
    ) throws SqlRuntimeException;
}
