package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * Output channel for IO Communication, typically used for network or IPC (Inter-Process Communication).
 * <p>
 * OutChannel extends the {@link WritableByteChannel} and provides more advanced methods for writing.
 * <p>
 * This interface and {@link InChannel} form the {@link IOChannel}.
 *
 * @author sunqian
 */
public interface OutChannel extends WritableByteChannel {

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
}
