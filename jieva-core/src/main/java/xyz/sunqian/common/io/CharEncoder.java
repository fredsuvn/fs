// package xyz.sunqian.common.io;
//
// import xyz.sunqian.annotations.Nonnull;
// import xyz.sunqian.annotations.Nullable;
// import xyz.sunqian.common.base.chars.CharsBuilder;
//
// import java.io.Reader;
// import java.nio.CharBuffer;
// import java.nio.charset.Charset;
//
// /**
//  * This interface represents the encoder to encode char data, from the specified data source, through zero or more
//  * intermediate handlers, finally produces a result or side effect. The following example shows a typical encoding:
//  * <pre>{@code
//  *     CharEncoder.from(input)
//  *         .readBlockSize(1024)
//  *         .readLimit(1024 * 8)
//  *         .handler(handler)
//  *         .writeTo(output);
//  * }</pre>
//  * There are types of methods in this interface:
//  * <ul>
//  *     <li>
//  *         Setting methods: to set the encoding arguments to the current encoder before a terminal method is invoked;
//  *     </li>
//  *     <li>
//  *         Terminal methods: the current encoder starts the encoding and becomes invalid. Once a terminal method is
//  *         invoked, any further operations to the encoder will be undefined;
//  *     </li>
//  * </ul>
//  * The encoder is lazy, operations on the source data are only performed when a terminal method is invoked, and
//  * source data are consumed only as needed.
//  *
//  * @author sunqian
//  */
// public interface CharEncoder {
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source.
//      *
//      * @param src the specified data source
//      * @return a new {@link CharEncoder} with the specified data source
//      */
//     static CharEncoder from(Reader src) {
//         return new CharEncoderImpl(src);
//     }
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source.
//      *
//      * @param src the specified data source
//      * @return a new {@link CharEncoder} with the specified data source
//      */
//     static CharEncoder from(char[] src) {
//         return new CharEncoderImpl(src);
//     }
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source, starting at the specified offset and up to the
//      * specified length.
//      *
//      * @param src the specified data source
//      * @param off the specified offset
//      * @param len the specified length
//      * @return a new {@link CharEncoder} with the specified data source
//      * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
//      */
//     static CharEncoder from(char[] src, int off, int len) throws IndexOutOfBoundsException {
//         IOChecker.checkOffLen(src.length, off, len);
//         if (off == 0 && len == src.length) {
//             return from(src);
//         }
//         CharBuffer buffer = CharBuffer.wrap(src, off, len);
//         return from(buffer);
//     }
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source.
//      *
//      * @param src the specified data source
//      * @return a new {@link CharEncoder} with the specified data source
//      */
//     static CharEncoder from(CharSequence src) {
//         return new CharEncoderImpl(src);
//     }
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source, starting at the specified start index inclusive
//      * and end at the specified end index exclusive.
//      *
//      * @param src   the specified data source
//      * @param start the specified start index inclusive
//      * @param end   the specified end index exclusive
//      * @return a new {@link CharEncoder} with the specified data source
//      * @throws IndexOutOfBoundsException if the bounds arguments are out of bounds
//      */
//     static CharEncoder from(CharSequence src, int start, int end) throws IndexOutOfBoundsException {
//         IOChecker.checkStartEnd(src.length(), start, end);
//         if (start == 0 && end == src.length()) {
//             return from(src);
//         }
//         return from(src.subSequence(start, end));
//     }
//
//     /**
//      * Returns a new {@link CharEncoder} with the specified data source.
//      *
//      * @param src the specified data source
//      * @return a new {@link CharEncoder} with the specified data source
//      */
//     static CharEncoder from(CharBuffer src) {
//         return new CharEncoderImpl(src);
//     }
//
//     /**
//      * Returns a wrapper {@link Handler} that wraps the given encoder to encode data in fixed-size blocks.
//      * <p>
//      * The wrapper splits the original data into blocks of the specified fixed size by {@link CharBuffer#slice()}, and
//      * each block will be passed to the given encoder sequentially. The remainder data, which is insufficient to form a
//      * full block, will be buffered until enough data is received. The content of the block is shared with the
//      * sub-content of the original data if, and only if, it is sliced by {@link CharBuffer#slice()}. If a block is
//      * formed by concatenating multiple original data pieces, its content is not shared.
//      * <p>
//      * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
//      * less than the specified fixed size.
//      *
//      * @param size    the specified fixed size
//      * @param encoder the given encoder
//      * @return a wrapper {@link Handler} that wraps the given encoder to encode data in fixed-size blocks
//      * @throws IllegalArgumentException if the specified size is less than or equal to 0
//      */
//     static Handler fixedSizeHandler(Handler encoder, int size) throws IllegalArgumentException {
//         return new CharEncoderImpl.FixedSizeEncoder(encoder, size);
//     }
//
//     /**
//      * Returns a wrapper {@link Handler} that wraps the given encoder to encode data in rounding down blocks.
//      * <p>
//      * The wrapper rounds down the size of the original data to the largest multiple ({@code >= 1}) of the specified
//      * size that does not exceed it, and splits the original data into the block of the rounded size by
//      * {@link CharBuffer#slice()}. The block will be passed to the given encoder. The remainder data, of which size is
//      * less than one multiple of the specified size, will be buffered until enough data is received. The content of the
//      * block is shared with the sub-content of the original data if, and only if, it is sliced by
//      * {@link CharBuffer#slice()}. If a block is formed by concatenating multiple original data pieces, its content is
//      * not shared.
//      * <p>
//      * Specially, in the last invocation (when {@code end == true}) of the given encoder, the last block's size may be
//      * less than one multiple of the specified size.
//      *
//      * @param size    the specified size
//      * @param encoder the given encoder
//      * @return a wrapper {@link Handler} that wraps the given encoder to encode data in rounding down blocks
//      * @throws IllegalArgumentException if the specified size is less than or equal to 0
//      */
//     static Handler withRounding(int size, Handler encoder) throws IllegalArgumentException {
//         return new CharEncoderImpl.RoundingEncoder(encoder, size);
//     }
//
//     /**
//      * Returns a wrapper {@link Handler} that wraps the given encoder to support buffering unconsumed data.
//      * <p>
//      * When the wrapper is invoked, if no buffered data exists, the original data is directly passed to the given
//      * encoder; if buffered data exists, a new buffer concatenating the buffered data followed by the original data is
//      * passed to the given. After the execution of the given encoder, any unconsumed data remaining in passed buffer
//      * will be buffered.
//      * <p>
//      * Specially, in the last invocation (when {@code end == true}) of the wrapper, no data buffered.
//      *
//      * @param encoder the given encoder
//      * @return a wrapper {@link Handler} that wraps the given encoder to support buffering unconsumed data
//      */
//     static Handler bufferedHandler(Handler encoder) {
//         return new CharEncoderImpl.BufferingEncoder(encoder);
//     }
//
//     /**
//      * Returns an empty {@link Handler} which does nothing but only returns the input data directly.
//      *
//      * @return an empty {@link Handler} which does nothing but only returns the input data directly
//      */
//     static Handler emptyHandler() {
//         return CharEncoderImpl.EmptyEncoder.SINGLETON;
//     }
//
//     /**
//      * Sets the maximum number of chars to read from the data source.
//      * <p>
//      * This is an optional setting method.
//      *
//      * @param readLimit the maximum number of chars to read from the data source, must {@code >= 0}
//      * @return this
//      * @throws IllegalArgumentException if the limit is negative
//      */
//     CharEncoder readLimit(long readLimit) throws IllegalArgumentException;
//
//     /**
//      * Sets the number of chars for each read operation from the data source, the default is
//      * {@link IOKit#bufferSize()}.
//      * <p>
//      * This setting is typically used for encoding in blocks.
//      * <p>
//      * This is an optional setting method.
//      *
//      * @param readBlockSize the number of chars for each read operation from the data source, must {@code > 0}
//      * @return this
//      * @throws IllegalArgumentException if the block size is negative
//      */
//     CharEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException;
//
//     /**
//      * Sets whether reading 0 char from the data source should be treated as reaching to the end and break the read
//      * loop. A read operation returning 0 char can occur in NIO. Default is {@code false}.
//      * <p>
//      * This is an optional setting method.
//      *
//      * @param endOnZeroRead whether reading 0 char from the data source should be treated as reaching to the end and
//      *                      break the read loop
//      * @return this
//      */
//     CharEncoder endOnZeroRead(boolean endOnZeroRead);
//
//     /**
//      * Adds the given handler to this encoder as the last handler.
//      * <p>
//      * When the encoding starts and exits at least one handler, the encoder reads a block of data from the data source,
//      * then passes the data block to the first handler, then passes the result of the first handler (if it is
//      * {@code null} then replaces it with an empty buffer) to the next handler, and so on. The last result of the last
//      * handler, which is the final result, will be written to the destination if it is not empty. The logic is as
//      * follows:
//      * <pre>{@code
//      * CharSegment segment = nextSegment(blockSize);
//      * CharBuffer data = segment.data();
//      * for (Handler handler : handlers) {
//      *     data = handler.encode(data == null ? emptyBuffer() : data, segment.end());
//      * }
//      * if (notEmpty(data)) {
//      *     writeTo(data);
//      * }
//      * }</pre>
//      * Note that the data blocks are typically read by {@link CharReader#read(int)} and its content may be shared with
//      * the data source. The encoder ignores the unconsumed data (which is the remaining data) in the data passed to the
//      * handler each time, to buffer the unconsumed data, try {@link #bufferedHandler(Handler)}.
//      * <p>
//      * This is an optional setting method. And provides some specific handler wrappers such as:
//      * {@link #fixedSizeHandler(Handler, int)}, {@link #withRounding(int, Handler)} and
//      * {@link #bufferedHandler(Handler)}.
//      *
//      * @param handler the given handler
//      * @return this
//      */
//     CharEncoder handler(Handler handler);
//
//     /**
//      * Adds the given handler wrapped by {@link #fixedSizeHandler(Handler, int)} to this encoder. This method is
//      * equivalent to:
//      * <pre>{@code
//      *     return handler(fixedSizeHandler(handler, size));
//      * }</pre>
//      *
//      * @param size    the specified fixed size for the {@link #fixedSizeHandler(Handler, int)}, must {@code > 0}
//      * @param handler the given handler
//      * @return this
//      * @throws IllegalArgumentException if the specified fixed size {@code <= 0}
//      */
//     default CharEncoder handler(Handler handler, int size) throws IllegalArgumentException {
//         return handler(fixedSizeHandler(handler, size));
//     }
//
//     /**
//      * Starts data processing and returns the actual number of bytes processed. The positions of the source and
//      * destination, if any, will be incremented by the actual length of the affected data.
//      * <p>
//      * This is a terminal method, and it is typically used to product side effects.
//      *
//      * @return the actual number of bytes processed
//      * @throws IORuntimeException if an I/O error occurs
//      */
//     long process() throws IORuntimeException;
//
//     /**
//      * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
//      * processed. The positions of the source and destination, if any, will be incremented by the actual length of the
//      * affected data.
//      * <p>
//      * This is a terminal method.
//      *
//      * @param dest the specified destination
//      * @return the actual number of bytes processed
//      * @throws IORuntimeException if an I/O error occurs
//      */
//     long writeTo(Appendable dest) throws IORuntimeException;
//
//     /**
//      * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
//      * processed. The position of the source, if any, will be incremented by the actual length of the affected data.
//      * <p>
//      * This is a terminal method.
//      *
//      * @param dest the specified destination
//      * @return the actual number of bytes processed
//      * @throws IORuntimeException if an I/O error occurs
//      */
//     long writeTo(char[] dest) throws IORuntimeException;
//
//     /**
//      * Starts data processing, writes the result into the specified destination (starting from the specified start index
//      * up to the specified length), and returns the actual number of bytes processed. The position of the source, if
//      * any, will be incremented by the actual length of the affected data.
//      * <p>
//      * This is a terminal method.
//      *
//      * @param dest   the specified destination
//      * @param offset the specified start index
//      * @param length the specified length
//      * @return the actual number of bytes processed
//      * @throws IORuntimeException if an I/O error occurs
//      */
//     long writeTo(char[] dest, int offset, int length) throws IORuntimeException;
//
//     /**
//      * Starts data processing, writes the result into the specified destination, and returns the actual number of bytes
//      * processed. The positions of the source and destination, if any, will be incremented by the actual length of the
//      * affected data.
//      * <p>
//      * This is a terminal method.
//      *
//      * @param dest the specified destination
//      * @return the actual number of bytes processed
//      * @throws IORuntimeException if an I/O error occurs
//      */
//     long writeTo(CharBuffer dest) throws IORuntimeException;
//
//     /**
//      * Starts data processing, and returns the result as a new array. This method is equivalent to:
//      * <pre>{@code
//      *     CharsBuilder builder = new CharsBuilder();
//      *     writeTo(builder);
//      *     return builder.toCharArray();
//      * }</pre>
//      * This is a terminal method.
//      *
//      * @return the processing result as a new array
//      * @throws IORuntimeException if an I/O error occurs
//      * @see #writeTo(Appendable)
//      */
//     default char[] toCharArray() throws IORuntimeException {
//         CharsBuilder builder = new CharsBuilder();
//         writeTo(builder);
//         return builder.toCharArray();
//     }
//
//     /**
//      * Starts data processing, and returns the result as a new buffer. This method is equivalent to:
//      * <pre>{@code
//      *     CharsBuilder builder = new CharsBuilder();
//      *     writeTo(builder);
//      *     return builder.toCharBuffer();
//      * }</pre>
//      * This is a terminal method.
//      *
//      * @return the processing result as a new buffer
//      * @throws IORuntimeException if an I/O error occurs
//      * @see #writeTo(Appendable)
//      */
//     default CharBuffer toCharBuffer() throws IORuntimeException {
//         CharsBuilder builder = new CharsBuilder();
//         writeTo(builder);
//         return builder.toCharBuffer();
//     }
//
//     /**
//      * Starts data processing, and returns the result as a new string. This method is equivalent to:
//      * <pre>{@code
//      *     return new String(toCharArray());
//      * }</pre>
//      * This is a terminal method.
//      *
//      * @return the processing result as a new string
//      * @throws IORuntimeException if an I/O error occurs
//      * @see #toCharArray()
//      */
//     String toString() throws IORuntimeException;
//
//     /**
//      * Returns a reader which represents and encompasses the entire data processing.
//      * <p>
//      * If there is no encoder in the processor: if the source is a reader, return the reader itself; if the source is an
//      * array or buffer or char sequence, returns the reader from {@link IOKit#newReader(char[])} or
//      * {@link IOKit#newReader(CharBuffer)} or {@link IOKit#newReader(CharSequence)}. Otherwise, the returned reader's
//      * read operations are performed only as needed, mark/reset operations are not supported, and the {@code close()}
//      * method will close the source if the source is closable.
//      * <p>
//      * This is a terminal method.
//      *
//      * @return a reader which represents and encompasses the entire data processing
//      */
//     Reader toReader();
//
//     /**
//      * Converts this {@link CharEncoder} to a {@link ByteEncoder} with the specified charset.
//      * <p>
//      * This is a terminal method.
//      *
//      * @param charset the specified charset
//      * @return a new {@link ByteEncoder} converted from this {@link CharEncoder} with the specified charset
//      */
//     default ByteEncoder toByteProcessor(Charset charset) {
//         return ByteEncoder.from(IOKit.newInputStream(toReader(), charset));
//     }
//
//     /**
//      * Handler of the {@link CharEncoder}, to do the specific encoding work.
//      *
//      * @author sunqian
//      */
//     interface Handler {
//
//         /**
//          * Handles the specific encoding work with the input data, and returns the handling result. The input data will
//          * not be {@code null} (but may be empty), but the return value can be {@code null}.
//          * <p>
//          * If return value is {@code null} and there exists a next handler, an empty buffer will be passed to the next
//          * handler.
//          *
//          * @param data the input data
//          * @param end  whether the input data is the last and there is no more data
//          * @return the result of the specific encoding work
//          * @throws Exception if any problem occurs
//          */
//         @Nullable
//         CharBuffer handle(@Nonnull CharBuffer data, boolean end) throws Exception;
//     }
// }
