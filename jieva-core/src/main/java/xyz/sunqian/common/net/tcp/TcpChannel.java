package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannel;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * {@link IOChannel} for TCP network.
 * <p>
 * When the remote endpoint sends a {@code FIN} message, {@link #isOpen()} may still return {@code true} (but
 * {@link #read(ByteBuffer)} will return {@code -1}) because in a TCP connection, the {@code FIN} message requires an
 * {@code ACK} response. Using {@link #close()} can close this channel.
 *
 * @author sunqian
 */
public interface TcpChannel extends IOChannel {

    /**
     * Returns the remote address of this channel.
     *
     * @return the remote address of this channel
     */
    @Nonnull
    InetSocketAddress remoteAddress();

    /**
     * Returns the local address of this channel.
     *
     * @return the local address of this channel
     */
    @Nonnull
    InetSocketAddress localAddress();

    /**
     * Returns the internal {@link SocketChannel} that supports this channel.
     * <p>
     * This method is used to provide high-performance (such as direct buffer) data transmission support, and any
     * modifications to the internal socket channel will affect the server.
     *
     * @return the internal {@link SocketChannel} that supports this channel
     */
    @Nonnull
    SocketChannel socketChannel();
}
