package xyz.sunqian.common.trash.data;

import xyz.sunqian.common.base.BaseConfigurator;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.string.StringKit;
import xyz.sunqian.common.io.IOKit;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * {@link BaseConfigurator} for IO operations.
 *
 * @param <T> actual type of this {@code IOConfigurator}
 * @author fredsuvn
 */
public interface IOConfigurator<T extends IOConfigurator<T>> extends BaseConfigurator<T> {

    /**
     * Sets input to given array.
     *
     * @param array given array
     * @return this
     */
    T input(byte[] array);

    /**
     * Sets input to given array, starting from given offset to end of array.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T input(byte[] array, int offset) {
        return input(array, offset, array.length);
    }

    /**
     * Sets input to given array, starting from given offset to specified length.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return this
     */
    default T input(byte[] array, int offset, int length) {
        return input(ByteBuffer.wrap(array, offset, length));
    }

    /**
     * Sets input to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T input(ByteBuffer buffer);

    /**
     * Sets input to given input stream.
     *
     * @param in given input stream
     * @return this
     */
    T input(InputStream in);

    /**
     * Sets input to given string with {@link CharsKit#defaultCharset()}.
     *
     * @param str given string
     * @return this
     */
    default T input(String str) {
        return input(str, CharsKit.defaultCharset());
    }

    /**
     * Sets input to given string with specified charset.
     *
     * @param str     given string
     * @param charset specified charset
     * @return this
     */
    default T input(String str, Charset charset) {
        return input(StringKit.getBytes(str, charset));
    }

    /**
     * Sets output to given array.
     *
     * @param array given array
     * @return this
     */
    T output(byte[] array);

    /**
     * Sets output to given array, starting from given offset.
     *
     * @param array  given array
     * @param offset given offset
     * @return this
     */
    default T output(byte[] array, int offset) {
        return output(ByteBuffer.wrap(array, offset, array.length - offset));
    }

    /**
     * Sets output to given buffer.
     *
     * @param buffer given buffer
     * @return this
     */
    T output(ByteBuffer buffer);

    /**
     * Sets output to given output stream.
     *
     * @param out given output stream
     * @return this
     */
    T output(OutputStream out);

    /**
     * Starts and does final process, returns number of bytes written in output.
     *
     * @return number of bytes written in output
     */
    long doFinal();

    /**
     * Starts and does final process, returns int number of bytes written in output, or {@link Integer#MAX_VALUE} if the
     * number is greater than {@link Integer#MAX_VALUE}.
     *
     * @return int number of bytes written in output or {@link Integer#MAX_VALUE} if the number is greater than
     * {@link Integer#MAX_VALUE}.
     */
    default int doFinalInt() {
        long num = doFinal();
        return (int) (num > Integer.MAX_VALUE ? Integer.MAX_VALUE : num);
    }

    /**
     * Starts and does final process, writes result into an array and returns.
     *
     * @return result array
     */
    default byte[] finalBytes() {
        return IOKit.read(finalStream());
    }

    /**
     * Returns an input stream which contains all configurations of current process, and will start and do final data
     * process when read methods are called.
     *
     * @return an input stream which contains all configurations of current process
     */
    InputStream finalStream();

    /**
     * Starts and does final process, builds result as string with {@link CharsKit#defaultCharset()} and returns.
     *
     * @return result as string with specified charset
     */
    default String finalString() {
        return finalString(CharsKit.defaultCharset());
    }

    /**
     * Starts and does final process, builds result as string with specified charset and returns.
     *
     * @param charset specified charset
     * @return result as string with specified charset
     */
    default String finalString(Charset charset) {
        return StringKit.of(finalBytes(), charset);
    }
}
