package space.sunqian.common.io;

import space.sunqian.annotations.Nonnull;

import java.io.IOException;
import java.io.InputStream;

/**
 * This class provides a convenient method to implement {@link InputStream#read(byte[])} and
 * {@link InputStream#read(byte[], int, int)}: {@link #doRead(byte[], int, int)}, which is invoked by
 * {@link #read(byte[])} and {@link #read(byte[], int, int)}, and the invoking generates that the parameters passed to
 * the {@link #doRead(byte[], int, int)} will never cause the {@link IndexOutOfBoundsException}. Therefore, when
 * implementing {@link #doRead(byte[], int, int)}, there is no need to check bounds on the passed parameters.
 *
 * @author sunqian
 */
public abstract class DoReadStream extends InputStream {

    /**
     * Implementation for {@link #read(byte[])} and {@link #read(byte[], int, int)}, and no need to check bounds on the
     * passed parameters.
     *
     * @param b   the array into which the data is read
     * @param off the start offset of the array to store the read data
     * @param len the maximum read number
     * @return the actual read number, or {@code -1} if reaches the end of the stream
     * @throws IOException if an I/O error occurs
     */
    protected abstract int doRead(byte @Nonnull [] b, int off, int len) throws IOException;

    @Override
    public int read(byte @Nonnull [] b) throws IOException {
        return doRead(b, 0, b.length);
    }

    @Override
    public int read(byte @Nonnull [] b, int off, int len) throws IOException {
        IOChecker.checkOffLen(off, len, b.length);
        return doRead(b, off, len);
    }
}
