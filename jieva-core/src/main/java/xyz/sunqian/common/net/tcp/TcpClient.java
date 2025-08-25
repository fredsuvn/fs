package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.net.NetClient;

import java.net.InetSocketAddress;

/**
 * Represents a TCP client, can be built with {@link #newBuilder()}.
 *
 * @author sunqian
 */
public interface TcpClient extends NetClient<InetSocketAddress> {

    /**
     * Returns a new builder for building {@link TcpClient}.
     *
     * @return a new builder for building {@link TcpClient}
     */
    static @Nonnull TcpClientBuilder newBuilder() {
        return new TcpClientBuilder();
    }

    /**
     * Blocks current thread until the next read operation is available. This method can be interrupted by
     * {@link #wakeUpRead()}.
     */
    void nextRead();

    /**
     * Wakes up the blocking for {@link #nextRead()}.
     */
    void wakeUpRead();
}
