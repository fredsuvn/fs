package xyz.sunqian.common.io;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * Char stream is used to process char data, from specified data source, through zero or more intermediate operations,
 * and finally produces a result or side effect. The following example shows an encoding-then-writing operation:
 * <pre>{@code
 *     CharStream.from(input)
 *         .blockSize(1024)
 *         .encoder(encoder)
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
     * @throws IORuntimeException thrown for any problem
     */
    static CharStream from(char[] source, int offset, int length) throws IORuntimeException {
        if (offset == 0 && length == source.length) {
            return from(source);
        }
        try {
            CharBuffer buffer = CharBuffer.wrap(source, offset, length);
            return from(buffer);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
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
     * Returns a new {@link Encoder} to round input data for given encoder, typically used for the encoder which is not
     * applicable to the setting of {@link #blockSize(int)}.
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
     * Returns a new buffered {@link Encoder} for buffering remaining data of given encoder, typically used for the
     * encoder which is not applicable to the setting of {@link #blockSize(int)}.
     * <p>
     * The buffered encoder passes the input data to given encoder. Given encoder can only process an initial part of
     * data, the remaining data will be buffered by buffered encoder, and the buffered data will be passed at next
     * calling (if not end) along with the next input data, merged into one data.
     * <p>
     * The buffered encoder is not thread-safe.
     *
     * @param encoder given encoder
     * @return a new buffered {@link Encoder} for buffering remaining data of given encoder
     */
    static Encoder bufferedEncoder(Encoder encoder) {
        return new CharStreamImpl.BufferedEncoder(encoder);
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
     * This setting is typically used when the data source is an input stream, or intermediate operation is set, default
     * is {@link JieIO#BUFFER_SIZE}. When the terminal method starts, it ensures that the size of data passed to the
     * intermediate operation is the value set by this method, until the last read operation where the remaining source
     * data might be smaller than this value.
     * <p>
     * This is an optional setting method.
     *
     * @param blockSize the number of chars for each read operation from data source
     * @return this
     */
    CharStream blockSize(int blockSize);

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
     * Sets encoder for encoding data from read operation, the encoding is an intermediate operation.
     * <p>
     * When the data processing starts, the encoder will be invoked after each read operation, size of passed data is
     * specified by {@link #blockSize(int)} (except for the last reading, which may be smaller than the block size).
     * Passed {@link CharBuffer} object, which is the first argument of {@link Encoder#encode(CharBuffer, boolean)}, is
     * readonly. And the returned {@link CharBuffer} will also be treated as readonly;
     * <p>
     * This is an optional setting method. This interface provides helper encoder implementations such as:
     * {@link #roundEncoder(Encoder, int)}, {@link #bufferedEncoder(Encoder)}. To set more than one encoder, try
     * {@link #encoders(Iterable)}.
     *
     * @param encoder encoder for encoding data from read operation
     * @return this
     * @see #encoders(Iterable)
     * @see #roundEncoder(Encoder, int)
     * @see #bufferedEncoder(Encoder)
     */
    CharStream encoder(Encoder encoder);

    /**
     * Sets a list of encoders for encoding data from read operation, the encoding is an intermediate operation.
     * <p>
     * The behavior of this list of encoders is equivalent to the following code:
     * <pre>{@code
     *     return encoder((data, end) -> {
     *         CharBuffer bytes = data;
     *         for (Encoder encoder : encoders) {
     *             bytes = encoder.encode(bytes, end);
     *         }
     *         return bytes;
     *     });
     * }</pre>
     * That is, pass the {@link CharBuffer} to the first encoder, then pass the return value of the first encoder to the
     * second encoder, and so on.
     * <p>
     * Note the given list of encoders is used directly, any modification to the list will affect the encoding.
     *
     * @param encoders a list of encoders for encoding data from read operation
     * @return this
     * @see #encoder(Encoder)
     */
    default CharStream encoders(Iterable<Encoder> encoders) {
        return encoder((data, end) -> {
            CharBuffer chars = data;
            for (Encoder encoder : encoders) {
                chars = encoder.encode(chars, end);
            }
            return chars;
        });
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
     * Encoder for encoding data in data processing.
     */
    interface Encoder {

        /**
         * Encodes specified data and return the result.
         * <p>
         * The specified data is readonly, and the result will also be treated as readonly.
         *
         * @param data specified data
         * @param end  whether current encoding is the last invocation
         * @return result of encoding
         */
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
