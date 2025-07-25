package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetServer;
import xyz.sunqian.common.task.TaskExecutor;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.net.UnknownHostException;
import java.util.concurrent.Executor;

/**
 * This interface represents a TCP/IP network server.
 *
 * @author sunqian
 */
public interface TcpNetServer extends NetServer<InetSocketAddress> {

    /**
     * Creates a new TCP/IP network server with the specified parameters.
     *
     * @param listener the listener to listen server events
     * @return a new TCP/IP network server with the specified listener to listen server events
     * @throws NetException if any error occurs
     */
    static @Nonnull TcpNetServer newServer(
        @Nonnull TcpNetListener listener
    ) throws NetException {
        try {
            ServerSocket server = new ServerSocket(0);
            return newServer(server, listener, TaskExecutor.newExecutor().asExecutorService(), IOKit.bufferSize());
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Creates a new TCP/IP network server with the specified parameters.
     *
     * @param port     the port number to bind to the server
     * @param listener the listener to listen server events
     * @return a new TCP/IP network server with the specified parameters
     * @throws NetException if any error occurs
     */
    static @Nonnull TcpNetServer newServer(
        int port, @Nonnull TcpNetListener listener
    ) throws NetException {
        try {
            ServerSocket server = new ServerSocket(port);
            return newServer(server, listener, TaskExecutor.newExecutor().asExecutorService(), IOKit.bufferSize());
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Creates a new TCP/IP network server with the specified parameters.
     *
     * @param port     the port number to bind to the server
     * @param backlog  the maximum length of the queue of incoming connections
     * @param listener the listener to listen server events
     * @return a new TCP/IP network server with the specified parameters
     * @throws NetException if any error occurs
     */
    static @Nonnull TcpNetServer newServer(
        int port, int backlog, @Nonnull TcpNetListener listener
    ) throws NetException {
        try {
            ServerSocket server = new ServerSocket(port, backlog);
            return newServer(server, listener, TaskExecutor.newExecutor().asExecutorService(), IOKit.bufferSize());
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Creates a new TCP/IP network server with the specified parameters.
     *
     * @param port     the port number to bind to the server
     * @param backlog  the maximum length of the queue of incoming connections
     * @param bindAddr the local address to bind to the server
     * @param listener the listener to listen server events
     * @return a new TCP/IP network server with the specified parameters
     */
    static @Nonnull TcpNetServer newServer(
        int port, int backlog, @Nonnull InetAddress bindAddr, @Nonnull TcpNetListener listener
    ) throws NetException {
        try {
            ServerSocket server = new ServerSocket(port, backlog, bindAddr);
            return newServer(server, listener, TaskExecutor.newExecutor().asExecutorService(), IOKit.bufferSize());
        } catch (Exception e) {
            throw new NetException(e);
        }
    }

    /**
     * Creates a new TCP/IP network server with the specified parameters.
     *
     * @param serverSocket the socket object to bind to the server
     * @param listener     the listener to listen server events
     * @param executor     the executor to execute events handling
     * @param bufSize      the buffer size for reading data from the remote client
     * @return a new TCP/IP network server with the specified parameters
     */
    static @Nonnull TcpNetServer newServer(
        @Nonnull ServerSocket serverSocket,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor,
        int bufSize
    ) {
        return SocketBack.newServer(serverSocket, listener, executor, bufSize);
    }

    /**
     * Returns the port number to which this server is bound, may be {@code -1} if the server is not bound yet.
     *
     * @return the port number to which this server is bound, may be {@code -1} if the server is not bound yet
     */
    default int getBoundPort() {
        InetSocketAddress address = getBoundAddress();
        return address != null ? address.getPort() : -1;
    }

    /**
     * Returns the IP this server is bound to, may be {@code null} if the server is not bound yet.
     *
     * @return the IP this server is bound to, may be {@code null} if the server is not bound yet
     */
    default @Nullable InetAddress getBoundIp() {
        InetSocketAddress address = getBoundAddress();
        return address != null ? address.getAddress() : null;
    }

    /**
     * Builder for creating new TCP/IP network server.
     *
     * @author sunqian
     */
    class Builder {

        private @Nullable Integer boundPort;
        private @Nullable InetAddress boundIp;
        private int bufSize = IOKit.bufferSize();
        private @Nullable Executor acceptExecutor;
        private @Nullable Executor workExecutor;

        private @Nullable Boolean soReuseAddr;
        private @Nullable Integer soTimeout;
        private @Nullable Integer soRcvBuf;

        /**
         * Sets the port number to which this server is bound.
         *
         * @param port the port number to which this server is bound
         * @return this
         */
        public Builder boundPort(int port) {
            this.boundPort = port;
            return this;
        }

        /**
         * Sets the IP this server is bound to.
         *
         * @param ip the IP this server is bound to
         * @return this
         */
        public Builder boundIp(@Nonnull InetAddress ip) {
            this.boundIp = ip;
            return this;
        }

        /**
         * Sets the IP, specified by the given hostname, to which this server is bound.
         *
         * @param hostname the given hostname
         * @return this
         * @throws NetException if the IP can not be determined
         */
        public Builder boundHost(@Nonnull String hostname) throws NetException {
            try {
                this.boundIp = InetAddress.getByName(hostname);
            } catch (UnknownHostException e) {
                throw new NetException(e);
            }
            return this;
        }

        /**
         * Sets the address this server is bound to.
         *
         * @param address the address this server is bound to
         * @return this
         */
        public Builder boundAddress(@Nonnull InetSocketAddress address) {
            this.boundPort = address.getPort();
            this.boundIp = address.getAddress();
            return this;
        }

        /**
         * Sets the {@code SO_REUSEADDR} Socket option: re-use address.
         *
         * @param soReuseAddr the {@code SO_REUSEADDR} Socket option
         * @return this
         * @see StandardSocketOptions#SO_REUSEADDR
         */
        public Builder soReuseAddr(boolean soReuseAddr) {
            this.soReuseAddr = soReuseAddr;
            return this;
        }

        /**
         * Sets the {@code SO_TIMEOUT} option: timeout for blocking operations, in milliseconds.
         *
         * @param soTimeout the {@code SO_TIMEOUT} option
         * @return this
         * @see SocketOptions#SO_TIMEOUT
         * @see ServerSocket#setSoTimeout(int)
         */
        public Builder soTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
            return this;
        }

        /**
         * Sets the {@code SO_RCVBUF} Socket option: the size of the socket receive buffer.
         *
         * @param soRcvBuf the {@code SO_RCVBUF} Socket option
         * @return this
         * @see StandardSocketOptions#SO_RCVBUF
         */
        public Builder soRcvBuf(int soRcvBuf) {
            this.soRcvBuf = soRcvBuf;
            return this;
        }


        // if (soReuseAddr != null) {
        //     socket.setReuseAddress(soReuseAddr);
        // }
        //     if (soTimeout != null) {
        //     socket.setSoTimeout(soTimeout);
        // }
        //     if (soRcvBuf != null) {
        //     socket.setReceiveBufferSize(soRcvBuf);
        // }
    }
}
