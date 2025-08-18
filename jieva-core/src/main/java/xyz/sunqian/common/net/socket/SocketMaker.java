package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.io.communicate.IOChannelHandler;
import xyz.sunqian.common.net.NetException;

/**
 * This interface is used to create server or client via Socket. The implementations should be thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
public interface SocketMaker {

    /**
     * Returns an instance of {@link NioSocketMaker} which implements the {@link SocketMaker}.
     *
     * @return an instance of {@link NioSocketMaker} which implements the {@link SocketMaker}
     */
    static @Nonnull NioSocketMaker byNio() {
        return new NioSocketMaker();
    }

    /**
     * Creates a new {@link TcpServerSpec} instance to start server instance.
     *
     * @param handler          the handler to handle the server events
     * @param handlerThreadNum the thread number of the thread pool, which is used for the handler
     * @return a new {@link TcpServerSpec} instance to start server instance
     * @throws NetException if any problem occurs during creation
     */
    @Nonnull
    TcpServerSpec makeTcpServer(
        @Nonnull IOChannelHandler<TcpChannelContext> handler, int handlerThreadNum
    ) throws NetException;
}
