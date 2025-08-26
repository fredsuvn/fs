package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

/**
 * Handler for handling events of IO Communication.
 *
 * @param <C> the type of the {@link IOChannel}
 * @author sunqian
 */
public interface IOChannelHandler<C extends IOChannel> {

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param channel the new channel
     * @throws Exception for any error
     */
    void channelOpen(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param channel the closed channel
     * @throws Exception for any error
     */
    void channelClose(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after a channel has available data.
     *
     * @param channel the channel has available data
     * @throws Exception for any error
     */
    void channelRead(@Nonnull C channel) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception, the exception may come from this handler or from
     * the container running this handler.
     * <p>
     * The behavior is undefined if this method still throws an exception.
     *
     * @param channel the channel parameter of the method where this handler throws the exception, may be {@code null}
     *                if the exception is not thrown from this handler
     * @param cause   the unhandled exception
     */
    void exceptionCaught(@Nullable C channel, @Nonnull Throwable cause);
}
