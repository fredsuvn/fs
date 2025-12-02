package space.sunqian.common.net.tcp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.net.NetClient;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Represents a TCP client based on an underlying {@link SocketChannel}, can be built with {@link #newBuilder()}.
 *
 * @author sunqian
 */
public interface TcpClient extends
    NetClient<InetSocketAddress, SocketChannel> {

    /**
     * Returns a new builder for building {@link TcpClient}.
     *
     * @return a new builder for building {@link TcpClient}
     */
    static @Nonnull TcpClientBuilder newBuilder() {
        return new TcpClientBuilder();
    }

    /**
     * Blocks current thread and waits for the client to be readable.
     */
    void awaitReadable();

    /**
     * Wakes up the thread blocked in {@link #awaitReadable()}.
     */
    void wakeUpReadable();

    /**
     * Returns the underlying socket channel that supports this client.
     * <p>
     * If the read method of the channel returns {@code -1}, it means the channel can no longer be read (usually because
     * the peer sent a {@code FIN} and entered the half-closed state). In this case, the channel can be closed. If it
     * returns {@code 0}, it indicates that all available data from the current read event has been read, but the
     * channel remains alive.
     *
     * @return the underlying socket channel that supports this client
     */
    @Override
    @Nonnull
    SocketChannel channel();
}
