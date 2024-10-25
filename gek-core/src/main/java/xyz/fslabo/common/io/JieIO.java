package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieChars;

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
                        transfer(source, dest);
                        return dest.toByteArray();
                    }
                } else {
                    return Arrays.copyOf(bytes, c);
                }
            } else {
                ByteArrayOutputStream dest = new ByteArrayOutputStream();
                long num = transfer(source, dest);
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
        long readCount = transfer(source, dest);
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
        long readCount = transfer(source, dest, number);
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
     * Transfers bytes from source stream into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static int transfer(InputStream source, byte[] dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers bytes from source stream into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static int transfer(InputStream source, byte[] dest, int offset, int length) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers bytes from source stream into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static int transfer(InputStream source, ByteBuffer dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source stream into dest stream, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).start();
     * </pre>
     *
     * @param source source stream
     * @param dest   dest stream
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static long transfer(InputStream source, OutputStream dest) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit, returns actual read number. If
     * the source has been ended and no data read out, return -1. This method is equivalent to ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source stream into dest stream within specified read limit and block size, returns actual
     * read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link ByteStream}):
     * <pre>
     *     return (int) ByteStream.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source stream
     * @param dest      dest stream
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see ByteStream
     */
    public static long transfer(InputStream source, OutputStream dest, long readLimit, int blockSize) throws IORuntimeException {
        return (int) ByteStream.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
    }

    /**
     * Transfers chars from source reader into dest array, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(dest.length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static int transfer(Reader source, char[] dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(dest.length).start();
    }

    /**
     * Transfers chars from source reader into dest array within specified offset and length, returns actual read
     * number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest, offset, length).readLimit(length).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest array
     * @param offset specified offset at which the data write start
     * @param length specified length to read and write
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static int transfer(Reader source, char[] dest, int offset, int length) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest, offset, length).readLimit(length).start();
    }

    /**
     * Transfers chars from source reader into dest buffer, returns actual read number. If the source has been ended and
     * no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(dest.remaining()).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest buffer
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static int transfer(Reader source, CharBuffer dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(dest.remaining()).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable, returns actual read number. If the source has been ended
     * and no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).start();
     * </pre>
     *
     * @param source source reader
     * @param dest   dest appendable
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static long transfer(Reader source, Appendable dest) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit, returns actual read number.
     * If the source has been ended and no data read out, return -1. This method is equivalent to ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(readLimit).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static long transfer(Reader source, Appendable dest, int readLimit) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(readLimit).start();
    }

    /**
     * Transfers bytes from source reader into dest appendable within specified read limit and block size, returns
     * actual read number. If the source has been ended and no data read out, return -1. This method is equivalent to
     * ({@link CharStream}):
     * <pre>
     *     return (int) CharStream.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
     * </pre>
     *
     * @param source    source reader
     * @param dest      dest appendable
     * @param readLimit specified read limit
     * @param blockSize specified block size
     * @return actual read number, or -1 if the source has been ended and no data read out
     * @throws IORuntimeException IO runtime exception
     * @see CharStream
     */
    public static long transfer(Reader source, Appendable dest, int readLimit, int blockSize) throws IORuntimeException {
        return (int) CharStream.from(source).to(dest).readLimit(readLimit).blockSize(blockSize).start();
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
