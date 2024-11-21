package xyz.sunqian.common.io;

import java.io.InputStream;
import java.io.Reader;
import java.nio.CharBuffer;

/**
 * This interface represents a char data source, abstracting different types of data sources such as input stream, char
 * array, char buffer, etc. It is used to read data from the data source, encode it (if necessary), and then write to
 * the destination or as an {@link InputStream} (this process is called final operation). There are two types of method
 * in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set parameters and options before the final methods are invoked, such as
 *         {@link #readLimit(long)}, {@link #blockSize(int)};
 *     </li>
 *     <li>
 *         Final methods, used to start final operation, such as {@link #to(Appendable)}. When a final method is
 *         finished, state of current source will be undefined;
 *     </li>
 * </ul>
 * Final operation will keep reading/encoding/writing until the source reaches to the end or specified {@code readLimit}
 * (by {@link #readLimit(long)}). Thus, the destination must ensure it has enough remaining space.
 *
 * @author fredsuvn
 */
public interface CharSource {

    /**
     * Returns a new {@link CharSource} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharSource}
     */
    static CharSource from(Reader source) {
        return new CharSourceImpl(source);
    }

    /**
     * Returns a new {@link CharSource} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharSource}
     */
    static CharSource from(char[] source) {
        return new CharSourceImpl(source);
    }

    /**
     * Returns a new {@link CharSource} with specified data source, starting from the start index up to the specified
     * length.
     *
     * @param source specified data source
     * @param offset start index
     * @param length specified length
     * @return a new {@link CharSource}
     */
    static CharSource from(char[] source, int offset, int length) {
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
     * Returns a new {@link CharSource} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharSource}
     */
    static CharSource from(CharBuffer source) {
        return new CharSourceImpl(source);
    }

    /**
     * Returns a new {@link CharSource} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharSource}
     */
    static CharSource from(CharSequence source) {
        return new CharSourceImpl(source);
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
        return new CharSourceImpl.RoundEncoder(encoder, expectedBlockSize);
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
        return new CharSourceImpl.BufferedEncoder(encoder);
    }

    /**
     * Reads data from the data source, encode it (if necessary), and then write to specified destination, returns the
     * actual read number that is success to encode and write. If current operation fails, the read and written number
     * are undefined.
     * <p>
     * If there is no {@code encoder} (see {@link #encoder(Encoder)}/{@link #encoders(Iterable)}), actual read number
     * equals to written number. Otherwise, they may not equal. Specifically, if it is detected that the data source has
     * already reached to the end before reading, return -1. If an error is thrown by an {@code encoder}, the error will
     * be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @param dest the destination to be written
     * @return the actual read number
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long to(Appendable dest) throws IOEncodingException, IORuntimeException;

    /**
     * Reads data from the data source, encode it (if necessary), and then write to specified destination, returns the
     * actual read number that is success to encode and write. If current operation fails, the read and written number
     * are undefined.
     * <p>
     * If there is no {@code encoder} (see {@link #encoder(Encoder)}/{@link #encoders(Iterable)}), actual read number
     * equals to written number. Otherwise, they may not equal. Specifically, if it is detected that the data source has
     * already reached to the end before reading, return -1. If an error is thrown by an {@code encoder}, the error will
     * be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @param dest the destination to be written
     * @return the actual read number
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long to(char[] dest) throws IOEncodingException, IORuntimeException;

    /**
     * Reads data from the data source, encode it (if necessary), and then write to specified destination (starting from
     * the start index up to the specified length), returns the actual read number that is success to encode and write.
     * If current operation fails, the read and written number are undefined.
     * <p>
     * If there is no {@code encoder} (see {@link #encoder(Encoder)}/{@link #encoders(Iterable)}), actual read number
     * equals to written number. Otherwise, they may not equal. Specifically, if it is detected that the data source has
     * already reached to the end before reading, return -1. If an error is thrown by an {@code encoder}, the error will
     * be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @param dest   the destination to be written
     * @param offset start index
     * @param length specified length
     * @return the actual read number
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long to(char[] dest, int offset, int length) throws IOEncodingException, IORuntimeException;

    /**
     * Reads data from the data source, encode it (if necessary), and then write to specified destination, returns the
     * actual read number that is success to encode and write. If current operation fails, the read and written number
     * are undefined.
     * <p>
     * If there is no {@code encoder} (see {@link #encoder(Encoder)}/{@link #encoders(Iterable)}), actual read number
     * equals to written number. Otherwise, they may not equal. Specifically, if it is detected that the data source has
     * already reached to the end before reading, return -1. If an error is thrown by an {@code encoder}, the error will
     * be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or stream, its position will be incremented by actual affected
     * length.
     * <p>
     * This is a final method.
     *
     * @param dest the destination to be written
     * @return the actual read number
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long to(CharBuffer dest) throws IOEncodingException, IORuntimeException;

    /**
     * Sets max chars number to read. May be -1 if sets to read to end, and this is default setting.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit max chars number to read
     * @return this
     */
    CharSource readLimit(long readLimit);

    /**
     * Sets the chars number for each reading from data source.
     * <p>
     * This setting is typically used when the source is an input stream, or when {@code encoder} (see
     * {@link #encoder(Encoder)}/{@link #encoders(Iterable)}) is set for final operation, default is
     * {@link JieIO#BUFFER_SIZE}. When the final operation starts, it ensures that the size of data passed to the
     * {@code encoder} is the value set by this method, until the last read where the remaining source data might be
     * smaller than this value.
     * <p>
     * This is an optional setting method.
     *
     * @param blockSize the chars number for each reading from data source
     * @return this
     */
    CharSource blockSize(int blockSize);

    /**
     * Sets whether to treat reading 0 chars as the end of the source (possible for {@code NIO}). If it is set to
     * {@code false}, the reading will keep reading until reaches to end of the source. Default is {@code false}.
     * <p>
     * This is an optional setting method.
     *
     * @param endOnZeroRead whether break reading immediately when the number of chars read is 0
     * @return this
     */
    CharSource endOnZeroRead(boolean endOnZeroRead);

    /**
     * Sets the data encoder to encode the source data in final operation.
     * <p>
     * When the final operation starts, data encoder will be called after each data reading, passing the read data as
     * the first argument. The size of passed data is specified by {@link #blockSize(int)}, except for the last reading,
     * which may be smaller than the block size. The {@link CharBuffer} object, which is the first argument of
     * {@link Encoder#encode(CharBuffer, boolean)}, may be reused. And the returned {@link CharBuffer} will also be
     * treated as reused;
     * <p>
     * This is an optional setting method. To set more than one encoder, use {@link #encoders(Iterable)}, and the
     * interface provides helper encoder implementations: {@link #roundEncoder(Encoder, int)},
     * {@link #bufferedEncoder(Encoder)}.
     *
     * @param encoder data encoder
     * @return this
     * @see #encoders(Iterable)
     * @see #roundEncoder(Encoder, int)
     * @see #bufferedEncoder(Encoder)
     */
    CharSource encoder(Encoder encoder);

    /**
     * Sets a list of {@code encoders} ({@link #encoder(Encoder)}) to encode the source data in final operation.
     * <p>
     * In final operation, data will be passed into first encoder and get the result of encoding. If the result is not
     * empty, the result will be passed into next encoder until the last encoder. If one result is empty, next encoder
     * will not be called and that result will be the final result of the whole encoder chain. Otherwise, encoding
     * result from last encoder will be the final result.
     *
     * @param encoders a list of encoder
     * @return this
     * @see #encoder(Encoder)
     */
    CharSource encoders(Iterable<Encoder> encoders);

    /**
     * Encoder to encode the data in the final operation.
     */
    interface Encoder {

        /**
         * Encodes specified data. The returned buffer's position will be zero and its limit will be the number of
         * encoding chars.
         *
         * @param data specified data
         * @param end  whether the current encoding is the last call
         * @return result of encoding
         */
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
