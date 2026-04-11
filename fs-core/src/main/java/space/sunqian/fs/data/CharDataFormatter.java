package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

/**
 * Represents char data formatter that formats a given data to formatting string.
 *
 * @param <T> the type of the data to be formatted
 * @author sunqian
 */
public interface CharDataFormatter<T> {

    /**
     * Formates and writes the given data to the given appender.
     *
     * @param data     the given data to be formatted
     * @param appender the appender to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void formatTo(@Nonnull T data, @Nonnull Appendable appender) throws IORuntimeException;
}
