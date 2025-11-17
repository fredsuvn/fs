package space.sunqian.common.net.tcp;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.net.NetServer;

import java.net.InetSocketAddress;

/**
 * Represents a TCP server, can be built with {@link #newBuilder()}.
 *
 * @author sunqian
 */
public interface TcpServer extends NetServer<InetSocketAddress> {

    /**
     * Returns a new builder for building {@link TcpServer}.
     *
     * @return a new builder for building {@link TcpServer}
     */
    static @Nonnull TcpServerBuilder newBuilder() {
        return new TcpServerBuilder();
    }
}
