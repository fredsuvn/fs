package xyz.sunqian.common.net;

import xyz.sunqian.annotations.Nonnull;

/**
 * Represents the type of network channel.
 * <p>
 * Note that using {@link Object#equals(Object)} and {@link Object#hashCode()} to determine whether the two types are
 * equal, not {@link #name()}.
 *
 * @author sunqian
 */
public interface NetChannelType {

    /**
     * Returns the {@code TCP/IP} network channel type.
     *
     * @return the {@code TCP/IP} network channel type
     */
    static @Nonnull NetChannelType tcpIp() {
        return TcpIp.SINGLETON;
    }

    /**
     * Returns the {@code UDP/IP} network channel type.
     *
     * @return the {@code UDP/IP} network channel type
     */
    static @Nonnull NetChannelType udpIp() {
        return UdpIp.SINGLETON;
    }

    /**
     * Returns the name of the network channel type.
     *
     * @return the name of the network channel type
     */
    String name();

    final class TcpIp implements NetChannelType {

        static final @Nonnull TcpIp SINGLETON = new TcpIp();

        private TcpIp() {
        }

        public String name() {
            return "TCP/IP";
        }
    }

    final class UdpIp implements NetChannelType {

        static final @Nonnull UdpIp SINGLETON = new UdpIp();

        private UdpIp() {
        }

        public String name() {
            return "UDP/IP";
        }
    }
}
