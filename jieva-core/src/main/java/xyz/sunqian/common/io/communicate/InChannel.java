package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

/**
 * Input channel for IO Communication, typically used for network or IPC (Inter-Process Communication).
 * <p>
 * InChannel extends the {@link ReadableByteChannel} and provides more advanced methods for reading. For its read
 * methods, if the number of bytes read is {@code -1}, means the channel is closed; if is {@code 0}, means all available
 * data has been read but the channel is still alive.
 * <p>
 * This interface and {@link OutChannel} form the {@link IOChannel}.
 *
 * @author sunqian
 */
public interface InChannel extends ReadableByteChannel {

    /**
     * Returns all available bytes in the channel, possibly empty if no data is available but the channel is still
     * alive, or {@code null} if the channel is closed.
     *
     * @return the available bytes in the channel, possibly empty, or {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    byte @Nullable [] availableBytes() throws IORuntimeException;

    /**
     * Returns all available bytes as a buffer in the channel, possibly empty if no data is available but the channel is
     * still alive, or {@code null} if the channel is closed. The position of the buffer is {@code 0}.
     *
     * @return the available bytes as a buffer in the channel, possibly empty, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    ByteBuffer availableBuffer() throws IORuntimeException;

    /**
     * Returns all available bytes as a string in the channel, possibly empty if no data is available but the channel is
     * still alive, or {@code null} if the channel is closed. The string is decoded by the
     * {@link CharsKit#defaultCharset()}.
     *
     * @return the available bytes as a string in the channel, possibly empty, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    String availableString() throws IORuntimeException;

    /**
     * Returns all available bytes as a string in the channel, possibly empty if no data is available but the channel is
     * still alive, or {@code null} if the channel is closed. The string is decoded by the specified charset.
     *
     * @param charset the specified charset
     * @return the available bytes as a string in the channel, possibly empty, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    String availableString(@Nonnull Charset charset) throws IORuntimeException;
}
