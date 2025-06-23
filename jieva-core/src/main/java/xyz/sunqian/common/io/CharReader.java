package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.chars.CharsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

/**
 * This interface is used to read chars for {@link Reader}.
 *
 * @author sunqian
 */
public interface CharReader {

    /**
     * Returns the buffer size of current instance.
     *
     * @return the buffer size of current instance
     */
    int bufferSize();

    /**
     * Reads all data from the source stream into a new array, continuing until reaches the end of the stream, and
     * returns the array. If the end of the source reader has already been reached, returns {@code null}.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
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
     * Reads the data of the specified length from the source stream into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the stream. If the end of the source reader
     * has already been reached, returns {@code null}.
     * <p>
     * Note the length cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @param len the specified read length, must {@code >= 0}
     * @return the array containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
     * Reads all data from the source stream into a new string, continuing until reaches the end of the stream, and
     * returns the string. If the end of the source reader has already been reached, returns {@code null}.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the string.
     *
     * @param src the source stream
     * @return the string containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads the data of the specified length from the source stream into a new string, and returns the string. If the
     * specified length {@code = 0}, returns an empty string without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the stream. If the end of the source reader
     * has already been reached, returns {@code null}.
     * <p>
     * Note the length cannot exceed the maximum limit of the string.
     *
     * @param src the source stream
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
     * Reads all from the source stream into the specified output stream, until the read number reaches the specified
     * length or reaches the end of the source stream, returns the actual number of chars read to. If the end of the
     * source stream has already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return CharReaderBack.readTo(src, dst, -1, bufferSize());
    }

    /**
     * Reads the data of the specified length from the source stream into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of chars
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return CharReaderBack.readTo(src, dst, len, bufferSize());
    }

    /**
     * Reads the data from the source stream into the specified array, until the read number reaches the array's length
     * or reaches the end of the source stream, returns the actual number of chars read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has already
     * been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, char @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return CharReaderBack.readTo(src, dst, 0, dst.length);
    }

    /**
     * Reads the data from the source stream into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source stream,
     * returns the actual number of chars read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has
     * already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        JieCheck.checkOffsetLength(dst.length, off, len);
        return CharReaderBack.readTo(src, dst, off, len);
    }

    /**
     * Reads the data from the source stream into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source stream, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return CharReaderBack.readTo(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source stream has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return CharReaderBack.readTo(src, dst, len);
    }

    /**
     * Reads the data from the source buffer into the specified appender, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of chars read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified appender
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull CharBuffer src, @Nonnull Appendable dst) throws IORuntimeException {
        return CharReaderBack.readTo(src, dst, -1);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified appender, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, long len
    ) throws IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        return CharReaderBack.readTo(src, dst, len);
    }
}
