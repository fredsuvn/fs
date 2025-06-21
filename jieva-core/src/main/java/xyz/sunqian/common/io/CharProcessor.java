package xyz.sunqian.common.io;

import xyz.sunqian.common.base.chars.CharsBuilder;

import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;
import static xyz.sunqian.common.io.CharEncoder.withFixedSize;

/**
 * Char processor is used to process char data, from the specified data source, through zero or more intermediate
 * operations (such as {@link #encoder(CharEncoder)}), and finally produces a result or side effect. The following
 * example shows a read-encode-write operation:
 * <pre>{@code
 *     CharProcessor.from(input)
 *         .readBlockSize(1024)
 *         .encoder(withFixedSize(64, (b, e) -> {
 *             //...
 *         }))
 *         .writeTo(output);
 * }</pre>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods: to set the data processing arguments before a terminal method has invoked;
 *     </li>
 *     <li>
 *         Terminal methods: to start the data processing. Note once a terminal method is invoked, the state of current
 *         processor will become undefined, and no safe guarantees for further operations;
 *     </li>
 * </ul>
 * Char processor is lazy, operations on the source data are only performed when a terminal method is invoked, and
 * source data are consumed only as needed.
 *
 * @author sunqian
 */
public interface CharProcessor {

    /**
     * Returns a new {@link CharProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharProcessor}
     */
    static CharProcessor from(Reader data) {
        return new CharProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharProcessor}
     */
    static CharProcessor from(char[] data) {
        return new CharProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link CharProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    static CharProcessor from(char[] data, int offset, int length) throws IndexOutOfBoundsException {
        checkOffsetLength(data.length, offset, length);
        if (offset == 0 && length == data.length) {
            return from(data);
        }
        CharBuffer buffer = CharBuffer.wrap(data, offset, length);
        return from(buffer);
    }

    /**
     * Returns a new {@link CharProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharProcessor}
     */
    static CharProcessor from(CharBuffer data) {
        return new CharProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharProcessor}
     */
    static CharProcessor from(CharSequence data) {
        return new CharProcessorImpl(data);
    }

    /**
     * Sets the maximum number of chars to read from the data source. This can be negative, meaning read until the end,
     * which is the default value.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit the maximum number of chars to read from the data source
     * @return this
     */
    CharProcessor readLimit(long readLimit);

    /**
     * Sets the number of chars for each read operation from the data source.
     * <p>
     * This setting is typically used when the data source is a reader, or intermediate operations are set, default is
     * {@link JieIO#bufferSize()}.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of chars for each read operation from the data source
     * @return this
     */
    CharProcessor readBlockSize(int readBlockSize);

    /**
     * Sets whether reading 0 char from the data source should be treated as reaching to the end and break the read
     * loop. A read operation returning 0 char can occur in NIO. Default is {@code false}.
     * <p>
     * This is an optional setting method.
     *
     * @param endOnZeroRead whether reading 0 char from the data source should be treated as reaching to the end and
     *                      break the read loop
     * @return this
     */
    CharProcessor endOnZeroRead(boolean endOnZeroRead);

    /**
     * Adds the given encoder for this processor. When the data processing starts, all encoders will be invoked after
     * each read operation as following:
     * <pre>{@code
     *     read-operation -> encoder-1 -> encoder-2 ... -> encoder-n -> terminal-operation
     * }</pre>
     * The encoder represents an intermediate operation, and all encoders can be considered as a combined encoder, of
     * which behavior is equivalent to:
     * <pre>{@code
     *     CharBuffer chars = data;
     *     for (Encoder encoder : encoders) {
     *         chars = encoder.encode(chars, end);
     *         if (chars == null) {
     *             break;
     *         }
     *     }
     *     return chars;
     * }</pre>
     * Size of passed data is uncertain. If it is the first encoder, the size may match the {@link #readBlockSize(int)}.
     * (except for the last reading, which may be smaller than the read block size).
     * <p>
     * The passed input data, which is the first argument of the {@link CharEncoder#encode(CharBuffer, boolean)},
     * depends on the encoder's upstream. If the upstream is the data source of this processor (i.e., it is the first
     * encoder), the data's position is 0, limit equals to capacity, size is determined by the
     * {@link #readBlockSize(int)} method, and the data can be writeable if the source is an array or writeable buffer.
     * Otherwise, the data is the result of the previous encoder, and its abilities is defined by the previous encoder.
     * <p>
     * This is an optional setting method. There are also more specific encoder wrappers available, such as:
     * <ul>
     *     <li>
     *         For fixed-size: {@link CharEncoder#withFixedSize(int, CharEncoder)};
     *     </li>
     *     <li>
     *         For rounding size: {@link CharEncoder#withRounding(int, CharEncoder)};
     *     </li>
     *     <li>
     *         for buffering: {@link CharEncoder#withBuffering(CharEncoder)};
     *     </li>
     * </ul>
     *
     * @param encoder the given encoder
     * @return this
     */
    CharProcessor encoder(CharEncoder encoder);

    /**
     * Adds the given encoder wrapped by {@link CharEncoder#withFixedSize(int, CharEncoder)} for this processor. This
     * method is equivalent to:
     * <pre>{@code
     *     return encoder(withFixedSize(size, encoder));
     * }</pre>
     *
     * @param size    the specified fixed size for the {@link CharEncoder#withFixedSize(int, CharEncoder)}
     * @param encoder the given encoder
     * @return this
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    default CharProcessor encoder(int size, CharEncoder encoder) throws IllegalArgumentException {
        return encoder(withFixedSize(size, encoder));
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
    long writeTo(Appendable dest) throws IORuntimeException;

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
    long writeTo(char[] dest) throws IORuntimeException;

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
    long writeTo(char[] dest, int offset, int length) throws IORuntimeException;

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
    long writeTo(CharBuffer dest) throws IORuntimeException;

    /**
     * Starts data processing, and returns the result as a new array. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     writeTo(builder);
     *     return builder.toCharArray();
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new array
     * @throws IORuntimeException if an I/O error occurs
     * @see #writeTo(Appendable)
     */
    default char[] toCharArray() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        writeTo(builder);
        return builder.toCharArray();
    }

    /**
     * Starts data processing, and returns the result as a new buffer. This method is equivalent to:
     * <pre>{@code
     *     CharsBuilder builder = new CharsBuilder();
     *     writeTo(builder);
     *     return builder.toCharBuffer();
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new buffer
     * @throws IORuntimeException if an I/O error occurs
     * @see #writeTo(Appendable)
     */
    default CharBuffer toCharBuffer() throws IORuntimeException {
        CharsBuilder builder = new CharsBuilder();
        writeTo(builder);
        return builder.toCharBuffer();
    }

    /**
     * Starts data processing, and returns the result as a new string. This method is equivalent to:
     * <pre>{@code
     *     return new String(toCharArray());
     * }</pre>
     * This is a terminal method.
     *
     * @return the processing result as a new string
     * @throws IORuntimeException if an I/O error occurs
     * @see #toCharArray()
     */
    String toString() throws IORuntimeException;

    /**
     * Returns a reader which represents and encompasses the entire data processing.
     * <p>
     * If there is no encoder in the processor: if the source is a reader, return the reader itself; if the source is an
     * array or buffer or char sequence, returns the reader from {@link JieIO#newReader(char[])} or
     * {@link JieIO#newReader(CharBuffer)} or {@link JieIO#newReader(CharSequence)}. Otherwise, the returned reader's read
     * operations are performed only as needed, mark/reset operations are not supported, and the {@code close()} method
     * will close the source if the source is closable.
     * <p>
     * This is a terminal method.
     *
     * @return a reader which represents and encompasses the entire data processing
     */
    Reader toReader();

    /**
     * Converts this {@link CharProcessor} to a {@link ByteProcessor} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link ByteProcessor} converted from this {@link CharProcessor} with the specified charset
     */
    default ByteProcessor toByteProcessor(Charset charset) {
        return ByteProcessor.from(JieIO.newInputStream(toReader(), charset));
    }
}
