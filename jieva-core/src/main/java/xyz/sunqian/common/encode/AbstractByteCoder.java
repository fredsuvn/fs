package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This abstract class provides skeletal implementation methods for {@link ByteEncoder} and {@link ByteDecoder}, and
 * skeletal implementation classes: {@link En} and {@link De}. The following example shows declaring an encoder based on
 * {@link En}:
 * <pre>{@code
 *     public class MyEncoder extends AbstractByteCoder.En {...}
 * }</pre>
 * <p>
 * These methods are minimize required to implement:
 * <ul>
 *     <li>
 *         {@link #doCode(byte[], int, int, byte[], int, int, long, boolean)};
 *     </li>
 *     <li>
 *         {@link #getOutputSize(int, long, boolean)};
 *     </li>
 * </ul>
 * And these methods can be overridden as needed:
 * <ul>
 *     <li>
 *         {@link #checkInputSize(int, boolean)};
 *     </li>
 *     <li>
 *         {@link #checkRemainingSpace(int, int)};
 *     </li>
 *     <li>
 *         {@link #streamEncoder()};
 *     </li>
 * </ul>
 *
 * @author sunqian
 */
public abstract class AbstractByteCoder implements ByteCoder {

    private static final String REMAINING_NOT_ENOUGH = "Remaining space is not enough.";
    private static final String INPUT_SIZE_ILLEGAL = "Input size is illegal: ";

    /**
     * This method is used to estimate the output size in bytes of input data of specified size after encoding/decoding.
     * If the exact output size cannot be determined, returns a maximum value that can accommodate all output, or
     * {@code -1} if the estimating fails.
     * <p>
     * The {@code startPos} and {@code end} parameters form a context that indicates the position of the input data
     * within the entire data. This method can determine the consumption of the input data based on the context and
     * returns the output size corresponding to the actual consumption size. Unconsumed data will be buffered and passed
     * at next invocation. Typically, this method and {@link #doCode(byte[], int, int, byte[], int, int, long, boolean)}
     * are invoked in sequence, so their data consumption algorithm must remain consistent. Moreover, if this method
     * returns {@code 0}, the {@code doCode} method will not be invoked.
     * <p>
     * By default, {@code inputSize} is validated once by {@link #checkInputSize(int, boolean)} before being passed to
     * this method. Therefore, there is no need to invoke {@link #checkInputSize(int, boolean)} again when implementing
     * this method.
     *
     * @param inputSize specified size of input data
     * @param startPos  the position of the first byte of the input data within the entire data
     * @param end       whether the input data is the last segment of the entire data
     * @return output size in bytes of input data of specified size after encoding/decoding
     * @throws EncodingException for encoding error
     * @throws DecodingException for decoding error
     */
    protected abstract int getOutputSize(
        int inputSize,
        long startPos,
        boolean end
    ) throws EncodingException, DecodingException;

    /**
     * This method encodes/decodes source data (from source start index to source end index) and writes result into
     * destination byte array (from destination start index to destination end index). It returns a long value of which
     * high 32 bits indicates read bytes number, low 32 bits indicates written bytes number. Using
     * {@link #buildDoCodeResult(int, int)} can build the return value.
     * <p>
     * The {@code startPos} and {@code end} parameters form a context that indicates the position of the given source
     * data within the entire source data. This method can determine the consumption of the source data based on the
     * context and writes the result corresponding to the actual consumption data into destination. Unconsumed data will
     * be buffered and passed at next invocation. Typically, {@link #getOutputSize(int, long, boolean)} and this method
     * are invoked in sequence, so their data consumption algorithm must remain consistent. Moreover, if the
     * {@code getOutputSize} returns {@code 0}, this method will not be invoked.
     * <p>
     * By default, the passed arguments will not be null, and bounds of source and destination have already been
     * validated by {@link #checkInputSize(int, boolean)}, {@link #checkRemainingSpace(int, int)} and
     * {@link #getOutputSize(int, long, boolean)}. Therefore, there is no need for duplicate validation.
     *
     * @param src      source data
     * @param srcOff   source start index
     * @param srcEnd   source end index
     * @param dst      destination byte array
     * @param dstOff   destination start index
     * @param dstEnd   destination end index
     * @param startPos the position of the first byte of the source data within the entire data
     * @param end      whether the source data is the last segment of the entire data
     * @return a long value of which high 32 bits indicates read bytes number, low 32 bits indicates written bytes
     * number
     * @throws EncodingException for encoding error
     * @throws DecodingException for decoding error
     */
    protected abstract long doCode(
        byte[] src,
        int srcOff,
        int srcEnd,
        byte[] dst,
        int dstOff,
        int dstEnd,
        long startPos,
        boolean end
    ) throws EncodingException, DecodingException;

    /**
     * Helps build the read-write-value from {@link #doCode(byte[], int, int, byte[], int, int, long, boolean)}, high 32
     * bits indicates read bytes number, low 32 bits indicates written bytes number.
     *
     * @param readSize  read size
     * @param writeSize write size
     * @return read-write-value from {@link #doCode(byte[], int, int, byte[], int, int, long, boolean)}
     */
    protected long buildDoCodeResult(int readSize, int writeSize) {
        long lr = readSize;
        long lw = writeSize;
        return lr << 32 | (lw & 0x00000000ffffffffL);
    }

    /**
     * Checks input size, default implementation like:
     * <pre>{@code
     *     if (inputSize < 0) {
     *         throw new EncodingException("Input size is illegal: -1.");
     *     }
     * }</pre>
     *
     * @param inputSize size of input data
     * @throws EncodingException if size of input data is illegal for encoding
     * @throws DecodingException if size of input data is illegal for decoding
     */
    protected abstract void checkInputSize(int inputSize, boolean end) throws EncodingException, DecodingException;

    /**
     * Checks remaining space, default implementation like:
     * <pre>{@code
     *     if (srcRemaining > dstRemaining) {
     *         throw new EncodingException("Remaining space is not enough.");
     *     }
     * }</pre>
     *
     * @param srcRemaining remaining size of source data
     * @param dstRemaining remaining size of destination space
     * @throws EncodingException if remaining space is not enough for encoding
     * @throws DecodingException if remaining space is not enough for decoding
     */
    protected abstract void checkRemainingSpace(
        int srcRemaining,
        int dstRemaining
    ) throws EncodingException, DecodingException;

    private byte[] doCode(byte[] source, long startPos, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(startPos, source.length, end);
        if (outputSize <= 0) {
            return JieBytes.emptyBytes();
        }
        byte[] dst = new byte[outputSize];
        int len = getActualWriteSize(
            doCode(source, 0, source.length, dst, 0, outputSize, startPos, end)
        );
        if (len == dst.length) {
            return dst;
        }
        return Arrays.copyOf(dst, len);
    }

    private ByteBuffer doCode(ByteBuffer source, long startPos, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(startPos, source.remaining(), end);
        if (outputSize <= 0) {
            return JieBytes.emptyBuffer();
        }
        byte[] dst = new byte[outputSize];
        long doCodeResult;
        if (source.hasArray()) {
            doCodeResult = doCode(
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dst,
                0,
                outputSize,
                startPos,
                end
            );
            source.position(source.position() + getActualReadSize(doCodeResult));
        } else {
            byte[] s = new byte[source.remaining()];
            int oldPos = source.position();
            source.get(s);
            doCodeResult = doCode(s, 0, s.length, dst, 0, outputSize, startPos, end);
            source.position(oldPos + getActualReadSize(doCodeResult));
        }
        int writeSize = getActualWriteSize(doCodeResult);
        if (writeSize == dst.length) {
            return ByteBuffer.wrap(dst);
        }
        return ByteBuffer.wrap(Arrays.copyOf(dst, writeSize));
    }

    private int doCode(byte[] source, byte[] dest, long startPos, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(startPos, source.length, end);
        if (outputSize <= 0) {
            return 0;
        }
        checkRemainingSpace(outputSize, dest.length);
        return getActualWriteSize(
            doCode(source, 0, source.length, dest, 0, outputSize, startPos, end)
        );
    }

    private int doCode(ByteBuffer source, ByteBuffer dest, long startPos, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(startPos, source.remaining(), end);
        if (outputSize <= 0) {
            return 0;
        }
        checkRemainingSpace(outputSize, dest.remaining());
        if (source.hasArray() && dest.hasArray()) {
            int oldPos = source.position();
            long doCodeResult = doCode(
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dest.array(),
                JieBuffer.getArrayStartIndex(dest),
                JieBuffer.getArrayStartIndex(dest) + outputSize,
                startPos,
                end
            );
            source.position(oldPos + getActualReadSize(doCodeResult));
            dest.position(dest.position() + getActualWriteSize(doCodeResult));
            return getActualWriteSize(doCodeResult);
        } else {
            ByteBuffer dst = doCode(source, startPos, end);
            int oldPos = dest.position();
            dest.put(dst);
            return dest.position() - oldPos;
        }
    }

    private int getActualReadSize(long doCodeResult) {
        return (int) (doCodeResult >>> 32);
    }

    private int getActualWriteSize(long doCodeResult) {
        return (int) doCodeResult;
    }

    private int getSafeOutputSize(long startPos, int inputSize, boolean end) {
        checkInputSize(inputSize, end);
        return getOutputSize(inputSize, startPos, end);
    }

    @Override
    public int getOutputSize(int inputSize) throws CodingException {
        return getSafeOutputSize(0, inputSize, true);
    }

    /**
     * This method generated a {@link BytesProcessor.Encoder} based on encoding/decoding algorithm of
     * {@link #getOutputSize(int, long, boolean)} and
     * {@link #doCode(byte[], int, int, byte[], int, int, long, boolean)}, and then wrap the generated encoder via
     * {@link JieIO#bufferedEncoder(BytesProcessor.Encoder)}.
     *
     * @return a {@link BytesProcessor.Encoder} based on current encoding/decoding algorithm
     */
    @Override
    public BytesProcessor.Encoder streamEncoder() {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {

            private long startPos = 0;

            @Override
            public ByteBuffer encode(ByteBuffer data, boolean end) {
                int pos = data.position();
                ByteBuffer ret = doCode(data, startPos, end);
                startPos += (data.position() - pos);
                return ret;
            }
        };
        return JieIO.bufferedEncoder(encoder);
    }

    /**
     * Abstract skeletal implementation of {@link ByteEncoder}, see {@link AbstractByteCoder} for more detail.
     *
     * @author sunqian
     * @see AbstractByteCoder
     */
    public abstract static class En extends AbstractByteCoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            return super.doCode(source, 0, true);
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            return super.doCode(source, 0, true);
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            return super.doCode(source, dest, 0, true);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            return super.doCode(source, dest, 0, true);
        }

        @Override
        protected void checkInputSize(int inputSize, boolean end) throws EncodingException, DecodingException {
            if (inputSize < 0) {
                throw new EncodingException(INPUT_SIZE_ILLEGAL + inputSize + ".");
            }
        }

        protected void checkRemainingSpace(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new EncodingException(REMAINING_NOT_ENOUGH);
            }
        }
    }

    /**
     * Abstract skeletal implementation of {@link ByteDecoder}, see {@link AbstractByteCoder} for more detail.
     *
     * @author sunqian
     * @see AbstractByteCoder
     */
    public abstract static class De extends AbstractByteCoder implements ByteDecoder {

        @Override
        public byte[] decode(byte[] data) throws DecodingException {
            return super.doCode(data, 0, true);
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            return super.doCode(data, 0, true);
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            return super.doCode(data, dest, 0, true);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            return super.doCode(data, dest, 0, true);
        }

        @Override
        protected void checkInputSize(int inputSize, boolean end) throws EncodingException, DecodingException {
            if (inputSize < 0) {
                throw new DecodingException(INPUT_SIZE_ILLEGAL + inputSize + ".");
            }
        }

        protected void checkRemainingSpace(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new DecodingException(REMAINING_NOT_ENOUGH);
            }
        }
    }
}