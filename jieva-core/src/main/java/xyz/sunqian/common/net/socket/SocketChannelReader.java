package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.net.NetChannelReader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * The Socket {@link NetChannelReader} implementation. If the result of {@link ReadableByteChannel#read(ByteBuffer)} is
 * {@code -1}, this reader will close the channel.
 *
 * @author sunqian
 */
public class SocketChannelReader implements NetChannelReader {

    private final @Nonnull ReadableByteChannel channel;
    private final @Nonnull IOOperator io;

    /**
     * Constructs with the specified channel and the buffer size for io operations.
     *
     * @param channel the specified channel
     * @param bufSize the buffer size for io operations
     */
    public SocketChannelReader(@Nonnull ReadableByteChannel channel, int bufSize) {
        this.channel = channel;
        this.io = IOOperator.get(bufSize);
    }

    @Override
    public byte @Nullable [] nextBytes() throws IORuntimeException {
        ByteBuffer buffer = nextBuffer();
        return buffer == null ? null : BufferKit.read(buffer);
    }

    @Override
    public @Nullable ByteBuffer nextBuffer() throws IORuntimeException {
        return io.available(this);
    }

    @Override
    public @Nullable String nextString(@Nonnull Charset charset) throws IORuntimeException {
        return io.availableString(this, charset);
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        int ret = channel.read(dst);
        if (ret < 0) {
            close();
        }
        return ret;
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }
}
