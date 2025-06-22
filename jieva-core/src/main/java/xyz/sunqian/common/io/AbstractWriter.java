package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

import java.io.IOException;
import java.io.Writer;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

/**
 * This class is a skeletal implementation for the {@link Writer}. Implementing the following methods is sufficient to
 * create a fully functional {@link Writer}：
 * <ul>
 *     <li>{@link #doWrite(char)};</li>
 *     <li>{@link #doWrite(char[], int, int)};</li>
 *     <li>{@link #doWrite(String, int, int)};</li>
 *     <li>{@link #doAppend(CharSequence, int, int)};</li>
 *     <li>{@link #flush()};</li>
 *     <li>{@link #close()};</li>
 * </ul>
 * When implementing those methods, there is no need to consider null pointers or boundary issues, as relevant checks
 * are done (If necessary) before invoking them.
 *
 * @author sunqian
 */
public abstract class AbstractWriter extends Writer {

    /**
     * Does write a char to this writer.
     *
     * @param c the char
     * @throws Exception if any error occurs
     */
    protected abstract void doWrite(char c) throws Exception;

    /**
     * Does write the specified number of chars from the given array, starting at the specified offset. Its behavior is
     * equivalent to the {@link Writer#write(char[], int, int)}.
     * <p>
     * Note there is no need to consider null pointers or boundary issues.
     *
     * @param cbuf the given array
     * @param off  the specified offset
     * @param len  the specified number
     * @throws Exception if any error occurs
     */
    protected abstract void doWrite(char @Nonnull [] cbuf, int off, int len) throws Exception;

    /**
     * Does write the specified number of chars from the given string, starting at the specified offset. Its behavior is
     * equivalent to the {@link Writer#write(String, int, int)}.
     * <p>
     * Note there is no need to consider null pointers or boundary issues.
     *
     * @param str the given string
     * @param off the specified offset
     * @param len the specified number
     * @throws Exception if any error occurs
     */
    protected abstract void doWrite(@Nonnull String str, int off, int len) throws Exception;

    /**
     * Does write the chars from the given char sequence, starting and ending at the specified indexes. Its behavior is
     * equivalent to the {@link Appendable#append(CharSequence, int, int)}.
     * <p>
     * Note there is no need to consider null pointers or boundary issues.
     *
     * @param csq   the given char sequence
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @throws Exception if any error occurs
     */
    protected abstract void doAppend(@Nullable CharSequence csq, int start, int end) throws Exception;

    @Override
    public void write(int c) throws IOException {
        try {
            doWrite((char) c);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public @Nonnull Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    @Override
    public void write(char @Nonnull [] cbuf) throws IOException {
        write0(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(char @Nonnull [] cbuf, int off, int len) throws IOException {
        checkOffsetLength(cbuf.length, off, len);
        write0(cbuf, off, len);
    }

    private void write0(char @Nonnull [] cbuf, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        try {
            doWrite(cbuf, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(@Nonnull String str) throws IOException {
        write0(str, 0, str.length());
    }

    @Override
    public void write(@Nonnull String str, int off, int len) throws IOException {
        checkOffsetLength(str.length(), off, len);
        write0(str, off, len);
    }

    private void write0(@Nonnull String str, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        try {
            doWrite(str, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public @Nonnull Writer append(@Nullable CharSequence csq) throws IOException {
        CharSequence cs = Jie.nonnull(csq, Jie.NULL_STRING);
        append0(cs, 0, cs.length());
        return this;
    }

    @Override
    public @Nonnull Writer append(@Nullable CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = Jie.nonnull(csq, Jie.NULL_STRING);
        checkOffsetLength(cs.length(), start, end - start);
        append0(cs, start, end);
        return this;
    }

    private void append0(@Nonnull CharSequence csq, int start, int end) throws IOException {
        if (start == end) {
            return;
        }
        try {
            doAppend(csq, start, end);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}