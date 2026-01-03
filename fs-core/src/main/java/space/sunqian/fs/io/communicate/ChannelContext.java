package space.sunqian.fs.io.communicate;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IOKit;

import java.nio.channels.ByteChannel;

/**
 * Represents context of an underlying channel (can be accessed by {@link #channel()}) for IO Communication. This
 * interface extends {@link ChannelReader} and {@link ChannelWriter}, providing a unified interface for reading and
 * writing data to the connected remote endpoint.
 * <p>
 * ChannelContext is typically used in callbacks of {@link ChannelHandler}. All callbacks triggered by events from the
 * same underlying channel will use the same {@link ChannelContext} instance, ensuring consistent access to
 * channel-specific state and attachments throughout the channel's lifecycle.
 *
 * @param <C> the type of the underlying channel
 * @author sunqian
 * @implSpec The default I/O methods of this interface use {@link IOKit} to read and write data from the underlying
 * channel, inherited from {@link ChannelReader} and {@link ChannelWriter}.
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
     * remaining lifetime. This method can be invoked multiple times to update the attachment.
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
