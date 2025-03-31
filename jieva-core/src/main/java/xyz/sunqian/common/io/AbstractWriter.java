package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

import java.io.IOException;
import java.io.Writer;

/**
 * This class is a skeletal implementation of the {@link Writer}, implementing these methods allows to quickly build a
 * new Writer:
 * <ul>
 *     <li>{@link #doWrite(char)};</li>
 *     <li>{@link #doWrite(char[], int, int)};</li>
 *     <li>{@link #doWrite(String, int, int)};</li>
 *     <li>{@link #doAppend(CharSequence, int, int)};</li>
 *     <li>{@link #flush()};</li>
 *     <li>{@link #close()};</li>
 * </ul>
 * Don't need to worry about null pointers or boundary issues when implementing the above methods, as the underlying
 * methods being called already handle these checks.
 *
 * @author fredsuvn
 */
public abstract class AbstractWriter extends Writer {

    /**
     * Writes a char.
     *
     * @param c a char
     * @throws Exception may any exception
     */
    protected abstract void doWrite(char c) throws Exception;

    /**
     * Writes chars in given array from specified offset up to specified length.
     * <p>
     * Null pointer or boundary issues are not considered when implementing this method.
     *
     * @param cbuf given array
     * @param off  specified offset
     * @param len  specified length
     * @throws Exception may any exception
     */
    protected abstract void doWrite(char[] cbuf, int off, int len) throws Exception;

    /**
     * Writes chars in given string from specified offset up to specified length.
     * <p>
     * Null pointer or boundary issues are not considered when implementing this method.
     *
     * @param str given string
     * @param off specified offset
     * @param len specified length
     * @throws Exception may any exception
     */
    protected abstract void doWrite(String str, int off, int len) throws Exception;

    /**
     * Writes chars in given char sequences from specified start index inclusive to end index exclusive.
     * <p>
     * Null pointer or boundary issues are not considered when implementing this method.
     *
     * @param csq   given char sequences
     * @param start specified start index inclusive
     * @param end   specified end index exclusive
     * @throws Exception may any exception
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
        IOBack.checkReadBounds(cbuf, off, len);
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
        IOBack.checkReadBounds(str, off, len);
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
        IOBack.checkReadBounds(cs, start, end - start);
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