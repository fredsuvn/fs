package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nullable;

import java.nio.ByteBuffer;

/**
 * This interface represents an encoder, which is a type of intermediate operation for {@link ByteProcessor}.
 *
 * @author sunqian
 */
public interface ByteEncoder {

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
    ByteBuffer encode(ByteBuffer data, boolean end) throws Exception;

    /**
     * Returns a new {@link ByteEncoder} that wraps the given encoder to encode data in fixed-size blocks. The returned
     * encoder splits incoming data into blocks of the specified block size, and for each block, it sequentially calls
     * the given encoder, passing the block as the data parameter. If incoming data is insufficient to form a full
     * block, it is buffered until enough data is received to form a full block.
     * <p>
     * In the last invocation (when {@code end == true}) of the returned encoder, even if the remainder data after
     * splitting is insufficient to form a full block, it will still be passed to the given encoder as the last block,
     * and this call is the given encoder's last invocation.
     *
     * @param size    the specified block size
     * @param encoder the given encoder
     * @return a new {@link ByteEncoder} that wraps the given encoder to encode data in fixed-size blocks
     */
    static ByteEncoder withFixedSize(int size, ByteEncoder encoder) {
        return new ByteProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a new {@link ByteEncoder} that wraps the given encoder to round down incoming data. The returned encoder
     * rounds down incoming data to the largest multiple of the specified size, and passes the rounded data to the given
     * encoder. The remainder data will be buffered until enough data is received to round.
     * <p>
     * However, in the last invocation (when {@code end == true}), all remaining data will be passed directly to the
     * given encoder.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a {@link ByteEncoder} to round down incoming data for the given encoder
     */
    static ByteEncoder withRounding(int size, ByteEncoder encoder) {
        return new ByteProcessorImpl.RoundEncoder(encoder, size);
    }

    /**
     * Returns a new {@link ByteEncoder} that wraps the given encoder to buffer unconsumed data. It is typically used
     * for the encoder which may not fully consume the passed data, requires buffering and consuming data in next
     * invocation. This encoder passes incoming data to the given encoder. The unconsumed remaining data after encoding
     * of the given encoder will be buffered and used in the next invocation.
     * <p>
     * However, in the last invocation (when {@code end == true}), no data will be buffered.
     *
     * @param encoder the given encoder
     * @return a {@link ByteEncoder} that buffers unconsumed data of the given encoder
     */
    static ByteEncoder withBuffering(ByteEncoder encoder) {
        return new ByteProcessorImpl.BufferedEncoder(encoder);
    }
}
