package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.function.VoidCallable;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

final class SimpleJdbcPoolImpl implements SimpleJdbcPool {

    private final @Nonnull SimplePool<@Nonnull Connection> pool;

    SimpleJdbcPoolImpl(
        @Nonnull String url,
        @Nullable String username,
        @Nullable String password,
        @Nonnull String driver,
        @Nonnull ConnectionFactory connectionFactory,
        @Nonnull Consumer<@Nonnull Connection> closer,
        @Nonnull Predicate<@Nonnull Connection> validator,
        int coreSize,
        int maxSize,
        @Nonnull Duration idleTimeout
    ) {
        Fs.uncheck(() -> Class.forName(driver));
        pool = SimplePool.<Connection>newBuilder()
            .coreSize(coreSize)
            .maxSize(maxSize)
            .idleTimeout(idleTimeout)
            .supplier(() -> connectionFactory.create(driver, url, username, password))
            .discarder(closer)
            .validator(validator)
            .build();
    }

    @Override
    public @Nullable Connection getConnection() throws SqlRuntimeException {
        Connection connection = pool.get();
        return connection == null ? null : new PooledConnection(connection, pool);
    }

    @Override
    public void clean() throws SqlRuntimeException {
        pool.clean();
    }

    @Override
    public void close() throws SqlRuntimeException {
        pool.close();
        List<VoidCallable> callables = pool.unreleasedObjects().stream()
            .map(c -> (VoidCallable) c::close)
            .collect(Collectors.toList());
        Fs.uncheck(callables, SqlRuntimeException::new);
    }

    @Override
    public boolean isClosed() {
        return pool.isClosed();
    }

    @Override
    public int size() {
        return pool.size();
    }

    @Override
    public int idleSize() {
        return pool.idleSize();
    }

    @Override
    public int activeSize() {
        return pool.activeSize();
    }
}
