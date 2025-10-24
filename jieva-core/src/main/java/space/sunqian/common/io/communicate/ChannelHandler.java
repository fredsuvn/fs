package space.sunqian.common.io.communicate;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.nio.channels.ByteChannel;

/**
 * Handler for handling underlying channel events of IO Communication.
 * <p>
 * All callbacks triggered by events from the same underlying channel will use the same {@link ChannelContext} instance,
 * ensuring consistent access to channel-specific state and attachments throughout the channel's lifecycle.
 *
 * @param <C>  the type of the {@link ChannelContext}
 * @param <CH> the type of the underlying channel
 * @author sunqian
 */
public interface ChannelHandler<C extends ChannelContext<CH>, CH extends ByteChannel> {

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param context the context for the new channel
     * @throws Exception for any error
     */
    void channelOpen(@Nonnull C context) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param context the context for the closed channel
     * @throws Exception for any error
     */
    void channelClose(@Nonnull C context) throws Exception;

    /**
     * This method is invoked after a channel receives data.
     *
     * @param context the context for the channel where the data is received
     * @throws Exception for any error
     */
    void channelRead(@Nonnull C context) throws Exception;

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
    void exceptionCaught(@Nullable C context, @Nonnull Throwable cause);
}
