package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.net.NetClient;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public interface TcpNetClient extends NetClient<InetSocketAddress> {

    /**
     * Returns the port number to which this client connects, may be {@code -1} if the client does not connect yet.
     *
     * @return the port number to which this client connects, may be {@code -1} if the client does not connect yet
     */
    default int getRemotePort() {
        InetSocketAddress address = getRemoteAddress();
        return address != null ? address.getPort() : -1;
    }

    /**
     * Returns the IP this client connects to, may be {@code null} if the client does not connect yet.
     *
     * @return the IP this client connects to, may be {@code null} if the client does not connect yet
     */
    default @Nullable InetAddress getRemoteIp() {
        InetSocketAddress address = getRemoteAddress();
        return address != null ? address.getAddress() : null;
    }

    /**
     * Returns the port number to which this server is bound, may be {@code -1} if the server is not bound yet.
     *
     * @return the port number to which this server is bound, may be {@code -1} if the server is not bound yet
     */
    default int getBoundPort() {
        InetSocketAddress address = getBoundAddress();
        return address != null ? address.getPort() : -1;
    }

    /**
     * Returns the IP this server is bound to, may be {@code null} if the server is not bound yet.
     *
     * @return the IP this server is bound to, may be {@code null} if the server is not bound yet
     */
    default @Nullable InetAddress getBoundIp() {
        InetSocketAddress address = getBoundAddress();
        return address != null ? address.getAddress() : null;
    }
}
