package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.coll.JieArray;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Static utility class for {@code IO} operations.
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
                return processBytes(source).toByteArray();
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
        return processChars(source).toCharArray();
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
        processChars(source).readLimit(number).writeTo(builder);
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
        return (int) processBytes(source).readLimit(dest.length).writeTo(dest);
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
        return (int) processBytes(source).readLimit(dest.remaining()).writeTo(dest);
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
        return (int) processBytes(source).writeTo(dest);
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
        return (int) processChars(source).readLimit(dest.length).writeTo(dest);
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
        return (int) processChars(source).readLimit(dest.remaining()).writeTo(dest);
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
        return processChars(source).writeTo(dest);
    }

    //---------------- Common End ----------------//

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    //---------------- Wrappers Begin ----------------//

    /**
     * Wraps the given array as an {@link InputStream}. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param array the given array
     * @return the given array as an {@link InputStream}
     */
    public static InputStream inStream(byte[] array) {
        return IOBack.in(array);
    }

    /**
     * Wraps the given array as an {@link InputStream} from the specified offset up to the specified length. It supports
     * mark/reset operations, and the close method does nothing.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link InputStream}
     */
    public static InputStream inStream(byte[] array, int offset, int length) {
        return IOBack.in(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link InputStream}. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link InputStream}
     */
    public static InputStream inStream(ByteBuffer buffer) {
        return IOBack.in(buffer);
    }

    /**
     * Wraps the given random access file as an {@link InputStream} from the specified file pointer offset. It supports
     * mark/reset operations by seek methods, and the close method will close the file. Any operation to the file will
     * affect the stream.
     *
     * @param random the given random access file
     * @param offset the specified file pointer offset
     * @return the given random access file as an {@link InputStream}
     * @throws IORuntimeException if an I/O error occurs
     */
    public static InputStream inStream(RandomAccessFile random, long offset) throws IORuntimeException {
        return IOBack.in(random, offset);
    }

    /**
     * Wraps the given reader as an {@link InputStream} with {@link JieChars#defaultCharset()}. It supports mark/reset
     * operations. The read position of the reader may not correspond to the position of the stream, and the close
     * method will close both the reader and stream at their current positions.
     *
     * @param reader the given reader
     * @return the given reader as an {@link InputStream}
     */
    public static InputStream inStream(Reader reader) {
        return inStream(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps the given reader as an {@link InputStream} with the specified charset. It supports mark/reset operations.
     * The read position of the reader may not correspond to the position of the stream, and the close method will close
     * both the reader and stream at their current positions.
     *
     * @param reader  the given reader
     * @param charset the specified charset
     * @return the given reader as an {@link InputStream}
     */
    public static InputStream inStream(Reader reader, Charset charset) {
        return IOBack.in(reader, charset);
    }

    /**
     * Wraps the given array as an {@link Reader}. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param array the given array
     * @return the given array as an {@link Reader}
     */
    public static Reader reader(char[] array) {
        return IOBack.reader(array);
    }

    /**
     * Wraps the given array as an {@link Reader} from the specified offset up to the specified length. It supports
     * mark/reset operations, and the close method does nothing.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link Reader}
     */
    public static Reader reader(char[] array, int offset, int length) {
        return IOBack.reader(array, offset, length);
    }

    /**
     * Wraps the given chars as an {@link Reader}. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param chars the given chars
     * @return the given array as an {@link Reader}
     */
    public static Reader reader(CharSequence chars) {
        return IOBack.reader(chars);
    }

    /**
     * Wraps the given buffer as an {@link Reader}. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link Reader}
     */
    public static Reader reader(CharBuffer buffer) {
        return IOBack.reader(buffer);
    }

    /**
     * Wraps the given stream as an {@link Reader} with {@link JieChars#defaultCharset()}. It supports mark/reset
     * operations. The read position of the stream may not correspond to the position of the reader, and the close
     * method will close both the stream and reader at their current positions.
     *
     * @param stream the given stream
     * @return the given stream as an {@link Reader}
     */
    public static Reader reader(InputStream stream) {
        return reader(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps the given stream as an {@link Reader} with the specified charset. It supports mark/reset operations. The
     * read position of the stream may not correspond to the position of the reader, and the close method will close
     * both the stream and reader at their current positions.
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given stream as an {@link Reader}
     */
    public static Reader reader(InputStream stream, Charset charset) {
        return IOBack.reader(stream, charset);
    }

    /**
     * Wraps the given array as an {@link OutputStream}. The close method does nothing.
     *
     * @param array the given array
     * @return the given array as an {@link OutputStream}
     */
    public static OutputStream outStream(byte[] array) {
        return IOBack.out(array);
    }

    /**
     * Wraps the given array as an {@link OutputStream} from the specified offset up to the specified length. The close
     * method does nothing.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link OutputStream}
     */
    public static OutputStream outStream(byte[] array, int offset, int length) {
        return IOBack.out(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link OutputStream}. The close method does nothing.
     *
     * @param buffer the given buffer
     * @return the given buffer as an {@link OutputStream}
     */
    public static OutputStream outStream(ByteBuffer buffer) {
        return IOBack.out(buffer);
    }

    /**
     * Wraps the given random access file as an {@link OutputStream} from the specified file pointer offset. The close
     * method will close the file. Any operation to the file will affect the stream.
     *
     * @param random the given random access file
     * @param offset the specified file pointer offset
     * @return the given random access file as an {@link OutputStream}
     * @throws IORuntimeException if an I/O error occurs
     */
    public static OutputStream outStream(RandomAccessFile random, long offset) throws IORuntimeException {
        return IOBack.out(random, offset);
    }

    /**
     * Wraps the given appender as an {@link OutputStream} with {@link JieChars#defaultCharset()}. The write position of
     * the appender may not correspond to the position of the stream, and the close method will close both the appender
     * and stream at their current positions.
     *
     * @param appender the given appender
     * @return the given appender as an {@link OutputStream}
     */
    public static OutputStream outStream(Appendable appender) {
        return outStream(appender, JieChars.defaultCharset());
    }

    /**
     * Wraps the given appender as an {@link OutputStream} with the specified charset. The write position of the
     * appender may not correspond to the position of the stream, and the close method will close both the appender and
     * stream at their current positions.
     *
     * @param appender the given appender
     * @param charset  the specified charset
     * @return the given appender as an {@link OutputStream}
     */
    public static OutputStream outStream(Appendable appender, Charset charset) {
        return IOBack.out(appender, charset);
    }

    /**
     * Wraps the given array as an {@link Writer}. The close method does nothing.
     *
     * @param array the given array
     * @return the given array as an {@link Writer}
     */
    public static Writer writer(char[] array) {
        return IOBack.writer(array);
    }

    /**
     * Wraps the given array as an {@link Writer} from the specified offset up to the specified length. The close method
     * does nothing.
     *
     * @param array  the given array
     * @param offset the specified offset
     * @param length the specified length
     * @return the given array as an {@link Writer}
     */
    public static Writer writer(char[] array, int offset, int length) {
        return IOBack.writer(array, offset, length);
    }

    /**
     * Wraps the given buffer as an {@link Writer}. The close method does nothing.
     *
     * @param buffer the given buffer
     * @return the given array as an {@link Writer}
     */
    public static Writer writer(CharBuffer buffer) {
        return IOBack.writer(buffer);
    }

    /**
     * Wraps the given stream as an {@link Writer} with {@link JieChars#defaultCharset()}. The write position of the
     * stream may not correspond to the position of the writer, and the close method will close both the stream and
     * writer at their current positions.
     *
     * @param stream the given stream
     * @return the given appender as an {@link Writer}
     */
    public static Writer writer(OutputStream stream) {
        return writer(stream, JieChars.defaultCharset());
    }

    /**
     * Wraps the given stream as an {@link Writer} with the specified charset. The write position of the stream may not
     * correspond to the position of the writer, and the close method will close both the stream and writer at their
     * current positions.
     *
     * @param stream  the given stream
     * @param charset the specified charset
     * @return the given appender as an {@link Writer}
     */
    public static Writer writer(OutputStream stream, Charset charset) {
        return IOBack.writer(stream, charset);
    }

    /**
     * Returns an empty {@link InputStream}.
     *
     * @return an empty {@link InputStream}
     */
    public static InputStream emptyInStream() {
        return IOBack.in();
    }

    /**
     * Returns an empty {@link Reader}.
     *
     * @return an empty {@link Reader}
     */
    public static Reader emptyReader() {
        return IOBack.reader();
    }

    /**
     * Returns an {@link OutputStream} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link OutputStream} that infinitely accepts data but immediately discards them
     */
    public static OutputStream nullOutStream() {
        return IOBack.out();
    }

    /**
     * Returns an {@link Writer} that infinitely accepts data but immediately discards them.
     *
     * @return an {@link Writer} that infinitely accepts data but immediately discards them
     */
    public static Writer nullWriter() {
        return IOBack.writer();
    }

    //---------------- Wrappers End ----------------//

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    //---------------- Processors Begin ----------------//

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor processBytes(InputStream data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor processBytes(byte[] data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link BytesProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    public static BytesProcessor processBytes(byte[] data, int offset, int length) throws IndexOutOfBoundsException {
        IOBack.checkReadBounds(data, offset, length);
        if (offset == 0 && length == data.length) {
            return processBytes(data);
        }
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
        return processBytes(buffer);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor processBytes(ByteBuffer data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processChars(Reader data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processChars(char[] data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link CharsProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    public static CharsProcessor processChars(char[] data, int offset, int length) throws IndexOutOfBoundsException {
        IOBack.checkReadBounds(data, offset, length);
        if (offset == 0 && length == data.length) {
            return processChars(data);
        }
        CharBuffer buffer = CharBuffer.wrap(data, offset, length);
        return processChars(buffer);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processChars(CharBuffer data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processChars(CharSequence data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks. The
     * returned encoder splits incoming data into blocks of the specified block size, and for each block, it
     * sequentially calls the given encoder, passing the block as the data parameter. If incoming data is insufficient
     * to form a full block, it is buffered until enough data is received to form a full block.
     * <p>
     * In the last invocation (when {@code end == true}) of the returned encoder, even if the remainder data after
     * splitting is insufficient to form a full block, it will still be passed to the given encoder as the last block,
     * and this call is the given encoder's last invocation.
     *
     * @param size    the specified block size
     * @param encoder the given encoder
     * @return a new {@link BytesProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks
     */
    public static BytesProcessor.Encoder fixedSizeEncoder(int size, BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} to round down incoming data for the given encoder, it is typically used
     * for the encoder which requires consuming data in multiples of the specified size. The returned encoder rounds
     * down incoming data to the largest multiple of the specified size and passes the rounded data to the given
     * encoder. The remainder data will be buffered until enough data is received to round.
     * <p>
     * However, in the last invocation (when {@code end == true}), all remaining data will be passed directly to the
     * given encoder.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a {@link BytesProcessor.Encoder} to round down incoming data for the given encoder
     */
    public static BytesProcessor.Encoder roundEncoder(int size, BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.RoundEncoder(encoder, size);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} that buffers unconsumed data of the given encoder, it is typically used
     * for the encoder which may not fully consume the passed data, requires buffering and consuming data in next
     * invocation. This encoder passes incoming data to the given encoder. The unconsumed remaining data after encoding
     * of the given encoder will be buffered and used in the next invocation.
     * <p>
     * However, in the last invocation (when {@code end == true}), no data will be buffered.
     *
     * @param encoder the given encoder
     * @return a {@link BytesProcessor.Encoder} that buffers unconsumed data of the given encoder
     */
    public static BytesProcessor.Encoder bufferedEncoder(BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.BufferedEncoder(encoder);
    }

    /**
     * Returns a {@link CharsProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks. The
     * returned encoder splits incoming data into blocks of the specified block size, and for each block, it
     * sequentially calls the given encoder, passing the block as the data parameter. If incoming data is insufficient
     * to form a full block, it is buffered until enough data is received to form a full block.
     * <p>
     * In the last invocation (when {@code end == true}) of the returned encoder, even if the remainder data after
     * splitting is insufficient to form a full block, it will still be passed to the given encoder as the last block,
     * and this call is the given encoder's last invocation.
     *
     * @param size    the specified block size
     * @param encoder the given encoder
     * @return a new {@link CharsProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks
     */
    public static CharsProcessor.Encoder fixedSizeEncoder(int size, CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a {@link CharsProcessor.Encoder} to round down incoming data for the given encoder, it is typically used
     * for the encoder which requires consuming data in multiples of the specified size. The returned encoder rounds
     * down incoming data to the largest multiple of the specified size and passes the rounded data to the given
     * encoder. The remainder data will be buffered until enough data is received to round.
     * <p>
     * However, in the last invocation (when {@code end == true}), all remaining data will be passed directly to the
     * given encoder.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a {@link CharsProcessor.Encoder} to round down incoming data for the given encoder
     */
    public static CharsProcessor.Encoder roundEncoder(int size, CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.RoundEncoder(encoder, size);
    }

    /**
     * Returns a {@link CharsProcessor.Encoder} that buffers unconsumed data of the given encoder, it is typically used
     * for the encoder which may not fully consume the passed data, requires buffering and consuming data in next
     * invocation. This encoder passes incoming data to the given encoder. The unconsumed remaining data after encoding
     * of the given encoder will be buffered and used in the next invocation.
     * <p>
     * However, in the last invocation (when {@code end == true}), no data will be buffered.
     *
     * @param encoder the given encoder
     * @return a {@link CharsProcessor.Encoder} that buffers unconsumed data of the given encoder
     */
    public static CharsProcessor.Encoder bufferedEncoder(CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.BufferedEncoder(encoder);
    }

    //---------------- Processors End ----------------//
}
