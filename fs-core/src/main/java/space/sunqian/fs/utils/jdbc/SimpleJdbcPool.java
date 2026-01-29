package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Simple JDBC connection pool interface that provides methods for acquiring and releasing database connections. This
 * pool is built on top of {@link SimplePool} and does not introduce any third-party dependencies.
 * <p>
 * Example usage:
 * <pre>{@code
 * SimpleJdbcPool pool = SimpleJdbcPool.newBuilder()
 *     .url("jdbc:h2:mem:test")
 *     .username("sa")
 *     //.password("password")
 *     .driverClassName("org.h2.Driver")
 *     .coreSize(5)
 *     .maxSize(20)
 *     .idleTimeout(Duration.ofMinutes(5))
 *     .build();
 *
 * try {
 *     // acquire a connection from the pool
 *     Connection connection = pool.getConnection();
 * } finally {
 *     // release the connection back to the pool, not actually close it
 *     connection.close();
 * }
 * }</pre>
 *
 * @author sunqian
 */
public interface SimpleJdbcPool {

    /**
     * Returns a builder for {@link SimpleJdbcPool}.
     *
     * @return a builder for {@link SimpleJdbcPool}
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Acquires a database connection from the pool, or {@code null} if no connection is available.
     * <p>
     * If any exception occurs during the acquisition process, {@link #close()} will be invoked to close this pool.
     *
     * @return the acquired connection, or {@code null} if no connection is available
     * @throws SqlRuntimeException if failed to acquire connection
     */
    @Nullable
    Connection getConnection() throws SqlRuntimeException;

    /**
     * Cleans the pool, removing idle connections that have timed out or been invalidated, or over the core size, adding
     * new connections up to the core size if necessary. The active connections will not be cleaned.
     * <p>
     * If any exception occurs during the clean process, {@link #close()} will be invoked to close this pool.
     *
     * @throws SqlRuntimeException if any exception occurs during the clean process
     */
    void clean() throws SqlRuntimeException;

    /**
     * Closes the pool and releases all resources. After calling this method, the pool cannot be used anymore. This
     * method will close all connections in the pool, including idle and active connections.
     *
     * @throws SqlRuntimeException if any exception occurs during the close process
     */
    void close() throws SqlRuntimeException;

    /**
     * Returns {@code true} if this pool is closed, {@code false} otherwise.
     *
     * @return {@code true} if this pool is closed, {@code false} otherwise
     */
    boolean isClosed();

    /**
     * Returns the total number of connections in this pool, including both idle and active connections.
     *
     * @return the total number of connections in this pool
     */
    int size();

    /**
     * Returns the number of idle connections in this pool.
     *
     * @return the number of idle connections in this pool
     */
    int idleSize();

    /**
     * Returns the number of active connections in this pool. If the pool is closed, this method will return the number
     * of unreleased active connections.
     *
     * @return the number of active connections in this pool
     */
    int activeSize();

    /**
     * Factory interface for creating database connections.
     */
    interface ConnectionFactory {

        /**
         * Creates a new connection.
         *
         * @param driverClassName the driver class name
         * @param url             the JDBC URL
         * @param username        the username, default is {@code null}
         * @param password        the password, default is {@code null}
         * @return a new connection
         * @throws SqlRuntimeException if failed to create connection
         */
        @Nonnull
        Connection create(
            @Nonnull String driverClassName,
            @Nonnull String url,
            @Nullable String username,
            @Nullable String password
        ) throws SqlRuntimeException;
    }

    /**
     * Builder class for {@link SimpleJdbcPool}.
     */
    class Builder {

        // default connection closer
        private static final @Nonnull Consumer<@Nonnull Connection> CLOSER = connection ->
            Fs.uncheck(connection::close, SqlRuntimeException::new);
        // default connection validation
        private static final @Nonnull Predicate<@Nonnull Connection> VALIDATOR = connection ->
            Fs.uncheck(() -> connection.isValid(1), SqlRuntimeException::new);

        // JDBC configuration
        private @Nullable String url;
        private @Nullable String username;
        private @Nullable String password;
        private @Nullable String driver;

        // Pool configuration
        private int coreSize = 5;
        private int maxSize = 10;
        private @Nonnull Duration idleTimeout = Duration.ofMinutes(5);
        private @Nullable ConnectionFactory connectionFactory = null;
        private @Nonnull Consumer<@Nonnull Connection> closer = CLOSER;
        private @Nonnull Predicate<@Nonnull Connection> validator = VALIDATOR;

        /**
         * Sets the JDBC URL for the database connection.
         *
         * @param url the JDBC URL
         * @return this builder
         */
        public @Nonnull Builder url(@Nonnull String url) {
            this.url = url;
            return this;
        }

        /**
         * Sets the username for the database connection, default is {@code null}.
         *
         * @param username the username
         * @return this builder
         */
        public @Nonnull Builder username(@Nullable String username) {
            this.username = username;
            return this;
        }

        /**
         * Sets the password for the database connection, default is {@code null}.
         *
         * @param password the password
         * @return this builder
         */
        public @Nonnull Builder password(@Nullable String password) {
            this.password = password;
            return this;
        }

        /**
         * Sets the JDBC driver class name.
         *
         * @param driver the JDBC driver class name
         * @return this builder
         */
        public @Nonnull Builder driverClassName(@Nullable String driver) {
            this.driver = driver;
            return this;
        }

        /**
         * Sets the core size of the connection pool. This is the minimum number of connections that will be maintained
         * in the pool. Default is {@code 5}.
         *
         * @param coreSize the core size of the connection pool
         * @return this builder
         * @throws IllegalArgumentException if {@code coreSize <= 0}
         */
        public @Nonnull Builder coreSize(int coreSize) throws IllegalArgumentException {
            Checker.checkArgument(coreSize > 0, "The coreSize must > 0.");
            this.coreSize = coreSize;
            return this;
        }

        /**
         * Sets the maximum size of the connection pool. This is the maximum number of connections that can be created
         * in the pool. Default is {@code 10}.
         *
         * @param maxSize the maximum size of the connection pool
         * @return this builder
         * @throws IllegalArgumentException if {@code maxSize < coreSize}
         */
        public @Nonnull Builder maxSize(int maxSize) throws IllegalArgumentException {
            Checker.checkArgument(maxSize >= coreSize, "The maxSize must >= coreSize.");
            this.maxSize = maxSize;
            return this;
        }

        /**
         * Sets the idle timeout for connections in the pool. Connections that have been idle for longer than this
         * duration will be eligible for removal during cleanup. Default is {@code 5 minutes}.
         *
         * @param idleTimeout the idle timeout for connections
         * @return this builder
         * @throws IllegalArgumentException if {@code idleTimeout <= 0}
         */
        public @Nonnull Builder idleTimeout(@Nonnull Duration idleTimeout) throws IllegalArgumentException {
            Checker.checkArgument(idleTimeout.toMillis() > 0, "The idleTimeout must > 0.");
            this.idleTimeout = idleTimeout;
            return this;
        }

        /**
         * Sets the connection factory for creating new database connections. By default, connections are created using
         * <pre>{@code
         * DriverManager.getConnection(url, username, password);
         * }</pre>.
         *
         * @param connectionFactory the connection factory for creating new database connections
         * @return this builder
         */
        public @Nonnull Builder connectionFactory(@Nonnull ConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
            return this;
        }

        /**
         * Sets the closer for closing database connections. By default, connections are closed using
         * <pre>{@code
         * connection.close();
         * }</pre>.
         *
         * @param closer the closer for closing database connections
         * @return this builder
         */
        public @Nonnull Builder closer(@Nonnull Consumer<@Nonnull Connection> closer) {
            this.closer = closer;
            return this;
        }

        /**
         * Sets the validator for checking connection validity. By default, connections are validated if
         * <pre>{@code
         * connection.isValid(1);
         * }</pre>.
         *
         * @param validator the validator for checking connection validity
         * @return this builder
         */
        public @Nonnull Builder validator(@Nonnull Predicate<@Nonnull Connection> validator) {
            this.validator = validator;
            return this;
        }

        /**
         * Builds and returns a new {@link SimpleJdbcPool}.
         *
         * @return the built new {@link SimpleJdbcPool}
         * @throws IllegalArgumentException if some configuration is invalid
         * @throws SqlRuntimeException      if failed to build the pool
         */
        public @Nonnull SimpleJdbcPool build() throws IllegalArgumentException, SqlRuntimeException {
            if (url == null) {
                throw new IllegalArgumentException("The url for JDBC connection must be set.");
            }
            if (driver == null) {
                throw new IllegalArgumentException("The driver class name for JDBC connection must be set.");
            }
            return new SimpleJdbcPoolImpl(
                url, username, password, driver,
                connectionFactory == null ? new ConnectionFactoryImpl() : connectionFactory,
                closer, validator, coreSize, maxSize, idleTimeout
            );
        }

        private static final class ConnectionFactoryImpl implements ConnectionFactory {

            private volatile @Nullable Class<?> driverClass;

            @Override
            public synchronized @Nonnull Connection create(
                @Nonnull String driverClassName,
                @Nonnull String url,
                @Nullable String username,
                @Nullable String password
            ) throws SqlRuntimeException {

                if (driverClass == null) {
                    driverClass = Fs.uncheck(() -> Class.forName(driverClassName), SqlRuntimeException::new);
                }
                return Fs.uncheck(() -> {
                        Connection realConnection;
                        if (username != null && password != null) {
                            realConnection = DriverManager.getConnection(url, username, password);
                        } else if (username != null) {
                            realConnection = DriverManager.getConnection(url, username, null);
                        } else {
                            realConnection = DriverManager.getConnection(url);
                        }
                        return realConnection;
                    },
                    SqlRuntimeException::new);
            }
        }
    }
}