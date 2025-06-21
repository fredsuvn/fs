package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Static utility class for I/O operations.
 *
 * @author sunqian
 */
public class JieIO {

    private static final int BUFFER_SIZE = 1024 * 8;
    private static final @Nonnull ByteReader br = newByteReader(bufferSize());
    private static final @Nonnull CharReader cr = newCharReader(bufferSize());

    //---------------- Common Start ----------------//

    /**
     * Returns the recommended IO buffer size, typically is 1024 * 8 = 8192.
     *
     * @return the recommended IO buffer size
     */
    public static int bufferSize() {
        return BUFFER_SIZE;
    }

    /**
     * Determines the recommended buffer size based on the given length and the given buffer size, and its logic is as
     * follows:
     * <pre>{@code
     * return len < 0 ? bufSize : (int) Math.min(len, bufSize);
     * }</pre>
     *
     * @param len     the given length
     * @param bufSize the given buffer size
     * @return the recommended buffer size based on the given length and the given buffer size
     */
    public static int bufferSize(long len, int bufSize) {
        return len < 0 ? bufSize : (int) Math.min(len, bufSize);
    }

    /**
     * Returns a new {@link ByteReader} instance with the given buffer size.
     *
     * @param bufSize the given buffer size
     * @return a new {@link ByteReader} instance with the given buffer size
     */
    public static ByteReader newByteReader(int bufSize) {
        return () -> bufSize;
    }

    /**
     * Returns a new {@link CharReader} instance with the given buffer size.
     *
     * @param bufSize the given buffer size
     * @return a new {@link CharReader} instance with the given buffer size
     */
    public static CharReader newCharReader(int bufSize) {
        return () -> bufSize;
    }

    /**
     * Reads all data from the source stream into a new array, continuing until reaches the end of the stream, and
     * returns the array. If the end of the source stream has already been reached, returns {@code null}.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the array.
     *
     * @param src the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nullable [] read(@Nonnull InputStream src) throws IORuntimeException {
        return br.read(src);
    }

    /**
     * Reads the data of the specified length from the source stream into a new array, and returns the array. If the
     * specified length {@code = 0}, returns an empty array without reading. Otherwise, this method keeps reading until
     * the read number reaches the specified length or reaches the end of the stream. If the end of the source stream
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
    public static byte @Nullable [] read(
        @Nonnull InputStream src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return br.read(src, len);
    }

    /**
     * Reads all data from the source channel into a new buffer, continuing until reaches the end of the channel, and
     * returns the buffer. The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of
     * which offset is {@code 0}. If the end of the source channel has already been reached, returns {@code null}.
     * <p>
     * Note the data in the channel cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @return the buffer containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable ByteBuffer read(@Nonnull ReadableByteChannel src) throws IORuntimeException {
        return br.read(src);
    }

    /**
     * Reads the data of the specified length from the source channel into a new buffer, and returns the buffer. If the
     * specified length {@code length = 0}, returns an empty buffer without reading. Otherwise, this method keeps
     * reading until the read number reaches the specified length or reaches the end of the channel. If the end of the
     * source channel has already been reached, returns {@code null}.
     * <p>
     * The buffer's position is {@code 0}, limit equals to capacity, and it has a backing array of which offset is
     * {@code 0}. And note the length cannot exceed the maximum limit of the buffer.
     *
     * @param src the source channel
     * @param len the specified read length, must {@code >= 0}
     * @return the buffer containing the data
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static @Nullable ByteBuffer read(
        @Nonnull ReadableByteChannel src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return br.read(src, len);
    }

    /**
     * Reads all from the source stream into the specified output stream, until the read number reaches the specified
     * length or reaches the end of the source stream, returns the actual number of bytes read to. If the end of the
     * source stream has already been reached, returns {@code -1}.
     * <p>
     * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull InputStream src, @Nonnull OutputStream dst) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified output stream, until the read
     * number reaches the specified length or reaches the end of the source stream, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * This method never invoke the {@link OutputStream#flush()} to force the backing buffer.
     *
     * @param src the source stream
     * @param dst the specified output stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull InputStream src, @Nonnull OutputStream dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return br.readTo(src, dst, len);
    }

    /**
     * Reads the data from the source stream into the specified array, until the read number reaches the array's length
     * or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has already
     * been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data from the source stream into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source stream,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source stream has
     * already been reached, returns {@code -1}.
     *
     * @param src the source stream
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return br.readTo(src, dst, off, len);
    }

    /**
     * Reads the data from the source stream into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source stream, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source stream has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length {@code < 0}, this method performs as {@link #readTo(InputStream, ByteBuffer)}; if the
     * specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source
     * stream has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source stream
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull InputStream src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        return br.readTo(src, dst, len);
    }

    /**
     * Reads all from the source channel into the specified output channel, until the read number reaches the specified
     * length or reaches the end of the source channel, returns the actual number of bytes read to. If the end of the
     * source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst
    ) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source channel into the specified output channel, until the read
     * number reaches the specified length or reaches the end of the source channel, returns the actual number of bytes
     * read to.
     * <p>
     * If the specified length {@code < 0}, this method reads all data; if the specified length {@code = 0}, returns
     * {@code 0} without reading; if the end of the source channel has already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified output channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static long readTo(
        @Nonnull ReadableByteChannel src, @Nonnull WritableByteChannel dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return br.readTo(src, dst, len);
    }

    /**
     * Reads the data from the source channel into the specified array, until the read number reaches the array's length
     * or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the array's length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data from the source channel into the specified array (starting at the specified offset and up to the
     * specified length), until the read number reaches the specified length or reaches the end of the source channel,
     * returns the actual number of bytes read to.
     * <p>
     * If the specified length {@code = 0}, returns {@code 0} without reading. If the end of the source channel has
     * already been reached, returns {@code -1}.
     *
     * @param src the source channel
     * @param dst the specified array
     * @param off the specified offset of the array
     * @param len the specified length to read
     * @return the actual number of bytes read
     * @throws IndexOutOfBoundsException if the array arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, byte @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return br.readTo(src, dst, off, len);
    }

    /**
     * Reads the data from the source channel into the specified buffer, until the read number reaches the buffer's
     * remaining or reaches the end of the source channel, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source channel has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source channel into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source channel, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source channel has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source channel
     * @param dst the specified buffer
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ReadableByteChannel src, @Nonnull ByteBuffer dst, int len
    ) throws IORuntimeException {
        return br.readTo(src, dst, len);
    }

    /**
     * Reads the data from the source buffer into the specified channel, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified channel
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified channel, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified channel
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull WritableByteChannel dst, int len
    ) throws IORuntimeException {
        return br.readTo(src, dst, len);
    }

    /**
     * Reads the data from the source buffer into the specified stream, until the read number reaches the buffer's
     * remaining or reaches the end of the source buffer, returns the actual number of bytes read to.
     * <p>
     * If the buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source buffer has
     * already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified stream
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull ByteBuffer src, @Nonnull OutputStream dst) throws IORuntimeException {
        return br.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source buffer into the specified stream, until the read number
     * reaches the buffer's remaining or reaches the end of the source buffer, returns the actual number of bytes read
     * to.
     * <p>
     * If the specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the
     * source buffer has already been reached, returns {@code -1}.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param src the source buffer
     * @param dst the specified stream
     * @param len the specified length, must {@code >= 0}
     * @return the actual number of bytes read
     * @throws IllegalArgumentException if the specified read length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static int readTo(
        @Nonnull ByteBuffer src, @Nonnull OutputStream dst, long len
    ) throws IORuntimeException {
        return br.readTo(src, dst, len);
    }

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
    public static char @Nullable [] read(@Nonnull Reader src) throws IORuntimeException {
        return cr.read(src);
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
    public static char @Nullable [] read(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return cr.read(src, len);
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
    public static @Nullable String string(@Nonnull Reader src) throws IORuntimeException {
        return cr.string(src);
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
    public static @Nullable String string(
        @Nonnull Reader src, int len
    ) throws IllegalArgumentException, IORuntimeException {
        return cr.string(src, len);
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
    public static long readTo(@Nonnull Reader src, @Nonnull Appendable dst) throws IORuntimeException {
        return cr.readTo(src, dst);
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
    public static long readTo(
        @Nonnull Reader src, @Nonnull Appendable dst, long len
    ) throws IllegalArgumentException, IORuntimeException {
        return cr.readTo(src, dst, len);
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
    public static int readTo(
        @Nonnull Reader src, char @Nonnull [] dst
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return cr.readTo(src, dst);
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
    public static int readTo(
        @Nonnull Reader src, char @Nonnull [] dst, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        return cr.readTo(src, dst, off, len);
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
    public static int readTo(@Nonnull Reader src, @Nonnull CharBuffer dst) throws IORuntimeException {
        return cr.readTo(src, dst);
    }

    /**
     * Reads the data of the specified length from the source stream into the specified buffer, until the read number
     * reaches the buffer's remaining or reaches the end of the source stream, returns the actual number of chars read
     * to.
     * <p>
     * If the specified length {@code < 0}, this method performs as {@link #readTo(Reader, CharBuffer)}; if the
     * specified length or buffer's remaining {@code = 0}, returns {@code 0} without reading; if the end of the source
     * stream has already been reached, returns {@code -1}.
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
    public static int readTo(
        @Nonnull Reader src, @Nonnull CharBuffer dst, int len
    ) throws IORuntimeException {
        return cr.readTo(src, dst, len);
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
    public static int readTo(@Nonnull CharBuffer src, @Nonnull Appendable dst) throws IORuntimeException {
        return cr.readTo(src, dst);
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
    public static int readTo(
        @Nonnull CharBuffer src, @Nonnull Appendable dst, long len
    ) throws IORuntimeException {
        return cr.readTo(src, dst, len);
    }

    /**
     * Writes the data to the specified appender from the given array.
     *
     * @param dst the specified appender
     * @param src the given array
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(@Nonnull Appendable dst, char @Nonnull [] src) throws IORuntimeException {
        write(dst, src, 0, src.length);
    }

    /**
     * Writes the data to the specified appender from the given array, starting at the specified offset and up to the
     * specified length.
     *
     * @param dst the specified appender
     * @param src the given array
     * @param off the specified offset
     * @param len the specified length
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    public static void write(
        @Nonnull Appendable dst, char @Nonnull [] src, int off, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        try {
            if (dst instanceof Writer) {
                ((Writer) dst).write(src, off, len);
                return;
            }
            dst.append(new String(src, off, len));
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the data from the source stream without blocking into a new array, and returns the array. The array's
     * length is based on the {@link InputStream#available()}.
     *
     * @param source the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nonnull [] available(@Nonnull InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
            if (available <= 0) {
                return new byte[0];
            }
            byte[] bytes = new byte[available];
            int c = source.read(bytes);
            if (c < 0) {
                return new byte[0];
            }
            if (c == available) {
                return bytes;
            }
            return Arrays.copyOf(bytes, c);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all bytes from the source stream and returns as a string with {@link JieChars#defaultCharset()}. If the end
     * of the source stream has already been reached, returns {@code null}.
     *
     * @param source the source stream
     * @return the string with {@link JieChars#defaultCharset()}
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(@Nonnull InputStream source) throws IORuntimeException {
        return string(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from the source stream and returns them as a string with the specified charset. If the end of the
     * source stream has already been reached, returns {@code null}.
     *
     * @param source  the source stream
     * @param charset the specified charset
     * @return the string with the specified charset
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nullable String string(
        @Nonnull InputStream source, @Nonnull Charset charset
    ) throws IORuntimeException {
        byte[] bytes = read(source);
        return bytes == null ? null : new String(bytes, charset);
    }

    //---------------- Common End ----------------//

    //---------------- Wrappers Begin ----------------//

    /**
     * Wraps the given array as an {@link InputStream}. It supports mark/reset operations, but the {@code close()}
     * method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(byte @Nonnull [] array) {
        return IOImpls.inputStream(array);
    }

    /**
     * Wraps the given array as an {@link InputStream} from the specified offset up to the specified length. It supports
     * mark/reset operations, but the {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(byte @Nonnull [] array, int offset, int length) {
        return IOImpls.inputStream(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link InputStream}. It supports mark/reset operations, but the {@code close()}
     * method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull ByteBuffer buffer) {
        return IOImpls.inputStream(buffer);
    }

    /**
     * Wraps the given random access file as an {@link InputStream} from the specified file pointer offset. It supports
     * mark/reset operations, and the {@code close()} method will close both the file and stream. Any operation to the
     * file will affect the stream.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param random the given random access file
     * @param offset the specified file pointer offset
     * @return the given random access file as an {@link InputStream}
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull InputStream newInputStream(
        @Nonnull RandomAccessFile random, long offset
    ) throws IORuntimeException {
        return IOImpls.inputStream(random, offset);
    }

    /**
     * Wraps the given reader as an {@link InputStream} with {@link JieChars#defaultCharset()}. It supports mark/reset
     * operations. The read position of the reader may not correspond to the position of the stream, and the
     * {@code close()} method will close both the reader and stream at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param reader the given reader
     * @return the given reader as an {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull Reader reader) {
        return newInputStream(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps the given reader as an {@link InputStream} with the specified charset. It supports mark/reset operations.
     * The read position of the reader may not correspond to the position of the stream, and the close method will
     * {@code close()} both the reader and stream at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param reader  the given reader
     * @param charset the specified charset
     * @return the given reader as an {@link InputStream}
     */
    public static @Nonnull InputStream newInputStream(@Nonnull Reader reader, @Nonnull Charset charset) {
        return IOImpls.inputStream(reader, charset);
    }

    /**
     * Wraps the given array as an {@link Reader}. It supports mark/reset operations, but the {@code close()} method has
     * no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link Reader}
     */
    public static @Nonnull Reader newReader(char @Nonnull [] array) {
        return IOImpls.reader(array);
    }

    /**
     * Wraps the given array as an {@link Reader} from the specified offset up to the specified length. It supports
     * mark/reset operations, but the {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link Reader}
     */
    public static @Nonnull Reader newReader(char @Nonnull [] array, int offset, int length) {
        return IOImpls.reader(array, offset, length);
    }

    /**
     * Wraps the given chars as an {@link Reader}. It supports mark/reset operations, but the {@code close()} method has
     * no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param chars the given chars
     * @return the given array as an {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull CharSequence chars) {
        return IOImpls.reader(chars);
    }

    /**
     * Wraps the given buffer as an {@link Reader}. It supports mark/reset operations, but the {@code close()} method
     * has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull CharBuffer buffer) {
        return IOImpls.reader(buffer);
    }

    /**
     * Wraps the given stream as an {@link Reader} with {@link JieChars#defaultCharset()}. It supports mark/reset
     * operations. The read position of the stream may not correspond to the position of the reader, and the
     * {@code close()} method will close both the stream and reader at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param stream the given stream
     * @return the given stream as an {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull InputStream stream) {
        return newReader(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps the given stream as an {@link Reader} with the specified charset. It supports mark/reset operations. The
     * read position of the stream may not correspond to the position of the reader, and the {@code close()} method will
     * close both the stream and reader at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given stream as an {@link Reader}
     */
    public static @Nonnull Reader newReader(@Nonnull InputStream stream, @Nonnull Charset charset) {
        return IOImpls.reader(stream, charset);
    }

    /**
     * Wraps the given array as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(byte @Nonnull [] array) {
        return IOImpls.outputStream(array);
    }

    /**
     * Wraps the given array as an {@link OutputStream} from the specified offset up to the specified length. The
     * {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(byte @Nonnull [] array, int offset, int length) {
        return IOImpls.outputStream(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull ByteBuffer buffer) {
        return IOImpls.outputStream(buffer);
    }

    /**
     * Wraps the given random access file as an {@link OutputStream} from the specified file pointer offset. The
     * {@code close()} method will close both the file and stream. Any operation to the file will affect the stream.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param random the given random access file
     * @param offset the specified file pointer offset
     * @return the given random access file as an {@link OutputStream}
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull OutputStream newOutputStream(
        @Nonnull RandomAccessFile random, long offset
    ) throws IORuntimeException {
        return IOImpls.outputStream(random, offset);
    }

    /**
     * Wraps the given appender as an {@link OutputStream} with {@link JieChars#defaultCharset()}. The write position of
     * the appender may not correspond to the position of the stream, and the {@code close()} method will close both the
     * appender and stream at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param appender the given appender
     * @return the given appender as an {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull Appendable appender) {
        return newOutputStream(appender, JieChars.defaultCharset());
    }

    /**
     * Wraps the given appender as an {@link OutputStream} with the specified charset. The write position of the
     * appender may not correspond to the position of the stream, and the {@code close()} method will close both the
     * appender and stream at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param appender the given appender
     * @param charset  the specified charset
     * @return the given appender as an {@link OutputStream}
     */
    public static @Nonnull OutputStream newOutputStream(@Nonnull Appendable appender, @Nonnull Charset charset) {
        return IOImpls.outputStream(appender, charset);
    }

    /**
     * Wraps the given array as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link Writer}
     */
    public static @Nonnull Writer newWriter(char @Nonnull [] array) {
        return IOImpls.writer(array);
    }

    /**
     * Wraps the given array as an {@link Writer} from the specified offset up to the specified length. The
     * {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link Writer}
     */
    public static @Nonnull Writer newWriter(char @Nonnull [] array, int offset, int length) {
        return IOImpls.writer(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given array as an {@link Writer}
     */
    public static @Nonnull Writer newWriter(@Nonnull CharBuffer buffer) {
        return IOImpls.writer(buffer);
    }

    /**
     * Wraps the given stream as an {@link Writer} with {@link JieChars#defaultCharset()}. The write position of the
     * stream may not correspond to the position of the writer, and the {@code close()} method will close both the
     * stream and writer at their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param stream the given stream
     * @return the given appender as an {@link Writer}
     */
    public static @Nonnull Writer newWriter(@Nonnull OutputStream stream) {
        return newWriter(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps the given stream as an {@link Writer} with the specified charset. The write position of the stream may not
     * correspond to the position of the writer, and the {@code close()} method will close both the stream and writer at
     * their current positions.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given appender as an {@link Writer}
     */
    public static @Nonnull Writer newWriter(@Nonnull OutputStream stream, @Nonnull Charset charset) {
        return IOImpls.writer(stream, charset);
    }

    /**
     * Returns an empty {@link InputStream}.
     *
     * @return an empty {@link InputStream}
     */
    public static @Nonnull InputStream emptyInputStream() {
        return IOImpls.emptyInputStream();
    }

    /**
     * Returns an empty {@link Reader}.
     *
     * @return an empty {@link Reader}
     */
    public static @Nonnull Reader emptyReader() {
        return IOImpls.emptyReader();
    }

    /**
     * Returns an {@link OutputStream} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link OutputStream} that infinitely accepts data but immediately discards them
     */
    public static @Nonnull OutputStream nullOutputStream() {
        return IOImpls.nullOutputStream();
    }

    /**
     * Returns an {@link Writer} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link Writer} that infinitely accepts data but immediately discards them
     */
    public static @Nonnull Writer nullWriter() {
        return IOImpls.nullWriter();
    }

    //---------------- Wrappers End ----------------//
}
