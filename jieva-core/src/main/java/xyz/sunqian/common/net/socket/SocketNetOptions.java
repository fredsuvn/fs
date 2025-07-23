package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.net.NetException;

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
 * Options for Socket.
 * <p>
 * This class provides public fields that can be directly set, rather than through setters. Only need to set the
 * required options, and the default values will be used for the unset options.
 *
 * @author sunqian
 */
public class SocketNetOptions {

    /**
     * TCP_NODELAY: disable the Nagle algorithm.
     *
     * @see StandardSocketOptions#TCP_NODELAY
     * @see Socket#setTcpNoDelay(boolean)
     */
    public Boolean tcpNoDelay;

    /**
     * SO_REUSEADDR: re-use address.
     *
     * @see StandardSocketOptions#SO_REUSEADDR
     * @see Socket#setReuseAddress(boolean)
     * @see ServerSocket#setReuseAddress(boolean)
     * @see DatagramSocket#setReuseAddress(boolean)
     * @see MulticastSocket#setReuseAddress(boolean)
     */
    public Boolean soReuseAddress;

    /**
     * SO_BROADCAST: allow transmission of broadcast datagrams.
     *
     * @see StandardSocketOptions#SO_BROADCAST
     * @see DatagramSocket#setBroadcast(boolean)
     * @see MulticastSocket#setBroadcast(boolean)
     */
    public Boolean soBroadcast;

    /**
     * SO_LINGER: linger on close if data is present, in seconds.
     *
     * @see StandardSocketOptions#SO_LINGER
     * @see Socket#setSoLinger(boolean, int)
     */
    public Integer soLinger;

    /**
     * SO_TIMEOUT: timeout for blocking operations, in milliseconds.
     *
     * @see SocketOptions#SO_TIMEOUT
     * @see Socket#setSoTimeout(int)
     * @see ServerSocket#setSoTimeout(int)
     * @see DatagramSocket#setSoTimeout(int)
     * @see MulticastSocket#setSoTimeout(int)
     */
    public Integer soTimeout;

    /**
     * SO_SNDBUF: the size of the socket send buffer.
     *
     * @see StandardSocketOptions#SO_SNDBUF
     * @see Socket#setSendBufferSize(int)
     * @see DatagramSocket#setSendBufferSize(int)
     * @see MulticastSocket#setSendBufferSize(int)
     */
    public Integer soSndBuf;

    /**
     * SO_RCVBUF: the size of the socket receive buffer.
     *
     * @see StandardSocketOptions#SO_RCVBUF
     * @see Socket#setReceiveBufferSize(int)
     * @see ServerSocket#setReceiveBufferSize(int)
     * @see DatagramSocket#setReceiveBufferSize(int)
     * @see MulticastSocket#setReceiveBufferSize(int)
     */
    public Integer soRcvBuf;

    /**
     * SO_KEEPALIVE: keep connection alive.
     *
     * @see StandardSocketOptions#SO_KEEPALIVE
     * @see Socket#setKeepAlive(boolean)
     */
    public Boolean soKeepalive;

    /**
     * SO_OOBINLIN: receipt of TCP urgent data.
     *
     * @see SocketOptions#SO_OOBINLINE
     * @see Socket#setOOBInline(boolean)
     */
    public Boolean soOobInline;

    /**
     * IP_TOS: the Type of Service (ToS) octet in the Internet Protocol (IP) header.
     *
     * @see StandardSocketOptions#IP_TOS
     * @see Socket#setTrafficClass(int)
     * @see DatagramSocket#setTrafficClass(int)
     * @see MulticastSocket#setTrafficClass(int)
     */
    public Integer ipTos;

    /**
     * IP_MULTICAST_IF: the network interface for Internet Protocol (IP) multicast datagrams.
     *
     * @see StandardSocketOptions#IP_MULTICAST_IF
     * @see MulticastSocket#setInterface(InetAddress)
     * @see MulticastSocket#setNetworkInterface(NetworkInterface)
     */
    public NetworkInterface ipMulticastIf;

    /**
     * IP_MULTICAST_TTL: the time-to-live for Internet Protocol (IP) multicast datagrams.
     *
     * @see StandardSocketOptions#IP_MULTICAST_TTL
     * @see MulticastSocket#setTimeToLive(int)
     */
    public Integer ipMulticastTtl;

    /**
     * IP_MULTICAST_LOOP: loopback for Internet Protocol (IP) multicast datagrams.
     *
     * @see StandardSocketOptions#IP_MULTICAST_LOOP
     * @see MulticastSocket#setLoopbackMode(boolean)
     */
    public Boolean ipMulticastLoop;

    /**
     * Returns a new {@link SocketNetOptions} of which all option fields are copied from this.
     *
     * @return a new {@link SocketNetOptions} of which all option fields are copied from this
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @Nonnull SocketNetOptions clone() {
        SocketNetOptions copy = new SocketNetOptions();
        copy.tcpNoDelay = tcpNoDelay;
        copy.soReuseAddress = soReuseAddress;
        copy.soBroadcast = soBroadcast;
        copy.soLinger = soLinger;
        copy.soTimeout = soTimeout;
        copy.soSndBuf = soSndBuf;
        copy.soRcvBuf = soRcvBuf;
        copy.soKeepalive = soKeepalive;
        copy.soOobInline = soOobInline;
        copy.ipTos = ipTos;
        copy.ipMulticastIf = ipMulticastIf;
        copy.ipMulticastTtl = ipMulticastTtl;
        copy.ipMulticastLoop = ipMulticastLoop;
        return copy;
    }

    /**
     * Applies the current options to the given socket.
     *
     * @param socket the given socket to apply the options to
     * @return the given socket
     * @throws NetException if an error occurs
     */
    public @Nonnull Socket applyTo(@Nonnull Socket socket) throws NetException {
        try {
            if (tcpNoDelay != null) {
                socket.setTcpNoDelay(tcpNoDelay);
            }
            if (soReuseAddress != null) {
                socket.setReuseAddress(soReuseAddress);
            }
            if (soLinger != null) {
                socket.setSoLinger(true, soLinger);
            }
            if (soTimeout != null) {
                socket.setSoTimeout(soTimeout);
            }
            if (soSndBuf != null) {
                socket.setSendBufferSize(soSndBuf);
            }
            if (soRcvBuf != null) {
                socket.setReceiveBufferSize(soRcvBuf);
            }
            if (soKeepalive != null) {
                socket.setKeepAlive(soKeepalive);
            }
            if (soOobInline != null) {
                socket.setOOBInline(soOobInline);
            }
            if (ipTos != null) {
                socket.setTrafficClass(ipTos);
            }
            return socket;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Applies the current options to the given socket.
     *
     * @param socket the given socket to apply the options to
     * @return the given socket
     * @throws NetException if an error occurs
     */
    public @Nonnull ServerSocket applyTo(@Nonnull ServerSocket socket) throws NetException {
        try {
            if (soReuseAddress != null) {
                socket.setReuseAddress(soReuseAddress);
            }
            if (soTimeout != null) {
                socket.setSoTimeout(soTimeout);
            }
            if (soRcvBuf != null) {
                socket.setReceiveBufferSize(soRcvBuf);
            }
            return socket;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Applies the current options to the given socket.
     *
     * @param socket the given socket to apply the options to
     * @return the given socket
     * @throws NetException if an error occurs
     */
    public @Nonnull DatagramSocket applyTo(@Nonnull DatagramSocket socket) throws NetException {
        try {
            if (soReuseAddress != null) {
                socket.setReuseAddress(soReuseAddress);
            }
            if (soBroadcast != null) {
                socket.setBroadcast(soBroadcast);
            }
            if (soTimeout != null) {
                socket.setSoTimeout(soTimeout);
            }
            if (soSndBuf != null) {
                socket.setSendBufferSize(soSndBuf);
            }
            if (soRcvBuf != null) {
                socket.setReceiveBufferSize(soRcvBuf);
            }
            if (ipTos != null) {
                socket.setTrafficClass(ipTos);
            }
            return socket;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Applies the current options to the given socket.
     *
     * @param socket the given socket to apply the options to
     * @return the given socket
     * @throws NetException if an error occurs
     */
    public @Nonnull MulticastSocket applyTo(@Nonnull MulticastSocket socket) throws NetException {
        try {
            if (soReuseAddress != null) {
                socket.setReuseAddress(soReuseAddress);
            }
            if (soBroadcast != null) {
                socket.setBroadcast(soBroadcast);
            }
            if (soTimeout != null) {
                socket.setSoTimeout(soTimeout);
            }
            if (soSndBuf != null) {
                socket.setSendBufferSize(soSndBuf);
            }
            if (soRcvBuf != null) {
                socket.setReceiveBufferSize(soRcvBuf);
            }
            if (ipTos != null) {
                socket.setTrafficClass(ipTos);
            }
            if (ipMulticastIf != null) {
                socket.setNetworkInterface(ipMulticastIf);
            }
            if (ipMulticastTtl != null) {
                socket.setTimeToLive(ipMulticastTtl);
            }
            if (ipMulticastLoop != null) {
                socket.setLoopbackMode(ipMulticastLoop);
            }
            return socket;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Applies the current options to the network channel.
     *
     * @param channel the network channel to apply the options to
     * @param <T>     the type of the network channel
     * @return the network channel
     * @throws NetException if an error occurs
     */
    public @Nonnull <T extends NetworkChannel> T applyTo(@Nonnull T channel) throws NetException {
        try {
            if (tcpNoDelay != null) {
                channel.setOption(StandardSocketOptions.TCP_NODELAY, tcpNoDelay);
            }
            if (soReuseAddress != null) {
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, soReuseAddress);
            }
            if (soBroadcast != null) {
                channel.setOption(StandardSocketOptions.SO_BROADCAST, soBroadcast);
            }
            if (soLinger != null) {
                channel.setOption(StandardSocketOptions.SO_LINGER, soLinger);
            }
            if (soSndBuf != null) {
                channel.setOption(StandardSocketOptions.SO_SNDBUF, soSndBuf);
            }
            if (soRcvBuf != null) {
                channel.setOption(StandardSocketOptions.SO_RCVBUF, soRcvBuf);
            }
            if (soKeepalive != null) {
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, soKeepalive);
            }
            if (ipTos != null) {
                channel.setOption(StandardSocketOptions.IP_TOS, ipTos);
            }
            if (ipMulticastIf != null) {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_IF, ipMulticastIf);
            }
            if (ipMulticastTtl != null) {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_TTL, ipMulticastTtl);
            }
            if (ipMulticastLoop != null) {
                channel.setOption(StandardSocketOptions.IP_MULTICAST_LOOP, ipMulticastLoop);
            }
            return channel;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }
}
