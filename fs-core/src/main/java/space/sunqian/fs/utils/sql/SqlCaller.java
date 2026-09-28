package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.convert.ObjectConverter;
import space.sunqian.fs.object.meta.ObjectMetaIntrospector;

import java.sql.Connection;

/**
 * This interface is the core service for sql operations. The core method is {@link #newTransaction()}, which is used to
 * execute SQL operations in a transaction.
 *
 * @author sunqian
 */
public interface SqlCaller {

    /**
     * Returns a new builder for building {@link SqlCaller}.
     *
     * @return a new builder
     */
    static @Nonnull Builder newBuilder() {
        return new Builder();
    }

    /**
     * Returns the connection pool used by this {@link SqlCaller}.
     *
     * @return the connection pool used by this {@link SqlCaller}
     */
    @Nonnull
    SqlConnectionPool connectionPool();

    /**
     * Returns the default bean introspector used by this {@link SqlCaller}.
     *
     * @return the default bean introspector used by this {@link SqlCaller}
     */
    @Nonnull
    ObjectMetaIntrospector beanIntrospector();

    /**
     * Returns the default object converter used by this {@link SqlCaller}.
     *
     * @return the default object converter used by this {@link SqlCaller}
     */
    @Nonnull
    ObjectConverter objectConverter();

    /**
     * Returns the default SQL name mapper used by this {@link SqlCaller}.
     *
     * @return the default SQL name mapper used by this {@link SqlCaller}
     */
    @Nonnull
    SqlNameMapper sqlNameMapper();

    /**
     * Creates and returns a new {@link SqlTransaction}, which bound to a {@link Connection} from the connection pool of
     * this SQL caller.
     *
     * @return the new {@link SqlTransaction} bound to a new {@link Connection}
     * @throws SqlRuntimeException if any error occurs
     */
    @Nonnull
    SqlTransaction newTransaction() throws SqlRuntimeException;

    /**
     * The builder for building {@link SqlCaller}.
     */
    class Builder {
    }
}
