package space.sunqian.common.io;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.ThreadSafe;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * This interface provides I/O operations for char.
 *
 * @author sunqian
 */
@ThreadSafe
public interface CharIOOperator {

    /**
     * Returns the buffer size for I/O operations.
     *
     * @return the buffer size for I/O operations
     */
    int bufferSize();

    /**
     * Reads all data from the reader into a new array, continuing until reaches the end of the reader, and returns the
     * array.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a new array containing the read data, or {@code null} if reaches the end of the reader and no data is
     * read
     * @throws IORuntimeException if an I/O error occurs
     */
    default char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.endChecker());
    }

    /**
     * Reads a specified length of data from the reader into a new array, and returns the array. If the specified length
     * is {@code 0}, returns an empty array without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or reaches the end of the reader.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     * <p>
     * Note this method will allocate a new array with the specified length, and the excessive length may cause out of
     * memory.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, or {@code null} if reaches the end of the reader and no data is
     * read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.endChecker());
    }

    /**
     * Reads all data from the reader as a string, continuing until reaches the end of the reader, and returns the
     * string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a string represents the read data, or {@code null} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = read(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads a specified length of data from the reader as a string, and returns the string. If the specified length is
     * {@code 0}, returns an empty string without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or reaches the end of the reader.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a string represents the read data, or {@code null} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default @Nullable String string(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        char[] chars = read(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads data from the reader into the appender, until reaches the end of the reader, and returns the actual number
     * of chars read to.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.endChecker());
    }

    /**
     * Reads a specified length of data from the reader into the appender, until the read number reaches the specified
     * length or reaches the end of the reader, returns the actual number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.endChecker());
    }

    /**
     * Reads data from the reader into the destination array, until the read number reaches the array's length or
     * reaches the end of the reader, and returns the actual number of chars read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.endChecker());
    }

    /**
     * Reads a specified length of data from the reader into the destination array, starting at the specified offset,
     * until the read number reaches the specified length or reaches the end of the reader, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(off, len, dst.length);
        return IOKit.readTo0(src, dst, off, len, IOChecker.endChecker());
    }

    /**
     * Reads data from the reader into the destination buffer, until reaches the end of the reader or buffer, and
     * returns the actual number of chars read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the destination buffer
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        return IOKit.readTo0WithActualLen(src, dst, dst.remaining(), IOChecker.endChecker());
    }

    /**
     * Reads a specified length of data from the reader into the destination buffer, until the read number reaches the
     * specified length or reaches the end of the reader or buffer, and returns the actual number of chars read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, or {@code -1} if reaches the end of the reader and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
    }

    /**
     * Reads available data from the reader, continuing until no data is immediately available, and returns the string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default char @Nullable [] available(@Nonnull Reader src) throws IORuntimeException {
        return IOKit.read0(src, bufferSize(), IOChecker.availableChecker());
    }

    /**
     * Reads a specified length of data from the reader, and returns the string. If the specified length is {@code 0},
     * returns an empty string without reading. Otherwise, this method keeps reading until the read number reaches the
     * specified length or no data is immediately available.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a new array containing the read data, possibly empty, or {@code null} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default char @Nullable [] available(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.read0(src, len, bufferSize(), IOChecker.availableChecker());
    }

    /**
     * Reads available data from the reader as a string, continuing until no data is immediately available, and returns
     * the string.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @return a string represents the read data, possibly empty, or {@code null} if reaches the end of the reader and
     * no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default @Nullable String availableString(@Nonnull Reader src) throws IORuntimeException {
        char[] chars = available(src);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads a specified length of data from the reader as a string, and returns the string. If the specified length is
     * {@code 0}, returns an empty string without reading. Otherwise, this method keeps reading until the read number
     * reaches the specified length or no data is immediately available.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code null}.
     *
     * @param src the reader
     * @param len the specified read length, must {@code >= 0}
     * @return a string represents the read data, possibly empty, or {@code null} if reaches the end of the reader and
     * no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default @Nullable String availableString(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        char[] chars = available(src, len);
        return chars == null ? null : new String(chars);
    }

    /**
     * Reads available data from the reader into the appender, until no data is immediately available, and returns the
     * actual number of chars read to.
     * <p>
     * If reaches the end of the reader and no data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default long availableTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, bufferSize(), IOChecker.availableChecker());
    }

    /**
     * Reads a specified length of data from the reader into the appender, until the read number reaches the specified
     * length or no data is immediately available, returns the actual number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading; if reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the appender
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default long availableTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, bufferSize(), IOChecker.availableChecker());
    }

    /**
     * Reads available data from the reader into the destination array, until the read number reaches the array's length
     * or no data is immediately available, and returns the actual number of chars read to.
     * <p>
     * If the array's length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int availableTo(@Nonnull Reader src, char @Nonnull [] dst) throws IORuntimeException {
        return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.availableChecker());
    }

    /**
     * Reads a specified length of data from the reader into the destination array, starting at the specified offset,
     * until the read number reaches the specified length or no data is immediately available, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length is {@code 0}, returns {@code 0} without reading. If reaches the end of the reader and no
     * data is read, returns {@code -1}.
     *
     * @param src the reader
     * @param dst the destination array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IndexOutOfBoundsException if the arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    default int availableTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        IOChecker.checkOffLen(off, len, dst.length);
        return IOKit.readTo0(src, dst, off, len, IOChecker.availableChecker());
    }

    /**
     * Reads available data from the reader into the destination buffer, until reaches the end of the buffer or no data
     * is immediately available, and returns the actual number of chars read to.
     * <p>
     * If the destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if reaches the end of the
     * reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the destination buffer
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default int availableTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        if (dst.remaining() == 0) {
            return 0;
        }
        return IOKit.readTo0WithActualLen(src, dst, dst.remaining(), IOChecker.availableChecker());
    }

    /**
     * Reads a specified length of data from the reader into the destination buffer, until the read number reaches the
     * specified length or reaches the end of the buffer or no data is immediately available, and returns the actual
     * number of chars read to.
     * <p>
     * If the specified length or destination buffer's remaining is {@code 0}, returns {@code 0} without reading; if
     * reaches the end of the reader and no data is read, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the reader
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of chars read to, possibly {@code 0}, or {@code -1} if reaches the end of the reader
     * and no data is read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    default int availableTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkLen(len);
        return IOKit.readTo0(src, dst, len, IOChecker.availableChecker());
    }
}
