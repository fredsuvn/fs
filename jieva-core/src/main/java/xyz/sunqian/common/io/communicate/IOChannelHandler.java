package xyz.sunqian.common.io.communicate;

import java.nio.ByteBuffer;

/**
 * Handles IO events.
 *
 * @param <A> the type of the channel address
 * @param <C> the type of the channel context
 * @author sunqian
 */
public interface IOChannelHandler<A, C extends IOChannelContext<A>> {

    /**
     * This method is invoked after a new channel is opened, and only once for each new channel.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelOpen(C context) throws Exception;

    /**
     * This method is invoked after a channel is closed by the remote endpoint, and only once for each channel.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelClose(C context) throws Exception;

    /**
     * This method is invoked after a channel receives new data.
     *
     * @param context the context of the channel
     * @param buffer  the buffer containing the data
     * @throws Exception for any error
     */
    void channelRead(C context, ByteBuffer buffer) throws Exception;

    /**
     * This method is invoked after catching the unhandled exception thrown from this handler. If this method still
     * throws an exception, the channel will be closed.
     *
     * @param context the context of the channel
     * @param cause   the unhandled exception
     * @throws Exception for any error
     */
    void exceptionCaught(C context, Throwable cause) throws Exception;
}
