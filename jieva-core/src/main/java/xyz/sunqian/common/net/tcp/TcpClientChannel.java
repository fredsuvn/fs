package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.communicate.IOChannel;

import java.net.InetSocketAddress;

/**
 * Client {@link IOChannel} for of TCP network.
 *
 * @author sunqian
 */
public interface TcpClientChannel extends IOChannel {

    /**
     * Returns the remote address of this channel.
     *
     * @return the remote address of this channel
     */
    @Nonnull
    InetSocketAddress remoteAddress();

    /**
     * Returns the local address of this channel.
     *
     * @return the local address of this channel
     */
    @Nonnull
    InetSocketAddress localAddress();

    /**
     * Blocks current thread and waits for the channel to be readable.
     */
    void awaitReadable();

    /**
     * Wakes up the thread blocked in {@link #awaitReadable()}.
     */
    void wakeUpReadable();
}
