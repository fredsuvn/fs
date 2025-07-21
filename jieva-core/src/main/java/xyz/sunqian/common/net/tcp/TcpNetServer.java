package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.IOKit;
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

    static @Nonnull TcpNetServer newServer(
        int port, @Nonnull TcpNetListener listener
    ) {
        return newServer(
            port,
            -1,
            null,
            listener,
            TaskExecutor.newExecutor().asExecutorService(),
            IOKit.bufferSize()
        );
    }

    static @Nonnull TcpNetServer newServer(
        int port,
        int backlog,
        @Nullable InetAddress bindAddr,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor,
        int bufSize
    ) {
        return TcpSocketBack.newServer(port, backlog, bindAddr, listener, executor, bufSize);
    }

    static @Nonnull TcpNetServer newServer(
        @Nonnull ServerSocket serverSocket,
        @Nonnull TcpNetListener listener,
        @Nonnull Executor executor,
        int bufSize
    ) {
        return TcpSocketBack.newServer(serverSocket, listener, executor, bufSize);
    }
}
