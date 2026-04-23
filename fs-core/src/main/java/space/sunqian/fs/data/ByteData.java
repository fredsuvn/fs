package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;

/**
 * Represents byte data, which can be written to {@link OutputStream} or {@link WritableByteChannel}.
 *
 * @author sunqian
 */
public interface ByteData {

    /**
     * Writes the current data to the given output stream.
     *
     * @param out the output stream to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void writeTo(@Nonnull OutputStream out) throws IORuntimeException;

    /**
     * Writes the current data to the given writable byte channel.
     *
     * @param channel the writable byte channel to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void writeTo(@Nonnull WritableByteChannel channel) throws IORuntimeException;
}
