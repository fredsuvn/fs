package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;

/**
 * Utilities kit for Socket.
 *
 * @author sunqian
 */
public class SocketKit {

    /**
     * Returns a new builder for building {@link TcpServer}.
     *
     * @return a new builder for building {@link TcpServer}
     */
    public static @Nonnull SocketTcpServerBuilder tcpServerBuilder() {
        return new SocketTcpServerBuilder();
    }
}
