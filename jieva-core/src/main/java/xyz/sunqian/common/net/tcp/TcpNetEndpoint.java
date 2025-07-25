package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.net.NetEndpoint;
import xyz.sunqian.common.net.NetException;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This interface represent an endpoint on the TCP/IP network.
 *
 * @author sunqian
 */
public interface TcpNetEndpoint extends NetEndpoint<InetSocketAddress> {

    /**
     * Returns the port number to which this endpoint is bound.
     *
     * @return the port number to which this endpoint is bound
     */
    default int getPort() {
        InetSocketAddress address = getAddress();
        return address.getPort();
    }

    /**
     * Returns the IP this endpoint is bound to.
     *
     * @return the IP this endpoint is bound to
     */
    default @Nonnull InetAddress getIp() {
        InetSocketAddress address = getAddress();
        return address.getAddress();
    }

    /**
     * Sends the message to this endpoint.
     *
     * @param msg the message to send
     * @throws NetException if any error occurs
     */
    void send(ByteBuffer msg) throws NetException;

    /**
     * Sends the message to this endpoint, with {@link Charset#defaultCharset()}.
     *
     * @param msg the message to send
     * @throws NetException if any error occurs
     */
    default void send(String msg) throws NetException {
        send(msg, Charset.defaultCharset());
    }

    /**
     * Sends the message to this endpoint, with the specified charset.
     *
     * @param msg     the message to send
     * @param charset the specified charset
     * @throws NetException if any error occurs
     */
    default void send(String msg, Charset charset) throws NetException {
        send(ByteBuffer.wrap(msg.getBytes(charset)));
    }
}
