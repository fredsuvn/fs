package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.channels.NetworkChannel;

/**
 * This interface represents a network option, such as a Socket Option.
 *
 * @param <T> the type of the option value
 * @author sunqian
 */
public interface NetOption<T> {

    /**
     * Returns the name of the option.
     *
     * @return the name of the option
     */
    @Nonnull
    String name();

    /**
     * Returns the value of the option.
     *
     * @return the value of the option
     */
    @Nonnull
    T value();

    /**
     * Applies this option to the given socket.
     *
     * @param socket the given socket
     * @throws Exception if any error occurs
     */
    default void applyTo(@Nonnull Socket socket) throws Exception {
    }

    /**
     * Applies this option to the given server socket. The default implementation does nothing.
     *
     * @param socket the given server socket
     * @throws Exception if any error occurs
     */
    default void applyTo(@Nonnull ServerSocket socket) throws Exception {
    }

    /**
     * Applies this option to the given datagram socket. The default implementation does nothing.
     *
     * @param socket the given datagram socket
     * @throws Exception if any error occurs
     */
    default void applyTo(@Nonnull DatagramSocket socket) throws Exception {
    }

    /**
     * Applies this option to the given multicast socket. The default implementation does nothing.
     *
     * @param socket the given multicast socket
     * @throws Exception if any error occurs
     */
    default void applyTo(@Nonnull MulticastSocket socket) throws Exception {
    }

    /**
     * Applies this option to the given network channel. The default implementation does nothing.
     *
     * @param channel the given network channel
     * @throws Exception if any error occurs
     */
    default <C extends NetworkChannel> void applyTo(@Nonnull C channel) throws Exception {
    }

    /**
     * Returns a new Socket Option: {@code TCP_NODELAY} - disable the Nagle algorithm.
     *
     * @param tcpNoDelay the value of {@code TCP_NODELAY} option
     * @return a new Socket Option: {@code TCP_NODELAY}
     * @see StandardSocketOptions#TCP_NODELAY
     * @see Socket#setTcpNoDelay(boolean)
     */
    static @Nonnull NetOption<Boolean> ofTcpNoDelay(boolean tcpNoDelay) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "TCP_NODELAY";
            }

            @Override
            public @Nonnull Boolean value() {
                return tcpNoDelay;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setTcpNoDelay(tcpNoDelay);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.TCP_NODELAY, tcpNoDelay);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_REUSEADDR} - re-use address.
     *
     * @param soReuseAddr the value of {@code SO_REUSEADDR} option
     * @return a new Socket Option: {@code SO_REUSEADDR}
     * @see StandardSocketOptions#SO_REUSEADDR
     * @see Socket#setReuseAddress(boolean)
     * @see ServerSocket#setReuseAddress(boolean)
     * @see DatagramSocket#setReuseAddress(boolean)
     * @see MulticastSocket#setReuseAddress(boolean)
     */
    static @Nonnull NetOption<Boolean> ofSoReuseAddr(boolean soReuseAddr) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "SO_REUSEADDR";
            }

            @Override
            public @Nonnull Boolean value() {
                return soReuseAddr;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setReuseAddress(soReuseAddr);
            }

            @Override
            public void applyTo(@Nonnull ServerSocket socket) throws Exception {
                socket.setReuseAddress(soReuseAddr);
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setReuseAddress(soReuseAddr);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setReuseAddress(soReuseAddr);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, soReuseAddr);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_BROADCAST} - allow transmission of broadcast datagrams.
     *
     * @param soBroadcast the value of {@code SO_BROADCAST} option
     * @return a new Socket Option: {@code SO_BROADCAST}
     * @see StandardSocketOptions#SO_BROADCAST
     * @see DatagramSocket#setBroadcast(boolean)
     * @see MulticastSocket#setBroadcast(boolean)
     */
    static @Nonnull NetOption<Boolean> ofSoBroadcast(boolean soBroadcast) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "SO_BROADCAST";
            }

            @Override
            public @Nonnull Boolean value() {
                return soBroadcast;
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setBroadcast(soBroadcast);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setBroadcast(soBroadcast);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_BROADCAST, soBroadcast);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_LINGER} - linger on close if data is present, in seconds.
     *
     * @param soLinger the value of {@code SO_LINGER} option
     * @return a new Socket Option: {@code SO_LINGER}
     * @see StandardSocketOptions#SO_LINGER
     * @see Socket#setSoLinger(boolean, int)
     */
    static @Nonnull NetOption<Integer> ofSoLinger(int soLinger) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "SO_LINGER";
            }

            @Override
            public @Nonnull Integer value() {
                return soLinger;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setSoLinger(true, soLinger);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_LINGER, soLinger);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_TIMEOUT} - timeout for blocking operations, in milliseconds.
     *
     * @param soTimeout the value of {@code SO_TIMEOUT} option
     * @return a new Socket Option: {@code SO_TIMEOUT}
     * @see SocketOptions#SO_TIMEOUT
     * @see Socket#setSoTimeout(int)
     * @see ServerSocket#setSoTimeout(int)
     * @see DatagramSocket#setSoTimeout(int)
     * @see MulticastSocket#setSoTimeout(int)
     */
    static @Nonnull NetOption<Integer> ofSoTimeout(int soTimeout) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "SO_TIMEOUT";
            }

            @Override
            public @Nonnull Integer value() {
                return soTimeout;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setSoTimeout(soTimeout);
            }

            @Override
            public void applyTo(@Nonnull ServerSocket socket) throws Exception {
                socket.setSoTimeout(soTimeout);
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setSoTimeout(soTimeout);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setSoTimeout(soTimeout);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_SNDBUF} - the size of the socket send buffer.
     *
     * @param soSndBuf the value of {@code SO_SNDBUF} option
     * @return a new Socket Option: {@code SO_SNDBUF}
     * @see StandardSocketOptions#SO_SNDBUF
     * @see Socket#setSendBufferSize(int)
     * @see DatagramSocket#setSendBufferSize(int)
     * @see MulticastSocket#setSendBufferSize(int)
     */
    static @Nonnull NetOption<Integer> ofSoSndBuf(int soSndBuf) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "SO_SNDBUF";
            }

            @Override
            public @Nonnull Integer value() {
                return soSndBuf;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setSendBufferSize(soSndBuf);
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setSendBufferSize(soSndBuf);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setSendBufferSize(soSndBuf);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_SNDBUF, soSndBuf);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_RCVBUF} - the size of the socket receive buffer.
     *
     * @param soRcvBuf the value of {@code SO_RCVBUF} option
     * @return a new Socket Option: {@code SO_RCVBUF}
     * @see StandardSocketOptions#SO_RCVBUF
     * @see Socket#setReceiveBufferSize(int)
     * @see ServerSocket#setReceiveBufferSize(int)
     * @see DatagramSocket#setReceiveBufferSize(int)
     * @see MulticastSocket#setReceiveBufferSize(int)
     */
    static @Nonnull NetOption<Integer> ofSoRcvBuf(int soRcvBuf) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "SO_RCVBUF";
            }

            @Override
            public @Nonnull Integer value() {
                return soRcvBuf;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setReceiveBufferSize(soRcvBuf);
            }

            @Override
            public void applyTo(@Nonnull ServerSocket socket) throws Exception {
                socket.setReceiveBufferSize(soRcvBuf);
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setReceiveBufferSize(soRcvBuf);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setReceiveBufferSize(soRcvBuf);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_RCVBUF, soRcvBuf);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_KEEPALIVE} - keep connection alive.
     *
     * @param soKeepalive the value of {@code SO_KEEPALIVE} option
     * @return a new Socket Option: {@code SO_KEEPALIVE}
     * @see StandardSocketOptions#SO_KEEPALIVE
     * @see Socket#setKeepAlive(boolean)
     */
    static @Nonnull NetOption<Boolean> ofSoKeepalive(boolean soKeepalive) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "SO_KEEPALIVE";
            }

            @Override
            public @Nonnull Boolean value() {
                return soKeepalive;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setKeepAlive(soKeepalive);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, soKeepalive);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code SO_OOBINLINE} - receipt of TCP urgent data.
     *
     * @param soOobInline the value of {@code SO_OOBINLINE} option
     * @return a new Socket Option: {@code SO_OOBINLINE}
     * @see SocketOptions#SO_OOBINLINE
     * @see Socket#setOOBInline(boolean)
     */
    static @Nonnull NetOption<Boolean> ofSoOobInline(boolean soOobInline) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "SO_OOBINLINE";
            }

            @Override
            public @Nonnull Boolean value() {
                return soOobInline;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setOOBInline(soOobInline);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code IP_TOS} - the Type of Service (ToS) octet in the Internet Protocol (IP)
     * header.
     *
     * @param ipTos the value of {@code IP_TOS} option
     * @return a new Socket Option: {@code IP_TOS}
     * @see StandardSocketOptions#IP_TOS
     * @see Socket#setTrafficClass(int)
     * @see DatagramSocket#setTrafficClass(int)
     * @see MulticastSocket#setTrafficClass(int)
     */
    static @Nonnull NetOption<Integer> ofIpTos(int ipTos) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "IP_TOS";
            }

            @Override
            public @Nonnull Integer value() {
                return ipTos;
            }

            @Override
            public void applyTo(@Nonnull Socket socket) throws Exception {
                socket.setTrafficClass(ipTos);
            }

            @Override
            public void applyTo(@Nonnull DatagramSocket socket) throws Exception {
                socket.setTrafficClass(ipTos);
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setTrafficClass(ipTos);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.IP_TOS, ipTos);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code IP_MULTICAST_IF} - the network interface for Internet Protocol (IP) multicast
     * datagrams.
     *
     * @param ipMulticastIf the value of {@code IP_MULTICAST_IF} option
     * @return a new Socket Option: {@code IP_MULTICAST_IF}
     * @see StandardSocketOptions#IP_MULTICAST_IF
     * @see MulticastSocket#setInterface(InetAddress)
     * @see MulticastSocket#setNetworkInterface(NetworkInterface)
     */
    static @Nonnull NetOption<NetworkInterface> ofIpMulticastIf(@Nonnull NetworkInterface ipMulticastIf) {
        return new NetOption<NetworkInterface>() {

            @Override
            public @Nonnull String name() {
                return "IP_MULTICAST_IF";
            }

            @Override
            public @Nonnull NetworkInterface value() {
                return ipMulticastIf;
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setNetworkInterface(ipMulticastIf);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, ipMulticastIf);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code IP_MULTICAST_TTL} - the time-to-live for Internet Protocol (IP) multicast
     * datagrams.
     *
     * @param ipMulticastTtl the value of {@code IP_MULTICAST_TTL} option
     * @return a new Socket Option: {@code IP_MULTICAST_TTL}
     * @see StandardSocketOptions#IP_MULTICAST_TTL
     * @see MulticastSocket#setTimeToLive(int)
     */
    static @Nonnull NetOption<Integer> ofIpMulticastTtl(int ipMulticastTtl) {
        return new NetOption<Integer>() {

            @Override
            public @Nonnull String name() {
                return "IP_MULTICAST_TTL";
            }

            @Override
            public @Nonnull Integer value() {
                return ipMulticastTtl;
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setTimeToLive(ipMulticastTtl);
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ipMulticastTtl);
            }
        };
    }

    /**
     * Returns a new Socket Option: {@code IP_MULTICAST_LOOP} - loopback for Internet Protocol (IP) multicast
     * datagrams.
     *
     * @param ipMulticastLoop the value of {@code IP_MULTICAST_LOOP} option
     * @return a new Socket Option: {@code IP_MULTICAST_LOOP}
     * @see StandardSocketOptions#IP_MULTICAST_LOOP
     * @see MulticastSocket#setLoopbackMode(boolean)
     */
    static @Nonnull NetOption<Boolean> ofIpMulticastLoop(boolean ipMulticastLoop) {
        return new NetOption<Boolean>() {

            @Override
            public @Nonnull String name() {
                return "IP_MULTICAST_LOOP";
            }

            @Override
            public @Nonnull Boolean value() {
                return ipMulticastLoop;
            }

            @Override
            public void applyTo(@Nonnull MulticastSocket socket) throws Exception {
                socket.setLoopbackMode(!ipMulticastLoop); // Note：the "true" for Socket API is "disable"!
            }

            @Override
            public void applyTo(@Nonnull NetworkChannel channel) throws Exception {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, ipMulticastLoop);
            }
        };
    }
}
