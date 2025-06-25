package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * This interface is used to operate chars for {@link Reader}.
 *
 * @author sunqian
 */
public interface CharOperator {

    /**
     * Returns the default {@link CharOperator} instance of which buffer size is {@link JieIO#bufferSize()}.
     *
     * @return the default {@link CharOperator} instance of which buffer size is {@link JieIO#bufferSize()}
     */
    static CharOperator defaultOperator() {
        return JieIO.defaultCharOperator();
    }

    /**
     * Returns a {@link CharOperator} instance with the given buffer size. If the buffer size equals to the default
     * buffer size, returns the default {@link CharOperator} instance, otherwise returns a new one.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a {@link CharOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static CharOperator get(int bufSize) throws IllegalArgumentException {
        return bufSize == JieIO.bufferSize() ? defaultOperator() : newOperator(bufSize);
    }

    /**
     * Returns a new {@link CharOperator} instance with the given buffer size.
     *
     * @param bufSize the given buffer size, must {@code > 0}
     * @return a new {@link CharOperator} instance with the given buffer size
     * @throws IllegalArgumentException if the given buffer size {@code <= 0}
     */
    static CharOperator newOperator(int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return new CharOperatorImpl(bufSize);
    }

    /**
     * Returns the buffer size of current instance.
     *
     * @return the buffer size of current instance
     */
    int bufferSize();

    /**
     * Reads all data from the source reader into a new array, continuing until reaches the end of the reader, and
     * returns the array. If the end of the source reader has already been reached, returns {@code null}.
     * <p>
     * Note the data in the reader cannot exceed the maximum limit of the array.
     *
     * @param src the source reader
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    @SuppressWarnings("resource")
    default char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        try {
            char[] buf = new char[bufferSize()];
            CharsBuilder builder = null;
            int off = 0;
            while (true) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toCharArray();
                    }
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = src.read();
                        if (r == -1) {
                            return buf;
                        }
                        builder = new CharsBuilder(buf.length + 1);
                        builder.append(buf);
                        builder.append(r);
                    } else {
                        builder.append(buf);
                    }
                    off = 0;
                }
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the data of the specified length from the source reader into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the reader. If the end of the source reader
     * has already been reached, returns {@code null}.
     * <p>
     * Note the length cannot exceed the maximum limit of the array.
     *
     * @param src the source reader
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        if (len == 0) {
            return new char[0];
        }
        try {
            char[] buf = new char[len];
            int off = 0;
            while (off < len) {
                int readSize = src.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    return off == 0 ? null : Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source reader into a new string, continuing until reaches the end of the reader, and
     * returns the string. If the end of the source reader has already been reached, returns {@code null}.
     * <p>
     * Note the data in the reader cannot exceed the maximum limit of the string.
     *
     * @param src the source reader
     * @return the string containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads the data of the specified length from the source reader into a new string, and returns the string. If the
     * specified length {@code = 0}, returns an empty string without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the reader. If the end of the source reader
     * has already been reached, returns {@code null}.
     * <p>
     * Note the length cannot exceed the maximum limit of the string.
     *
     * @param src the source reader
     * @param len the specified read length, must {@code >= 0}
     * @return the string containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default @Nullable String string(@Nonnull Reader src, int len) throws IllegalArgumentException, IORuntimeException {
        char[] chars = read(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads all data from the source reader into the specified output appender, until the read number reaches the
     * specified length or reaches the end of the source reader, returns the actual number of chars read to. If the end
     * of the source reader has already been reached, returns {@code -1}.
     *
     * @param src the source reader
     * @param dst the specified output appender
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return CharOperatorImpl.readTo0(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source reader into the specified output appender, until the read
     * number reaches the specified length or reaches the end of the source reader, returns the actual number of chars
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source reader has
     * already been reached, returns {@code -1}.
     *
     * @param src the source reader
     * @param dst the specified output appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return CharOperatorImpl.readTo0(src, dst, len, bufferSize());
    }

    /**
     * Reads the data from the source reader into the specified array, until the read number reaches the array's length
     * or reaches the end of the source reader, returns the actual number of chars read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source reader has already
     * been reached, returns {@code -1}.
     *
     * @param src the source reader
     * @param dst the specified array
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return CharOperatorImpl.readTo0(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source reader into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source reader,
     * returns the actual number of chars read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source reader has
     * already been reached, returns {@code -1}.
     *
     * @param src the source reader
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(dst.length, off, len);
        return CharOperatorImpl.readTo0(src, dst, off, len);
    }

    /**
     * Reads the data from the source reader into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source reader, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source reader has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source reader
     * @param dst the specified buffer
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return CharOperatorImpl.readTo0(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source reader into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source reader, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source reader has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source reader
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read, or {@code -1} if the end has already been reached
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return CharOperatorImpl.readTo0(src, dst, len);
    }
}
