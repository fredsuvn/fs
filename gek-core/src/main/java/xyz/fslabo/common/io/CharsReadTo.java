package xyz.fslabo.common.io;

import java.io.Reader;
import java.nio.CharBuffer;
import java.util.function.Function;

/**
 * This interface is {@code char} version of {@link ReadTo}, represents a transmission operation that reads chars from a
 * data source into a destination. There are two types of method in this interface:
 * <ul>
 *     <li>
 *         Setting methods, used to set options of current transmission operation before this it is started;
 *     </li>
 *     <li>
 *         Final methods, start transmission, and when the transmission is finished, the current instance will
 *         be invalid;
 *     </li>
 * </ul>
 * The transmission will keep reading and writing until source reaches to the end or specified {@code readLimit} (by
 * {@link #readLimit(long)}). Therefore, the destination must ensure it has sufficient remaining space.
 *
 * @author fredsuvn
 */
public interface CharsReadTo {

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    CharsReadTo input(Reader source);

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    CharsReadTo input(char[] source);

    /**
     * Sets the data source to read, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @param offset start index
     * @param length specified length
     * @return this
     */
    CharsReadTo input(char[] source, int offset, int length);

    /**
     * Sets the data source to read.
     * <p>
     * This is a setting method.
     *
     * @param source the data source to read
     * @return this
     */
    CharsReadTo input(CharBuffer source);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    CharsReadTo output(Appendable dest);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    CharsReadTo output(char[] dest);

    /**
     * Sets the destination to written, starting from the start index up to the specified length
     * <p>
     * This is a setting method.
     *
     * @param dest   the destination to written
     * @param offset start index
     * @param length specified length
     * @return this
     */
    CharsReadTo output(char[] dest, int offset, int length);

    /**
     * Sets the destination to written.
     * <p>
     * This is a setting method.
     *
     * @param dest the destination to written
     * @return this
     */
    CharsReadTo output(CharBuffer dest);

    /**
     * Sets max chars number to read. May be -1 if set to read to end, and this is default setting.
     * <p>
     * This is a setting method.
     *
     * @param readLimit max chars number to read
     * @return this
     */
    CharsReadTo readLimit(long readLimit);

    /**
     * Sets the chars number for each reading from data source. This setting is used for read stream or need data
     * conversion, default is {@link JieIO#BUFFER_SIZE}.
     * <p>
     * This is a setting method.
     *
     * @param blockSize the chars number for each reading from data source
     * @return this
     */
    CharsReadTo blockSize(int blockSize);

    /**
     * Sets whether break transmission immediately when the number of chars read is 0. If it is set to {@code false},
     * the reading will continue until reach to end of the source. Default is {@code false}.
     * <p>
     * This is a setting method.
     *
     * @param breakIfNoRead whether break reading immediately when the number of chars read is 0
     * @return this
     */
    CharsReadTo breakIfNoRead(boolean breakIfNoRead);

    /**
     * Set data conversion. If the conversion is not null, source chars will be converted by the conversion at first,
     * then written into the destination. Size of source chars read to convert is determined by {@code blockSize}, but
     * it could be less than {@code blockSize} if remaining readable size is not enough. Default is {@code null}.
     * <p>
     * Note that the {@link CharBuffer} instance passed as the argument may not always be new, it could be reused. And
     * returned {@link CharBuffer} will also be considered as such.
     * <p>
     * This is a setting method.
     *
     * @param conversion data conversion
     * @return this
     */
    CharsReadTo conversion(Function<CharBuffer, CharBuffer> conversion);

    /**
     * Starts this transmission, returns the actual chars number that read and success to deal with.
     * <p>
     * If the {@code conversion} is {@code null}, read number equals to written number. Otherwise, the written number
     * may not equal to read number, and this method returns actual read number. Specifically, if it is detected that
     * the data source reaches to the end and no data has been read, return -1.
     * <p>
     * This is a final method.
     *
     * @return the actual chars number that read and success to deal with
     * @throws IORuntimeException IO runtime exception
     */
    long start() throws IORuntimeException;
}
