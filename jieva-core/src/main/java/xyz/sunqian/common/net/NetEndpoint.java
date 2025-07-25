package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

import java.net.SocketAddress;

/**
 * This interface represents a network endpoint in a connection.
 *
 * @param <A> the type of the socket address
 * @author sunqian
 */
public interface NetEndpoint<A extends SocketAddress> {

    /**
     * Closes the connection to this endpoint.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the address this endpoint is bound to.
     *
     * @return the address this endpoint is bound to
     */
    @Nonnull
    A getAddress();
}
