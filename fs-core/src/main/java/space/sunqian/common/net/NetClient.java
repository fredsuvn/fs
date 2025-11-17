package space.sunqian.common.net;

import space.sunqian.annotations.Nonnull;

import java.nio.channels.Channel;

/**
 * This interface represents a network client, based on an underlying {@link Channel}.
 *
 * @param <A> the type of the address in the network
 * @param <C> the type of the underlying channel
 * @author sunqian
 */
public interface NetClient<A, C extends Channel> extends Cloneable {

    /**
     * Disconnects and closes this client.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the remote address this client connects to.
     * <p>
     * If the client is closed, it still returns the address at which the connection was alive.
     *
     * @return the remote address this client connects to
     */
    @Nonnull
    A remoteAddress();

    /**
     * Returns the address this client is bound to.
     * <p>
     * If the client is closed, it still returns the address at which the connection was alive.
     *
     * @return the address this client is bound to
     */
    @Nonnull
    A localAddress();

    /**
     * Returns whether this client connects to the server.
     *
     * @return whether this client connects to the server
     */
    boolean isConnected();

    /**
     * Returns whether this client is closed.
     *
     * @return whether this client is closed
     */
    boolean isClosed();

    /**
     * Returns the underlying channel of this client.
     *
     * @return the underlying channel of this client
     */
    @Nonnull
    C channel();
}
