package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.net.NetException;
import xyz.sunqian.common.net.NetServer;
import xyz.sunqian.common.task.TaskExecutor;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.concurrent.Executor;

/**
 * This interface represents a TCP/IP network server.
 *
 * @author sunqian
 */
public interface TcpNetServer extends NetServer {

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
     * Returns the port number to bind to the server, may be {@code -1} if the port is not bound.
     *
     * @return the port number to bind to the server, may be {@code -1} if the port is not bound
     */
    int getPort();
}
