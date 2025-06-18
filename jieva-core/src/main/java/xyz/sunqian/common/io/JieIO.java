package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.bytes.BytesBuilder;
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
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Static utility class for I/O operations.
 *
 * @author sunqian
 */
public class JieIO {

    private static final int BUFFER_SIZE = 1024 * 8;

    //---------------- Common Start ----------------//

    /**
     * Returns a recommended IO buffer size, typically is 1024 * 8 = 8192.
     *
     * @return a recommended IO buffer size
     */
    public static int bufferSize() {
        return BUFFER_SIZE;
    }

    /**
     * Reads all data from the source stream into a new array, continuing until the end of the stream has been reached,
     * and returns the array.
     * <p>
     * Note the data in the stream cannot exceed the maximum limit of the array.
     *
     * @param source the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    @SuppressWarnings("resource")
    public static byte @Nonnull [] read(@Nonnull InputStream source) throws IORuntimeException {
        try {
            byte[] buf = new byte[Math.max(source.available(), bufferSize())];
            BytesBuilder builder = null;
            int off = 0;
            while (true) {
                int readSize = source.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    if (builder != null) {
                        builder.append(buf, 0, off);
                        return builder.toByteArray();
                    }
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
                if (off == buf.length) {
                    if (builder == null) {
                        int r = source.read();
                        if (r == -1) {
                            return buf;
                        }
                        builder = new BytesBuilder(buf.length + 1);
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
     * Reads data of the specified length from the source stream into a new array, and returns the array. If the
     * {@code length < 0}, this method performs as {@link #read(InputStream)}. If {@code length == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified length or
     * the end of the stream has been reached.
     * <p>
     * Note the length cannot exceed the maximum limit of the array.
     *
     * @param source the source stream
     * @param length the specified read length
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte @Nonnull [] read(@Nonnull InputStream source, int length) throws IORuntimeException {
        if (length < 0) {
            return read(source);
        }
        if (length == 0) {
            return new byte[0];
        }
        try {
            byte[] buf = new byte[length];
            int off = 0;
            while (off < length) {
                int readSize = source.read(buf, off, buf.length - off);
                if (readSize < 0) {
                    return Arrays.copyOfRange(buf, 0, off);
                }
                off += readSize;
            }
            return buf;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source channel into a new buffer, continuing until the end of the channel has been
     * reached, and returns the buffer. The buffer's position is {@code 0}, limit equals capacity, and it has a backing
     * array of which offset is {@code 0}.
     * <p>
     * Note the data in the channel cannot exceed the maximum limit of the buffer.
     *
     * @param source the source channel
     * @return the buffer containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    @SuppressWarnings("resource")
    public static @Nonnull ByteBuffer read(@Nonnull ReadableByteChannel source) throws IORuntimeException {
        try {
            BytesBuilder builder = null;
            ByteBuffer buf = ByteBuffer.allocate(bufferSize());
            while (true) {
                int readSize = source.read(buf);
                if (readSize < 0) {
                    break;
                }
                if (buf.remaining() == 0) {
                    if (builder == null) {
                        ByteBuffer b = ByteBuffer.allocate(1);
                        int r = source.read(b);
                        if (r < 0) {
                            buf.flip();
                            return buf;
                        }
                        builder = new BytesBuilder(buf.capacity());
                        buf.flip();
                        builder.append(buf);
                        builder.append(b.get(0));
                    } else {
                        buf.flip();
                        builder.append(buf);
                    }
                    buf.flip();
                }
            }
            if (builder == null) {
                return ByteBuffer.wrap(Arrays.copyOfRange(buf.array(), 0, buf.position()));
            } else {
                if (buf.position() > 0) {
                    buf.flip();
                    builder.append(buf);
                }
                return builder.toByteBuffer();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads data of the specified length from the source channel into a new buffer, and returns the buffer. If the
     * {@code length < 0}, this method performs as {@link #read(ReadableByteChannel)}. If {@code length == 0}, returns
     * an empty buffer without reading. Otherwise, this method keeps reading until the read number reaches the specified
     * length or the end of the channel has been reached.
     * <p>
     * The buffer's position is {@code 0}, limit equals capacity, and it has a backing array of which offset is
     * {@code 0}. And note the length cannot exceed the maximum limit of the buffer.
     *
     * @param source the source channel
     * @param length the specified read length
     * @return the buffer containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull ByteBuffer read(@Nonnull ReadableByteChannel source, int length) throws IORuntimeException {
        if (length < 0) {
            return read(source);
        }
        if (length == 0) {
            return ByteBuffer.allocate(0);
        }
        try {
            ByteBuffer dst = ByteBuffer.allocate(length);
            while (dst.remaining() > 0) {
                int readSize = source.read(dst);
                if (readSize < 0) {
                    return ByteBuffer.wrap(Arrays.copyOfRange(dst.array(), 0, dst.position()));
                }
            }
            dst.flip();
            return dst;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source reader into a new array, continuing until the end of the reader, and returns the
     * array.
     *
     * @param source the source reader
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char @Nonnull [] read(@Nonnull Reader source) throws IORuntimeException {
        return CharProcessor.from(source).toCharArray();
    }

    /**
     * Reads the specified number of data from the source reader into a new array, and returns the array. If
     * {@code number < 0}, this method performs as {@link #read(Reader)}. If {@code number == 0}, returns an empty array
     * without reading. Otherwise, this method keeps reading until the read number reaches the specified number or the
     * end of the reader has been reached.
     *
     * @param source the source reader
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char @Nonnull [] read(@Nonnull Reader source, int number) throws IORuntimeException {
        if (number < 0) {
            return read(source);
        }
        if (number == 0) {
            return new char[0];
        }
        try {
            int b = source.read();
            if (b == -1) {
                return new char[0];
            }
            char[] dest = new char[number];
            dest[0] = (char) b;
            int remaining = number - 1;
            int offset = 1;
            while (remaining > 0) {
                int readSize = source.read(dest, offset, remaining);
                if (readSize < 0) {
                    return Arrays.copyOfRange(dest, 0, number - remaining);
                }
                remaining -= readSize;
                offset += readSize;
            }
            return dest;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads all data from the source reader into a string, continuing until the end of the reader, and returns the
     * string.
     *
     * @param source the source reader
     * @return the string containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String string(@Nonnull Reader source) throws IORuntimeException {
        StringBuilder builder = new StringBuilder();
        readTo(source, builder);
        return builder.toString();
    }

    /**
     * Reads the specified number of data from the source reader into a string, and returns the string. If
     * {@code number < 0}, this method performs as {@link #string(Reader)}. If {@code number == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified number or
     * the end of the reader has been reached.
     *
     * @param source the source reader
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static @Nonnull String string(@Nonnull Reader source, int number) throws IORuntimeException {
        StringBuilder builder = new StringBuilder();
        CharProcessor.from(source).readLimit(number).writeTo(builder);
        return builder.toString();
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

    /**
     * Reads data from the source stream into the specified array until the array is completely filled or the end of the
     * stream is reached. Returns the actual number of bytes read
     *
     * @param source the source stream
     * @param dest   the specified array
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream source, byte @Nonnull [] dest) throws IORuntimeException {
        return (int) ByteProcessor.from(source).readLimit(dest.length).writeTo(dest);
    }

    /**
     * Reads data from the source stream into the specified buffer until the buffer is completely filled or the end of
     * the stream is reached. Returns the actual number of bytes read
     *
     * @param source the source stream
     * @param dest   the specified buffer
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull InputStream source, @Nonnull ByteBuffer dest) throws IORuntimeException {
        return (int) ByteProcessor.from(source).readLimit(dest.remaining()).writeTo(dest);
    }

    /**
     * Reads data from the source stream into the specified output stream until the end of the source stream is reached.
     * Returns the actual number of bytes read
     *
     * @param source the source stream
     * @param dest   the specified output stream
     * @return the actual number of bytes read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull InputStream source, @Nonnull OutputStream dest) throws IORuntimeException {
        return ByteProcessor.from(source).writeTo(dest);
    }

    /**
     * Reads data from the source reader into the specified array until the array is completely filled or the end of the
     * reader is reached. Returns the actual number of chars read
     *
     * @param source the source reader
     * @param dest   the specified array
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull Reader source, char @Nonnull [] dest) throws IORuntimeException {
        return (int) CharProcessor.from(source).readLimit(dest.length).writeTo(dest);
    }

    /**
     * Reads data from the source reader into the specified buffer until the buffer is completely filled or the end of
     * the reader is reached. Returns the actual number of chars read
     *
     * @param source the source reader
     * @param dest   the specified buffer
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static int readTo(@Nonnull Reader source, @Nonnull CharBuffer dest) throws IORuntimeException {
        return (int) CharProcessor.from(source).readLimit(dest.remaining()).writeTo(dest);
    }

    /**
     * Reads data from the source reader into the specified appender until the end of the reader is reached. Returns the
     * actual number of chars read
     *
     * @param source the source reader
     * @param dest   the specified appender
     * @return the actual number of chars read
     * @throws IORuntimeException if an I/O error occurs
     */
    public static long readTo(@Nonnull Reader source, @Nonnull Appendable dest) throws IORuntimeException {
        return CharProcessor.from(source).writeTo(dest);
    }

    /**
     * Writes the specified chars (starting at the specified offset up to the specified length) into the given
     * appender.
     *
     * @param appender the given appender
     * @param chars    the specified chars
     * @param offset   the specified offset
     * @param length   the specified length
     * @throws IndexOutOfBoundsException if the specified offset or length is out of bounds
     * @throws IOException               if an I/O error occurs
     */
    public static void write(
        @Nonnull Appendable appender, char @Nonnull [] chars, int offset, int length
    ) throws IndexOutOfBoundsException, IOException {
        if (appender instanceof Writer) {
            ((Writer) appender).write(chars, offset, length);
            return;
        }
        appender.append(new String(chars, offset, length));
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
