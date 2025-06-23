package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;

import java.io.IOException;
import java.io.Writer;

/**
 * This class provides a convenient method to implement
 * {@link Writer#write(char[])}/{@link Writer#write(char[], int, int)} and
 * {@link Writer#write(String)}/{@link Writer#write(String, int, int)}:
 * {@link #doWrite(char[], int, int)}/{@link #doWrite(String, int, int)}, which are invoked by
 * {@link #write(char[])}/{@link #write(char[], int, int)} and {@link #write(String)}/{@link #write(String, int, int)},
 * and the invoking generates that the parameters passed to the
 * {@link #doWrite(char[], int, int)}/{@link #doWrite(String, int, int)} will never cause the
 * {@link IndexOutOfBoundsException}. Therefore, when implementing
 * {@link #doWrite(char[], int, int)}/{@link #doWrite(String, int, int)}, there is no need to check bounds on the passed
 * parameters.
 *
 * @author sunqian
 */
public abstract class DoWriteWriter extends Writer {

    /**
     * Implementation for {@link #write(char[])} and {@link #write(char[], int, int)}, and no need to check bounds on
     * the passed parameters.
     *
     * @param cbuf the data to be written
     * @param off  the start offset of the data
     * @param len  the write number
     * @throws IOException if an I/O error occurs
     */
    protected abstract void doWrite(char @Nonnull [] cbuf, int off, int len) throws IOException;

    /**
     * Implementation for {@link #write(String)} and {@link #write(String, int, int)}, and no need to check bounds on
     * the passed parameters.
     *
     * @param str the string to be written
     * @param off the start offset of the string
     * @param len the write number
     * @throws IOException if an I/O error occurs
     */
    protected abstract void doWrite(@Nonnull String str, int off, int len) throws IOException;

    @Override
    public void write(char @Nonnull [] cbuf) throws IOException {
        doWrite(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(char @Nonnull [] cbuf, int off, int len) throws IOException {
        JieCheck.checkOffsetLength(cbuf.length, off, len);
        doWrite(cbuf, off, len);
    }

    @Override
    public void write(@Nonnull String str) throws IOException {
        doWrite(str, 0, str.length());
    }

    @Override
    public void write(@Nonnull String str, int off, int len) throws IOException {
        JieCheck.checkOffsetLength(str.length(), off, len);
        doWrite(str, off, len);
    }
}
