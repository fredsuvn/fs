// package space.sunqian.fs.jdbc;
//
// import space.sunqian.annotation.Nonnull;
//
// import java.sql.Connection;
//
// /**
//  * JDBC client interface for executing SQL statements.
//  *
//  * @author sunqian
//  */
// public interface JdbcClient {
//
//     /**
//      * Returns a connection of this client.
//      *
//      * @return a connection of this client
//      * @throws JdbcException if any error occurs during connection creation
//      */
//     @Nonnull
//     Connection getConnection() throws JdbcException;
//
//     /**
//      * Returns a SQL builder which is bound to this client.
//      *
//      * @return a SQL builder which is bound to this client
//      * @throws JdbcException if any error occurs during SQL builder creation
//      */
//     default @Nonnull SqlBuilder newSqlBuilder() throws JdbcException {
//         return SqlBuilder.newBuilder(getConnection());
//     }
//
//     /**
//      * Closes this client.
//      *
//      * @throws JdbcException if any error occurs during client closure
//      */
//     void close() throws JdbcException;
//
//     /**
//      * Builder class for {@link JdbcClient}.
//      */
//     interface Builder {
//
//         /**
//          * Sets the database URL.
//          *
//          * @param url the database URL
//          * @return this builder
//          */
//         @Nonnull
//         Builder url(@Nonnull String url);
//
//         /**
//          * Sets the database username.
//          *
//          * @param username the database username
//          * @return this builder
//          */
//         @Nonnull
//         Builder username(@Nonnull String username);
//
//         /**
//          * Sets the database password.
//          *
//          * @param password the database password
//          * @return this builder
//          */
//         @Nonnull
//         Builder password(@Nonnull String password);
//
//         /**
//          * Sets the core pool size.
//          *
//          * @param coreSize the core pool size
//          * @return this builder
//          */
//         @Nonnull
//         Builder coreSize(int coreSize);
//
//         /**
//          * Sets the maximum pool size.
//          *
//          * @param maxSize the maximum pool size
//          * @return this builder
//          */
//         @Nonnull
//         Builder maxSize(int maxSize);
//
//         /**
//          * Sets the idle timeout in milliseconds.
//          *
//          * @param idleTimeoutMillis the idle timeout in milliseconds
//          * @return this builder
//          */
//         @Nonnull
//         Builder idleTimeout(long idleTimeoutMillis);
//
//         /**
//          * Builds the JdbcClient instance.
//          *
//          * @return the built JdbcClient instance
//          * @throws JdbcException if any error occurs during building
//          */
//         @Nonnull
//         JdbcClient build() throws JdbcException;
//     }
//
//     /**
//      * Creates a new builder for JdbcClient.
//      *
//      * @return a new builder instance
//      */
//     static @Nonnull Builder newBuilder() {
//         return new JdbcClientImpl.BuilderImpl();
//     }
// }