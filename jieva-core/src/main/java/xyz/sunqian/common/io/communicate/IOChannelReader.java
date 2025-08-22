package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * The reader used to read received data from the remote endpoint of the channel.
 * <p>
 * This interface directly extends the {@link ReadableByteChannel},  if the {@link ReadableByteChannel#read(ByteBuffer)}
 * returns {@code -1}, means the channel is closed; if returns {@code 0}, means the received data for the current
 * read-event has been read completely.
 *
 * @author sunqian
 */
public interface IOChannelReader extends ReadableByteChannel {

    /**
     * Returns the next received bytes from the remote endpoint of the channel, may be {@code null} if the channel is
     * closed.
     *
     * @return the next received bytes from the remote endpoint of the channel, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    byte @Nullable [] nextBytes() throws IORuntimeException;

    /**
     * Returns the next received bytes as buffer from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed. The buffer's position is {@code 0}.
     *
     * @return the next received bytes as buffer from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    ByteBuffer nextBuffer() throws IORuntimeException;

    /**
     * Returns the next received bytes as string from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed. The returned string is encoded by {@link CharsKit#defaultCharset()}.
     *
     * @return the next received bytes as string from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    default @Nullable String nextString() throws IORuntimeException {
        return nextString(CharsKit.defaultCharset());
    }

    /**
     * Returns the next received bytes as string from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed. The returned string is encoded by the specified charset.
     *
     * @param charset the specified charset
     * @return the next received bytes as string from the remote endpoint of the channel, may be {@code null} if the
     * channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    String nextString(@Nonnull Charset charset) throws IORuntimeException;
}
