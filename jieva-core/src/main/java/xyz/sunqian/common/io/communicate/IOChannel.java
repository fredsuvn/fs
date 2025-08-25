package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;

/**
 * Channel for IO Communication, typically used for network or IPC (Inter-Process Communication).
 * <p>
 * IOChannel extends the {@link ByteChannel} and provides more advanced methods for reading and writing. For its read
 * methods, if the number of bytes read is {@code -1}, means the channel is closed; if is {@code 0}, means all available
 * data has been read but the channel is still alive.
 * <p>
 * There is a skeletal implementation: {@link AbstractIOChannel}, which can help to implement this interface with
 * minimal effort.
 *
 * @author sunqian
 */
public interface IOChannel extends ByteChannel {

    /**
     * Returns the available bytes in the channel, possibly empty if no data is available but the channel is still
     * alive, or {@code null} if the channel is closed.
     *
     * @return the available bytes in the channel, possibly empty, or {@code null} if the channel is closed.
     * @throws IORuntimeException if an error occurs
     */
    byte @Nullable [] availableBytes() throws IORuntimeException;

    /**
     * Returns the available bytes as a buffer in the channel, possibly empty if no data is available but the channel is
     * still alive, or {@code null} if the channel is closed. The position of the buffer is {@code 0}.
     *
     * @return the available bytes as a buffer in the channel, possibly empty, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    ByteBuffer availableBuffer() throws IORuntimeException;

    /**
     * Returns the available bytes as a string in the channel, possibly empty if no data is available but the channel is
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
     * Returns the available bytes as a string in the channel, possibly empty if no data is available but the channel is
     * still alive, or {@code null} if the channel is closed. The string is decoded by the specified charset.
     *
     * @param charset the specified charset
     * @return the available bytes as a string in the channel, possibly empty, may be {@code null} if the channel is
     * closed.
     * @throws IORuntimeException if an error occurs
     */
    @Nullable
    String availableString(@Nonnull Charset charset) throws IORuntimeException;

    /**
     * Writes the given bytes to the channel.
     *
     * @param src the given bytes
     * @throws IORuntimeException if an error occurs
     */
    void writeBytes(byte @Nonnull [] src) throws IORuntimeException;

    /**
     * Writes the given buffer to the channel. The position of the buffer will increment by the actual write number.
     *
     * @param src the given buffer
     * @throws IORuntimeException if an error occurs
     */
    void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException;

    /**
     * Writes bytes encoded from the given string to the channel, with {@link CharsKit#defaultCharset()}.
     *
     * @param src the given string
     * @throws IORuntimeException if an error occurs
     */
    void writeString(@Nonnull String src) throws IORuntimeException;

    /**
     * Writes bytes encoded from the given string to the channel, with the specified charset.
     *
     * @param src     the given string
     * @param charset the specified charset
     * @throws IORuntimeException if an error occurs
     */
    void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException;

    /**
     * Blocks current thread and waits for the channel to be readable.
     */
    void awaitReadable();

    /**
     * Wakes up the thread blocked in {@link #awaitReadable()}.
     */
    void wakeUpReadable();
}
