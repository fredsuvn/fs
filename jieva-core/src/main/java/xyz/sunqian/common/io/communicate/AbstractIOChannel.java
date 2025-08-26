package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;

/**
 * Skeletal implementation of {@link IOChannel} to help minimize effort in implementing {@link IOChannel}.
 * <p>
 * The read and write methods of this class is based on a given {@link ByteChannel}, and the advanced I/O operations are
 * based on the {@link #operator} witch is gotten by the specified buffer size. These two field is accessible for
 * subclasses: {@link #channel} and {@link #operator}.
 */
public abstract class AbstractIOChannel implements IOChannel {

    protected final @Nonnull ByteChannel channel;
    protected final @Nonnull IOOperator operator;

    /**
     * Constructs with the given {@link ByteChannel} and the buffer size for advanced I/O operations.
     *
     * @param channel the given {@link ByteChannel}
     * @param bufSize the buffer size for advanced I/O operations
     * @throws IllegalArgumentException if the buffer size {@code <=0}
     */
    protected AbstractIOChannel(@Nonnull ByteChannel channel, int bufSize) throws IllegalArgumentException {
        this.channel = channel;
        this.operator = IOOperator.get(bufSize);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        if (!channel.isOpen()) {
            return -1;
        }
        // if (ret < 0) {
        //     channel.close();
        // }
        return channel.read(dst);
    }

    @Override
    public int write(ByteBuffer src) throws IOException {
        if (!channel.isOpen()) {
            throw new ClosedChannelException();
        }
        return channel.write(src);
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    public byte @Nullable [] availableBytes() throws IORuntimeException {
        return operator.availableBytes(this);
    }

    public @Nullable ByteBuffer availableBuffer() throws IORuntimeException {
        return operator.available(this);
    }

    public @Nullable String availableString() throws IORuntimeException {
        return availableString(CharsKit.defaultCharset());
    }

    public @Nullable String availableString(@Nonnull Charset charset) throws IORuntimeException {
        return operator.availableString(this, charset);
    }

    public void writeBytes(byte @Nonnull [] src) throws IORuntimeException {
        writeBuffer(ByteBuffer.wrap(src));
    }

    public void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException {
        BufferKit.readTo(src, this);
    }

    public void writeString(@Nonnull String src) throws IORuntimeException {
        writeString(src, CharsKit.defaultCharset());
    }

    public void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException {
        writeBytes(src.getBytes(charset));
    }
}
