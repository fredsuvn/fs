package space.sunqian.common.io.communicate;

import space.sunqian.annotations.Nonnull;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.io.BufferKit;
import space.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * Channel writer for IO Communication, typically used for network or IPC (Inter-Process Communication).
 * <p>
 * The reader holds an underlying channel to write and provides advanced write operations.
 *
 * @param <C> the type of underlying channel
 * @author sunqian
 */
public interface ChannelWriter<C extends WritableByteChannel> {

    /**
     * Writes the given bytes to the connected remote endpoint.
     *
     * @param src the given bytes
     * @throws IORuntimeException if an error occurs
     */
    default void writeBytes(byte @Nonnull [] src) throws IORuntimeException {
        writeBuffer(ByteBuffer.wrap(src));
    }

    /**
     * Writes the given buffer to the connected remote endpoint. The position of the buffer will increment by the actual
     * write number.
     *
     * @param src the given buffer
     * @throws IORuntimeException if an error occurs
     */
    default void writeBuffer(@Nonnull ByteBuffer src) throws IORuntimeException {
        BufferKit.readTo(src, channel());
    }

    /**
     * Writes bytes encoded from the given string to the connected remote endpoint, with
     * {@link CharsKit#defaultCharset()}.
     *
     * @param src the given string
     * @throws IORuntimeException if an error occurs
     */
    default void writeString(@Nonnull String src) throws IORuntimeException {
        writeString(src, CharsKit.defaultCharset());
    }

    /**
     * Writes bytes encoded from the given string to the connected remote endpoint, with the specified charset.
     *
     * @param src     the given string
     * @param charset the specified charset
     * @throws IORuntimeException if an error occurs
     */
    default void writeString(@Nonnull String src, @Nonnull Charset charset) throws IORuntimeException {
        byte[] bytes = src.getBytes(charset);
        writeBytes(bytes);
    }

    /**
     * Returns the underlying channel of this writer.
     *
     * @return the underlying channel of this writer
     */
    @Nonnull
    C channel();
}
