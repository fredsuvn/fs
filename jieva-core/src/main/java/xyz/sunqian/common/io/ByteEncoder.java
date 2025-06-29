package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

/**
 * This interface represents the encoder to encode byte data, from the specified data source, through zero or more
 * intermediate handlers, finally produces a result or side effect, and the result will be written into a destination
 * (if any). The following example shows a typical encoding:
 * <pre>{@code
 *     ByteEncoder.from(input)
 *         .readBlockSize(1024)
 *         .readLimit(1024 * 8)
 *         .handler(handler)
 *         .writeTo(output);
 * }</pre>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods: to set the encoding arguments to the current encoder before a terminal method is invoked;
 *     </li>
 *     <li>
 *         Terminal methods: the current encoder starts the encoding and becomes invalid. Once a terminal method is
 *         invoked, any further operations to the encoder will be undefined;
 *     </li>
 * </ul>
 * The encoder is lazy, operations on the source data are only performed when a terminal method is invoked, and source
 * data are consumed only as needed. Positions of the source and destination will increment by the actual read and write
 * number.
 *
 * @author sunqian
 */
public interface ByteEncoder {

    /**
     * Returns a new {@link ByteEncoder} with the specified data source.
     *
     * @param src the specified data source
     * @return a new {@link ByteEncoder} with the specified data source
     */
    static ByteEncoder from(InputStream src) {
        return new ByteEncoderImpl(src);
    }

    /**
     * Returns a new {@link ByteEncoder} with the specified data source.
     *
     * @param src the specified data source
     * @return a new {@link ByteEncoder} with the specified data source
     */
    static ByteEncoder from(byte[] src) {
        return new ByteEncoderImpl(src, 0, src.length);
    }

    /**
     * Returns a new {@link ByteEncoder} with the specified data source, starting at the specified offset and up to the
     * specified length.
     *
     * @param src the specified data source
     * @param off the specified offset
     * @param len the specified length
     * @return a new {@link ByteEncoder} with the specified data source
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static ByteEncoder from(byte[] src, int off, int len) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.length, off, len);
        return new ByteEncoderImpl(src, off, len);
    }

    /**
     * Returns a new {@link ByteEncoder} with the specified data source.
     *
     * @param src the specified data source
     * @return a new {@link ByteEncoder} with the specified data source
     */
    static ByteEncoder from(ByteBuffer src) {
        return new ByteEncoderImpl(src);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to ensure the data received by the handler each
     * time is of a specified fixed size.
     * <p>
     * The wrapper splits the original data, from the encoder or previous handler, into the specified fixed size blocks
     * using sliced views via {@link ByteBuffer#slice()}, then passes each block to the given handler sequentially.
     * <p>
     * In the case that the original data, or the remainder data after splitting, is insufficient to form a full block:
     * <ul>
     *     <li>
     *         if the {@code end} flag of the original data is {@code true}, the wrapper will pass the remaining data to
     *         the given handler;
     *     </li>
     *     <li>
     *         otherwise the wrapper will buffer the remaining data internally until enough data are received in the
     *         future handling.
     *     </li>
     * </ul>
     *
     * @param handler the given handler
     * @param size    the specified fixed size
     * @return a {@link Handler} wrapper that wraps the given handler to ensure the data received by the handler each
     * time is of a specified fixed size
     * @throws IllegalArgumentException if the specified fixed size {@code <= 0}
     */
    static Handler newFixedSizeHandler(Handler handler, int size) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new ByteEncoderImpl.FixedSizeEncoder(handler, size);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to ensure the data received by the handler each
     * time is of a rounded size, which is the original data length rounded down to the maximum multiples of the
     * specified size.
     * <p>
     * The wrapper rounds down the original data, from the encoder or previous handler, to a block of the maximum
     * multiples of the specified size using sliced views via {@link ByteBuffer#slice()}, then passes the block to the
     * given handler.
     * <p>
     * In the case that the original data, or the remainder data after rounding, is insufficient to form a full block:
     * <ul>
     *     <li>
     *         if the {@code end} flag of the original data is {@code true}, the wrapper will pass the remaining data to
     *         the given handler;
     *     </li>
     *     <li>
     *         otherwise the wrapper will buffer the remaining data internally until enough data are received in the
     *         future handling.
     *     </li>
     * </ul>
     *
     * @param handler the given handler
     * @param size    the specified size
     * @return a {@link Handler} wrapper that wraps the given handler to ensure the data received by the handler each
     * time is of a rounded size, which is the original data length rounded down to the maximum multiples of the
     * specified size
     * @throws IllegalArgumentException if the specified size {@code <= 0}
     */
    static Handler newRoundedSizeHandler(Handler handler, int size) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new ByteEncoderImpl.RoundingEncoder(handler, size);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to support buffering unconsumed data.
     * <p>
     * Upon receiving data, the wrapper concatenates any previously buffered unconsumed data (if any) with the current
     * input, then passes the combined data to the given handler. After handling, if there exists remaining unconsumed
     * data and the {@code end} flag is false, the remaining unconsumed data will be buffered internally for the future
     * handling.
     *
     * @param handler the given handler
     * @return a {@link Handler} wrapper that wraps the given handler to support buffering unconsumed data
     */
    static Handler newBufferedHandler(Handler handler) {
        return new ByteEncoderImpl.BufferingEncoder(handler);
    }

    /**
     * Returns an empty {@link Handler} which does nothing but only returns the input data directly.
     *
     * @return an empty {@link Handler} which does nothing but only returns the input data directly
     */
    static Handler emptyHandler() {
        return ByteEncoderImpl.EmptyEncoder.SINGLETON;
    }

    /**
     * Sets the maximum number of bytes to read from the data source.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit the maximum number of bytes to read from the data source, must {@code >= 0}
     * @return this
     * @throws IllegalArgumentException if the limit is negative
     */
    ByteEncoder readLimit(long readLimit) throws IllegalArgumentException;

    /**
     * Sets the number of bytes for each read operation from the data source, the default is
     * {@link IOKit#bufferSize()}.
     * <p>
     * This setting is typically used for encoding in blocks.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of bytes for each read operation from the data source, must {@code > 0}
     * @return this
     * @throws IllegalArgumentException if the block size is negative
     */
    ByteEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException;

    /**
     * Adds the given handler to this encoder as the last handler.
     * <p>
     * When the encoding starts and exits at least one handler, the encoder reads a block of data from the data source,
     * then passes the data block to the first handler, then passes the result of the first handler (if it is
     * {@code null} then replaces it with an empty buffer) to the next handler, and so on. The last result of the last
     * handler, which is the final result, will be written to the destination if it is not empty. The logic is as
     * follows:
     * <pre>{@code
     * ByteSegment segment = nextSegment(blockSize);
     * ByteBuffer data = segment.data();
     * for (Handler handler : handlers) {
     *     data = handler.encode(data == null ? emptyBuffer() : data, segment.end());
     * }
     * if (notEmpty(data)) {
     *     writeTo(data);
     * }
     * }</pre>
     * Note that the data blocks are typically read by {@link ByteReader#read(int)} and its content may be shared with
     * the data source. The encoder ignores the remaining unconsumed data of the passed data passed, to buffer the
     * unconsumed data, try {@link #handlerWithBuffered(Handler)} or {@link #newBufferedHandler(Handler)}.
     * <p>
     * This is an optional setting method. And provides some specific handler wrappers such as:
     * {@link #newFixedSizeHandler(Handler, int)}, {@link #newRoundedSizeHandler(Handler, int)} and
     * {@link #newBufferedHandler(Handler)}.
     *
     * @param handler the given handler
     * @return this
     */
    ByteEncoder handler(Handler handler);

    /**
     * Adds the given handler wrapped by {@link #newFixedSizeHandler(Handler, int)} to this encoder. This method is
     * equivalent to:
     * <pre>{@code
     *     return handler(newFixedSizeHandler(handler, size));
     * }</pre>
     *
     * @param size    the specified fixed size for the {@link #newFixedSizeHandler(Handler, int)}, must {@code > 0}
     * @param handler the given handler
     * @return this
     * @throws IllegalArgumentException if the specified fixed size {@code <= 0}
     */
    default ByteEncoder handlerWithFixedSize(Handler handler, int size) throws IllegalArgumentException {
        return handler(newFixedSizeHandler(handler, size));
    }

    /**
     * Adds the given handler wrapped by {@link #newRoundedSizeHandler(Handler, int)} to this encoder. This method is
     * equivalent to:
     * <pre>{@code
     *     return handler(newRoundedSizeHandler(handler, size));
     * }</pre>
     *
     * @param size    the specified size for the {@link #newRoundedSizeHandler(Handler, int)}, must {@code > 0}
     * @param handler the given handler
     * @return this
     * @throws IllegalArgumentException if the specified size {@code <= 0}
     */
    default ByteEncoder handlerWithRoundedSize(Handler handler, int size) throws IllegalArgumentException {
        return handler(newRoundedSizeHandler(handler, size));
    }

    /**
     * Adds the given handler wrapped by {@link #newBufferedHandler(Handler)} to this encoder. This method is equivalent
     * to:
     * <pre>{@code
     *     return handler(newBufferedHandler(handler));
     * }</pre>
     *
     * @param handler the given handler
     * @return this
     */
    default ByteEncoder handlerWithBuffered(Handler handler) {
        return handler(newBufferedHandler(handler));
    }

    /**
     * Starts the encoding and returns the actual number of bytes encoded. This method has no destination for encoding
     * results to be written into, it is typically used to product the side effects.
     * <p>
     * This is a terminal method.
     *
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IORuntimeException if an I/O error occurs
     */
    long encode() throws IORuntimeException;

    /**
     * Starts the encoding and writes the result into the specified output stream, returns the actual number of bytes
     * encoded.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified output stream
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IORuntimeException if an I/O error occurs
     */
    long encodeTo(@Nonnull OutputStream dst) throws IORuntimeException;

    /**
     * Starts the encoding and writes the result into the specified channel, returns the actual number of bytes
     * encoded.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified channel
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IORuntimeException if an I/O error occurs
     */
    long encodeTo(@Nonnull WritableByteChannel dst) throws IORuntimeException;

    /**
     * Starts the encoding and writes the result into the specified array, returns the actual number of bytes encoded.
     * Note the remaining space of the array need to be sufficient.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified array
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IORuntimeException if an I/O error occurs
     */
    int encodeTo(byte @Nonnull [] dst) throws IORuntimeException;

    /**
     * Starts the encoding and writes the result into the specified array (starting at the specified offset), returns
     * the actual number of bytes encoded. Note the remaining space of the array need to be sufficient.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified array
     * @param off the specified offset of the array
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    int encodeTo(byte @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Starts the encoding and writes the result into the specified buffer, returns the actual number of bytes encoded.
     * Note the remaining space of the buffer need to be sufficient.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified buffer
     * @return the actual number of bytes encoded, may be {@code 0}
     * @throws IORuntimeException if an I/O error occurs
     */
    int encodeTo(@Nonnull ByteBuffer dst) throws IORuntimeException;

    /**
     * Starts the encoding, and returns the result as a new array. This method is equivalent to:
     * <pre>{@code
     *     BytesBuilder builder = new BytesBuilder();
     *     encodeTo(builder);
     *     return builder.toByteArray();
     * }</pre>
     * <p>
     * This is a terminal method.
     *
     * @return the encoding result as a new array
     * @throws IORuntimeException if an I/O error occurs
     * @see #encodeTo(OutputStream)
     */
    default byte[] toByteArray() throws IORuntimeException {
        BytesBuilder builder = new BytesBuilder();
        encodeTo(builder);
        return builder.toByteArray();
    }

    /**
     * Starts the encoding, and returns the result as a new buffer. This method is equivalent to:
     * <pre>{@code
     *     BytesBuilder builder = new BytesBuilder();
     *     encodeTo(builder);
     *     return builder.toByteBuffer();
     * }</pre>
     * <p>
     * This is a terminal method.
     *
     * @return the encoding result as a new buffer
     * @throws IORuntimeException if an I/O error occurs
     * @see #encodeTo(OutputStream)
     */
    default ByteBuffer toByteBuffer() throws IORuntimeException {
        BytesBuilder builder = new BytesBuilder();
        encodeTo(builder);
        return builder.toByteBuffer();
    }

    /**
     * Starts the encoding, and returns the result as a new string with {@link JieChars#defaultCharset()}. This method
     * is equivalent to:
     * <pre>{@code
     *     return new String(toByteArray(), CharKit.defaultCharset());
     * }</pre>
     * This is a terminal method.
     *
     * @return the encoding result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toByteArray()
     */
    String toString() throws IORuntimeException;

    /**
     * Starts the encoding, and returns the result as a new string with the specified charset. This method is equivalent
     * to:
     * <pre>{@code
     *     return new String(toByteArray(), charset);
     * }</pre>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return the encoding result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toByteArray()
     */
    default String toString(Charset charset) throws IORuntimeException {
        return new String(toByteArray(), charset);
    }

    /**
     * Returns an input stream which represents and encompasses this encoder and its encoding behavior.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: based on the data source;</li>
     *     <li>close: closes the data source;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     * <p>
     * This is a terminal method.
     *
     * @return an input stream which represents and encompasses this encoder and its encoding behavior
     */
    InputStream asInputStream();

    /**
     * Converts this {@link ByteEncoder} to a {@link CharEncoder} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link CharEncoder} converted from this {@link ByteEncoder} with the specified charset
     */
    default CharEncoder toCharEncoder(Charset charset) {
        return CharEncoder.from(IOKit.newReader(asInputStream(), charset));
    }

    /**
     * Handler of the {@link ByteEncoder}, to do the specific encoding work.
     *
     * @author sunqian
     */
    interface Handler {

        /**
         * Handles the specific encoding work with the input data, and returns the handling result. The input data will
         * not be {@code null} (but may be empty), but the return value can be {@code null}.
         * <p>
         * If return value is {@code null} and there exists a next handler, an empty buffer will be passed to the next
         * handler.
         *
         * @param data the input data
         * @param end  whether the input data is the last and there is no more data
         * @return the result of the specific encoding work
         * @throws Exception if any problem occurs
         */
        @Nullable
        ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) throws Exception;
    }
}
