package xyz.sunqian.common.io;

import xyz.sunqian.common.base.bytes.BytesBuilder;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;
import static xyz.sunqian.common.io.ByteEncoder.withFixedSize;

/**
 * Byte processor is used to process byte data, from the specified data source, through zero or more intermediate
 * operations (such as {@link #encoder(ByteEncoder)}), and finally produces a result or side effect. The following
 * example shows a read-encode-write operation:
 * <pre>{@code
 *     ByteProcessor.from(input)
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
 * Byte processor is lazy, operations on the source data are only performed when a terminal method is invoked, and
 * source data are consumed only as needed.
 *
 * @author sunqian
 */
public interface ByteProcessor {

    /**
     * Returns a new {@link ByteProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link ByteProcessor}
     */
    static ByteProcessor from(InputStream data) {
        return new ByteProcessorImpl(data);
    }

    /**
     * Returns a new {@link ByteProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link ByteProcessor}
     */
    static ByteProcessor from(byte[] data) {
        return new ByteProcessorImpl(data);
    }

    /**
     * Returns a new {@link ByteProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link ByteProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    static ByteProcessor from(byte[] data, int offset, int length) throws IndexOutOfBoundsException {
        checkOffsetLength(data.length, offset, length);
        if (offset == 0 && length == data.length) {
            return from(data);
        }
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
        return from(buffer);
    }

    /**
     * Returns a new {@link ByteProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link ByteProcessor}
     */
    static ByteProcessor from(ByteBuffer data) {
        return new ByteProcessorImpl(data);
    }

    /**
     * Sets the maximum number of bytes to read from the data source. This can be negative, meaning read until the end,
     * which is the default value.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit the maximum number of bytes to read from the data source
     * @return this
     */
    ByteProcessor readLimit(long readLimit);

    /**
     * Sets the number of bytes for each read operation from the data source.
     * <p>
     * This setting is typically used when the data source is an input stream, or intermediate operations are set,
     * default is {@link JieIO#bufferSize()}.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of bytes for each read operation from the data source
     * @return this
     */
    ByteProcessor readBlockSize(int readBlockSize);

    /**
     * Sets whether reading 0 byte from the data source should be treated as reaching to the end and break the read
     * loop. A read operation returning 0 byte can occur in NIO. Default is {@code false}.
     * <p>
     * This is an optional setting method.
     *
     * @param endOnZeroRead whether reading 0 byte from the data source should be treated as reaching to the end and
     *                      break the read loop
     * @return this
     */
    ByteProcessor endOnZeroRead(boolean endOnZeroRead);

    /**
     * Adds the given encoder for this processor. When the data processing starts, all encoders will be invoked after
     * each read operation as following:
     * <pre>{@code
     *     read-operation -> encoder-1 -> encoder-2 ... -> encoder-n -> terminal-operation
     * }</pre>
     * The encoder represents an intermediate operation, and all encoders can be considered as a combined encoder, of
     * which behavior is equivalent to:
     * <pre>{@code
     *     ByteBuffer bytes = data;
     *     for (Encoder encoder : encoders) {
     *         bytes = encoder.encode(bytes, end);
     *         if (bytes == null) {
     *             break;
     *         }
     *     }
     *     return bytes;
     * }</pre>
     * The passed input data, which is the first argument of the {@link ByteEncoder#encode(ByteBuffer, boolean)},
     * depends on the encoder's upstream. If the upstream is the data source of this processor (i.e., it is the first
     * encoder), the data's position is 0, limit equals to capacity, size is determined by the
     * {@link #readBlockSize(int)} method, and the data can be writeable if the source is an array or writeable buffer.
     * Otherwise, the data is the result of the previous encoder, and its abilities is defined by the previous encoder.
     * <p>
     * This is an optional setting method. There are also more specific encoder wrappers available, such as:
     * <ul>
     *     <li>
     *         For fixed-size: {@link ByteEncoder#withFixedSize(int, ByteEncoder)};
     *     </li>
     *     <li>
     *         For rounding size: {@link ByteEncoder#withRounding(int, ByteEncoder)};
     *     </li>
     *     <li>
     *         for buffering: {@link ByteEncoder#withBuffering(ByteEncoder)};
     *     </li>
     * </ul>
     *
     * @param encoder the given encoder
     * @return this
     */
    ByteProcessor encoder(ByteEncoder encoder);

    /**
     * Adds the given encoder wrapped by {@link ByteEncoder#withFixedSize(int, ByteEncoder)} for this processor. This
     * method is equivalent to:
     * <pre>{@code
     *     return encoder(withFixedSize(size, encoder));
     * }</pre>
     *
     * @param size    the specified fixed size for the {@link ByteEncoder#withFixedSize(int, ByteEncoder)}
     * @param encoder the given encoder
     * @return this
     * @throws IllegalArgumentException if the specified size is less than or equal to 0
     */
    default ByteProcessor encoder(int size, ByteEncoder encoder) throws IllegalArgumentException {
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
     * array or buffer, returns the stream from {@link JieIO#inStream(byte[])} or {@link JieIO#inStream(ByteBuffer)}.
     * Otherwise, the returned stream's read operations are performed only as needed, mark/reset operations are not
     * supported, and the {@code close()} method will close the source if the source is closable.
     * <p>
     * This is a terminal method.
     *
     * @return an input stream which represents and encompasses the entire data processing
     */
    InputStream toInputStream();

    /**
     * Converts this {@link ByteProcessor} to a {@link CharProcessor} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link CharProcessor} converted from this {@link ByteProcessor} with the specified charset
     */
    default CharProcessor toCharProcessor(Charset charset) {
        return CharProcessor.from(JieIO.reader(toInputStream(), charset));
    }
}
