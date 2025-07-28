package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nullable;

/**
 * This interface represents a network client, as opposed to the server.
 *
 * @param <A> the type of the address this client connects to or is bound to
 * @author sunqian
 */
public interface NetClient<A> {

    /**
     * Starts this client, and returns immediately after startup. This method will not block the current thread,
     * {@link #await()} will.
     *
     * @throws NetException if any error occurs
     */
    void start() throws NetException;

    /**
     * Blocks current thread until this client is closed.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;

    /**
     * Closes this client. The connection will be disconnected.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the address this client connects to, may be {@code null} if the client does not connect yet.
     *
     * @return the address this client connects to, may be {@code null} if the client does not connect yet
     */
    @Nullable
    A getRemoteAddress();

    /**
     * Returns the local address this client is bound to, may be {@code null} if the client is not bound yet.
     *
     * @return the local address this client is bound to, may be {@code null} if the client is not bound yet
     */
    @Nullable
    A getLocalAddress();

    /**
     * Returns the current state of this client.
     *
     * @return the current state of this client
     */
    NetState getState();
}
