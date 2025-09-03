package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This class provides a convenient method to implement {@link OutputStream#write(byte[])} and
 * {@link OutputStream#write(byte[], int, int)}: {@link #doWrite(byte[], int, int)}, which is invoked by
 * {@link #write(byte[])} and {@link #write(byte[], int, int)}, and the invoking generates that the parameters passed to
 * the {@link #doWrite(byte[], int, int)} will never cause the {@link IndexOutOfBoundsException}. Therefore, when
 * implementing {@link #doWrite(byte[], int, int)}, there is no need to check bounds on the passed parameters.
 *
 * @author sunqian
 */
public abstract class DoWriteStream extends OutputStream {

    /**
     * Implementation for {@link #write(byte[])} and {@link #write(byte[], int, int)}, and no need to check bounds on
     * the passed parameters.
     *
     * @param b   the data to be written
     * @param off the start offset of the data
     * @param len the write number
     * @throws IOException if an I/O error occurs
     */
    protected abstract void doWrite(byte @Nonnull [] b, int off, int len) throws IOException;

    @Override
    public void write(byte @Nonnull [] b) throws IOException {
        doWrite(b, 0, b.length);
    }

    @Override
    public void write(byte @Nonnull [] b, int off, int len) throws IOException {
        IOChecker.checkOffLen(off, len, b.length);
        doWrite(b, off, len);
    }
}
