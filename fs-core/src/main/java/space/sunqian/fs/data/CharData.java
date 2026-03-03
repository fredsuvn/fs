package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

/**
 * Represents char data, which can be written to {@link Appendable}.
 *
 * @author sunqian
 */
public interface CharData {

    /**
     * Writes the current data to the given writer.
     *
     * @param appender the appender to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void writeTo(@Nonnull Appendable appender) throws IORuntimeException;
}
