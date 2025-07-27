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
     * Closes this client. The connection will be disconnected, and it disconnects typically friendly.
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
     * Returns the address this client is bound to, may be {@code null} if the client is not bound yet.
     *
     * @return the address this client is bound to, may be {@code null} if the client is not bound yet
     */
    @Nullable
    A getBoundAddress();
}
