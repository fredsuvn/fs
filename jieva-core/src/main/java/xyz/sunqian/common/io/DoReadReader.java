package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.IOException;
import java.io.Reader;

/**
 * This class provides a convenient method to implement {@link Reader#read(char[])} and
 * {@link Reader#read(char[], int, int)}: {@link #doRead(char[], int, int)}, which is invoked by {@link #read(char[])}
 * and {@link #read(char[], int, int)}, and the invoking generates that the parameters passed to the
 * {@link #doRead(char[], int, int)} will never cause the {@link IndexOutOfBoundsException}. Therefore, when
 * implementing {@link #doRead(char[], int, int)}, there is no need to check bounds on the passed parameters.
 *
 * @author sunqian
 */
public abstract class DoReadReader extends Reader {

    /**
     * Implementation for {@link #read(char[])} and {@link #read(char[], int, int)}, and no need to check bounds on the
     * passed parameters.
     *
     * @param b   the array into which the data is read
     * @param off the start offset of the array to store the read data
     * @param len the maximum read number
     * @return the actual read number, or {@code -1} if reaches the end of the stream
     * @throws IOException if an I/O error occurs
     */
    protected abstract int doRead(char @Nonnull [] b, int off, int len) throws IOException;

    @Override
    public int read(char @Nonnull [] cbuf) throws IOException {
        return doRead(cbuf, 0, cbuf.length);
    }

    @Override
    public int read(char @Nonnull [] cbuf, int off, int len) throws IOException {
        IOChecker.checkOffLen(off, len, cbuf.length);
        return doRead(cbuf, off, len);
    }
}
