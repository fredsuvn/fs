package xyz.sunqian.common.net.udp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.net.NetException;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;

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
     * Returns a new UDP sender. The parameter {@code broadcast} determines whether to enable broadcast, the broadcast
     * method can only be used when the {@code broadcast} is enabled.
     *
     * @param broadcast whether to enable broadcast
     * @return a new UDP sender
     * @throws NetException if an error occurs
     */
    static @Nonnull UdpSender newSender(boolean broadcast) throws NetException {
        return Jie.uncheck(() -> UdpSenderImpls.newSender(broadcast), NetException::new);
    }

    /**
     * Sends buffer data to the specified address.
     *
     * @param data    the buffer data to be sent, its position will increment by the number of actual bytes sent
     * @param address the specified address to send to
     * @throws NetException if an error occurs
     */
    void sendData(@Nonnull ByteBuffer data, @Nonnull SocketAddress address) throws NetException;

    /**
     * Sends data to the specified address.
     *
     * @param data    the data to be sent
     * @param address the specified address to send to
     * @throws NetException if an error occurs
     */
    default void sendData(byte @Nonnull [] data, @Nonnull SocketAddress address) throws NetException {
        sendData(ByteBuffer.wrap(data), address);
    }

    /**
     * Sends data to the specified address. The string will be encoded using {@link CharsKit#defaultCharset()}.
     *
     * @param str     the string to be sent
     * @param address the specified address to send to
     * @throws NetException if an error occurs
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
     * @throws NetException if an error occurs
     */
    default void sendString(
        @Nonnull String str, @Nonnull Charset charset, @Nonnull SocketAddress address
    ) throws NetException {
        sendData(str.getBytes(charset), address);
    }

    /**
     * Sends the given datagram packet.
     *
     * @param packet the given datagram packet
     * @throws NetException if an error occurs
     */
    default void sendPacket(@Nonnull DatagramPacket packet) throws NetException {
        sendData(packet.getData(), packet.getSocketAddress());
    }

    /**
     * Sends a local broadcast, the content of the broadcast is the given byte buffer.
     *
     * @param data the given byte buffer to be sent, its position will increment by the number of actual bytes sent
     * @param port the port of the broadcast
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    default void sendBroadcast(@Nonnull ByteBuffer data, int port) throws NetException {
        sendData(data, new InetSocketAddress(broadcastAddress(), port));
    }

    /**
     * Sends a local broadcast, the content of the broadcast is the given byte array.
     *
     * @param data the given byte array to be sent
     * @param port the port of the broadcast
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    default void sendBroadcast(byte @Nonnull [] data, int port) throws NetException {
        sendBroadcast(ByteBuffer.wrap(data), port);
    }

    /**
     * Sends a local broadcast, the content of the broadcast is the given string. The string will be encoded using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param str  the given string to be sent
     * @param port the port of the broadcast
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    default void sendBroadcast(@Nonnull String str, int port) throws NetException {
        sendBroadcast(str, CharsKit.defaultCharset(), port);
    }

    /**
     * Sends a local broadcast, the content of the broadcast is the given string. The string will be encoded using the
     * specified charset.
     *
     * @param str     the given string to be sent
     * @param charset the charset to encode the string
     * @param port    the port of the broadcast
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    default void sendBroadcast(@Nonnull String str, @Nonnull Charset charset, int port) throws NetException {
        sendBroadcast(str.getBytes(charset), port);
    }

    /**
     * Returns the broadcast address.
     *
     * @return the broadcast address
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    @Nonnull
    InetAddress broadcastAddress() throws NetException;

    /**
     * Refreshes and returns the broadcast address.
     *
     * @return the broadcast address
     * @throws NetException if the broadcast is disabled, or an error occurs
     */
    @Nonnull
    InetAddress refreshBroadcastAddress() throws NetException;

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
    DatagramChannel channel();
}
