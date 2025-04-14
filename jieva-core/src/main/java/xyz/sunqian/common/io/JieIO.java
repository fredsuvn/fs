package xyz.sunqian.common.io;

import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.coll.JieArray;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Static utility class for I/O operations.
 *
 * @author sunqian
 */
public class JieIO {

    /**
     * The default IO buffer size: 1024 * 8 = 8192.
     */
    public static final int BUFFER_SIZE = 1024 * 8;

    //---------------- Common Start ----------------//

    /**
     * Reads all data from the source stream into a new array, continuing until the end of the stream, and returns the
     * array.
     *
     * @param source the source stream
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte[] read(InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return new byte[0];
                }
                if (c == available) {
                    int r = source.read();
                    if (r == -1) {
                        return bytes;
                    } else {
                        BytesBuilder builder = new BytesBuilder(available + 1);
                        builder.append(bytes);
                        builder.append(r);
                        readTo(source, builder);
                        return builder.toByteArray();
                    }
                } else {
                    return Arrays.copyOf(bytes, c);
                }
            } else {
                return JieBytes.process(source).toByteArray();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads the specified number of data from the source stream into a new array, and returns the array. If
     * {@code number < 0}, this method performs as {@link #read(InputStream)}. If {@code number == 0}, returns an empty
     * array without reading. Otherwise, this method keeps reading until the read number reaches the specified number or
     * the end of the stream has been reached.
     *
     * @param source the source stream
     * @param number the specified number
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static byte[] read(InputStream source, int number) throws IORuntimeException {
        try {
            if (number < 0) {
                return read(source);
            }
            if (number == 0) {
                return new byte[0];
            }
            int b = source.read();
            if (b == -1) {
                return new byte[0];
            }
            byte[] dest = new byte[number];
            dest[0] = (byte) b;
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
     * Reads all data from the source reader into a new array, continuing until the end of the reader, and returns the
     * array.
     *
     * @param source the source reader
     * @return the array containing the data
     * @throws IORuntimeException if an I/O error occurs
     */
    public static char[] read(Reader source) throws IORuntimeException {
        return JieChars.process(source).toCharArray();
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
    public static char[] read(Reader source, int number) throws IORuntimeException {
        try {
            if (number < 0) {
                return read(source);
            }
            if (number == 0) {
                return new char[0];
            }
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
    public static String string(Reader source) throws IORuntimeException {
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
    public static String string(Reader source, int number) throws IORuntimeException {
        StringBuilder builder = new StringBuilder();
        JieChars.process(source).readLimit(number).writeTo(builder);
        return builder.toString();
    }

    /**
     * Reads all bytes from the source stream and returns them as a string with {@link JieChars#defaultCharset()}.
     *
     * @param source the source stream
     * @return the string
     * @throws IORuntimeException if an I/O error occurs
     */
    public static String string(InputStream source) throws IORuntimeException {
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
    public static String string(InputStream source, Charset charset) throws IORuntimeException {
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
    public static byte[] available(InputStream source) throws IORuntimeException {
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
    public static String avalaibleString(InputStream source) throws IORuntimeException {
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
    public static String avalaibleString(InputStream source, Charset charset) throws IORuntimeException {
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
    public static int readTo(InputStream source, byte[] dest) throws IORuntimeException {
        return (int) JieBytes.process(source).readLimit(dest.length).writeTo(dest);
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
    public static int readTo(InputStream source, ByteBuffer dest) throws IORuntimeException {
        return (int) JieBytes.process(source).readLimit(dest.remaining()).writeTo(dest);
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
    public static long readTo(InputStream source, OutputStream dest) throws IORuntimeException {
        return JieBytes.process(source).writeTo(dest);
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
    public static int readTo(Reader source, char[] dest) throws IORuntimeException {
        return (int) JieChars.process(source).readLimit(dest.length).writeTo(dest);
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
    public static int readTo(Reader source, CharBuffer dest) throws IORuntimeException {
        return (int) JieChars.process(source).readLimit(dest.remaining()).writeTo(dest);
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
    public static long readTo(Reader source, Appendable dest) throws IORuntimeException {
        return JieChars.process(source).writeTo(dest);
    }

    //---------------- Common End ----------------//

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

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
    public static InputStream inStream(byte[] array) {
        return Wrappers.in(array);
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
    public static InputStream inStream(byte[] array, int offset, int length) {
        return Wrappers.in(array, offset, length);
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
    public static InputStream inStream(ByteBuffer buffer) {
        return Wrappers.in(buffer);
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
    public static InputStream inStream(RandomAccessFile random, long offset) throws IORuntimeException {
        return Wrappers.in(random, offset);
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
    public static InputStream inStream(Reader reader) {
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
    public static InputStream inStream(Reader reader, Charset charset) {
        return Wrappers.in(reader, charset);
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
    public static Reader reader(char[] array) {
        return Wrappers.reader(array);
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
    public static Reader reader(char[] array, int offset, int length) {
        return Wrappers.reader(array, offset, length);
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
    public static Reader reader(CharSequence chars) {
        return Wrappers.reader(chars);
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
    public static Reader reader(CharBuffer buffer) {
        return Wrappers.reader(buffer);
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
    public static Reader reader(InputStream stream) {
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
    public static Reader reader(InputStream stream, Charset charset) {
        return Wrappers.reader(stream, charset);
    }

    /**
     * Wraps the given array as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link OutputStream}
     */
    public static OutputStream outStream(byte[] array) {
        return Wrappers.out(array);
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
    public static OutputStream outStream(byte[] array, int offset, int length) {
        return Wrappers.out(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link OutputStream}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link OutputStream}
     */
    public static OutputStream outStream(ByteBuffer buffer) {
        return Wrappers.out(buffer);
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
    public static OutputStream outStream(RandomAccessFile random, long offset) throws IORuntimeException {
        return Wrappers.out(random, offset);
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
    public static OutputStream outStream(Appendable appender) {
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
    public static OutputStream outStream(Appendable appender, Charset charset) {
        return Wrappers.out(appender, charset);
    }

    /**
     * Wraps the given array as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param array the given array
     * @return the given array as an {@link Writer}
     */
    public static Writer writer(char[] array) {
        return Wrappers.writer(array);
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
    public static Writer writer(char[] array, int offset, int length) {
        return Wrappers.writer(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link Writer}. The {@code close()} method has no effect.
     * <p>
     * Note the returned wrapper itself does not guarantee thread safety.
     *
     * @param buffer the given buffer
     * @return the given array as an {@link Writer}
     */
    public static Writer writer(CharBuffer buffer) {
        return Wrappers.writer(buffer);
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
    public static Writer writer(OutputStream stream) {
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
    public static Writer writer(OutputStream stream, Charset charset) {
        return Wrappers.writer(stream, charset);
    }

    /**
     * Returns an empty {@link InputStream}.
     *
     * @return an empty {@link InputStream}
     */
    public static InputStream emptyInStream() {
        return Wrappers.emptyIn();
    }

    /**
     * Returns an empty {@link Reader}.
     *
     * @return an empty {@link Reader}
     */
    public static Reader emptyReader() {
        return Wrappers.emptyReader();
    }

    /**
     * Returns an {@link OutputStream} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link OutputStream} that infinitely accepts data but immediately discards them
     */
    public static OutputStream nullOutStream() {
        return Wrappers.nullOut();
    }

    /**
     * Returns an {@link Writer} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link Writer} that infinitely accepts data but immediately discards them
     */
    public static Writer nullWriter() {
        return Wrappers.nullWriter();
    }

    //---------------- Wrappers End ----------------//
}
