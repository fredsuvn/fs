package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

/**
 * This interface represents a network endpoint in a connection.
 *
 * @param <A> the type of the address this endpoint is bound to
 * @author sunqian
 */
public interface NetEndpoint<A> {

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
