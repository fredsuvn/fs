package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.communicate.IOChannel;

/**
 * This interface represents a network client.
 *
 * @param <A> the type of the address in the network
 * @author sunqian
 */
public interface NetClient<A> {

    /**
     * Disconnects and closes this client.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the remote address this client connects to.
     *
     * @return the remote address this client connects to
     */
    @Nonnull
    A remoteAddress();

    /**
     * Returns the address this client is bound to, may be {@code null} if the client has not connected yet.
     *
     * @return the address this client is bound to, may be {@code null} if the client has not connected yet
     */
    @Nullable
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
     * Returns an I/O channel of this client.
     *
     * @return an I/O channel of this client
     */
    @Nonnull
    IOChannel ioChannel();
}
