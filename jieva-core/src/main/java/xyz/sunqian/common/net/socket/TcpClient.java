package xyz.sunqian.common.net.socket;

import xyz.sunqian.common.net.NetClient;

import java.net.InetSocketAddress;

/**
 * Represents a TCP client.
 *
 * @author sunqian
 */
public interface TcpClient extends NetClient<InetSocketAddress> {

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
