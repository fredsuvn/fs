package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.net.NetException;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

/**
 * This interface is used to send UDP data.
 *
 * @author sunqian
 */
public interface UdpSender extends Cloneable {

    /**
     * Returns a new UDP sender which disables broadcast. This method is equivalent to {@code newSender(false)}.
     *
     * @return a new UDP sender
     * @throws NetException if an error occurs
     */
    static @Nonnull UdpSender newSender() throws NetException {
        return newSender(false);
    }

    /**
     * Returns a new UDP sender.
     *
     * @param broadcast whether to enable broadcast
     * @return a new UDP sender
     * @throws NetException if an error occurs
     */
    static @Nonnull UdpSender newSender(boolean broadcast) throws NetException {
        return Jie.uncheck((Callable<UdpSender>) () -> new UdpSenderImpl(broadcast), NetException::new);
    }

    /**
     * Sends buffer data to the specified address.
     *
     * @param data    the buffer data to be sent, its position will increment by the number of actual bytes sent
     * @param address the specified address to send to
     * @throws NetException If an error occurs
     */
    void sendBuffer(@Nonnull ByteBuffer data, @Nonnull SocketAddress address) throws NetException;

    /**
     * Sends data to the specified address.
     *
     * @param data    the data to be sent
     * @param address the specified address to send to
     * @throws NetException If an error occurs
     */
    default void sendBytes(byte @Nonnull [] data, @Nonnull SocketAddress address) throws NetException {
        sendBuffer(ByteBuffer.wrap(data), address);
    }

    /**
     * Sends data to the specified address. The string will be encoded using {@link CharsKit#defaultCharset()}.
     *
     * @param str     the string to be sent
     * @param address the specified address to send to
     * @throws NetException If an error occurs
     */
    default void sendString(@Nonnull String str, @Nonnull SocketAddress address) throws NetException {
        sendString(str, CharsKit.defaultCharset(), address);
    }

    /**
     * Sends string to the specified address. The string will be encoded using the specified charset.
     *
     * @param str     the string to be sent
     * @param charset the charset to encode the string
     * @param address the specified address to send to
     * @throws NetException If an error occurs
     */
    default void sendString(
        @Nonnull String str, @Nonnull Charset charset, @Nonnull SocketAddress address
    ) throws NetException {
        sendBytes(str.getBytes(charset), address);
    }

    /**
     * Closes this sender.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Returns the underlying channel that supports this sender.
     * <p>
     * This method is used to provide high-performance (such as direct buffer) data transmission support, and any
     * modifications to the underlying channel will affect the sender.
     *
     * @return the underlying channel that supports this sender
     */
    @Nonnull
    DatagramChannel datagramChannel();
}
