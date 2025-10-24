package space.sunqian.common.net.tcp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.io.communicate.ChannelContext;
import space.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Represents context of a tcp channel for TCP network, based on {@link SocketChannel}.
 * <p>
 * TcpContext is typically used in callbacks of {@link TcpServerHandler}. All callbacks triggered by events from the
 * same underlying channel will use the same {@link TcpContext} instance, ensuring consistent access to channel-specific
 * state and attachments throughout the channel's lifecycle.
 *
 * @author sunqian
 */
public interface TcpContext extends ChannelContext<SocketChannel> {

    /**
     * Returns the address of the client.
     * <p>
     * Note even if the underlying channel is closed, it still returns the address at which the connection was alive.
     *
     * @return the address of the client
     */
    @Nonnull
    InetSocketAddress clientAddress();

    /**
     * Returns the address of the server.
     * <p>
     * Note even if the underlying channel is closed, it still returns the address at which the connection was alive.
     *
     * @return the address of the server
     */
    @Nonnull
    InetSocketAddress serverAddress();

    /**
     * Disconnects and closes the client.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;
}
