package xyz.sunqian.common.net.tcp;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.communicate.ChannelHandler;

import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Handler for handling tcp server events for TCP network server, based on {@link ServerSocketChannel} and
 * {@link SocketChannel}.
 * <p>
 * All callbacks triggered by events from the same underlying channel will use the same {@link TcpContext} instance,
 * ensuring consistent access to channel-specific state and attachments throughout the channel's lifecycle.
 *
 * @author sunqian
 */
public interface TcpServerHandler extends ChannelHandler<TcpContext, SocketChannel> {

    /**
     * Returns an instance of {@link TcpServerHandler} that does nothing but discards received data.
     *
     * @return an instance of {@link TcpServerHandler} that does nothing but discards received data
     */
    static @Nonnull TcpServerHandler nullHandler() {
        return NullServerHandler.INST;
    }

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param context the context for the new channel
     * @throws Exception for any error
     */
    @Override
    void channelOpen(@Nonnull TcpContext context) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param context the context for the closed channel
     * @throws Exception for any error
     */
    @Override
    void channelClose(@Nonnull TcpContext context) throws Exception;

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
    void channelRead(@Nonnull TcpContext context) throws Exception;

    /**
     * This method is invoked for all active clients in the event loop after each selector wake-up
     * ({@link Selector#select(long)}). It can be used to uniformly handle some issues, such as connection heartbeats.
     * <p>
     * Note the default implementation does nothing.
     *
     * @param context the context for the active channel
     * @throws Exception for any error
     */
    default void channelLoop(@Nonnull TcpContext context) throws Exception {
    }

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
    void exceptionCaught(@Nullable TcpContext context, @Nonnull Throwable cause);
}
