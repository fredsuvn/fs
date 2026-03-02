package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents char data formatter that formats a given data to formatting string.
 *
 * @param <T> the type of the data to be formatted
 * @author sunqian
 */
public interface CharDataFormatter<T> {

    /**
     * Formates and writes the given data to the given writer.
     *
     * @param data   the given data to be formatted
     * @param writer the writer to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void formatTo(@Nonnull T data, @Nonnull Writer writer) throws IORuntimeException;

    /**
     * Formates the given data to a string.
     *
     * @param data the given data to be formatted
     * @return the formatting string
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nonnull String format(@Nonnull T data) throws IORuntimeException {
        StringWriter sb = new StringWriter();
        formatTo(data, sb);
        return sb.toString();
    }
}
