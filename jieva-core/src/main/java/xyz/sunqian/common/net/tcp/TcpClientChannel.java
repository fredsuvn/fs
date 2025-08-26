package xyz.sunqian.common.net.tcp;

import xyz.sunqian.common.io.communicate.IOChannel;

/**
 * Client {@link IOChannel} for of TCP network.
 *
 * @author sunqian
 */
public interface TcpClientChannel extends IOChannel {

    /**
     * Blocks current thread and waits for the channel to be readable.
     */
    void awaitReadable();

    /**
     * Wakes up the thread blocked in {@link #awaitReadable()}.
     */
    void wakeUpReadable();
}
