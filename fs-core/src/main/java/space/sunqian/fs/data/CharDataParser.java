package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.Reader;

/**
 * Represents a char data parser that parses data from a data source (e.g. a {@link Reader}) to a data object.
 *
 * @param <T> the type of the parsed data object
 * @author sunqian
 */
public interface CharDataParser<T> {

    /**
     * Parses and returns the data from the given reader to a data object.
     *
     * @param reader the given reader
     * @return the parsed data object
     * @throws IORuntimeException if an I/O error occurs during parsing
     **/
    @Nonnull
    T parse(@Nonnull Reader reader) throws IORuntimeException;
}
