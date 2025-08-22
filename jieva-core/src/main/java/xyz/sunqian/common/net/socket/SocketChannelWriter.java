package xyz.sunqian.common.net.socket;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.net.NetChannelWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * The Socket {@link NetChannelWriter} implementation.
 *
 * @author sunqian
 */
public class SocketChannelWriter implements NetChannelWriter {

    private final @Nonnull WritableByteChannel channel;

    /**
     * Constructs with the specified channel and the buffer size for io operations.
     *
     * @param channel the specified channel
     */
    public SocketChannelWriter(@Nonnull WritableByteChannel channel) {
        this.channel = channel;
    }

    @Override
    public void writeBytes(byte @Nonnull [] src) throws IORuntimeException {
        writeBuffer(ByteBuffer.wrap(src));
    }

    @Override
    public void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException {
        BufferKit.readTo(src, this);
    }

    @Override
    public void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException {
        writeBytes(src.getBytes(charset));
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
}
