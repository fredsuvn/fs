package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.bytes.BytesProcessor;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieIO;

import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 * Char processor is used to process char data, from the specified data source, through zero or more intermediate
 * operations (such as {@link #encoder(Encoder)}), and finally produces a result or side effect. The following example
 * shows an encoding-then-writing operation:
 * <pre>{@code
 *     JieChars.process(input)
 *         .readBlockSize(1024)
 *         .encoder(en1)
 *         .encoder(en2, 64)
 *         .writeTo(output);
 * }</pre>
 * There are types of methods in this interface:
 * <ul>
 *     <li>
 *         Setting methods, to set the data processing arguments before a terminal method has invoked;
 *     </li>
 *     <li>
 *         Terminal methods, to start the data processing. Once a terminal method is invoked, the state of current
 *         processor will become undefined, and no safe guarantees for further operations;
 *     </li>
 * </ul>
 * Char processor is lazy, operations on the source data are only performed when a terminal method is invoked, and source
 * data are consumed only as needed.
 *
 * @author sunqian
 */
public interface CharsProcessor {

    /**
     * Sets the maximum number of chars to read from the data source. This can be negative, meaning read until the end,
     * which is the default value.
     * <p>
     * This is an optional setting method.
     *
     * @param readLimit the maximum number of chars to read from the data source
     * @return this
     */
    CharsProcessor readLimit(long readLimit);

    /**
     * Sets the number of chars for each read operation from the data source.
     * <p>
     * This setting is typically used when the data source is a reader, or intermediate operations are set, default is
     * {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is an optional setting method.
     *
     * @param readBlockSize the number of chars for each read operation from the data source
     * @return this
     */
    CharsProcessor readBlockSize(int readBlockSize);

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
    CharsProcessor endOnZeroRead(boolean endOnZeroRead);

    /**
     * Adds an encoder for this processor. When the data processing starts, all encoders will be invoked after each read
     * operation as following:
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
     * Passed {@link CharBuffer} object, which is the first argument of {@link Encoder#encode(CharBuffer, boolean)}, can
     * be read-only (for example, when the source is a reader), or writable (for example, when the source is a char
     * array or char buffer), and will be discarded after each invocation. The returned {@link CharBuffer} will be
     * treated as read-only;
     * <p>
     * This is an optional setting method. There are also more specific encoder methods available, such as:
     * <ul>
     *     <li>
     *         For fixed-size: {@link #encoder(int, Encoder)}, {@link JieChars#fixedSizeEncoder(int, Encoder)};
     *     </li>
     *     <li>
     *         For rounding size: {@link #roundEncoder(int, Encoder)}, {@link JieChars#roundEncoder(int, Encoder)};
     *     </li>
     *     <li>
     *         for buffering: {@link #bufferedEncoder(Encoder)}, {@link JieChars#bufferedEncoder(Encoder)};
     *     </li>
     * </ul>
     *
     * @param encoder the encoder
     * @return this
     */
    CharsProcessor encoder(Encoder encoder);

    /**
     * Adds an encoder wrapped by {@link JieChars#fixedSizeEncoder(int, Encoder)} for this processor. This is a specific
     * encoder, typically used for consuming data in fixed-size blocks. The behavior of this method is equivalent to:
     * <pre>{@code
     *     return encoder(JieChars.fixedSizeEncoder(size, encoder));
     * }</pre>
     *
     * @param size    the specified fixed-size
     * @param encoder the encoder
     * @return this
     */
    default CharsProcessor encoder(int size, Encoder encoder) {
        return encoder(JieChars.fixedSizeEncoder(size, encoder));
    }

    /**
     * Adds an encoder wrapped by {@link JieChars#roundEncoder(int, Encoder)} for this processor. This is a specific
     * encoder, typically used for consuming data in multiples of the specified size. The behavior of this method is
     * equivalent to:
     * <pre>{@code
     *     encoder(JieChars.roundEncoder(size, encoder));
     * }</pre>
     *
     * @param size    the specified size
     * @param encoder the encoder
     * @return this
     */
    default CharsProcessor roundEncoder(int size, Encoder encoder) {
        return encoder(JieChars.roundEncoder(size, encoder));
    }

    /**
     * Adds an encoder wrapped by {@link JieChars#bufferedEncoder(Encoder)} for this processor. This is a specific
     * encoder, typically used for the encoder which may not fully consume current passed data, requires buffering and
     * consuming in next invocation. The behavior of this method is equivalent to:
     * <pre>{@code
     *     encoder(JieChars.bufferedEncoder(encoder));
     * }</pre>
     *
     * @param encoder the encoder
     * @return this
     */
    default CharsProcessor bufferedEncoder(Encoder encoder) {
        return encoder(JieChars.bufferedEncoder(encoder));
    }

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. If an exception is thrown during the processing, it will be wrapped by {@link ProcessingException} and
     * rethrown.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     */
    long writeTo(Appendable dest) throws ProcessingException, IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. If an exception is thrown during the processing, it will be wrapped by {@link ProcessingException} and
     * rethrown.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     */
    long writeTo(char[] dest) throws ProcessingException, IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination (starting from the specified start index
     * up to the specified length), and returns the actual number of bytes processed. If an exception is thrown during
     * the processing, it will be wrapped by {@link ProcessingException} and rethrown.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest   the specified destination
     * @param offset the specified start index
     * @param length the specified length
     * @return the actual number of bytes processed
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     */
    long writeTo(char[] dest, int offset, int length) throws ProcessingException, IORuntimeException;

    /**
     * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
     * processed. If an exception is thrown during the processing, it will be wrapped by {@link ProcessingException} and
     * rethrown.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @param dest the specified destination
     * @return the actual number of bytes processed
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     */
    long writeTo(CharBuffer dest) throws ProcessingException, IORuntimeException;

    /**
     * Starts data processing without writing any result, and returns the actual number of bytes processed. If an
     * exception is thrown during the processing, it will be wrapped by {@link ProcessingException} and rethrown.
     * <p>
     * If the source and/or destination is a buffer or reader/writer, its position will be incremented by actual
     * affected length.
     * <p>
     * This is a terminal method.
     *
     * @return the actual number of bytes processed
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     */
    long writeTo() throws ProcessingException, IORuntimeException;

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
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     * @see #writeTo(Appendable)
     */
    default char[] toCharArray() throws ProcessingException, IORuntimeException {
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
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     * @see #writeTo(Appendable)
     */
    default CharBuffer toCharBuffer() throws ProcessingException, IORuntimeException {
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
     * @throws ProcessingException to wrap the original exception during the processing
     * @throws IORuntimeException  if an I/O error occurs
     * @see #toCharArray()
     */
    String toString() throws ProcessingException, IORuntimeException;

    /**
     * Returns a reader which represents and encompasses the entire data processing. The reader is lazy, read operations
     * on the source data are performed only as needed, and doesn't support mark/reset operations. The {@code close()}
     * method will close the source if the source is closable.
     * <p>
     * This is a terminal method.
     *
     * @return a reader which represents and encompasses the entire data processing
     */
    Reader toReader();

    /**
     * Converts this {@link CharsProcessor} to a {@link BytesProcessor} with the specified charset.
     * <p>
     * This is a terminal method.
     *
     * @param charset the specified charset
     * @return a new {@link BytesProcessor} converted from this {@link CharsProcessor} with the specified charset
     */
    default BytesProcessor toByteProcessor(Charset charset) {
        return JieBytes.process(JieIO.inStream(toReader(), charset));
    }

    /**
     * Encoder for encoding data in the data processing.
     */
    interface Encoder {

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
        CharBuffer encode(CharBuffer data, boolean end) throws Exception;
    }
}
