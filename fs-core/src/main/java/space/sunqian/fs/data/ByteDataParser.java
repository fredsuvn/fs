package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * Represents a byte data parser that parses data from a data source (e.g. a {@link InputStream}) to a data object.
 *
 * @param <T> the type of the parsed data object
 * @author sunqian
 */
public interface ByteDataParser<T> {

    /**
     * Parses and returns the data from the given input stream to a data object.
     *
     * @param input the given input stream
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    @Nonnull
    T parse(@Nonnull InputStream input) throws IORuntimeException;

    /**
     * Parses and returns the data from the given readable byte channel to a data object.
     *
     * @param channel the given readable byte channel
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    @Nonnull
    T parse(@Nonnull ReadableByteChannel channel) throws IORuntimeException;
}
