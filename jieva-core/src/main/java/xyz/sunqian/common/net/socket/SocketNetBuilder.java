package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.net.NetException;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.nio.channels.MulticastChannel;
import java.nio.channels.NetworkChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * The Socket network builder, to configure the network options and socket options.
 *
 * @author sunqian
 */
public class SocketNetBuilder {

    // net options:
    private @Nullable Integer bindPort;
    private @Nullable InetAddress bindAddr;
    private @Nullable Integer backlog;
    private @Nullable Integer remotePort;
    private @Nullable InetAddress remoteAddr;
    // net options end.

    // Socket options:
    private @Nullable Boolean tcpNoDelay;
    private @Nullable Boolean soReuseAddr;
    private @Nullable Boolean soBroadcast;
    private @Nullable Integer soLinger;
    private @Nullable Integer soTimeout;
    private @Nullable Integer soSndBuf;
    private @Nullable Integer soRcvBuf;
    private @Nullable Boolean soKeepalive;
    private @Nullable Boolean soOobInline;
    private @Nullable Integer ipTos;
    private @Nullable NetworkInterface ipMulticastIf;
    private @Nullable Integer ipMulticastTtl;
    private @Nullable Boolean ipMulticastLoop;
    // Socket options end.

    /**
     * Sets the port number which is locally bound to
     *
     * @param localPort the port number which is locally bound to
     * @return this
     */
    public SocketNetBuilder bindPort(int localPort) {
        this.bindPort = localPort;
        return this;
    }

    /**
     * Sets the {@link InetAddress} which is locally bound to
     *
     * @param localAddr the {@link InetAddress} which is locally bound to
     * @return this
     */
    public SocketNetBuilder bindAddr(@Nonnull InetAddress localAddr) {
        this.bindAddr = localAddr;
        return this;
    }

    /**
     * Sets the {@link InetAddress}, which is locally bound to, by the specified hostname
     *
     * @param hostname the specified hostname
     * @return this
     * @throws NetException if the {@link InetAddress} can not be determined
     */
    public SocketNetBuilder bindAddr(@Nonnull String hostname) throws NetException {
        try {
            this.bindAddr = InetAddress.getByName(hostname);
        } catch (Exception e) {
            throw new NetException(e);
        }
        return this;
    }

    /**
     * Sets the requested maximum length of the queue of incoming connections.
     *
     * @param backlog the requested maximum length of the queue of incoming connections
     * @return this
     */
    public SocketNetBuilder backlog(int backlog) {
        this.backlog = backlog;
        return this;
    }

    /**
     * Sets the remote port number. This is typically used for client.
     *
     * @param remotePort the remote port number
     * @return this
     */
    public SocketNetBuilder remotePort(int remotePort) {
        this.remotePort = remotePort;
        return this;
    }

    /**
     * Sets the remote {@link InetAddress}. This is typically used for client.
     *
     * @param remoteAddr the remote {@link InetAddress}
     * @return this
     */
    public SocketNetBuilder remoteAddr(@Nonnull InetAddress remoteAddr) {
        this.remoteAddr = remoteAddr;
        return this;
    }

    /**
     * Sets the remote {@link InetAddress} by the specified hostname. This is typically used for client.
     *
     * @param hostname the specified hostname
     * @return this
     * @throws NetException if the {@link InetAddress} can not be determined
     */
    public SocketNetBuilder remoteAddr(@Nonnull String hostname) throws NetException {
        try {
            this.remoteAddr = InetAddress.getByName(hostname);
        } catch (Exception e) {
            throw new NetException(e);
        }
        return this;
    }

    /**
     * Sets TCP_NODELAY: disable the Nagle algorithm.
     *
     * @param tcpNoDelay the TCP_NODELAY option
     * @return this
     * @see StandardSocketOptions#TCP_NODELAY
     * @see Socket#setTcpNoDelay(boolean)
     */
    public SocketNetBuilder tcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
        return this;
    }

    /**
     * Sets SO_REUSEADDR: re-use address.
     *
     * @param soReuseAddr the SO_REUSEADDR option
     * @return this
     * @see StandardSocketOptions#SO_REUSEADDR
     * @see Socket#setReuseAddress(boolean)
     * @see ServerSocket#setReuseAddress(boolean)
     * @see DatagramSocket#setReuseAddress(boolean)
     * @see MulticastSocket#setReuseAddress(boolean)
     */
    public SocketNetBuilder soReuseAddr(boolean soReuseAddr) {
        this.soReuseAddr = soReuseAddr;
        return this;
    }

    /**
     * Sets SO_BROADCAST: allow transmission of broadcast datagrams.
     *
     * @param soBroadcast the SO_BROADCAST option
     * @return this
     * @see StandardSocketOptions#SO_BROADCAST
     * @see DatagramSocket#setBroadcast(boolean)
     * @see MulticastSocket#setBroadcast(boolean)
     */
    public SocketNetBuilder soBroadcast(boolean soBroadcast) {
        this.soBroadcast = soBroadcast;
        return this;
    }

    /**
     * Sets SO_LINGER: linger on close if data is present, in seconds.
     *
     * @param soLinger the SO_LINGER option
     * @return this
     * @see StandardSocketOptions#SO_LINGER
     * @see Socket#setSoLinger(boolean, int)
     */
    public SocketNetBuilder soLinger(int soLinger) {
        this.soLinger = soLinger;
        return this;
    }

    /**
     * Sets SO_TIMEOUT: timeout for blocking operations, in milliseconds.
     *
     * @param soTimeout the SO_TIMEOUT option
     * @return this
     * @see SocketOptions#SO_TIMEOUT
     * @see Socket#setSoTimeout(int)
     * @see ServerSocket#setSoTimeout(int)
     * @see DatagramSocket#setSoTimeout(int)
     * @see MulticastSocket#setSoTimeout(int)
     */
    public SocketNetBuilder soTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
        return this;
    }

    /**
     * Sets SO_SNDBUF: the size of the socket send buffer.
     *
     * @param soSndBuf the SO_SNDBUF option
     * @return this
     * @see StandardSocketOptions#SO_SNDBUF
     * @see Socket#setSendBufferSize(int)
     * @see DatagramSocket#setSendBufferSize(int)
     * @see MulticastSocket#setSendBufferSize(int)
     */
    public SocketNetBuilder soSndBuf(int soSndBuf) {
        this.soSndBuf = soSndBuf;
        return this;
    }

    /**
     * Sets SO_RCVBUF: the size of the socket receive buffer.
     *
     * @param soRcvBuf the SO_RCVBUF option
     * @return this
     * @see StandardSocketOptions#SO_RCVBUF
     * @see Socket#setReceiveBufferSize(int)
     * @see ServerSocket#setReceiveBufferSize(int)
     * @see DatagramSocket#setReceiveBufferSize(int)
     * @see MulticastSocket#setReceiveBufferSize(int)
     */
    public SocketNetBuilder soRcvBuf(int soRcvBuf) {
        this.soRcvBuf = soRcvBuf;
        return this;
    }

    /**
     * Sets SO_KEEPALIVE: keep connection alive.
     *
     * @param soKeepalive the SO_KEEPALIVE option
     * @return this
     * @see StandardSocketOptions#SO_KEEPALIVE
     * @see Socket#setKeepAlive(boolean)
     */
    public SocketNetBuilder soKeepalive(boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
        return this;
    }

    /**
     * Sets SO_OOBINLIN: receipt of TCP urgent data.
     *
     * @param soOobInline the SO_OOBINLIN option
     * @return this
     * @see SocketOptions#SO_OOBINLINE
     * @see Socket#setOOBInline(boolean)
     */
    public SocketNetBuilder soOobInline(boolean soOobInline) {
        this.soOobInline = soOobInline;
        return this;
    }

    /**
     * Sets IP_TOS: the Type of Service (ToS) octet in the Internet Protocol (IP) header.
     *
     * @param ipTos the IP_TOS option
     * @return this
     * @see StandardSocketOptions#IP_TOS
     * @see Socket#setTrafficClass(int)
     * @see DatagramSocket#setTrafficClass(int)
     * @see MulticastSocket#setTrafficClass(int)
     */
    public SocketNetBuilder ipTos(int ipTos) {
        this.ipTos = ipTos;
        return this;
    }

    /**
     * Sets IP_MULTICAST_IF: the network interface for Internet Protocol (IP) multicast datagrams.
     *
     * @param ipMulticastIf the IP_MULTICAST_IF option
     * @return this
     * @see StandardSocketOptions#IP_MULTICAST_IF
     * @see MulticastSocket#setInterface(InetAddress)
     * @see MulticastSocket#setNetworkInterface(NetworkInterface)
     */
    public SocketNetBuilder ipMulticastIf(@Nonnull NetworkInterface ipMulticastIf) {
        this.ipMulticastIf = ipMulticastIf;
        return this;
    }

    /**
     * Sets IP_MULTICAST_TTL: the time-to-live for Internet Protocol (IP) multicast datagrams.
     *
     * @param ipMulticastTtl the IP_MULTICAST_TTL option
     * @return this
     * @see StandardSocketOptions#IP_MULTICAST_TTL
     * @see MulticastSocket#setTimeToLive(int)
     */
    public SocketNetBuilder ipMulticastTtl(int ipMulticastTtl) {
        this.ipMulticastTtl = ipMulticastTtl;
        return this;
    }

    /**
     * Sets IP_MULTICAST_LOOP: loopback for Internet Protocol (IP) multicast datagrams.
     *
     * @param ipMulticastLoop the IP_MULTICAST_LOOP option
     * @return this
     * @see StandardSocketOptions#IP_MULTICAST_LOOP
     * @see MulticastSocket#setLoopbackMode(boolean)
     */
    public SocketNetBuilder ipMulticastLoop(boolean ipMulticastLoop) {
        this.ipMulticastLoop = ipMulticastLoop;
        return this;
    }

    /**
     * Returns a new {@link SocketNetBuilder} of which all option fields are copied from this.
     *
     * @return a new {@link SocketNetBuilder} of which all option fields are copied from this
     */
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public @Nonnull SocketNetBuilder clone() {
        SocketNetBuilder copy = new SocketNetBuilder();
        copy.bindPort = bindPort;
        copy.bindAddr = bindAddr;
        copy.backlog = backlog;
        copy.remotePort = remotePort;
        copy.remoteAddr = remoteAddr;
        copy.tcpNoDelay = tcpNoDelay;
        copy.soReuseAddr = soReuseAddr;
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
     * Builds a new {@link Socket} with the current options.
     *
     * @return a new {@link Socket} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull Socket buildSocket() throws NetException {
        Integer remotePort = this.remotePort;
        if (remotePort == null) {
            throw new NetException("remotePort is null");
        }
        InetAddress remoteAddr = this.remoteAddr;
        if (remoteAddr == null) {
            throw new NetException("remoteAddr is null");
        }
        Socket socket;
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        try {
            if (bindPort == null || bindAddr == null) {
                socket = new Socket(remoteAddr, remotePort);
            } else {
                socket = new Socket(remoteAddr, remotePort, bindAddr, bindPort);
            }
            return applyTo(socket);
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link ServerSocket} with the current options.
     *
     * @return a new {@link ServerSocket} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull ServerSocket buildServerSocket() throws NetException {
        ServerSocket socket;
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        Integer backlog = this.backlog;
        try {
            if (bindPort == null) {
                socket = new ServerSocket();
            } else if (backlog == null) {
                socket = new ServerSocket(bindPort);
            } else if (bindAddr == null) {
                socket = new ServerSocket(bindPort, backlog);
            } else {
                socket = new ServerSocket(bindPort, backlog, bindAddr);
            }
            return applyTo(socket);
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link DatagramSocket} with the current options.
     *
     * @return a new {@link DatagramSocket} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull DatagramSocket buildDatagramSocket() throws NetException {
        DatagramSocket socket;
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        try {
            if (bindPort == null) {
                socket = new DatagramSocket();
            } else if (bindAddr == null) {
                socket = new DatagramSocket(bindPort);
            } else {
                socket = new DatagramSocket(bindPort, bindAddr);
            }
            return applyTo(socket);
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link MulticastSocket} with the current options.
     *
     * @return a new {@link MulticastSocket} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull MulticastSocket buildMulticastSocket() throws NetException {
        MulticastSocket socket;
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        try {
            if (bindPort == null) {
                socket = new MulticastSocket();
            } else if (bindAddr == null) {
                socket = new MulticastSocket(bindPort);
            } else {
                socket = new MulticastSocket(new InetSocketAddress(bindAddr, bindPort));
            }
            return applyTo(socket);
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link SocketChannel} with the current options.
     *
     * @return a new {@link SocketChannel} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull SocketChannel buildSocketChannel() throws NetException {
        Integer remotePort = this.remotePort;
        if (remotePort == null) {
            throw new NetException("remotePort is null");
        }
        InetAddress remoteAddr = this.remoteAddr;
        if (remoteAddr == null) {
            throw new NetException("remoteAddr is null");
        }
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        try {
            SocketChannel channel = SocketChannel.open();
            if (bindPort != null && bindAddr != null) {
                channel.bind(new InetSocketAddress(bindAddr, bindPort));
            }
            applyTo(channel);
            channel.connect(new InetSocketAddress(remoteAddr, remotePort));
            return channel;
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link ServerSocketChannel} with the current options.
     *
     * @return a new {@link ServerSocketChannel} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull ServerSocketChannel buildServerSocketChannel() throws NetException {
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        Integer backlog = this.backlog;
        try {
            ServerSocketChannel channel = ServerSocketChannel.open();
            if (bindPort != null && bindAddr != null) {
                if (backlog == null) {
                    channel.bind(new InetSocketAddress(bindAddr, bindPort));
                } else {
                    channel.bind(new InetSocketAddress(bindAddr, bindPort), backlog);
                }
            }
            return applyTo(channel);
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Builds a new {@link DatagramChannel} with the current options.
     *
     * @return a new {@link DatagramChannel} with the current options
     * @throws NetException if an error occurs
     */
    public @Nonnull DatagramChannel buildDatagramChannel() throws NetException {
        Integer bindPort = this.bindPort;
        InetAddress bindAddr = this.bindAddr;
        Integer backlog = this.backlog;
        try {
            DatagramChannel channel = DatagramChannel.open();
            if (bindPort != null && bindAddr != null) {
                if (backlog == null) {
                    channel.bind(new InetSocketAddress(bindAddr, bindPort));
                } else {
                    //channel.bind(new InetSocketAddress(bindAddr, bindPort), backlog);
                }
            }
            return applyTo(channel);
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
    public @Nonnull Socket applyTo(@Nonnull Socket socket) throws NetException {
        try {
            if (tcpNoDelay != null) {
                socket.setTcpNoDelay(tcpNoDelay);
            }
            if (soReuseAddr != null) {
                socket.setReuseAddress(soReuseAddr);
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
            if (soReuseAddr != null) {
                socket.setReuseAddress(soReuseAddr);
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
            if (soReuseAddr != null) {
                socket.setReuseAddress(soReuseAddr);
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
            if (soReuseAddr != null) {
                socket.setReuseAddress(soReuseAddr);
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
            if (soReuseAddr != null) {
                channel.setOption(StandardSocketOptions.SO_REUSEADDR, soReuseAddr);
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
