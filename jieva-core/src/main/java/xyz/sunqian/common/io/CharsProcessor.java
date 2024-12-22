package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Char processor is used to process char data, from specified data source, through zero or more intermediate
 * operations, and finally produces a result or side effect. The following example shows an encoding-then-writing
 * operation:
 * <pre>{@code
 *     CharStream.from(input)
 *         .readBlockSize(1024)
 *         .encoder(en1)
 *         .encoder(en2, 64)
 *         .writeTo(output);
 * }</pre>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods, to set processing arguments before a terminal method has invoked;
 *     </li>
 *     <li>
 *         Terminal methods, to start the data processing, once a terminal method is invoked, the state of current
 *         processor becomes undefined, and no safe guarantees for further operations;
 *     </li>
 * </ul>
 * Char processor is lazy, operations on the source data are only performed when a terminal method is invoked, and source
 * data are consumed only as needed.
 *
 * @author sunqian
 */
public interface CharsProcessor {

    /**
     * Returns a new {@link CharsProcessor} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharsProcessor}
     */
    static CharsProcessor from(Reader source) {
        return new CharsProcessorImpl(source);
    }

    /**
     * Returns a new {@link CharsProcessor} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharsProcessor}
     */
    static CharsProcessor from(char[] source) {
        return new CharsProcessorImpl(source);
    }

    /**
     * Returns a new {@link CharsProcessor} with specified data source, starting from the start index up to the
     * specified length.
     *
     * @param source specified data source
     * @param offset start index
     * @param length specified length
     * @return a new {@link CharsProcessor}
     * @throws IndexOutOfBoundsException thrown bounds problem
     */
    static CharsProcessor from(char[] source, int offset, int length) throws IndexOutOfBoundsException {
        IOMisc.checkReadBounds(source, offset, length);
        if (offset == 0 && length == source.length) {
            return from(source);
        }
        CharBuffer buffer = CharBuffer.wrap(source, offset, length);
        return from(buffer);
    }

    /**
     * Returns a new {@link CharsProcessor} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharsProcessor}
     */
    static CharsProcessor from(CharBuffer source) {
        return new CharsProcessorImpl(source);
    }

    /**
     * Returns a new {@link CharsProcessor} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharsProcessor}
     */
    static CharsProcessor from(CharSequence source) {
        return new CharsProcessorImpl(source);
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
    CharsProcessor readLimit(long readLimit);

    /**
     * Sets the number of chars for each read operation from data source.
     * <p>
     * This setting is typically used when the data source is a reader, or intermediate operations are set, default is
     * {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of chars for each read operation from data source
     * @return this
     */
    CharsProcessor readBlockSize(int readBlockSize);

    /**
     * Sets whether reading 0 char from data source should be treated as reaching to the end and break the read loop. A
     * read operation returning 0 char can occur in NIO. Default is {@code false}.
     * <p>
     * This is an optional setting method.
     *
     * @param endOnZeroRead whether reading 0 char from data source should be treated as reaching to the end and break
     *                      the read loop
     * @return this
     */
    CharsProcessor endOnZeroRead(boolean endOnZeroRead);

    /**
     * Adds an encoder for this processor. When the data processing starts, all encoders will be invoked after each read
     * operation as following:
     * <pre>{@code
     *     read operation -> encoder-1 -> encoder-2 ... -> encoder-n -> terminal operation
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
     * Size of passed data is uncertain, if it is the first encoder, the size may match the {@link #readBlockSize(int)}.
     * (except for the last reading, which may be smaller than the read block size). To a certain size, try
     * {@link #encoder(int, Encoder)}.
     * <p>
     * Passed {@link CharBuffer} object, which is the first argument of {@link Encoder#encode(CharBuffer, boolean)}, can
     * be read-only (for example, when the source is a reader), or writable (for example, when the source is a char
     * array or char buffer), and discarded after each invocation. The returned {@link CharBuffer} will also be treated
     * as read-only;
     * <p>
     * This is an optional setting method. Additionally, there are also more helper methods:
     * <ul>
     *     <li>
     *         For fixed-size: {@link #encoder(int, Encoder)}, {@link JieIO#fixedSizeEncoder(int, Encoder)};
     *     </li>
     *     <li>
     *         For round size: {@link #roundEncoder(int, Encoder)}, {@link JieIO#roundEncoder(int, Encoder)};
     *     </li>
     *     <li>
     *         for buffering: {@link #bufferedEncoder(Encoder)}, {@link JieIO#bufferedEncoder(Encoder)};
     *     </li>
     * </ul>
     *
     * @param encoder encoder for encoding data from read operation
     * @return this
     */
    CharsProcessor encoder(Encoder encoder);

    /**
     * Adds an encoder for this processor. This is a special type of {@link #encoder(Encoder)}, typically used for the
     * encoder which requires consuming data of fixed-size. The behavior of this method is equivalent to:
     * <pre>{@code
     *     return encoder(JieIO.fixedSizeEncoder(size, encoder));
     * }</pre>
     *
     * @param size    specified fixed-size
     * @param encoder encoder for encoding data from read operation
     * @return this
     */
    default CharsProcessor encoder(int size, Encoder encoder) {
        return encoder(JieIO.fixedSizeEncoder(size, encoder));
    }

    /**
     * Adds an encoder for this processor. This is a special type of {@link #encoder(Encoder)}, typically used for the
     * encoder which requires consuming data in multiples of specified size. The behavior of this method is equivalent
     * to:
     * <pre>{@code
     *     return encoder(JieIO.roundEncoder(size, encoder));
     * }</pre>
     *
     * @param size    specified size
     * @param encoder encoder for encoding data from read operation
     * @return this
     */
    default CharsProcessor roundEncoder(int size, Encoder encoder) {
        return encoder(JieIO.roundEncoder(size, encoder));
    }

    /**
     * Adds an encoder for this processor. This is a special type of {@link #encoder(Encoder)}, typically used for the
     * encoder which may not fully consume current passed data, requires buffering and consuming in next invocation. The
     * behavior of this method is equivalent to:
     * <pre>{@code
     *     return encoder(JieIO.bufferedEncoder(encoder));
     * }</pre>
     *
     * @param encoder encoder for encoding data from read operation
     * @return this
     */
    default CharsProcessor bufferedEncoder(Encoder encoder) {
        return encoder(JieIO.bufferedEncoder(encoder));
    }

    /**
     * Starts data processing and writes processed data into specified destination, returns the actual number of chars
     * processed, which is typically the number of chars actually read. If an error is thrown by an {@code encoder}, the
     * error will be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return returns the actual number of chars processed, which is typically the number of chars actually read
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(Appendable dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination, returns the actual number of chars
     * processed, which is typically the number of chars actually read. If an error is thrown by an {@code encoder}, the
     * error will be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return returns the actual number of chars processed, which is typically the number of chars actually read
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(char[] dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination (starting from specified start index
     * up to specified length), returns the actual number of chars processed, which is typically the number of chars
     * actually read. If an error is thrown by an {@code encoder}, the error will be wrapped by
     * {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest   specified destination to be written
     * @param offset specified start index
     * @param length specified length
     * @return returns the actual number of chars processed, which is typically the number of chars actually read
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(char[] dest, int offset, int length) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing and writes processed data into specified destination, returns the actual number of chars
     * processed, which is typically the number of chars actually read. If an error is thrown by an {@code encoder}, the
     * error will be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest specified destination to be written
     * @return returns the actual number of chars processed, which is typically the number of chars actually read
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo(CharBuffer dest) throws IOEncodingException, IORuntimeException;

    /**
     * Starts data processing without writing data into destination, returns actual number of read chars. This method is
     * typically used to produce side effects via the {@code encoder}. If an error is thrown by an {@code encoder}, the
     * error will be wrapped by {@link IOEncodingException} to be thrown, use {@link Throwable#getCause()} to get it.
     * <p>
     * If the source is a buffer or reader/writer, its position will be incremented by actual affected length.
     * <p>
     * This is a terminal method.
     *
     * @return actual number of read chars
     * @throws IOEncodingException to wrap the error thrown by encoder
     * @throws IORuntimeException  thrown for any other IO problems
     */
    long writeTo() throws IOEncodingException, IORuntimeException;

    /**
     * Returns a char array which is the result of data processing by this processor. This method is equivalent to:
     * <pre>{@code
     *     CharArrayWriter writer = new CharArrayWriter();
     *     writeTo(writer);
     *     return writer.toCharArray();
     * }</pre>
     * This is a terminal method.
     *
     * @return a char array which is the result of data processing by this processor
     * @throws IORuntimeException thrown for any IO problems
     */
    default char[] writeToCharArray() throws IORuntimeException {
        CharArrayWriter writer = new CharArrayWriter();
        writeTo(writer);
        return writer.toCharArray();
    }

    /**
     * Returns a char buffer which is the result of data processing by this processor. This method is equivalent to:
     * <pre>{@code
     *     return CharBuffer.wrap(writeToCharArray());
     * }</pre>
     * This is a terminal method.
     *
     * @return a char buffer which is the result of data processing by this processor
     * @throws IORuntimeException thrown for any IO problems
     */
    default CharBuffer writeToCharBuffer() throws IORuntimeException {
        return CharBuffer.wrap(writeToCharArray());
    }

    /**
     * Returns a string which is built from the result of data processing by this processor. This method is equivalent
     * to:
     * <pre>{@code
     *     return new String(writeToCharArray());
     * }</pre>
     * This is a terminal method.
     *
     * @return a string which is built from the result of data processing by this processor
     * @throws IORuntimeException thrown for any IO problems
     */
    default String writeToString() throws IORuntimeException {
        return new String(writeToCharArray());
    }

    /**
     * Returns a reader which encompasses the entire data processing. The reader is lazy, read operations on the source
     * data are performed only as needed, and doesn't support mark/reset operations. The close method will close the
     * source if the source is also closable.
     * <p>
     * This is a terminal method.
     *
     * @return a reader which encompasses the entire data processing
     * @throws IORuntimeException thrown for any IO problems
     */
    Reader toReader() throws IORuntimeException;

    /**
     * Converts this char processor to byte processor with specified charset.
     * <p>
     * This is a setting method but this char processor still be invalid after current invocation.
     *
     * @return a new {@link CharsProcessor} converted from this char processor with specified charset
     */
    default BytesProcessor toByteProcessor(Charset charset) {
        return BytesProcessor.from(JieIO.inputStream(toReader(), charset));
    }

    /**
     * Encoder for encoding data in data processing.
     */
    interface Encoder {

        /**
         * Encodes specified input data and return the result. Specified input data will not be null (but may be empty),
         * and the return value can be null. If {@code null} is returned, next encoder, if it exists, will not be
         * invoked; If an {@code empty} buffer is returned, next encoder, if it exists, will be invoked.
         *
         * @param data specified data
         * @param end  whether current encoding is the last invocation
         * @return result of encoding
         */
        @Nullable
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
