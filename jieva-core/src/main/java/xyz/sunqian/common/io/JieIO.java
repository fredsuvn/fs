package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.collect.JieArray;

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
     * Writes the data to the specified output channel from the given buffer.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified output channel
     * @param buf the given buffer
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(@Nonnull WritableByteChannel dst, @Nonnull ByteBuffer buf) throws IORuntimeException {
        try {
            int c = buf.remaining();
            while (c > 0) {
                c -= dst.write(buf);
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
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
     * Writes the data of the specified length to the specified appender from the given buffer. If the buffer's
     * remaining {@code = 0}, this method returns immediately without writing.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified appender
     * @param src the given buffer
     * @throws IORuntimeException if an I/O error occurs
     */
    public static void write(
        @Nonnull Appendable dst, @Nonnull CharBuffer src
    ) throws IndexOutOfBoundsException, IORuntimeException {
        int remaining = src.remaining();
        if (remaining == 0) {
            return;
        }
        try {
            dst.append(src, src.position(), src.limit());
            src.position(src.limit());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Writes the data of the specified length to the specified appender from the given buffer. If the specified length
     * or buffer's remaining {@code = 0}, this method returns immediately without writing.
     * <p>
     * The buffer's position increments by the actual read number.
     *
     * @param dst the specified appender
     * @param src the given buffer
     * @param len the specified length, must {@code >= 0}
     * @throws IllegalArgumentException if the specified length is illegal
     * @throws IORuntimeException       if an I/O error occurs
     */
    public static void write(
        @Nonnull Appendable dst, @Nonnull CharBuffer src, int len
    ) throws IndexOutOfBoundsException, IORuntimeException {
        JieCheck.checkArgument(len >= 0, "len must >= 0.");
        if (len == 0 || src.remaining() == 0) {
            return;
        }
        try {
            int actualLen = Math.min(src.remaining(), len);
            int newLimit = src.position() + actualLen;
            dst.append(src, src.position(), newLimit);
            src.position(newLimit);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all bytes from the source stream and returns them as a string with {@link JieChars#defaultCharset()}.
     *
     * @param source the source stream
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String string(@Nonnull InputStream source) throws IORuntimeException {
        return string(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from the source stream and returns them as a string with the specified charset.
     *
     * @param source  the source stream
     * @param charset the specified charset
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String string(
        @Nonnull InputStream source, @Nonnull Charset charset
    ) throws IORuntimeException {
        byte[] bytes = read(source);
        if (JieArray.isEmpty(bytes)) {
            return "";
        }
        return new String(bytes, charset);
    }

    /**
     * Reads available data from the source stream into a new array and returns the array.
     *
     * @param source the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nonnull [] available(@Nonnull InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return new byte[0];
                }
                if (c == available) {
                    return bytes;
                }
                return Arrays.copyOf(bytes, c);
            }
            if (available == 0) {
                byte[] b = new byte[1];
                int readSize = source.read(b);
                if (readSize <= 0) {
                    return new byte[0];
                }
                return b;
            }
            return new byte[0];
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads available bytes from the source stream and returns them as a string with
     * {@link JieChars#defaultCharset()}.
     *
     * @param source the source stream
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String avalaibleString(@Nonnull InputStream source) throws IORuntimeException {
        return avalaibleString(source, JieChars.defaultCharset());
    }

    /**
     * Reads available bytes from the source stream and returns them as a string with the specified charset.
     *
     * @param source  the source stream
     * @param charset the specified charset
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String avalaibleString(
        @Nonnull InputStream source, @Nonnull Charset charset
    ) throws IORuntimeException {
        try {
            byte[] bytes = available(source);
            if (JieArray.isEmpty(bytes)) {
                return "";
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    //---------------- Common End ----------------//

    //------------------------------------------------------------//
    //------------------------------------------------------------//

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
    public static @Nonnull InputStream inStream(byte @Nonnull [] array) {
        return WrapperImpls.in(array);
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
    public static @Nonnull InputStream inStream(byte @Nonnull [] array, int offset, int length) {
        return WrapperImpls.in(array, offset, length);
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
    public static @Nonnull InputStream inStream(@Nonnull ByteBuffer buffer) {
        return WrapperImpls.in(buffer);
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
    public static @Nonnull InputStream inStream(
        @Nonnull RandomAccessFile random, long offset
    ) throws IORuntimeException {
        return WrapperImpls.in(random, offset);
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
    public static @Nonnull InputStream inStream(@Nonnull Reader reader) {
        return inStream(reader, JieChars.defaultCharset());
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
    public static @Nonnull InputStream inStream(@Nonnull Reader reader, @Nonnull Charset charset) {
        return WrapperImpls.in(reader, charset);
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
    public static @Nonnull Reader reader(char @Nonnull [] array) {
        return WrapperImpls.reader(array);
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
    public static @Nonnull Reader reader(char @Nonnull [] array, int offset, int length) {
        return WrapperImpls.reader(array, offset, length);
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
    public static @Nonnull Reader reader(@Nonnull CharSequence chars) {
        return WrapperImpls.reader(chars);
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
    public static @Nonnull Reader reader(@Nonnull CharBuffer buffer) {
        return WrapperImpls.reader(buffer);
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
    public static @Nonnull Reader reader(@Nonnull InputStream stream) {
        return reader(stream, JieChars.defaultCharset());
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
    public static @Nonnull Reader reader(@Nonnull InputStream stream, @Nonnull Charset charset) {
        return WrapperImpls.reader(stream, charset);
    }

    /**
     * Wraps the given array as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link OutputStream}
     */
    public static @Nonnull OutputStream outStream(byte @Nonnull [] array) {
        return WrapperImpls.out(array);
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
    public static @Nonnull OutputStream outStream(byte @Nonnull [] array, int offset, int length) {
        return WrapperImpls.out(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link OutputStream}
     */
    public static @Nonnull OutputStream outStream(@Nonnull ByteBuffer buffer) {
        return WrapperImpls.out(buffer);
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
    public static @Nonnull OutputStream outStream(
        @Nonnull RandomAccessFile random, long offset
    ) throws IORuntimeException {
        return WrapperImpls.out(random, offset);
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
    public static @Nonnull OutputStream outStream(@Nonnull Appendable appender) {
        return outStream(appender, JieChars.defaultCharset());
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
    public static @Nonnull OutputStream outStream(@Nonnull Appendable appender, @Nonnull Charset charset) {
        return WrapperImpls.out(appender, charset);
    }

    /**
     * Wraps the given array as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link Writer}
     */
    public static @Nonnull Writer writer(char @Nonnull [] array) {
        return WrapperImpls.writer(array);
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
    public static @Nonnull Writer writer(char @Nonnull [] array, int offset, int length) {
        return WrapperImpls.writer(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given array as an {@link Writer}
     */
    public static @Nonnull Writer writer(@Nonnull CharBuffer buffer) {
        return WrapperImpls.writer(buffer);
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
    public static @Nonnull Writer writer(@Nonnull OutputStream stream) {
        return writer(stream, JieChars.defaultCharset());
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
    public static @Nonnull Writer writer(@Nonnull OutputStream stream, @Nonnull Charset charset) {
        return WrapperImpls.writer(stream, charset);
    }

    /**
     * Returns an empty {@link InputStream}.
     *
     * @return an empty {@link InputStream}
     */
    public static @Nonnull InputStream emptyInStream() {
        return WrapperImpls.emptyIn();
    }

    /**
     * Returns an empty {@link Reader}.
     *
     * @return an empty {@link Reader}
     */
    public static @Nonnull Reader emptyReader() {
        return WrapperImpls.emptyReader();
    }

    /**
     * Returns an {@link OutputStream} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link OutputStream} that infinitely accepts data but immediately discards them
     */
    public static @Nonnull OutputStream nullOutStream() {
        return WrapperImpls.nullOut();
    }

    /**
     * Returns an {@link Writer} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link Writer} that infinitely accepts data but immediately discards them
     */
    public static @Nonnull Writer nullWriter() {
        return WrapperImpls.nullWriter();
    }

    //---------------- Wrappers End ----------------//
}
