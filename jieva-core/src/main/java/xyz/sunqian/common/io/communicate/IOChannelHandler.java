package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

/**
 * Handles IO events.
 *
 * @param <A> the type of the channel address
 * @param <R> the type of the channel reader
 * @param <W> the type of the channel writer
 * @param <C> the type of the channel context
 * @author sunqian
 */
public interface IOChannelHandler<
    A,
    R extends IOChannelReader,
    W extends IOChannelWriter,
    C extends IOChannelContext<A, R, W>
    > {

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelOpen(@Nonnull C context) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelClose(@Nonnull C context) throws Exception;

    /**
     * This method is invoked after a channel receives new data, and the context's reader from the
     * {@link IOChannelContext#reader()} may read the data.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelRead(@Nonnull C context) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception thrown from the channel of this context. The
     * behavior is undefined if this method still throws an exception.
     *
     * @param context the context of the channel, may be {@code null} if the exception is not thrown by a context
     * @param cause   the unhandled exception
     */
    void exceptionCaught(@Nullable C context, @Nonnull Throwable cause);
}
