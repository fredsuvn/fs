package space.sunqian.fs.net.tcp;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IOKit;
import space.sunqian.fs.io.communicate.ChannelReader;
import space.sunqian.fs.io.communicate.ChannelWriter;
import space.sunqian.fs.net.NetClient;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Represents a TCP client based on an underlying {@link SocketChannel}, can be built with {@link #newBuilder()}.
 *
 * @author sunqian
 * @implNote The default I/O methods of this interface use {@link IOKit} to read and write data from the underlying
 * channel, inherited from {@link ChannelReader} and {@link ChannelWriter}.
 */
public interface TcpClient extends
    NetClient<InetSocketAddress, SocketChannel>, ChannelReader<SocketChannel>, ChannelWriter<SocketChannel> {

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
    void readWait();

    /**
     * Wakes up the thread blocked in {@link #readWait()}.
     */
    void readWakeUp();

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
