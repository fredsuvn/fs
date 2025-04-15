package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;

import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;

/**
 * This is a static utilities class provides utilities for {@code bytes}.
 *
 * @author sunqian
 */
public class JieBytes {

    private static final byte[] EMPTY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(EMPTY_BYTES);

    //---------------- Bytes Begin ----------------//

    /**
     * Returns whether the given buffer is null or empty.
     *
     * @param buffer the given buffer
     * @return whether the given buffer is null or empty
     */
    public static boolean isEmpty(@Nullable ByteBuffer buffer) {
        return buffer == null || !buffer.hasRemaining();
    }

    /**
     * Returns an empty byte array.
     *
     * @return an empty byte array
     */
    public static byte[] emptyBytes() {
        return EMPTY_BYTES;
    }

    /**
     * Returns an empty byte buffer.
     *
     * @return an empty byte buffer
     */
    public static ByteBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Returns a new heap buffer with the specified capacity. The new buffer's position will be 0, limit and capacity
     * will be the specified capacity.
     *
     * @param capacity the specified capacity
     * @return a new heap buffer with the specified capacity
     */
    public static ByteBuffer buffer(int capacity) {
        return buffer(capacity, false);
    }

    /**
     * Returns a new buffer with the specified capacity. The new buffer's position will be 0, limit and capacity will be
     * the specified capacity.
     *
     * @param capacity the specified capacity
     * @param direct   whether the returned buffer is direct
     * @return a new buffer with the specified capacity
     */
    public static ByteBuffer buffer(int capacity, boolean direct) {
        return direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer.allocate(capacity);
    }


    /**
     * Returns a new buffer (not direct, not readonly) of which content copied from the given data. The new buffer's
     * position will be 0, limit and capacity will be the length of the given data.
     *
     * @param data the given data
     * @return a new buffer (not direct, not readonly) of which content copied from the given data
     */
    public static ByteBuffer copyBuffer(byte[] data) {
        return copyBuffer(data, false);
    }

    /**
     * Returns a new buffer (not readonly) of which content copied from the given data. The new buffer's position will
     * be 0, limit and capacity will be the length of the given data.
     *
     * @param data   the given data
     * @param direct whether the returned buffer is direct
     * @return a new buffer (not readonly) of which content copied from the given data
     */
    public static ByteBuffer copyBuffer(byte[] data, boolean direct) {
        ByteBuffer buffer = direct ? ByteBuffer.allocateDirect(data.length) : ByteBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new buffer of which content copied from the given data. The direct and readonly options inherit the
     * given data. The new buffer's position will be 0, limit and capacity will be the length of the given data.
     *
     * @param data the given data
     * @return a new buffer of which content copied from the given data
     */
    public static ByteBuffer copyBuffer(ByteBuffer data) {
        ByteBuffer buffer = data.isDirect() ?
            ByteBuffer.allocateDirect(data.remaining()) : ByteBuffer.allocate(data.remaining());
        int pos = data.position();
        buffer.put(data);
        data.position(pos);
        buffer.flip();
        return buffer;
    }

    /**
     * Returns a new array of which content copied from given data. The position of given data will not be changed,
     * rather than incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content copied from given data
     */
    public static byte[] copyBytes(ByteBuffer data) {
        int pos = data.position();
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        data.position(pos);
        return bytes;
    }

    /**
     * Reads given data into a new array then returns. The position of given data will be incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content read from given data
     */
    public static byte[] getBytes(ByteBuffer data) {
        byte[] bytes = new byte[data.remaining()];
        data.get(bytes);
        return bytes;
    }

    /**
     * Puts content of specified length from given source into destination. The positions of two buffers will be
     * incremented by specified length.
     *
     * @param source given source
     * @param dest   given destination
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on length do not hold
     * @throws IllegalArgumentException If there is insufficient space in the destination
     */
    public static void putBuffer(ByteBuffer source, ByteBuffer dest, int length)
        throws IllegalArgumentException, BufferOverflowException {
        ByteBuffer slice = slice(source, 0, length);
        dest.put(slice);
        source.position(source.position() + length);
    }

    public static ByteBuffer slice(ByteBuffer buffer, int length) throws IllegalArgumentException {
        return slice(buffer, 0, length);
    }

    /**
     * Returns a new buffer whose content is a shared subsequence of given buffer's content. The content of the new
     * buffer will start at specified offset from given buffer's current position, up to specified length. Changes to
     * given buffer's content will be visible in the new buffer, and vice versa.
     * <p>
     * The two buffers' position, limit, and mark values will be independent. The new buffer's position will be zero,
     * its capacity and its limit will be the specified length, and its mark will be undefined. The new buffer will be
     * direct if, and only if, given buffer is direct, and it will be read-only if, and only if, given buffer is
     * read-only. The position of given buffer will not be changed.
     * <p>
     * Specially if specified length is {@code 0}, returns {@link #emptyBuffer()}.
     *
     * @param buffer given buffer
     * @param offset specified offset to {@code position}
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on offset and length do not hold
     */
    public static ByteBuffer slice(ByteBuffer buffer, int offset, int length) throws IllegalArgumentException {
        if (length == 0) {
            return emptyBuffer();
        }
        int pos = buffer.position();
        int limit = buffer.limit();
        buffer.position(pos + offset);
        buffer.limit(pos + offset + length);
        ByteBuffer slice = buffer.slice();
        buffer.position(pos);
        buffer.limit(limit);
        return slice;
    }

    //---------------- Bytes End ----------------//

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    //---------------- Process Begin ----------------//

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor process(InputStream data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor process(byte[] data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link BytesProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    public static BytesProcessor process(byte[] data, int offset, int length) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(data, offset, length);
        if (offset == 0 && length == data.length) {
            return process(data);
        }
        ByteBuffer buffer = ByteBuffer.wrap(data, offset, length);
        return process(buffer);
    }

    /**
     * Returns a new {@link BytesProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link BytesProcessor}
     */
    public static BytesProcessor process(ByteBuffer data) {
        return new BytesProcessorImpl(data);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks. The
     * returned encoder splits incoming data into blocks of the specified block size, and for each block, it
     * sequentially calls the given encoder, passing the block as the data parameter. If incoming data is insufficient
     * to form a full block, it is buffered until enough data is received to form a full block.
     * <p>
     * In the last invocation (when {@code end == true}) of the returned encoder, even if the remainder data after
     * splitting is insufficient to form a full block, it will still be passed to the given encoder as the last block,
     * and this call is the given encoder's last invocation.
     *
     * @param size    the specified block size
     * @param encoder the given encoder
     * @return a new {@link BytesProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks
     */
    public static BytesProcessor.Encoder fixedSizeEncoder(int size, BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} that wraps the given encoder to round down incoming data. The returned
     * encoder rounds down incoming data to the largest multiple of the specified size, and passes the rounded data to
     * the given encoder. The remainder data will be buffered until enough data is received to round.
     * <p>
     * However, in the last invocation (when {@code end == true}), all remaining data will be passed directly to the
     * given encoder.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a {@link BytesProcessor.Encoder} to round down incoming data for the given encoder
     */
    public static BytesProcessor.Encoder roundEncoder(int size, BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.RoundEncoder(encoder, size);
    }

    /**
     * Returns a {@link BytesProcessor.Encoder} that wraps the given encoder to buffer unconsumed data. It is typically
     * used for the encoder which may not fully consume the passed data, requires buffering and consuming data in next
     * invocation. This encoder passes incoming data to the given encoder. The unconsumed remaining data after encoding
     * of the given encoder will be buffered and used in the next invocation.
     * <p>
     * However, in the last invocation (when {@code end == true}), no data will be buffered.
     *
     * @param encoder the given encoder
     * @return a {@link BytesProcessor.Encoder} that buffers unconsumed data of the given encoder
     */
    public static BytesProcessor.Encoder bufferedEncoder(BytesProcessor.Encoder encoder) {
        return new BytesProcessorImpl.BufferedEncoder(encoder);
    }

    //---------------- Process End ----------------//
}
