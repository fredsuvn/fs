package space.sunqian.common.net.udp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.net.NetServer;

import java.net.InetSocketAddress;

/**
 * Represents a UDP server, can be built with {@link #newBuilder()}.
 *
 * @author sunqian
 */
public interface UdpServer extends NetServer<InetSocketAddress> {

    /**
     * Returns a new builder for building {@link UdpServer}.
     *
     * @return a new builder for building {@link UdpServer}
     */
    static @Nonnull UdpServerBuilder newBuilder() {
        return new UdpServerBuilder();
    }
}
