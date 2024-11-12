package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieChars;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.function.Function;

/**
 * This is a static utilities class provides utilities for {@code IO} operations.
 *
 * @author fresduvn
 */
public class JieIO {

    /**
     * Default IO buffer size: 1024 * 8 = 8192.
     */
    public static final int BUFFER_SIZE = 1024 * 8;

    /**
     * Reads all bytes from source stream into an array. Returns the array, or null if no data read out and reaches to
     * the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static byte[] read(InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return null;
                }
                if (c == available) {
                    int r = source.read();
                    if (r == -1) {
                        return bytes;
                    } else {
                        ByteArrayOutputStream dest = new ByteArrayOutputStream(available + 1);
                        dest.write(bytes);
                        dest.write(r);
                        readTo(source, dest);
                        return dest.toByteArray();
                    }
                } else {
                    return Arrays.copyOf(bytes, c);
                }
            } else {
                ByteArrayOutputStream dest = new ByteArrayOutputStream();
                long num = readTo(source, dest);
                return num < 0 ? null : dest.toByteArray();
            }
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads specified number of bytes from source stream into an array. Returns the array, or null if no data read out
     * and reaches to the end of stream.
     * <p>
     * If the number &lt; 0, read all as {@link #read(InputStream)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the stream.
     *
     * @param source source stream
     * @param number specified number
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
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
                return null;
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
     * Reads all chars from source reader into a string. Returns the string, or null if no data read out and reaches to
     * the end of reader.
     *
     * @param source source reader
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String read(Reader source) throws IORuntimeException {
        StringBuilder dest = new StringBuilder();
        long readCount = readTo(source, dest);
        if (readCount == -1) {
            return null;
        }
        return dest.toString();
    }

    /**
     * Reads specified number of chars from source reader into a string. Returns the string, or null if no data read out
     * and reaches to the end of reader.
     * <p>
     * If the number &lt; 0, read all as {@link #read(Reader)}; els if the number is 0, no read and return an empty
     * array; else this method will keep reading until the read number reaches to the specified number, or the reading
     * reaches the end of the reader.
     *
     * @param source source reader
     * @param number specified number
     * @return the string, or null if no data read out and reaches to the end of reader
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String read(Reader source, int number) throws IORuntimeException {
        StringBuilder dest = new StringBuilder();
        long readCount = CharStream.from(source).to(dest).readLimit(number).start();
        if (readCount == -1) {
            return null;
        }
        return dest.toString();
    }

    /**
     * Reads all bytes from source stream into a string with {@link JieChars#defaultCharset()}. Returns the string, or
     * null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String readString(InputStream source) throws IORuntimeException {
        return readString(source, JieChars.defaultCharset());
    }

    /**
     * Reads all bytes from source stream into a string with specified charset. Returns the string, or null if no data
     * read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String readString(InputStream source, Charset charset) throws IORuntimeException {
        byte[] bytes = read(source);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    /**
     * Reads available bytes from source stream into an array. Returns the array, or null if no data read out and
     * reaches to the end of stream.
     *
     * @param source source stream
     * @return the array, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static byte[] available(InputStream source) throws IORuntimeException {
        try {
            int available = source.available();
            if (available > 0) {
                byte[] bytes = new byte[available];
                int c = source.read(bytes);
                if (c == -1) {
                    return null;
                }
                if (c == available) {
                    return bytes;
                }
                return Arrays.copyOf(bytes, c);
            }
            if (available == 0) {
                byte[] b = new byte[1];
                int readSize = source.read(b);
                if (readSize == -1) {
                    return null;
                }
                if (readSize == 0) {
                    return new byte[0];
                }
                return b;
            }
            return null;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads available bytes from source stream into a string with {@link JieChars#defaultCharset()}. Returns the
     * string, or null if no data read out and reaches to the end of stream.
     *
     * @param source source stream
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String avalaibleString(InputStream source) throws IORuntimeException {
        return avalaibleString(source, JieChars.defaultCharset());
    }

    /**
     * Reads available bytes from source stream into a string with specified charset. Returns the string, or null if no
     * data read out and reaches to the end of stream.
     *
     * @param source  source stream
     * @param charset specified charset
     * @return the string, or null if no data read out and reaches to the end of stream
     * @throws IORuntimeException IO runtime exception
     */
    @Nullable
    public static String avalaibleString(InputStream source, Charset charset) throws IORuntimeException {
        try {
            byte[] bytes = available(source);
            if (bytes == null) {
                return null;
            }
            return new String(bytes, charset);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Reads bytes from source stream and writes them into dest array, returns actual read number. If the source has
     * been ended and no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     * @see ByteStream#start()
     */
    public static int readTo(InputStream source, byte[] dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Reads bytes from source stream and writes them into dest buffer, returns actual read number. If the source has
     * been ended and no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     * @see ByteStream#start()
     */
    public static int readTo(InputStream source, ByteBuffer dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Reads bytes from source stream and writes them into dest stream, returns actual read number. If the source has
     * been ended and no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     * @see ByteStream#start()
     */
    public static long readTo(InputStream source, OutputStream dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).start();
    }

    /**
     * Reads chars from source reader and writes them into dest array, returns actual read number. If the source has
     * been ended and no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     * @see CharStream#start()
     */
    public static int readTo(Reader source, char[] dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Reads chars from source reader and writes them into dest buffer, returns actual read number. If the source has
     * been ended and no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     * @see CharStream#start()
     */
    public static int readTo(Reader source, CharBuffer dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Reads bytes from source reader and writes them into dest appendable, returns actual read number. If the source
     * has been ended and no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest appendable
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     * @see CharStream#start()
     */
    public static long readTo(Reader source, Appendable dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).start();
    }

    /**
     * Wraps given array as an {@link InputStream}.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array given array
     * @return given array as an {@link InputStream}
     */
    public static InputStream in(byte[] array) {
        return InImpls.in(array);
    }

    /**
     * Wraps given array as an {@link InputStream} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link InputStream}
     */
    public static InputStream in(byte[] array, int offset, int length) {
        return InImpls.in(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link InputStream}.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link InputStream}
     */
    public static InputStream in(ByteBuffer buffer) {
        return InImpls.in(buffer);
    }

    /**
     * Wraps given random access file as an {@link InputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and first seeks to specified
     * initial file pointer when creating the stream and re-seeks if calls reset method. The close method will close the
     * file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream in(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        return InImpls.in(random, initialSeek);
    }

    /**
     * Wraps given reader as an {@link InputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader given reader
     * @return given reader as an {@link InputStream}
     */
    public static InputStream in(Reader reader) {
        return in(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps given reader as an {@link InputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader  given reader
     * @param charset specified charset
     * @return given reader as an {@link InputStream}
     */
    public static InputStream in(Reader reader, Charset charset) {
        return InImpls.in(reader, charset);
    }

    /**
     * Wraps given array as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array given array
     * @return given array as an {@link Reader}
     */
    public static Reader reader(char[] array) {
        return InImpls.reader(array);
    }

    /**
     * Wraps given array as an {@link Reader} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Reader}
     */
    public static Reader reader(char[] array, int offset, int length) {
        return InImpls.reader(array, offset, length);
    }

    /**
     * Wraps given chars as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link StringReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param chars given chars
     * @return given array as an {@link Reader}
     */
    public static Reader reader(CharSequence chars) {
        return InImpls.reader(chars);
    }

    /**
     * Wraps given buffer as an {@link Reader}.
     * <p>
     * Returned reader does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Reader}
     */
    public static Reader reader(CharBuffer buffer) {
        return InImpls.reader(buffer);
    }

    /**
     * Wraps given stream as an {@link Reader} with {@link JieChars#defaultCharset()}.
     * <p>
     * The returned stream is similar to {@link InputStreamReader} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. It does support mark/reset operations. The read
     * position of the reader and stream may not correspond, the close method will close both reader and stream at their
     * current positions.
     *
     * @param inputStream given stream
     * @return given stream as an {@link Reader}
     */
    public static Reader reader(InputStream inputStream) {
        return reader(inputStream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as an {@link Reader} with specified charset.
     * <p>
     * The returned stream is similar to {@link InputStreamReader} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. It does support mark/reset operations. The read
     * position of the reader and stream may not correspond, the close method will close both reader and stream at their
     * current positions.
     *
     * @param inputStream given stream
     * @param charset     specified charset
     * @return given stream as an {@link Reader}
     */
    public static Reader reader(InputStream inputStream, Charset charset) {
        return InImpls.reader(inputStream, charset);
    }

    /**
     * Wraps given array as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream out(byte[] array) {
        return OutImpls.out(array);
    }

    /**
     * Wraps given array as {@link OutputStream} from specified offset up to specified length.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream out(byte[] array, int offset, int length) {
        return OutImpls.out(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link OutputStream}
     */
    public static OutputStream out(ByteBuffer buffer) {
        return OutImpls.out(buffer);
    }

    /**
     * Wraps given random access file as an {@link OutputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It first seeks to specified initial file pointer when creating
     * the stream. The close method will close the file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream out(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        return OutImpls.out(random, initialSeek);
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream out(Appendable appender) {
        return out(appender, JieChars.defaultCharset());
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @param charset  specified charset
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream out(Appendable appender, Charset charset) {
        return OutImpls.out(appender, charset);
    }

    /**
     * Wraps given array as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link Writer}
     */
    public static Writer writer(char[] array) {
        return OutImpls.writer(array);
    }

    /**
     * Wraps given array as {@link Writer} from specified offset up to specified length.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Writer}
     */
    public static Writer writer(char[] array, int offset, int length) {
        return OutImpls.writer(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Writer}
     */
    public static Writer writer(CharBuffer buffer) {
        return OutImpls.writer(buffer);
    }

    /**
     * Wraps given stream as an {@link Writer} with {@link JieChars#defaultCharset()}.
     * <p>
     * The returned stream is similar to {@link OutputStreamWriter} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. The write position of the writer and stream may not
     * correspond, the close method will close both writer and stream at their current positions.
     *
     * @param outputStream given stream
     * @return given stream as an {@link Writer}
     */
    public static Writer writer(OutputStream outputStream) {
        return writer(outputStream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as an {@link Writer} with specified charset.
     * <p>
     * The returned stream is similar to {@link OutputStreamWriter} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. The write position of the writer and stream may not
     * correspond, the close method will close both writer and stream at their current positions.
     *
     * @param outputStream given stream
     * @param charset      specified charset
     * @return given stream as an {@link Writer}
     */
    public static Writer writer(OutputStream outputStream, Charset charset) {
        return OutImpls.writer(outputStream, charset);
    }

    /**
     * Returns a {@link TransformInputStream} with source stream, block size and transformer.
     * <p>
     * Note the block size could be negative or {@code 0}, in this case all bytes would be read once from source stream
     * to transform.
     * <p>
     * See {@link TransformInputStream} and
     * {@link TransformInputStream#TransformInputStream(InputStream, int, Function)}.
     *
     * @param source      source stream
     * @param blockSize   block size
     * @param transformer given transformer
     * @return a {@link TransformInputStream}
     * @see TransformInputStream
     * @see TransformInputStream#TransformInputStream(InputStream, int, Function)
     */
    public static TransformInputStream transform(
        InputStream source, int blockSize, Function<byte[], byte[]> transformer) {
        return new TransformInputStream(source, blockSize, transformer);
    }

    /**
     * Returns an empty input stream.
     *
     * @return an empty input stream
     */
    public static InputStream emptyInputStream() {
        return EmptyInputStream.SINGLETON;
    }

    private static final class EmptyInputStream extends InputStream {

        private static final EmptyInputStream SINGLETON = new EmptyInputStream();

        @Override
        public int read() {
            return -1;
        }
    }
}
