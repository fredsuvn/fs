package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;

import java.nio.channels.ByteChannel;

/**
 * Channel context for IO Communication.
 * <p>
 * This interface extends {@link ChannelReader} and {@link ChannelWriter} to provides advanced read/write operations,
 * and the underlying channel is a {@link ByteChannel}.
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
}
