package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;

/**
 * Represents a TCP server.
 *
 * @author sunqian
 */
public interface TcpServer {

    /**
     * Starts this server, and returns immediately after startup. This method will not block the current thread,
     * {@link #await()} will.
     *
     * @throws NetException if any error occurs
     */
    void start() throws NetException;

    /**
     * Closes this server.
     *
     * @throws NetException if any error occurs
     */
    void close() throws NetException;

    /**
     * Blocks current thread until this server is closed.
     *
     * @throws NetException if any error occurs
     */
    void await() throws NetException;

    /**
     * Returns the address this server is bound to, may be {@code null} if the server is not bound yet.
     *
     * @return the address this server is bound to, may be {@code null} if the server is not bound yet
     */
    @Nullable
    InetSocketAddress getBoundAddress();
}
