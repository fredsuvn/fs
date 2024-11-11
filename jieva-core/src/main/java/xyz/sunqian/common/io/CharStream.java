package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

import java.io.Reader;
import java.nio.CharBuffer;
import java.util.function.Function;

/**
 * The interface represents a char stream for transferring char data, from specified source to specified destination.
 * There are two types of method in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set parameters and options for current stream before the transferring starts;
 *     </li>
 *     <li>
 *         Final methods, start do final things, such as {@link #transfer()}. And when a final method is finished,
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
     * Returns a new buffered {@link ByteStream.Encoder} for rounding and buffering given encoder, typically used for
     * the encoder which is not applicable to the setting of {@link #blockSize(int)}, or the input data needs to be
     * filtered.
     * <p>
     * The buffered encoder first filters input data if the filter is not {@code null} (skipped if it is {@code null}).
     * Then it rounds length of the data to a max value which is the largest multiple of the specified expected block
     * size, and pass the rounding data to given encoder. The remainder data will be buffered for next calling, until
     * the last calling (where the {@code end} is {@code true}). In last calling, buffered data and filtered/input data
     * will be passed to the given encoder.
     * <p>
     * The buffered encoder is not thread-safe.
     *
     * @param encoder           given encoder
     * @param expectedBlockSize specified expected block size
     * @param filter            the filter
     * @return a new buffered {@link ByteStream.Encoder} for rounding and buffering given encoder
     */
    static Encoder bufferedEncoder(
        Encoder encoder, int expectedBlockSize, @Nullable Function<CharBuffer, CharBuffer> filter) {
        return new CharStreamImpl.BufferedEncoder(encoder, expectedBlockSize, filter);
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
     * Sets max chars number to read. May be -1 if set to read to end, and this is default setting.
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
     * {@link #encoder(Encoder)}) is required for the transfer, default is {@link JieIO#BUFFER_SIZE}. This stream
     * ensures that the size of source data passed to the {@code encoding} is the value set by this method each times
     * when the transfer starts, until the last read where the remaining source data might be smaller than this value.
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
     *         If no byte was read, the {@link #transfer()} returns {@code 0} rather than {@code -1};
     *     </li>
     *     <li>
     *         If the {@code encoder} (specified by {@link #encoder(Encoder)}) is not {@code null}, the 2nd argument of
     *         {@link Encoder#encode(CharBuffer, boolean)} will be passed to {@code true};
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
     * Set the data encoder to encode the source data before it is written to the destination. The encoded data will
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
     * This is a setting method, and the interface provides helper encoder implementations:
     * {@link #bufferedEncoder(Encoder, int, Function)}.
     *
     * @param encoder data encoder
     * @return this
     * @see #bufferedEncoder(Encoder, int, Function)
     */
    CharStream encoder(Encoder encoder);

    /**
     * Starts transfer data from source into destination through this stream, returns the actual chars number that read
     * and success to transfer. Ensure that the remaining of destination is enough, otherwise a
     * {@link IORuntimeException} will be thrown and the actual read and written chars number is undefined.
     * <p>
     * If the {@code encoder} (see {@link #encoder(Encoder)}) is {@code null}, read number equals to written number.
     * Otherwise, the written number may not equal to read number, and this method returns actual read number.
     * Specifically, if it is detected that the data source has already reached to the end before starting, return -1.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @return the actual chars number that read and success to transfer
     * @throws IORuntimeException IO runtime exception
     */
    long transfer() throws IORuntimeException;

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
