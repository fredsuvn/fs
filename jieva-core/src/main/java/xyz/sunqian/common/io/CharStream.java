package xyz.sunqian.common.io;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * The interface represents a char stream for transferring char data, from specified source to specified destination.
 * There are two types of method in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set parameters and options for current stream before the transferring starts;
 *     </li>
 *     <li>
 *         Final methods, start do final things, such as {@link #start()}. And when a final method is finished,
 *         state of current stream instance will be invalid and undefined;
 *     </li>
 * </ul>
 * The stream will keep reading and writing until the source reaches to the end or specified {@code readLimit} (by
 * {@link #readLimit(long)}). Therefore, the destination must ensure it has sufficient remaining space.
 *
 * @author fredsuvn
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
     */
    static CharStream from(char[] source, int offset, int length) {
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
     * Returns a new round {@link Encoder} for rounding input data of given encoder, typically used for the encoder
     * which is not applicable to the setting of {@link #blockSize(int)}.
     * <p>
     * The round encoder rounds length of the data to a max value which is the largest multiple of the specified
     * expected block size, and passes the rounding data to given encoder. The remainder data will be buffered for next
     * calling, until the last calling (where the {@code end} is {@code true}). In last calling, buffered data and input
     * data will be merged and passed to the given encoder.
     * <p>
     * The round encoder is not thread-safe.
     *
     * @param encoder           given encoder
     * @param expectedBlockSize specified expected block size
     * @return a new round {@link Encoder} for rounding input data of given encoder
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
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(Appendable dest);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(char[] dest);

    /**
     * Sets the destination to be written, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination to be written
     * @param offset start index
     * @param length specified length
     * @return this
     */
    CharStream to(char[] dest, int offset, int length);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(CharBuffer dest);

    /**
     * Sets max chars number to read. May be -1 if sets to read to end, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readLimit max chars number to read
     * @return this
     */
    CharStream readLimit(long readLimit);

    /**
     * Sets the bytes number for each reading from data source.
     * <p>
     * This setting is typically used when the source is an input stream, or when {@code encoding} (see
     * {@link #encoder(Encoder)}/{@link #encoders(Iterable)}) is required for the transfer, default is
     * {@link JieIO#BUFFER_SIZE}. This stream ensures that the size of source data passed to the {@code encoding} is the
     * value set by this method each times when the transfer starts, until the last read where the remaining source data
     * might be smaller than this value.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the bytes number for each reading from data source
     * @return this
     */
    CharStream blockSize(int blockSize);

    /**
     * Sets whether end the transfer operation if a read operation returns zero bytes (possible for {@code NIO}). If it
     * is set to {@code false}, the reading will continue until reaches to end of the source. Default is {@code false}.
     * <p>
     * If the transfer is ended in this scenario:
     * <ul>
     *     <li>
     *         If no byte was read, the {@link #start()} returns {@code 0} rather than {@code -1};
     *     </li>
     *     <li>
     *         If the {@code encoders} (specified by {@link #encoder(Encoder)}/{@link #encoders(Iterable)}) is not
     *         {@code null}, the 2nd argument of {@link Encoder#encode(CharBuffer, boolean)} will be passed to
     *         {@code true};
     *     </li>
     * </ul>
     * <p>
     * This is a setting method.
     *
     * @param endOnZeroRead whether break reading immediately when the number of bytes read is 0
     * @return this
     */
    CharStream endOnZeroRead(boolean endOnZeroRead);

    /**
     * Sets the data encoder to encode the source data before it is written to the destination. The encoded data will
     * then be written to the destination. This setting is optional; if not set, the source data will be written
     * directly to the destination.
     * <p>
     * When the transfer starts, data encoder will be called after each data read, passing the read data as the first
     * argument. The value returned by the encoder will be the actual data written to the destination. The length of
     * data read in each read operation is specified by {@link #blockSize(int)}, and the remaining data of last read may
     * be smaller than this value.
     * <p>
     * Note:
     * <ul>
     *     <li>
     *         The {@link CharBuffer} passed as the argument is not always a new instance or new allocated, it may be
     *         reused. And the returned {@link CharBuffer} will also be treated as potentially reusable;
     *     </li>
     *     <li>
     *         The encoder can be either a stateless function or a stateful, non-thread-safe object, depending on the
     *         implementation.
     *     </li>
     * </ul>
     * <p>
     * This is a setting method. To set more than one encoder, use {@link #encoders(Iterable)}, and the interface
     * provides helper encoder implementations: {@link #roundEncoder(Encoder, int)}, {@link #bufferedEncoder(Encoder)}.
     *
     * @param encoder data encoder
     * @return this
     * @see #encoders(Iterable)
     * @see #roundEncoder(Encoder, int)
     * @see #bufferedEncoder(Encoder)
     */
    CharStream encoder(Encoder encoder);

    /**
     * Sets a list of {@code encoder} ({@link #encoder(Encoder)}) for this byte stream.
     * <p>
     * The stream will pass the input data into first encoder and get the result of encoding, if the result is not empty
     * then pass it into next encoder until the last encoder. If one result is empty, next encoder will not be called
     * and that result will be returned as final result of the whole encoder chain. Otherwise, encoding result from last
     * encoder will be returned.
     *
     * @param encoders a list of encoder
     * @return this
     * @see #encoder(Encoder)
     */
    CharStream encoders(Iterable<Encoder> encoders);

    /**
     * Starts transfer from source into destination through this stream, returns the actual chars number that read and
     * success to transfer. Ensure that the remaining of destination is enough, otherwise a {@link IORuntimeException}
     * will be thrown. If current transfer fails, the read and written number are undefined.
     * <p>
     * If the {@code encoders} (see {@link #encoder(Encoder)}/{@link #encoders(Iterable)}) is {@code null}, read number
     * equals to written number. Otherwise, the written number may not equal to read number, and this method returns
     * actual read number. Specifically, if it is detected that the data source has already reached to the end before
     * starting, return -1. If an error is thrown by the {@code encoders}, the error will be wrapped by
     * {@link IOEncodingException} to throw, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @return the actual chars number that read and success to transfer
     * @throws IOEncodingException to wrap the error thrown by encoders
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long start() throws IOEncodingException, IORuntimeException;

    /**
     * Encoder to encode the char data in the stream transfer processing.
     */
    interface Encoder {

        /**
         * Encodes specified data in chars. The returned buffer's position will be zero and its limit will be the number
         * of encoding chars.
         *
         * @param data specified data
         * @param end  whether the current encoding is the last call (source has been read to the end)
         * @return result of encoding
         */
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
