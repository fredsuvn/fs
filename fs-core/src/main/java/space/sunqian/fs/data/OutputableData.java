package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * Represents data that is output-able, i.e., can be written to an output stream or writable byte channel.
 *
 * @author sunqian
 */
public interface OutputableData {

    /**
     * Writes the current data to the given output stream, using {@link CharsKit#defaultCharset()} if this type of data
     * needs charset encoding.
     *
     * @param out the output stream to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    default void writeTo(@Nonnull OutputStream out) throws IORuntimeException {
        writeTo(out, CharsKit.defaultCharset());
    }

    /**
     * Writes the current data to the given output stream, using the specified charset if this type of data needs
     * charset encoding.
     *
     * @param out     the output stream to write to
     * @param charset the charset to use
     * @throws IORuntimeException if an I/O error occurs
     */
    void writeTo(@Nonnull OutputStream out, @Nonnull Charset charset) throws IORuntimeException;

    /**
     * Writes the current data to the given writable byte channel, using {@link CharsKit#defaultCharset()} if this type
     * of data needs charset encoding.
     *
     * @param channel the writable byte channel to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    default void writeTo(@Nonnull WritableByteChannel channel) throws IORuntimeException {
        writeTo(channel, CharsKit.defaultCharset());
    }

    /**
     * Writes the current data to the given writable byte channel, using the specified charset if this type of data
     * needs charset encoding.
     *
     * @param channel the writable byte channel to write to
     * @param charset the charset to use
     * @throws IORuntimeException if an I/O error occurs
     */
    default void writeTo(@Nonnull WritableByteChannel channel, @Nonnull Charset charset) throws IORuntimeException {
        writeTo(Channels.newOutputStream(channel), charset);
    }
}
