package xyz.sunqian.common.io.communicate;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * This interface is a writer used to write data to the remote endpoint of the channel, and it directly extends the
 * {@link WritableByteChannel}.
 *
 * @author sunqian
 */
public interface IOChannelWriter extends WritableByteChannel {

    /**
     * Writes the specified bytes to the remote endpoint of the channel.
     *
     * @param src the given bytes
     * @throws IORuntimeException if an error occurs
     */
    void writeBytes(byte @Nonnull [] src) throws IORuntimeException;

    /**
     * Writes the specified byte buffer to the remote endpoint of the channel. The position of the buffer will increment
     * to its limit.
     *
     * @param src the given byte buffer
     * @throws IORuntimeException if an error occurs
     */
    void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException;

    /**
     * Writes the specified string to the remote endpoint of the channel. The string will be decoded using
     * {@link CharsKit#defaultCharset()}.
     *
     * @param src the given string
     * @throws IORuntimeException if an error occurs
     */
    default void writeString(@Nonnull String src) throws IORuntimeException {
        writeString(src, CharsKit.defaultCharset());
    }

    /**
     * Writes the specified string to the remote endpoint of the channel. The string will be decoded using the specified
     * charset.
     *
     * @param src     the given string
     * @param charset the specified charset
     * @throws IORuntimeException if an error occurs
     */
    void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException;
}
