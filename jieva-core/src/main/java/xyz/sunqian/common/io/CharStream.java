package xyz.sunqian.common.io;

import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Char stream is used to process char data, from specified data source, through zero or more intermediate operations,
 * and finally produces a result or side effect. The following example shows an encoding-then-writing operation:
 * <pre>{@code
 *     CharStream.from(input)
 *         .blockSize(1024)
 *         .encoder(en1)
 *         .encoder(en2, 64)
 *         .writeTo(output);
 * }</pre>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods, to set processing arguments before a terminal method has invoked;
 *     </li>
 *     <li>
 *         Terminal methods, to start the data processing, once a terminal method is invoked, the state of current
 *         stream becomes undefined, and no safe guarantees for further operations on current stream;
 *     </li>
 * </ul>
 * Char stream is lazy, operations on the source data are only performed when a terminal method is invoked, and source
 * data are consumed only as needed.
 *
 * @author sunqian
 */
public interface CharStream {

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(Reader source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(char[] source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source, starting from the start index up to the specified
     * length.
     *
     * @param source specified data source
     * @param offset start index
     * @param length specified length
     * @return a new {@link CharStream}
     * @throws IndexOutOfBoundsException thrown bounds problem
     */
    static CharStream from(char[] source, int offset, int length) throws IndexOutOfBoundsException {
        IOMisc.checkReadBounds(source, offset, length);
        if (offset == 0 && length == source.length) {
            return from(source);
        }
        CharBuffer buffer = CharBuffer.wrap(source, offset, length);
        return from(buffer);
    }

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(CharBuffer source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(CharSequence source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link Encoder} to round input data for given encoder, it is typically used for the encoder which
     * requires consuming data in multiples of fixed-size block.
     * <p>
     * This encoder rounds input data (possibly following buffered data from the previous invocation) to the largest
     * multiple of the expected block size and passes the rounded data to the given encoder. Any remainder data will be
     * buffered and used in the next invocation. However, in the last invocation (where the {@code end} is
     * {@code true}), all data (buffered data followed by input data) will be passed directly to the given encoder.
     * <p>
     * This encoder is not thread-safe.
     *
     * @param encoder           given encoder
     * @param expectedBlockSize specified expected block size
     * @return a new {@link Encoder} to round input data for given encoder
     */
    static Encoder roundEncoder(Encoder encoder, int expectedBlockSize) {
        return new CharStreamImpl.RoundEncoder(encoder, expectedBlockSize);
    }

    /**
     * Returns a new {@link Encoder} that buffers remaining data for given encoder, it is typically used for the encoder
     * which requires consuming data in next invocation.
     * <p>
     * This encoder passes input data (possibly following buffered data from the previous invocation) to the given
     * encoder. Any remaining data after encoding of given encoder will be buffered and used in the next invocation.
     * However, in the last invocation (where the {@code end} is {@code true}), no data will be buffered.
     * <p>
     * This encoder is not thread-safe.
     *
     * @param encoder given encoder
     * @return a new {@link Encoder} that buffers remaining data for given encoder
     */
    static Encoder bufferedEncoder(Encoder encoder) {
        return new CharStreamImpl.BufferedEncoder(encoder);
    }

    /**
     * Returns a new {@link Encoder} that guarantees a specified fixed-size data block is passed to the given encoder in
     * each invocation, it is typically used for the encoder which requires consuming data in fixed-size block.
     * <p>
     * Note in last invocation (where the {@code end} is {@code true}), size of remainder data may be smaller than
     * specified fixed-size.
     * <p>
     * This encoder is not thread-safe.
     *
     * @param encoder given encoder
     * @param size    specified fixed-size
     * @return a new {@link Encoder} that guarantees a specified fixed-size data block is passed to the given encoder in
     * each invocation
     */
    static Encoder fixedSizeEncoder(Encoder encoder, int size) {
        return new CharStreamImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Sets maximum number of chars to read from data source. This can be -1, meaning read until the end, which is the
     * default value.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit maximum number of chars to read from data source
     * @return this
     */
    CharStream readLimit(long readLimit);

    /**
     * Sets the number of chars for each read operation from data source.
     * <p>
     * This setting is typically used when the data source is an input stream, or intermediate operations are set,
     * default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is an optional setting method.
     *
     * @param blockSize the number of chars for each read operation from data source
     * @return this
     */
    CharStream readBlockSize(int blockSize);

    /**
     * Sets whether to treat a read operation from data source that returns 0 chars as an indication to break the read
     * loop, similar to reaching the end. A read operation returning 0 chars can occur in NIO. Default is
     * {@code false}.
     * <p>
     * This is an optional setting method.
     *
     * @param endOnZeroRead whether to treat a read operation from data source that returns 0 chars as an indication to
     *                      break the read loop
     * @return this
     */
    CharStream endOnZeroRead(boolean endOnZeroRead);

    /**
     * Adds an encoder for encoding which is an intermediate operation. When the data processing starts, all encoders
     * will be invoked after each read operation as following:
     * <pre>{@code
     *     read operation -> encoder-1 -> encoder-2 ... -> encoder-n -> terminal operation
     * }</pre>
     * All encoders can be considered as a combined encoder, of which behavior is equivalent to:
     * <pre>{@code
     *     CharBuffer chars = data;
     *     for (Encoder encoder : encoders) {
     *         chars = encoder.encode(chars, end);
     *     }
     *     return chars;
     * }</pre>
     * Size of passed data is uncertain, if it is the first encoder, the size may match the {@link #readBlockSize(int)}.
     * (except for the last reading, which may be smaller than the block size). To a certain size, try
     * {@link #encoder(Encoder, int)}.
     * <p>
     * Passed {@link CharBuffer} object, which is the first argument of {@link Encoder#encode(CharBuffer, boolean)}, can
     * be read-only (for example, when the source is an input stream), or writable (for example, when the source is a
     * char array or char buffer), and discarded after each invocation. The returned {@link CharBuffer} will also be
     * treated as read-only;
     * <p>
     * This is an optional setting method. This interface also provides helper encoder implementations:
     * <ul>
     *     <li>
     *         {@link #roundEncoder(Encoder, int)};
     *     </li>
     *     <li>
     *         {@link #bufferedEncoder(Encoder)};
     *     </li>
     *     <li>
     *         {@link #fixedSizeEncoder(Encoder, int)};
     *     </li>
     * </ul>
     *
     * @param encoder encoder for encoding data from read operation
     * @return this
     */
    CharStream encoder(Encoder encoder);

    /**
     * Adds an encoder for encoding which is an intermediate operation. This method is equivalent to adding a fixed-size
     * encoder by {@link #encoder(Encoder)} and {@link #fixedSizeEncoder(Encoder, int)}:
     * <pre>{@code
     *     return encoder(fixedSizeEncoder(encoder, size));
     * }</pre>
     *
     * @param encoder encoder for encoding data from read operation
     * @param size    specified fixed-size
     * @return this
     */
    default CharStream encoder(Encoder encoder, int size) {
        return encoder(fixedSizeEncoder(encoder, size));
    }

    /**
     * Starts data processing and writes processed data into specified destination, returns actual number of read
     * chars.
     * <p>
     * Specifically, if it is detected that the data source has already reached to the end before reading return -1. If
     * an error is thrown by an {@code encoder}, the error will be wrapped by {@link IOEncodingException} to be thrown,
     * use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(Appendable dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination, returns actual number of read
     * chars.
     * <p>
     * Specifically, if it is detected that the data source has already reached to the end before reading return -1. If
     * an error is thrown by an {@code encoder}, the error will be wrapped by {@link IOEncodingException} to be thrown,
     * use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(char[] dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination (starting from specified start index
     * up to specified length), returns actual number of read chars.
     * <p>
     * Specifically, if it is detected that the data source has already reached to the end before reading return -1. If
     * an error is thrown by an {@code encoder}, the error will be wrapped by {@link IOEncodingException} to be thrown,
     * use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a terminal method.
     *
     * @param dest   specified destination to be written
     * @param offset specified start index
     * @param length specified length
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(char[] dest, int offset, int length) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination, returns actual number of read
     * chars.
     * <p>
     * Specifically, if it is detected that the data source has already reached to the end before reading return -1. If
     * an error is thrown by an {@code encoder}, the error will be wrapped by {@link IOEncodingException} to be thrown,
     * use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(CharBuffer dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing without writing data into destination, returns actual number of read chars. This method is
     * typically used to produce side effects via the {@code encoder}.
     * <p>
     * Specifically, if it is detected that the data source has already reached to the end before reading return -1. If
     * an error is thrown by an {@code encoder}, the error will be wrapped by {@link IOEncodingException} to be thrown,
     * use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source is a buffer or stream, its position will be incremented by actual affected length.
     * <p>
     * This is a terminal method.
     *
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo() throws IOEncodingException, IORuntimeException;

    /**
     * Returns a reader which encompasses the entire data processing. The reader is lazy, read operations on the source
     * data are performed only as needed, and doesn't support mark/reset operations. The close method will close the
     * source if the source is also closable.
     * <p>
     * This is a terminal method.
     *
     * @return a reader which encompasses the entire data processing
     * @throws IORuntimeException thrown for any IO problems
     */
    Reader toReader() throws IORuntimeException;

    /**
     * Converts this char stream to byte stream with specified charset.
     * <p>
     * This is a setting method but this char stream still be invalid after current invocation.
     *
     * @return a new {@link CharStream} converted from this char stream with specified charset
     */
    default ByteStream toByteStream(Charset charset) {
        return ByteStream.from(JieIO.inputStream(toReader(), charset));
    }

    /**
     * Encoder for encoding data in data processing.
     */
    interface Encoder {

        /**
         * Encodes specified data and return the result.
         * <p>
         * Note specified data may be empty (but never null).
         *
         * @param data specified data
         * @param end  whether current encoding is the last invocation
         * @return result of encoding
         */
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
