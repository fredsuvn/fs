package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Represents byte data formatter that formats a given data to formatting bytes.
 *
 * @param <T> the type of the data to be formatted
 * @author sunqian
 */
public interface ByteDataFormatter<T> {

    /**
     * Formates and writes the given data to the given output stream.
     *
     * @param data the given data to be formatted
     * @param out  the output stream to write to
     * @throws DataFormattingException if any error occurs during formatting
     */
    void formatTo(@Nonnull T data, @Nonnull OutputStream out) throws DataFormattingException;

    /**
     * Formates and writes the given data to the given writable byte channel.
     *
     * @param data    the given data to be formatted
     * @param channel the output channel to write to
     * @throws DataFormattingException if any error occurs during formatting
     */
    default void formatTo(@Nonnull T data, @Nonnull WritableByteChannel channel) throws DataFormattingException {
        formatTo(data, Channels.newOutputStream(channel));
    }
}
