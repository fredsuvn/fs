package space.sunqian.fs.io;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.chars.CharsBuilder;

import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * This interface represents a processor to process char data, from the specified data source, through zero or more
 * intermediate operations, finally produces a result or side effect, and the result will be written into a destination
 * (if any). The following example shows a typical processing:
 * <pre>{@code
 * CharProcessor.from(input)
 *     .readBlockSize(1024)
 *     .readLimit(1024 * 8)
 *     .transformer(transformer)
 *     .processTo(output);
 * }</pre>
 * <p>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods: set the processing arguments to the current processor before a terminal method is invoked;
 *     </li>
 *     <li>
 *         Terminal methods: produce the processing result or side effect. Once a terminal method is invoked, the current
 *         processor will be invalid, any further operations of the current processor will be undefined;
 *     </li>
 * </ul>
 * The processor is lazy, operations on the source data are only performed when a terminal method is invoked, and the
 * source data are consumed only as needed. Positions of the source and destination will increment by the actual read
 * and write number.
 *
 * @author sunqian
 */
public interface CharProcessor {

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified reader.
     *
     * @param src the specified reader
     * @return a new {@link CharProcessor} whose data source is the specified reader
     */
    static @Nonnull CharProcessor from(@Nonnull Reader src) {
        return new CharProcessorImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified array.
     *
     * @param src the specified array
     * @return a new {@link CharProcessor} whose data source is the specified array
     */
    static @Nonnull CharProcessor from(char @Nonnull [] src) {
        return new CharProcessorImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is a specified length of data from the specified array,
     * starting at the specified offset.
     *
     * @param src the specified array
     * @param off the specified offset
     * @param len the specified length
     * @return a new {@link CharProcessor} whose data source is a specified length of data from the specified array,
     * starting at the specified offset
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharProcessor from(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return new CharProcessorImpl(CharReader.from(src, off, len));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified char sequence.
     *
     * @param src the specified char sequence
     * @return a new {@link CharProcessor} whose data source is the specified char sequence
     */
    static @Nonnull CharProcessor from(@Nonnull CharSequence src) {
        return new CharProcessorImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified char sequence, starting at the specified
     * start index inclusive and ending at the specified end index exclusive.
     *
     * @param src   the specified char sequence
     * @param start the specified start index inclusive
     * @param end   the specified end index exclusive
     * @return a new {@link CharProcessor} whose data source is the specified char sequence, starting at the specified
     * start index inclusive and ending at the specified end index exclusive.
     * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
     */
    static @Nonnull CharProcessor from(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        return new CharProcessorImpl(CharReader.from(src, start, end));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified buffer.
     *
     * @param src the specified buffer
     * @return a new {@link CharProcessor} whose data source is the specified buffer
     */
    static @Nonnull CharProcessor from(@Nonnull CharBuffer src) {
        return new CharProcessorImpl(CharReader.from(src));
    }

    /**
     * Returns a new {@link CharProcessor} whose data source is the specified {@link CharReader}.
     *
     * @param src the specified {@link CharReader}
     * @return a new {@link CharProcessor} whose data source is the specified {@link CharReader}
     */
    static @Nonnull CharProcessor from(@Nonnull CharReader src) {
        return new CharProcessorImpl(src);
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
    CharProcessor readLimit(long readLimit) throws IllegalArgumentException;

    /**
     * Sets the number of chars for each read operation from the data source. The default is {@link IOKit#bufferSize()}.
     * This setting is typically used for block-based processing.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of chars for each read operation from the data source, must {@code > 0}
     * @return this
     * @throws IllegalArgumentException if the block size is non-positive
     */
    @Nonnull
    CharProcessor readBlockSize(int readBlockSize) throws IllegalArgumentException;

    /**
     * Adds the given transformer to this processor as the last transformer.
     * <p>
     * When the processing starts and there is at least one transformer, the processor reads a block of data from the
     * data source, then passes the data block to the first transformer, then passes the result of the first transformer
     * to the next transformer, and so on. If any transformer returns {@code null}, the current transformation chain
     * will break. The last result of the last transformer, which serves as the final result, will be written to the
     * destination if the final result is not empty. The processor continues this read-transform loop, until it reaches
     * the end of the data source, or read number reaches the limit value set by {@link #readLimit(long)}. The logic is
     * as follows:
     * <pre>{@code
     * while (true) {
     *     CharSegment block = readNextBlock(blockSize, readLimit);
     *     CharBuffer data = block.data();
     *     boolean end = block.end();
     *     for (CharTransformer transformer : transformers) {
     *         if (data == null) {
     *             break;
     *         }
     *         data = transformer.transform(data, end);
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
     * Normally, for the data block passed to the transformer, the unconsumed portion is ignored by the processor and
     * will not be prepended to the next passed block. Use {@link CharTransformer#withBuffered(CharTransformer)} if it
     * is needed.
     * <p>
     * This is an optional setting method. {@link CharProcessor} also provides some specific transformer wrappers such
     * as: {@link CharTransformer#withFixedSize(CharTransformer, int)} and
     * {@link CharTransformer#withMultipleSize(CharTransformer, int)}.
     *
     * @param transformer the given transformer
     * @return this
     */
    @Nonnull
    CharProcessor transformer(@Nonnull CharTransformer transformer);

    /**
     * Starts data processing and returns the actual number of chars read. If reaches the end of the data source and no
     * data is read, returns {@code -1}.
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method, and it is typically used to produce side effects. This method is equivalent to:
     * <pre>{@code
     * return processeTo(IOKit.nullWriter());
     * }</pre>
     *
     * @return the actual number of chars read, or {@code -1} if reaches the end of the data source and no data is read
     * @throws IORuntimeException if an I/O error occurs
     */
    default long process() throws IORuntimeException {
        return processTo(IOKit.nullWriter());
    }

    /**
     * Starts data processing, writes the processing result to the specified destination, and returns the actual number
     * of chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
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
    long processTo(@Nonnull Appendable dst) throws IORuntimeException;

    /**
     * Starts data processing, writes the processing result to the specified destination, and returns the actual number
     * of chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
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
    long processTo(char @Nonnull [] dst) throws IORuntimeException;

    /**
     * Starts data processing, writes the processing result to the specified destination (starting at the specified
     * offset), and returns the actual number of chars read. If reaches the end of the data source and no data is read,
     * returns {@code -1}.
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
    long processTo(char @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException;

    /**
     * Starts data processing, writes the processing result to the specified destination, and returns the actual number
     * of chars read. If reaches the end of the data source and no data is read, returns {@code -1}.
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
    long processTo(@Nonnull CharBuffer dst) throws IORuntimeException;

    /**
     * Starts the processing, and returns the result as a new array. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     processeTo(builder);
     *     return builder.toCharArray();
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the processing result as a new array
     * @throws IORuntimeException if an I/O error occurs
     * @see #processTo(Appendable)
     */
    default char @Nonnull [] toCharArray() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        processTo(builder);
        return builder.toCharArray();
    }

    /**
     * Starts the processing, and returns the result as a new buffer. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     processeTo(builder);
     *     return builder.toCharBuffer();
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the processing result as a new buffer
     * @throws IORuntimeException if an I/O error occurs
     * @see #processTo(Appendable)
     */
    default @Nonnull CharBuffer toCharBuffer() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        processTo(builder);
        return builder.toCharBuffer();
    }

    /**
     * Starts the processing, and returns the result as a new string. This method is equivalent to:
     * <pre>{@code
     *     return new String(toCharArray());
     * }</pre>
     * <p>
     * The position of the data source, if any, will be incremented by the actual read number.
     * <p>
     * This is a terminal method.
     *
     * @return the processing result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toCharArray()
     */
    @Nonnull
    String toString() throws IORuntimeException;

    /**
     * Returns this {@link ByteProcessor} as a {@link Reader}. The status and data source of this processor are shared
     * with the returned {@link Reader}, and the returned {@link Reader}'s content represents the processing result.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: Unsupported if any transformer(s) exist, otherwise based on the data source;</li>
     *     <li>close: closes the data source;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     * <p>
     * This is a terminal method.
     *
     * @return this {@link ByteProcessor} as a {@link Reader}
     */
    @Nonnull
    Reader asReader();

    /**
     * Returns this {@link CharProcessor} as a {@link CharReader}. The status and data source of this processor are
     * shared with the returned {@link CharReader}, and the returned {@link CharReader}'s content represents the
     * processing result.
     * <p>
     * The result's support is as follows:
     * <ul>
     *     <li>mark/reset: Unsupported if any transformer(s) exist, otherwise based on the data source;</li>
     *     <li>close: closes the data source;</li>
     *     <li>thread safety: no;</li>
     * </ul>
     * <p>
     * This is a terminal method.
     *
     * @return this {@link CharProcessor} as a {@link CharReader}
     */
    @Nonnull
    CharReader asCharReader();

    /**
     * Converts this {@link CharProcessor} to a {@link ByteProcessor} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link ByteProcessor} converted from this {@link CharProcessor} with the specified charset
     */
    default @Nonnull ByteProcessor toByteEncoder(@Nonnull Charset charset) {
        return ByteProcessor.from(IOKit.newInputStream(asReader(), charset));
    }
}
