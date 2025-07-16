package xyz.sunqian.common.net.data;

import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.string.StringKit;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * This interface represents data adapter to convert or write data into other types.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekData extends GekDataOutput {

    /**
     * Wraps given string to {@link OfArray}. The given string will be decoded by {@link CharsKit#defaultCharset()}.
     *
     * @param str given string
     * @return the {@link OfArray}
     */
    static GekData.OfArray wrap(String str) {
        return wrap(StringKit.getBytes(str));
    }

    /**
     * Wraps given array to {@link OfArray}. The given array will be back array of returned data.
     *
     * @param array given array
     * @return the {@link OfArray}
     */
    static GekData.OfArray wrap(byte[] array) {
        return wrap(array, 0, array.length);
    }

    /**
     * Wraps given array of specified length to {@link OfArray} from given offset. The given array will be back array of
     * returned data.
     *
     * @param array  given array
     * @param offset given offset
     * @param length specified length
     * @return the {@link OfArray}
     */
    static GekData.OfArray wrap(byte[] array, int offset, int length) {
        CheckKit.checkRangeInBounds(offset, offset + length, 0, array.length);
        return new ArrayData(array, offset, length);
    }

    /**
     * Wraps given buffer to {@link OfBuffer}.
     *
     * @param buffer given buffer
     * @return the {@link OfBuffer}
     */
    static GekData wrap(ByteBuffer buffer) {
        return new BufferData(buffer);
    }

    /**
     * Wraps given stream to {@link OfStream}.
     *
     * @param stream given stream
     * @return the {@link OfStream}
     */
    static GekData wrap(InputStream stream) {
        return new StreamData(stream);
    }

    /**
     * Returns string with specified charset of this data.
     *
     * @param charset specified charset
     * @return string with specified charset of this data
     */
    default String toString(Charset charset) {
        return new String(toArray(), charset);
    }

    /**
     * Returns whether this data is type of {@link OfArray} which has a back array.
     *
     * @return whether this data is type of {@link OfArray} which has a back array
     */
    boolean isArrayData();

    /**
     * Returns this data as {@link OfArray} if this is type of {@link OfArray}. Otherwise, it throws
     * {@link GekDataException}.
     *
     * @return this data as {@link OfArray} if this is type of {@link OfArray}
     */
    OfArray asArrayData();

    /**
     * Returns whether this data is type of {@link OfBuffer} which has a back buffer.
     *
     * @return whether this data is type of {@link OfBuffer} which has a back buffer
     */
    boolean isBufferData();

    /**
     * Returns this data as {@link OfBuffer} if this is type of {@link OfBuffer}. Otherwise, it throws
     * {@link GekDataException}.
     *
     * @return this data as {@link OfBuffer} if this is type of {@link OfBuffer}
     */
    OfBuffer asBufferData();

    /**
     * Returns whether this data is type of {@link OfStream} which has a back stream.
     *
     * @return whether this data is type of {@link OfStream} which has a back stream
     */
    boolean isStreamData();

    /**
     * Returns this data as {@link OfStream} if this is type of {@link OfStream}. Otherwise, it throws
     * {@link GekDataException}.
     *
     * @return this data as {@link OfStream} if this is type of {@link OfStream}
     */
    OfStream asStreamData();

    /**
     * A type of {@link GekData} which has a back array to be wrapped. Any operation to back array will be reflected to
     * this data, and vice versa.
     */
    interface OfArray extends GekData {

        /**
         * Returns back array to be wrapped.
         *
         * @return back array to be wrapped
         */
        byte[] array();

        /**
         * Returns wrapped offset of back array.
         *
         * @return wrapped offset of back array
         */
        int arrayOffset();

        /**
         * Returns length of this data, it is also wrapped length of back array.
         *
         * @return length of this data
         */
        int length();
    }

    /**
     * A type of {@link GekData} which has a back buffer. Any operation to back buffer will be reflected to this data
     * (such as moving of position caused by reading/writing), and vice versa.
     */
    interface OfBuffer extends GekData {
        /**
         * Returns back buffer.
         *
         * @return back buffer
         */
        ByteBuffer buffer();
    }

    /**
     * A type of {@link GekData} which has a back stream. Any operation to back stream will be reflected to this data
     * (such as moving of position caused by reading/writing), and vice versa.
     */
    interface OfStream extends GekData {
        /**
         * Returns back stream.
         *
         * @return back stream
         */
        InputStream stream();
    }
}
