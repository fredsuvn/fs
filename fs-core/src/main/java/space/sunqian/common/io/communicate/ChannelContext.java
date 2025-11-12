package space.sunqian.common.io.communicate;

import space.sunqian.annotations.Nonnull;

import java.nio.channels.ByteChannel;

/**
 * Represents context of an underlying channel for IO Communication, extends {@link ChannelReader} and
 * {@link ChannelWriter} to provides advanced read/write operations, and the underlying channel is a
 * {@link ByteChannel}.
 * <p>
 * ChannelContext is typically used in callbacks of {@link ChannelHandler}. All callbacks triggered by events from the
 * same underlying channel will use the same {@link ChannelContext} instance, ensuring consistent access to
 * channel-specific state and attachments throughout the channel's lifecycle.
 *
 * @param <C> the type of the underlying channel
 * @author sunqian
 */
public interface ChannelContext<C extends ByteChannel> extends ChannelReader<C>, ChannelWriter<C> {

    /**
     * Returns the underlying channel of this context.
     *
     * @return the underlying channel of this context
     */
    @Override
    @Nonnull
    C channel();

    /**
     * Attaches the given attachment to this context. The given attachment object is bound to this context for its
     * entire lifetime.
     *
     * @param attachment the given attachment to be attached
     */
    void attach(Object attachment);

    /**
     * Returns the attachment of this context. The attachment is bound to this context by {@link #attach(Object)}.
     *
     * @return the attachment of this context
     */
    Object attachment();
}
