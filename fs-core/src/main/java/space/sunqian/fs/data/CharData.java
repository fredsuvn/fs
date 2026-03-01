package space.sunqian.fs.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.Writer;

/**
 * Represents char data, which can be written to {@link Writer}.
 *
 * @author sunqian
 */
public interface CharData {

    /**
     * Writes the current data to the given writer.
     *
     * @param writer the writer to write to
     * @throws IORuntimeException if an I/O error occurs
     */
    void writeTo(@Nonnull Writer writer) throws IORuntimeException;
}
