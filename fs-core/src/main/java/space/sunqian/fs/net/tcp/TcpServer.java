package space.sunqian.fs.net.tcp;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.net.NetServer;

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
