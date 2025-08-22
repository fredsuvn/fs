package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

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
     * This method is invoked after a channel is closed, and only once for each channel.
     *
     * @param context the context of the channel
     * @throws Exception for any error
     */
    void channelClose(C context) throws Exception;

    /**
     * This method is invoked after a channel receives new data.
     * <p>
     * The parameter {@code reader} is a {@link ReadableByteChannel} that can be used to read the received data. And, if
     * the {@link ReadableByteChannel#read(ByteBuffer)} returns {@code -1}, means the channel is closed; if returns
     * {@code 0}, means the received data for the current read-event has been read completely.
     *
     * @param context the context of the channel
     * @param reader  the reader for reading received data
     * @throws Exception for any error
     */
    void channelRead(C context, ReadableByteChannel reader) throws Exception;

    /**
     * This method is invoked after catching an unhandled exception thrown from the channel of this context. The
     * behavior is undefined if this method still throws an exception.
     *
     * @param context the context of the channel, may be {@code null} if the exception is not thrown by a context
     * @param cause   the unhandled exception
     */
    void exceptionCaught(@Nullable C context, @Nonnull Throwable cause);
}
