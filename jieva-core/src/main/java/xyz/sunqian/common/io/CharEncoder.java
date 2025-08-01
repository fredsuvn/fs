package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsBuilder;

import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This interface represents the encoder to encode char data, from the specified data source, through zero or more
 * intermediate handlers, finally produces a result or side effect, and the result will be written into a destination
 * (if any). The following example shows a typical encoding:
 * <pre>{@code
 *     from(input)
 *         .readBlockSize(1024)
 *         .readLimit(1024 * 8)
 *         .handler(handler)
 *         .encodeTo(output);
 * }</pre>
 * <p>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods: set the encoding arguments to the current encoder before a terminal method is invoked;
 *     </li>
 *     <li>
 *         Terminal methods: produce the encoding result or side effect. Once a terminal method is invoked, the current
 *         encoder will be invalid, any further operations of the current encoder will be undefined;
 *     </li>
 * </ul>
 * The encoder is lazy, operations on the source data are only performed when a terminal method is invoked, and the
 * source data are consumed only as needed. Positions of the source and destination will increment by the actual read
 * and write number.
 *
 * @author sunqian
 */
public interface CharEncoder {

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified reader.
     *
     * @param src the specified reader
     * @return a new {@link CharEncoder} whose data source is the specified reader
     */
    static @Nonnull CharEncoder from(@Nonnull Reader src) {
        return new CharEncoderImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified array.
     *
     * @param src the specified array
     * @return a new {@link CharEncoder} whose data source is the specified array
     */
    static @Nonnull CharEncoder from(char @Nonnull [] src) {
        return new CharEncoderImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is a specified length of data from the specified array,
     * starting at the specified offset.
     *
     * @param src the specified array
     * @param off the specified offset
     * @param len the specified length
     * @return a new {@link CharEncoder} whose data source is a specified length of data from the specified array,
     * starting at the specified offset
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharEncoder from(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return new CharEncoderImpl(CharReader.from(src, off, len));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified char sequence.
     *
     * @param src the specified char sequence
     * @return a new {@link CharEncoder} whose data source is the specified char sequence
     */
    static @Nonnull CharEncoder from(@Nonnull CharSequence src) {
        return new CharEncoderImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified char sequence, starting at the specified
     * start index inclusive and ending at the specified end index exclusive.
     *
     * @param src   the specified char sequence
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @return a new {@link CharEncoder} whose data source is the specified char sequence, starting at the specified
     * start index inclusive and ending at the specified end index exclusive.
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharEncoder from(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        return new CharEncoderImpl(CharReader.from(src, start, end));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified buffer.
     *
     * @param src the specified buffer
     * @return a new {@link CharEncoder} whose data source is the specified buffer
     */
    static @Nonnull CharEncoder from(@Nonnull CharBuffer src) {
        return new CharEncoderImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharEncoder} whose data source is the specified {@link CharReader}.
     *
     * @param src the specified {@link CharReader}
     * @return a new {@link CharEncoder} whose data source is the specified {@link CharReader}
     */
    static @Nonnull CharEncoder from(@Nonnull CharReader src) {
        return new CharEncoderImpl(src);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to ensure that the size of the data passed to it
     * is a specified fixed size.
     * <p>
     * The wrapper splits the input data into blocks of a specified fixed size and sequentially passes each block to the
     * given handler. If any remaining data is insufficient to form a full block, it will be buffered and prepended to
     * the next input data in subsequent invocations. The combined data will be processed with the same block-splitting
     * logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, even if the remaining data is insufficient to form a full
     * block, it will still be passed to the given handler.
     *
     * @param handler the given handler
     * @param size    the specified fixed size, must {@code > 0}
     * @return a {@link Handler} wrapper that wraps the given handler to ensure that the size of the data passed to it
     * is a specified fixed size
     * @throws IllegalArgumentException if the specified fixed size {@code <= 0}
     */
    static @Nonnull Handler newFixedSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new CharEncoderImpl.FixedSizeHandler(handler, size);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to ensure that the size of the data passed to it
     * is a multiple of the specified size. The multiple is calculated as the largest multiple of the specified size not
     * exceeding the input length.
     * <p>
     * The wrapper truncates the input data to the calculated multiple length, and passes the truncated data to the
     * given handler. If any residual data remains after truncation (shorter than the specified size), it will be
     * buffered and prepended to the next input data in subsequent invocations. The combined data will be processed with
     * the same truncation logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, even if the residual data is shorter than the specified
     * size, it will still be passed to the given handler.
     *
     * @param handler the given handler
     * @param size    the specified size, must {@code > 0}
     * @return a {@link Handler} wrapper that wraps the given handler to ensure that the size of the data passed to it
     * is a multiple of the specified size
     * @throws IllegalArgumentException if the specified size {@code <= 0}
     */
    static @Nonnull Handler newMultipleSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
        IOChecker.checkSize(size);
        return new CharEncoderImpl.MultipleSizeHandler(handler, size);
    }

    /**
     * Returns a {@link Handler} wrapper that wraps the given handler to support buffering unconsumed data.
     * <p>
     * The wrapper passes input data to the given handler, after processing, the unconsumed data (if any) will be
     * buffered and prepended to the next input data in subsequent invocations. The combined data will be processed with
     * the same buffering logic.
     * <p>
     * Specifically, if the {@code end} flag is {@code true}, the unconsumed data (if any) will be discarded.
     *
     * @param handler the given handler
     * @return a {@link Handler} wrapper that wraps the given handler to support buffering unconsumed data
     */
    static @Nonnull Handler newBufferedHandler(@Nonnull Handler handler) {
        return new CharEncoderImpl.BufferedHandler(handler);
    }

    /**
     * Returns an empty {@link Handler} which does nothing but only returns the input data directly.
     *
     * @return an empty {@link Handler} which does nothing but only returns the input data directly
     */
    static @Nonnull Handler emptyHandler() {
        return CharEncoderImpl.EmptyHandler.SINGLETON;
    }

    /**
     * Sets the maximum number of chars to read from the data source.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit the maximum number of chars to read from the data source, must {@code >= 0}
     * @return this
     * @throws IllegalArgumentException if the limit is negative
     */
    @Nonnull
    CharEncoder readLimit(long readLimit) throws IllegalArgumentException;

    /**
     * Sets the number of chars for each read operation from the data source. The default is {@link IOKit#bufferSize()}.
     * This setting is typically used for block-based encoding.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of chars for each read operation from the data source, must {@code > 0}
     * @return this
     * @throws IllegalArgumentException if the block size is non-positive
     */
    @Nonnull
    CharEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException;

    /**
     * Adds the given handler to this encoder as the last handler.
     * <p>
     * When the encoding starts and there is at least one handler, the encoder reads a block of data from the data
     * source, then passes the data block to the first handler, then passes the result of the first handler to the next
     * handler, and so on. If any handler returns {@code null}, subsequent handlers in the chain are skipped for that
     * block. The last result of the last handler, which serves as the final result, will be written to the destination
     * if the final result is not empty. The encoder continues this read-handle loop, until it reaches the end of the
     * data source, or read number reaches the limit value set by {@link #readLimit(long)}. The logic is as follows:
     * <pre>{@code
     * while (true) {
     *     CharSegment block = readNextBlock(blockSize, readLimit);
     *     CharBuffer data = block.data();
     *     boolean end = block.end();
     *     for (Handler handler : handlers) {
     *         if (data == null) {
     *             break;
     *         }
     *         data = handler.handle(data, end);
     *     }
     *     if (notEmpty(data)) {
     *         writeTo(data);
     *     }
     *     if (end) {
     *         break;
     *     }
     * }
     * }</pre>
     * <p>
     * The block size is typically determined by {@link #readBlockSize(int)}. And the data blocks are typically read by
     * {@link CharReader#read(int)}, thus, their content may be shared with the data source. Changes to the data blocks
     * may be reflected in the data source.
     * <p>
     * Normally, for the data block passed to the handler, the unconsumed portion is ignored by the encoder and will not
     * be prepended to the next passed block. Use {@link #newBufferedHandler(Handler)} if it is needed.
     * <p>
     * This is an optional setting method. {@link CharEncoder} also provides some specific handler wrappers such as:
     * {@link #newFixedSizeHandler(Handler, int)} and {@link #newMultipleSizeHandler(Handler, int)}.
     *
     * @param handler the given handler
     * @return this
     */
    @Nonnull
    CharEncoder handler(@Nonnull Handler handler);

    /**
     * Starts data encoding and returns the actual number of chars read. If reaches the end of the data source and no
     * data is read, returns {@code -1}.
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method, and it is typically used to produce side effects. This method is equivalent to:
     * <pre>{@code
     * return encodeTo(IOKit.nullWriter());
     * }</pre>
     *
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default long encode() throws IORuntimeException {
        return encodeTo(IOKit.nullWriter());
    }

    /**
     * Starts data encoding, writes the encoding result to the specified destination, and returns the actual number of
     * chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
     * <p>
     * The positions of the data source and destination, if any, will be incremented by the actual read and write
     * numbers.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified destination
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    long encodeTo(@Nonnull Appendable dst) throws IORuntimeException;

    /**
     * Starts data encoding, writes the encoding result to the specified destination, and returns the actual number of
     * chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
     * <p>
     * The positions of the data source and destination, if any, will be incremented by the actual read and write
     * numbers.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified destination
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    long encodeTo(char @Nonnull [] dst) throws IORuntimeException;

    /**
     * Starts data encoding, writes the encoding result to the specified destination (starting at the specified offset),
     * and returns the actual number of chars read. If reaches the end of the data source and no data is read, returns
     * {@code -1}.
     * <p>
     * The positions of the data source and destination, if any, will be incremented by the actual read and write
     * numbers.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified destination
     * @param off the specified offset
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     * @throws IORuntimeException        if an I/O error occurs
     */
    long encodeTo(char @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Starts data encoding, writes the encoding result to the specified destination, and returns the actual number of
     * chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
     * <p>
     * The positions of the data source and destination, if any, will be incremented by the actual read and write
     * numbers.
     * <p>
     * This is a terminal method.
     *
     * @param dst the specified destination
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    long encodeTo(@Nonnull CharBuffer dst) throws IORuntimeException;

    /**
     * Starts the encoding, and returns the result as a new array. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     encodeTo(builder);
     *     return builder.toCharArray();
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the encoding result as a new array
     * @throws IORuntimeException if an I/O error occurs
     * @see #encodeTo(Appendable)
     */
    default char @Nonnull [] toCharArray() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        encodeTo(builder);
        return builder.toCharArray();
    }

    /**
     * Starts the encoding, and returns the result as a new buffer. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     encodeTo(builder);
     *     return builder.toCharBuffer();
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the encoding result as a new buffer
     * @throws IORuntimeException if an I/O error occurs
     * @see #encodeTo(Appendable)
     */
    default @Nonnull CharBuffer toCharBuffer() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        encodeTo(builder);
        return builder.toCharBuffer();
    }

    /**
     * Starts the encoding, and returns the result as a new string. This method is equivalent to:
     * <pre>{@code
     *     return new String(toCharArray());
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the encoding result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toCharArray()
     */
    @Nonnull
    String toString() throws IORuntimeException;

    /**
     * Returns this {@link ByteEncoder} as a {@link Reader}. The status and data source of this encoder are shared with
     * the returned {@link Reader}, and the returned {@link Reader}'s content represents the encoding result.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: Unsupported if any handler(s) exist, otherwise based on the data source;</li>
     *     <li>close: closes the data source;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     * <p>
     * This is a terminal method.
     *
     * @return this {@link ByteEncoder} as a {@link Reader}
     */
    @Nonnull
    Reader asReader();

    /**
     * Returns this {@link CharEncoder} as a {@link CharReader}. The status and data source of this encoder are shared
     * with the returned {@link CharReader}, and the returned {@link CharReader}'s content represents the encoding
     * result.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: Unsupported if any handler(s) exist, otherwise based on the data source;</li>
     *     <li>close: closes the data source;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     * <p>
     * This is a terminal method.
     *
     * @return this {@link CharEncoder} as a {@link CharReader}
     */
    @Nonnull
    CharReader asCharReader();

    /**
     * Converts this {@link CharEncoder} to a {@link ByteEncoder} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link ByteEncoder} converted from this {@link CharEncoder} with the specified charset
     */
    default @Nonnull ByteEncoder toByteEncoder(@Nonnull Charset charset) {
        return ByteEncoder.from(IOKit.newInputStream(asReader(), charset));
    }

    /**
     * Handler of the {@link CharEncoder}, to do the specific encoding work.
     *
     * @author sunqian
     */
    interface Handler {

        /**
         * Handles the specific encoding work with the input data, and returns the handling result. The input data will
         * not be {@code null} (but may be empty), but the return value can be {@code null}. If it returns {@code null},
         * the current handling loop will be broken (see {@link #handler(Handler)}).
         *
         * @param data the input data
         * @param end  whether the input data is the last and there is no more data
         * @return the result of the specific encoding work
         * @throws Exception if any problem occurs
         * @see #handler(Handler)
         */
        @Nullable
        CharBuffer handle(@Nonnull CharBuffer data, boolean end) throws Exception;
    }
}
