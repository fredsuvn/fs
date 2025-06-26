package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static xyz.sunqian.common.io.ByteEncoder.Handler.withFixedSize;

/**
 * This interface represents the encoder to encode byte data, from the specified data source, through zero or more
 * intermediate handlers, finally produces a result or side effect. The following example shows a typical encoding:
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
 * The encoder is lazy, operations on the source data are only performed when a terminal method is invoked, and
 * source data are consumed only as needed.
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
        return new ByteEncoderImpl(src);
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
        if (off == 0 && len == src.length) {
            return from(src);
        }
        ByteBuffer buffer = ByteBuffer.wrap(src, off, len);
        return from(buffer);
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
     * When the encoding starts and the handlers are also added, the encoder reads a block of data from the data source,
     * then passes the data block to the first handler, then passes the result of the first handler to the next handler,
     * and so on. The last result of the last handler, which is the final result, will be written to the destination.
     * The logic is as follows:
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
     * <p>
     * This is an optional setting method. There are also more specific encoder wrappers available, such as:
     * <ul>
     *     <li>
     *         For fixed-size: {@link Handler#withFixedSize(int, Handler)};
     *     </li>
     *     <li>
     *         For rounding size: {@link Handler#withRounding(int, Handler)};
     *     </li>
     *     <li>
     *         for buffering: {@link Handler#withBuffering(Handler)};
     *     </li>
     * </ul>
     *
     * @param encoder the given encoder
     * @return this
     */
    ByteEncoder handler(Handler encoder);

    /**
     * Adds the given encoder wrapped by {@link Handler#withFixedSize(int, Handler)} for this processor. This method is
     * equivalent to:
     * <pre>{@code
     *     return encoder(withFixedSize(size, encoder));
     * }</pre>
     *
     * @param size    the specified fixed size for the {@link Handler#withFixedSize(int, Handler)}
     * @param encoder the given encoder
     * @return this
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    default ByteEncoder handler(int size, Handler encoder) throws IllegalArgumentException {
        return handler(withFixedSize(size, encoder));
    }

    /**
     * Starts data processing and returns the actual number of bytes processed. The positions of the source and
     * destination, if any, will be incremented by the actual length of the affected data.
     * <p>
     * This is a terminal method, and it is typically used to product side effects.
     *
     * @return the actual number of bytes processed
     * @throws IORuntimeException if an I/O error occurs
     */
    long process() throws IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. The positions of the source and destination, if any, will be incremented by the actual length of the
     * affected data.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws IORuntimeException if an I/O error occurs
     */
    long writeTo(OutputStream dest) throws IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. The position of the source, if any, will be incremented by the actual length of the affected data.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws IORuntimeException if an I/O error occurs
     */
    long writeTo(byte[] dest) throws IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination (starting from the specified start index
     * up to the specified length), and returns the actual number of bytes processed. The position of the source, if
     * any, will be incremented by the actual length of the affected data.
     * <p>
     * This is a terminal method.
     *
     * @param dest   the specified destination
     * @param offset the specified start index
     * @param length the specified length
     * @return the actual number of bytes processed
     * @throws IORuntimeException if an I/O error occurs
     */
    long writeTo(byte[] dest, int offset, int length) throws IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. The positions of the source and destination, if any, will be incremented by the actual length of the
     * affected data.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws IORuntimeException if an I/O error occurs
     */
    long writeTo(ByteBuffer dest) throws IORuntimeException;

    /**
     * Starts data processing, and returns the result as a new array. This method is equivalent to:
     * <pre>{@code
     *     BytesBuilder builder = new BytesBuilder();
     *     writeTo(builder);
     *     return builder.toByteArray();
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new array
     * @throws IORuntimeException if an I/O error occurs
     * @see #writeTo(OutputStream)
     */
    default byte[] toByteArray() throws IORuntimeException {
        BytesBuilder builder = new BytesBuilder();
        writeTo(builder);
        return builder.toByteArray();
    }

    /**
     * Starts data processing, and returns the result as a new buffer. This method is equivalent to:
     * <pre>{@code
     *     BytesBuilder builder = new BytesBuilder();
     *     writeTo(builder);
     *     return builder.toByteBuffer();
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new buffer
     * @throws IORuntimeException if an I/O error occurs
     * @see #writeTo(OutputStream)
     */
    default ByteBuffer toByteBuffer() throws IORuntimeException {
        BytesBuilder builder = new BytesBuilder();
        writeTo(builder);
        return builder.toByteBuffer();
    }

    /**
     * Starts data processing, and returns the result as a new string with {@link JieChars#defaultCharset()}. This
     * method is equivalent to:
     * <pre>{@code
     *     return new String(toByteArray(), JieChars.defaultCharset());
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toByteArray()
     */
    String toString() throws IORuntimeException;

    /**
     * Starts data processing, and returns the result as a new string with the specified charset. This method is
     * equivalent to:
     * <pre>{@code
     *     return new String(toByteArray(), charset);
     * }</pre>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return the processing result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toByteArray()
     */
    default String toString(Charset charset) throws IORuntimeException {
        return new String(toByteArray(), charset);
    }

    /**
     * Returns an input stream which represents and encompasses the entire data processing.
     * <p>
     * If there is no encoder in the processor: if the source is a stream, return the stream itself; if the source is an
     * array or buffer, returns the stream from {@link IOKit#newInputStream(byte[])} or
     * {@link IOKit#newInputStream(ByteBuffer)}. Otherwise, the returned stream's read operations are performed only as
     * needed, mark/reset operations are not supported, and the {@code close()} method will close the source if the
     * source is closable.
     * <p>
     * This is a terminal method.
     *
     * @return an input stream which represents and encompasses the entire data processing
     */
    InputStream toInputStream();

    /**
     * Converts this {@link ByteEncoder} to a {@link CharEncoder} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link CharEncoder} converted from this {@link ByteEncoder} with the specified charset
     */
    default CharEncoder toCharEncoder(Charset charset) {
        return CharEncoder.from(IOKit.newReader(toInputStream(), charset));
    }

    /**
     * This interface represents an encoder, which is a type of intermediate operation for {@link ByteEncoder}.
     *
     * @author sunqian
     */
    interface Handler {

        /**
         * Encodes the specified input data and return the result. The specified input data will not be null (but may be
         * empty), and the return value can be null.
         * <p>
         * If it returns null, the next encoder will not be invoked and the encoding chain will be interrupted; If it
         * returns an empty buffer, the encoding chain will continue.
         *
         * @param data the specified input data
         * @param end  whether the current encoding is the last invocation
         * @return the result of encoding
         * @throws Exception thrown for any problems
         */
        @Nullable
        ByteBuffer encode(ByteBuffer data, boolean end) throws Exception;

        /**
         * Returns a wrapper {@link Handler} that wraps the given encoder to encode data in fixed-size blocks.
         * <p>
         * The wrapper splits the original data into blocks of the specified fixed size by {@link ByteBuffer#slice()},
         * and each block will be passed to the given encoder sequentially. The remainder data, which is insufficient to
         * form a full block, will be buffered until enough data is received. The content of the block is shared with
         * the sub-content of the original data if, and only if, it is sliced by {@link ByteBuffer#slice()}. If a block
         * is formed by concatenating multiple original data pieces, its content is not shared.
         * <p>
         * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may
         * be less than the specified fixed size.
         *
         * @param size    the specified fixed size
         * @param encoder the given encoder
         * @return a wrapper {@link Handler} that wraps the given encoder to encode data in fixed-size blocks
         * @throws IllegalArgumentException if the specified size is less than or equal to 0
         */
        static Handler withFixedSize(int size, Handler encoder) throws IllegalArgumentException {
            return new ByteEncoderImpl.FixedSizeEncoder(encoder, size);
        }

        /**
         * Returns a wrapper {@link Handler} that wraps the given encoder to encode data in rounding down blocks.
         * <p>
         * The wrapper rounds down the size of the original data to the largest multiple ({@code >= 1}) of the specified
         * size that does not exceed it, and splits the original data into the block of the rounded size by
         * {@link ByteBuffer#slice()}. The block will be passed to the given encoder. The remainder data, of which size
         * is less than one multiple of the specified size, will be buffered until enough data is received. The content
         * of the block is shared with the sub-content of the original data if, and only if, it is sliced by
         * {@link ByteBuffer#slice()}. If a block is formed by concatenating multiple original data pieces, its content
         * is not shared.
         * <p>
         * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may
         * be less than one multiple of the specified size.
         *
         * @param size    the specified size
         * @param encoder the given encoder
         * @return a wrapper {@link Handler} that wraps the given encoder to encode data in rounding down blocks
         * @throws IllegalArgumentException if the specified size is less than or equal to 0
         */
        static Handler withRounding(int size, Handler encoder) throws IllegalArgumentException {
            return new ByteEncoderImpl.RoundingEncoder(encoder, size);
        }

        /**
         * Returns a wrapper {@link Handler} that wraps the given encoder to support buffering unconsumed data.
         * <p>
         * When the wrapper is invoked, if no buffered data exists, the original data is directly passed to the given
         * encoder; if buffered data exists, a new buffer concatenating the buffered data followed by the original data
         * is passed to the given. After the execution of the given encoder, any unconsumed data remaining in passed
         * buffer will be buffered.
         * <p>
         * Specially, in the last invocation (when {@code end == true}) of the wrapper, no data buffered.
         *
         * @param encoder the given encoder
         * @return a wrapper {@link Handler} that wraps the given encoder to support buffering unconsumed data
         */
        static Handler withBuffering(Handler encoder) {
            return new ByteEncoderImpl.BufferingEncoder(encoder);
        }

        /**
         * Returns an empty {@link Handler} which does nothing but only returns the input data directly.
         *
         * @return an empty {@link Handler} which does nothing but only returns the input data directly
         */
        static Handler emptyEncoder() {
            return ByteEncoderImpl.EmptyEncoder.SINGLETON;
        }
    }
}
