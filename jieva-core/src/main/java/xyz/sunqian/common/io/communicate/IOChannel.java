package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.IOKit;
import xyz.sunqian.common.io.IOOperator;
import xyz.sunqian.common.io.IORuntimeException;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;

/**
 * The channel used to read/write data between this point and the remote endpoint.
 * <p>
 * This interface extends the {@link ByteChannel},  if the read methods returns {@code -1}, means the channel is closed;
 * if returns {@code 0}, means the received data of the current read-event has been read completely.
 *
 * @author sunqian
 */
public interface IOChannel extends ByteChannel {

    /**
     * Creates a new {@link IOChannel} based on the given {@link ByteChannel}.
     * <p>
     * Note if the read methods returns {@code -1}, the returned {@link IOChannel} will auto close the channel.
     *
     * @param channel the given {@link ByteChannel}
     * @return a new {@link IOChannel} based on the given {@link ByteChannel}
     */
    static @Nonnull IOChannel newChannel(@Nonnull ByteChannel channel) {
        return newChannel(channel, IOKit.bufferSize());
    }

    /**
     * Creates a new {@link IOChannel} based on the given {@link ByteChannel}.
     * <p>
     * Note if the read methods returns {@code -1}, the returned {@link IOChannel} will auto close the channel.
     *
     * @param channel the given {@link ByteChannel}
     * @param bufSize the buffer size for the advanced IO operations
     * @return a new {@link IOChannel} based on the given {@link ByteChannel}
     */
    static @Nonnull IOChannel newChannel(@Nonnull ByteChannel channel, int bufSize) {
        return new IOChannel() {

            private final @Nonnull IOOperator operator = IOOperator.get(bufSize);

            @Override
            public boolean isOpen() {
                return channel.isOpen();
            }

            @Override
            public void close() throws IOException {
                channel.close();
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                if (!channel.isOpen()) {
                    throw new ClosedChannelException();
                }
                return channel.write(src);
            }

            @Override
            public int read(ByteBuffer dst) throws IOException {
                if (!channel.isOpen()) {
                    return -1;
                }
                int ret = channel.read(dst);
                if (ret < 0) {
                    channel.close();
                }
                return ret;
            }

            @Override
            public IOOperator operator() {
                return operator;
            }
        };
    }

    /**
     * Returns the operator for the advanced IO operations.
     *
     * @return the operator for the advanced IO operations
     */
    IOOperator operator();

    /**
     * Returns the next received bytes from the remote endpoint, may be {@code null} if the channel is closed.
     *
     * @return the next received bytes from the remote endpoint, may be {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    default byte @Nullable [] nextBytes() throws IORuntimeException {
        return operator().readBytes(this);
    }

    /**
     * Returns the next received bytes as buffer from the remote endpoint, may be {@code null} if the channel is closed.
     * The buffer's position is {@code 0}.
     *
     * @return the next received bytes as buffer from the remote endpoint, may be {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    default @Nullable ByteBuffer nextBuffer() throws IORuntimeException {
        return operator().read(this);
    }

    /**
     * Returns the next received bytes as string from the remote endpoint, may be {@code null} if the channel is closed.
     * The returned string is encoded by {@link CharsKit#defaultCharset()}.
     *
     * @return the next received bytes as string from the remote endpoint, may be {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    default @Nullable String nextString() throws IORuntimeException {
        return nextString(CharsKit.defaultCharset());
    }

    /**
     * Returns the next received bytes as string from the remote endpoint, may be {@code null} if the channel is closed.
     * The returned string is encoded by the specified charset.
     *
     * @param charset the specified charset
     * @return the next received bytes as string from the remote endpoint, may be {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    default @Nullable String nextString(@Nonnull Charset charset) throws IORuntimeException {
        return operator().string(this, charset);
    }

    /**
     * Writes the specified bytes to the remote endpoint.
     *
     * @param src the given bytes
     * @throws IORuntimeException if an error occurs
     */
    default void writeBytes(byte @Nonnull [] src) throws IORuntimeException {
        writeBuffer(ByteBuffer.wrap(src));
    }

    /**
     * Writes the specified byte buffer to the remote endpoint. The position of the buffer will increment to its limit.
     *
     * @param src the given byte buffer
     * @throws IORuntimeException if an error occurs
     */
    default void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException {
        BufferKit.readTo(src, this);
    }

    /**
     * Writes the specified string to the remote endpoint. The string will be decoded using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param src the given string
     * @throws IORuntimeException if an error occurs
     */
    default void writeString(@Nonnull String src) throws IORuntimeException {
        writeString(src, CharsKit.defaultCharset());
    }

    /**
     * Writes the specified string to the remote endpoint. The string will be decoded using the specified charset.
     *
     * @param src     the given string
     * @param charset the specified charset
     * @throws IORuntimeException if an error occurs
     */
    default void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException {
        writeBytes(src.getBytes(charset));
    }
}
