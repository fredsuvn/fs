package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;

import java.io.IOException;
import java.io.Writer;

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
     * Does write the specified number of chars from the given array, starting at the specified offset.
     * <p>
     * Null there is no need to consider null pointers or boundary issues.
     *
     * @param cbuf the given array
     * @param off  the specified offset
     * @param len  the specified number
     * @throws Exception if any error occurs
     */
    protected abstract void doWrite(char[] cbuf, int off, int len) throws Exception;

    /**
     * Does write the specified number of chars from the given string, starting at the specified offset.
     * <p>
     * Null there is no need to consider null pointers or boundary issues.
     *
     * @param str the given string
     * @param off the specified offset
     * @param len the specified number
     * @throws Exception if any error occurs
     */
    protected abstract void doWrite(String str, int off, int len) throws Exception;

    /**
     * Does write the chars from the given char sequence, starting and ending at the specified indexes.
     * <p>
     * Null there is no need to consider null pointers or boundary issues.
     *
     * @param csq   the given char sequence
     * @param start specified start index inclusive
     * @param end   specified end index exclusive
     * @throws Exception if any error occurs
     */
    protected abstract void doAppend(CharSequence csq, int start, int end) throws Exception;

    @Override
    public void write(int c) throws IOException {
        try {
            doWrite((char) c);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        JieCheck.checkOffsetLength(cbuf, off, len);
        if (len <= 0) {
            return;
        }
        try {
            doWrite(cbuf, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        write(cbuf, 0, cbuf.length);
    }

    @Override
    public void write(String str) throws IOException {
        write(str, 0, str.length());
    }

    @Override
    public Writer append(char c) throws IOException {
        write(c);
        return this;
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        JieCheck.checkOffsetLength(str, off, len);
        if (len <= 0) {
            return;
        }
        try {
            doWrite(str, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Writer append(@Nullable CharSequence csq) throws IOException {
        CharSequence cs = nonNull(csq);
        return append(cs, 0, cs.length());
    }

    @Override
    public Writer append(@Nullable CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = nonNull(csq);
        JieCheck.checkOffsetLength(cs, start, end - start);
        if (start == end) {
            return this;
        }
        try {
            doAppend(cs, start, end);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }

    private CharSequence nonNull(@Nullable CharSequence csq) {
        return csq == null ? "null" : csq;
    }
}