package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.nio.channels.NetworkChannel;
import java.util.Map;

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

    /**
     * Returns a new builder for building {@link TcpClient}.
     *
     * @return a new builder for building {@link TcpClient}
     */
    public static @Nonnull SocketTcpClientBuilder tcpClientBuilder() {
        return new SocketTcpClientBuilder();
    }

    static void setSocketOptions(
        @Nonnull Map<@Nonnull SocketOption<?>, Object> socketOptions,
        @Nonnull NetworkChannel channel
    ) {
        socketOptions.forEach((name, value) -> {
            try {
                channel.setOption(Jie.as(name), value);
            } catch (Exception e) {
                throw new NetException(e);
            }
        });
    }
}
