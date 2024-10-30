package xyz.fslabo.common.io;

import java.io.Reader;
import java.nio.CharBuffer;

/**
 * The interface represents a char stream for transferring char data, from specified data source to specified
 * destination. There are two types of method in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set parameters and options for current stream before the transferring starts;
 *     </li>
 *     <li>
 *         Final methods, start transfer, and when the transfer is finished, the current instance will be invalid;
 *     </li>
 * </ul>
 * The stream will keep reading and writing until the source reaches to the end or specified {@code readLimit} (by
 * {@link #readLimit(long)}). Therefore, the destination must ensure it has sufficient remaining space.
 *
 * @author fredsuvn
 */
public interface CharStream {

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(Reader source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(char[] source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source, starting from the start index up to the specified
     * length.
     *
     * @param source specified data source
     * @param offset start index
     * @param length specified length
     * @return a new {@link CharStream}
     */
    static CharStream from(char[] source, int offset, int length) {
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
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(CharBuffer source) {
        return new CharStreamImpl(source);
    }

    /**
     * Returns a new {@link CharStream} with specified data source.
     *
     * @param source specified data source
     * @return a new {@link CharStream}
     */
    static CharStream from(CharSequence source) {
        return new CharStreamImpl(source);
    }

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(Appendable dest);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(char[] dest);

    /**
     * Sets the destination to be written, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination to be written
     * @param offset start index
     * @param length specified length
     * @return this
     */
    CharStream to(char[] dest, int offset, int length);

    /**
     * Sets the destination to be written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to be written
     * @return this
     */
    CharStream to(CharBuffer dest);

    /**
     * Sets max chars number to read. May be -1 if set to read to end, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readLimit max chars number to read
     * @return this
     */
    CharStream readLimit(long readLimit);

    /**
     * Sets the chars number for each reading from data source.
     * <p>
     * This setting is typically used when the source is a reader, or when {@code encoding} (see
     * {@link #encoder(Encoder)}) is required for the transfer, default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the chars number for each reading from data source
     * @return this
     */
    CharStream blockSize(int blockSize);

    /**
     * Sets whether break the transfer operation immediately if a read operation returns zero chars. If it is set to
     * {@code false}, the reading will continue until reach to end of the source. Default is {@code false}.
     * <p>
     * This is a setting method.
     *
     * @param breakOnZeroRead whether break reading immediately when the number of chars read is 0
     * @return this
     */
    CharStream breakOnZeroRead(boolean breakOnZeroRead);

    /**
     * Set the data encoder to encode the source data before it is written to the destination. The encoded data will
     * then be written to the destination. This setting is optional; if not set, the source data will be written
     * directly to the destination.
     * <p>
     * When the transfer starts, data encoder will be called after each data read, passing the read data as the first
     * argument. The value returned by the encoder will be the actual data written to the destination. The length of
     * data read in each read operation is specified by {@link #blockSize(int)}, and the remaining data of last read may
     * be smaller than this value.
     * <p>
     * Note that the {@link CharBuffer} passed as the argument is not always a new instance or new allocated, it may be
     * reused. And the returned {@link CharBuffer} will also be treated as potentially reusable.
     * <p>
     * This is a setting method.
     *
     * @param encoder data encoder
     * @return this
     */
    CharStream encoder(Encoder encoder);

    /**
     * Starts transfer through this stream, returns the actual chars number that read and success to transfer. Ensure
     * that the remaining of destination is enough, otherwise a {@link IORuntimeException} will be thrown and the actual
     * read and written chars number is undefined.
     * <p>
     * If the {@code encoder} (see {@link #encoder(Encoder)}) is {@code null}, read number equals to written number.
     * Otherwise, the written number may not equal to read number, and this method returns actual read number.
     * Specifically, if it is detected that the data source has already reached to the end before starting, return -1.
     * <p>
     * This is a final method.
     *
     * @return the actual chars number that read and success to transfer
     * @throws IORuntimeException IO runtime exception
     */
    long start() throws IORuntimeException;

    /**
     * Encoder to encode the char data in the stream transfer processing.
     */
    interface Encoder {

        /**
         * Encodes specified data in chars. The returned buffer's position will be zero and its limit will be the number
         * of encoding chars.
         *
         * @param data specified data
         * @param end  whether the current encoding is the last call (source has been read to the end)
         * @return result of encoding
         */
        CharBuffer encode(CharBuffer data, boolean end);
    }
}
