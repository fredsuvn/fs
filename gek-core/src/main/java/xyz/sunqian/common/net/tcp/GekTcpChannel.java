package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.data.GekData;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;

/**
 * This class represents a TCP/IP connection channel between local and remote endpoint.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekTcpChannel {

    /**
     * Returns address of remote endpoint.
     *
     * @return address of remote endpoint
     */
    InetAddress getRemoteAddress();

    /**
     * Returns port of remote endpoint.
     *
     * @return port of remote endpoint
     */
    int getRemotePort();

    /**
     * Returns address of local endpoint.
     *
     * @return address of local endpoint
     */
    InetAddress getLocalAddress();

    /**
     * Returns port of remote endpoint.
     *
     * @return port of local endpoint
     */
    int getLocalPort();

    /**
     * Returns socket address of remote endpoint.
     *
     * @return socket address of remote endpoint
     */
    SocketAddress getRemoteSocketAddress();

    /**
     * Returns socket address of local endpoint.
     *
     * @return socket address of local endpoint
     */
    SocketAddress getLocalSocketAddress();

    /**
     * Returns whether this channel is opened.
     *
     * @return whether this channel is opened
     */
    boolean isOpened();

    /**
     * Returns whether this channel is closed.
     *
     * @return whether this channel is closed
     */
    boolean isClosed();

    /**
     * Closes this channel, blocks current thread for buffered operations.
     */
    default void close() {
        close(null);
    }

    /**
     * Closes this channel, blocks current thread for buffered operations in given timeout.
     *
     * @param timeout given timeout, maybe null to always wait
     */
    void close(@Nullable Duration timeout);

    /**
     * Closes this channel immediately, without blocking and buffered operations.
     */
    void closeNow();

    /**
     * Sends data to remote endpoint. The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(GekData data);

    /**
     * Sends data to remote endpoint. The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(byte[] data);

    /**
     * Sends data of specified length from given offset to remote endpoint. The written data may be buffered before the
     * {@link #flush()} is called.
     *
     * @param data   the data
     * @param offset given offset
     * @param length specified length
     */
    void send(byte[] data, int offset, int length);

    /**
     * Sends data to remote endpoint. The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(ByteBuffer data);

    /**
     * Sends data to remote endpoint. The written data may be buffered before the {@link #flush()} is called.
     *
     * @param data the data
     */
    void send(InputStream data);

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(GekData data) {
        send(data);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(byte[] data) {
        send(data);
        flush();
    }

    /**
     * Sends data of specified length from given offset to remote endpoint and flushes immediately.
     *
     * @param data   the data
     * @param offset given offset
     * @param length specified length
     */
    default void sendAndFlush(byte[] data, int offset, int length) {
        send(data, offset, length);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(ByteBuffer data) {
        send(data);
        flush();
    }

    /**
     * Sends data to remote endpoint and flushes immediately.
     *
     * @param data the data
     */
    default void sendAndFlush(InputStream data) {
        send(data);
        flush();
    }

    /**
     * Flushes buffered data to be written to remote endpoint.
     */
    void flush();

    /**
     * Returns underlying object which implements {@link GekTcpChannel} interface, such as {@link Socket}.
     *
     * @return underlying object
     */
    Object getSource();
}
