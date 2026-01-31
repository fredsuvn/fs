package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsKit;
import space.sunqian.fs.io.IORuntimeException;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

/**
 * Represents the parser that parses data from a stream or reader to a data object.
 *
 * @param <T> the type of the parsed data object
 * @author sunqian
 */
public interface DataParser<T> {

    /**
     * Parses the data from the input stream then returns the parsed data object, using
     * {@link CharsKit#defaultCharset()} if the data is charset-sensitive.
     *
     * @param input the input stream
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     */
    default @Nonnull T parse(@Nonnull InputStream input) throws IORuntimeException {
        return parse(input, CharsKit.defaultCharset());
    }

    /**
     * Parses the data from the input stream then returns the parsed data object, using the specified charset if the
     * data is charset-sensitive.
     *
     * @param input   the input stream
     * @param charset the charset to use for parsing
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     **/
    @Nonnull
    T parse(@Nonnull InputStream input, @Nonnull Charset charset) throws IORuntimeException;

    /**
     * Parses the data from the reader then returns the parsed data object.
     *
     * @param reader the reader
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     **/
    @Nonnull
    T parse(@Nonnull Reader reader) throws IORuntimeException;
}
