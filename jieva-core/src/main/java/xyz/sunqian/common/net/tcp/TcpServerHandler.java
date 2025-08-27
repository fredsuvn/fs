package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.communicate.ChannelContext;
import xyz.sunqian.common.io.communicate.ChannelHandler;
import xyz.sunqian.common.net.NetException;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * Handler for tcp server events.
 *
 * @author sunqian
 */
public interface TcpServerHandler extends ChannelHandler<TcpServerHandler.Context> {

    /**
     * Returns an instance of {@link TcpServerHandler} that does nothing but discards received data.
     *
     * @return an instance of {@link TcpServerHandler} that does nothing but discards received data
     */
    static @Nonnull TcpServerHandler nullHandler() {
        return NullServerHandler.SINGLETON;
    }

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param context the context for the new channel
     * @throws Exception for any error
     */
    @Override
    void channelOpen(@Nonnull TcpServerHandler.Context context) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param context the context for the closed channel
     * @throws Exception for any error
     */
    @Override
    void channelClose(@Nonnull TcpServerHandler.Context context) throws Exception;

    /**
     * This method is invoked after the server receives data.
     * <p>
     * If the read method of the channel returns {@code -1}, it means the channel can no longer be read (usually because
     * the peer sent a {@code FIN} and entered the half-closed state). In this case, the channel can be closed to
     * complete the close operation. If it returns {@code 0}, it indicates that all available data from the current read
     * event has been read, but the channel remains alive.
     *
     * @param context the context for the channel where the data is received
     * @throws Exception for any error
     */
    @Override
    void channelRead(@Nonnull TcpServerHandler.Context context) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception, the exception may come from this handler or from
     * the container running this handler.
     * <p>
     * The behavior is undefined if this method still throws an exception.
     *
     * @param context the context parameter of the method where this handler throws the exception, may be {@code null}
     *                if the exception is not thrown from this handler
     * @param cause   the unhandled exception
     */
    @Override
    void exceptionCaught(@Nullable TcpServerHandler.Context context, @Nonnull Throwable cause);

    /**
     * Context for {@link TcpServerHandler}, based on {@link SocketChannel}.
     */
    interface Context extends ChannelContext<SocketChannel> {

        /**
         * Returns the address of the client.
         * <p>
         * Note even if the underlying channel is closed, it still returns the address at which the connection was
         * alive.
         *
         * @return the address of the client
         */
        @Nonnull
        InetSocketAddress clientAddress();

        /**
         * Returns the address of the server.
         * <p>
         * Note even if the underlying channel is closed, it still returns the address at which the connection was
         * alive.
         *
         * @return the address of the server
         */
        @Nonnull
        InetSocketAddress serverAddress();

        /**
         * Disconnects and closes the client.
         *
         * @throws NetException if any error occurs
         */
        void close() throws NetException;
    }
}
