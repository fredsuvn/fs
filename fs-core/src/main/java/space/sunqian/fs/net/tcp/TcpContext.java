package space.sunqian.fs.net.tcp;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.communicate.ChannelContext;
import space.sunqian.fs.io.communicate.ChannelReader;
import space.sunqian.fs.io.communicate.ChannelWriter;
import space.sunqian.fs.net.NetException;

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
 * @implSpec The default I/O methods of this interface use {@link IOKit} to read and write data from the underlying
 * channel, inherited from {@link ChannelReader} and {@link ChannelWriter}.
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
