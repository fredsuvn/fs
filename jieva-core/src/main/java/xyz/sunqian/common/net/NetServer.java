package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nullable;

/**
 * This interface represents a network server.
 *
 * @param <A> the type of the address this server is bound to
 * @author sunqian
 */
public interface NetServer<A> {

    /**
     * Starts this server, and returns immediately after startup. This method will not block the current thread,
     * {@link #await()} will.
     *
     * @throws NetException if any error occurs
     */
    void start() throws NetException;

    /**
     * Closes this server.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Blocks current thread until this server is closed.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;

    /**
     * Returns the address this server is bound to, may be {@code null} if the server is not bound yet.
     *
     * @return the address this server is bound to, may be {@code null} if the server is not bound yet
     */
    @Nullable
    A getBoundAddress();
}
