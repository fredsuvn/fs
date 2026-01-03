package space.sunqian.fs.io.communicate;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.io.IOOperator;
import space.sunqian.fs.io.IORuntimeException;

import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

/**
 * Skeletal implementation of {@link ChannelContext}. Its read methods are based on the specified {@link IOOperator}
 * provided by {@link #ioOperator()}. The underlying channel is provided by {@link #channel()}.
 *
 * @param <C> the type of the underlying channel
 * @author sunqian
 */
public abstract class AbstractChannelContext<C extends ByteChannel> implements ChannelContext<C> {

    private final @Nonnull C channel;
    private @Nullable Object attachment;

    /**
     * Constructs with the given underlying channel.
     *
     * @param channel the underlying channel
     */
    protected AbstractChannelContext(@Nonnull C channel) {
        this.channel = channel;
    }

    /**
     * Returns the {@link IOOperator} to be used for read operations.
     *
     * @return the {@link IOOperator} to be used for read operations
     */
    protected abstract @Nonnull IOOperator ioOperator();

    @Override
    public @Nonnull C channel() {
        return channel;
    }

    @Override
    public void attach(Object attachment) {
        this.attachment = attachment;
    }

    @Override
    public Object attachment() {
        return attachment;
    }

    @Override
    public byte @Nullable [] availableBytes() throws IORuntimeException {
        return ioOperator().availableBytes(channel());
    }

    @Override
    public @Nullable String availableString() throws IORuntimeException {
        return ioOperator().availableString(channel());
    }

    @Override
    public @Nullable String availableString(@Nonnull Charset charset) throws IORuntimeException {
        return ioOperator().availableString(channel(), charset);
    }
}
