package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This abstract class provides skeletal implementation methods for {@link ByteEncoder} and {@link ByteDecoder}, and
 * skeletal implementation classes: {@link En} and {@link De}. The following example shows declare an encoder based on
 * {@link En}:
 * <pre>{@code
 *     public class MyEncoder extends AbstractByteCoder.En {...}
 * }</pre>
 * <p>
 * These methods are minimize required to implement:
 * <ul>
 *     <li>
 *         {@link #doCode(long, byte[], int, int, byte[], int, int, boolean)};
 *     </li>
 *     <li>
 *         {@link #getOutputSize(int, boolean)};
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
     * Returns output size in bytes for the specified input size. If the exact output size cannot be determined, returns
     * an estimated maximum value that can accommodate the output. Or {@code -1} if it cannot be estimated.
     * <p>
     * The {@code end} parameter tells whether specified input data to be encoded is the last segment of the entire
     * data. If the {@code end} parameter is false, it indicates that the data of specified input size may not be
     * expected to be fully consumed. The returned output size should only match the expected size of consumed data.
     * <p>
     * By default, {@code inputSize} is validated once by {@link #checkInputSize(int, boolean)} before being passed to
     * this method. Therefore, there is no need to invoke {@link #checkInputSize(int, boolean)} again when implementing
     * this method.
     *
     * @param inputSize specified input size
     * @param end       whether specified input data to be encoded is the last segment of the entire data
     * @return output size in bytes for the specified input size
     * @throws EncodingException for encoding error
     * @throws DecodingException for encoding error
     */
    protected abstract int getOutputSize(int inputSize, boolean end) throws EncodingException, DecodingException;

    /**
     * This method encodes/decodes source data (from source start index to source end index) and writes result into
     * destination byte array (from destination start index to destination end index). It returns a long value of which
     * high 32 bits indicates read bytes number, low 32 bits indicates written bytes number
     * ({@link #buildDoCodeResult(int, int)} can be used to build the return value). The source data within the
     * specified bounds does not need to be fully consumed, and the destination within the specified bounds does not
     * need to be fully filled.
     * <p>
     * By default, the passed arguments will not be null, and bounds of source and destination have already been
     * validated by {@link #checkInputSize(int, boolean)}, {@link #checkRemainingSpace(int, int)} and
     * {@link #getOutputSize(int, boolean)}. Therefore, there is no need for duplicate validation.
     *
     * @param startPos the position of the first byte of the current data within the entire data
     * @param src      source data
     * @param srcOff   source start index
     * @param srcEnd   source end index
     * @param dst      destination byte array
     * @param dstOff   destination start index
     * @param dstEnd   destination end index
     * @param end      whether the current data to be encoded is the last segment of the entire data
     * @return a long value of which high 32 bits indicates read byte number, low 32 bits indicates written byte number
     */
    protected abstract long doCode(
        long startPos,
        byte[] src,
        int srcOff,
        int srcEnd,
        byte[] dst,
        int dstOff,
        int dstEnd,
        boolean end
    ) throws EncodingException, DecodingException;

    /**
     * Helps build the read-write-value from {@link #doCode(long, byte[], int, int, byte[], int, int, boolean)}, high 32
     * bits indicates read bytes number, low 32 bits indicates written bytes number.
     *
     * @param readSize  read size
     * @param writeSize write size
     * @return read-write-value from {@link #doCode(long, byte[], int, int, byte[], int, int, boolean)}
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

    private byte[] doCode(long startPos, byte[] source, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(source.length, end);
        byte[] dst = new byte[outputSize];
        int len = getActualWriteSize(
            doCode(startPos, source, 0, source.length, dst, 0, outputSize, end)
        );
        if (len == dst.length) {
            return dst;
        }
        return Arrays.copyOf(dst, len);
    }

    private ByteBuffer doCode(long startPos, ByteBuffer source, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(source.remaining(), end);
        byte[] dst = new byte[outputSize];
        long doCodeResult;
        if (source.hasArray()) {
            doCodeResult = doCode(
                startPos,
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dst,
                0,
                outputSize,
                end
            );
            source.position(source.position() + getActualReadSize(doCodeResult));
        } else {
            byte[] s = new byte[source.remaining()];
            int oldPos = source.position();
            source.get(s);
            doCodeResult = doCode(startPos, s, 0, s.length, dst, 0, outputSize, end);
            source.position(oldPos + getActualReadSize(doCodeResult));
        }
        int writeSize = getActualWriteSize(doCodeResult);
        if (writeSize == dst.length) {
            return ByteBuffer.wrap(dst);
        }
        return ByteBuffer.wrap(Arrays.copyOf(dst, writeSize));
    }

    private int doCode(long startPos, byte[] source, byte[] dest, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(source.length, end);
        checkRemainingSpace(outputSize, dest.length);
        return getActualWriteSize(
            doCode(startPos, source, 0, source.length, dest, 0, outputSize, end)
        );
    }

    private int doCode(long startPos, ByteBuffer source, ByteBuffer dest, boolean end) throws EncodingException {
        int outputSize = getSafeOutputSize(source.remaining(), end);
        checkRemainingSpace(outputSize, dest.remaining());
        if (source.hasArray() && dest.hasArray()) {
            int oldPos = source.position();
            long doCodeResult = doCode(
                startPos,
                source.array(),
                JieBuffer.getArrayStartIndex(source),
                JieBuffer.getArrayEndIndex(source),
                dest.array(),
                JieBuffer.getArrayStartIndex(dest),
                JieBuffer.getArrayStartIndex(dest) + outputSize,
                end
            );
            source.position(oldPos + getActualReadSize(doCodeResult));
            dest.position(dest.position() + getActualWriteSize(doCodeResult));
            return getActualWriteSize(doCodeResult);
        } else {
            ByteBuffer dst = doCode(startPos, source, end);
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

    private int getSafeOutputSize(int inputSize, boolean end) {
        checkInputSize(inputSize, end);
        return getOutputSize(inputSize, end);
    }

    @Override
    public int getOutputSize(int inputSize) throws CodingException {
        return getSafeOutputSize(inputSize, true);
    }

    /**
     * The default implementation is wrapped by {@link JieIO#bufferedEncoder(BytesProcessor.Encoder)}. This means that
     * the input data does not need to be fully consumed, any unconsumed data will be buffered for the next invocation.
     *
     * @return a {@link BytesProcessor.Encoder} with current coding logic
     * @see BytesProcessor
     * @see BytesProcessor.Encoder
     */
    @Override
    public BytesProcessor.Encoder streamEncoder() {
        BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {

            private long startPos = 0;

            @Override
            public ByteBuffer encode(ByteBuffer data, boolean end) {
                int pos = data.position();
                ByteBuffer ret = doCode(startPos, data, end);
                startPos += (data.position() - pos);
                return ret;
            }
        };
        return JieIO.bufferedEncoder(encoder);
    }

    /**
     * Abstract skeletal implementation of {@link ByteEncoder}, see {@link AbstractByteCoder}.
     *
     * @author sunqian
     * @see AbstractByteCoder
     */
    public abstract static class En extends AbstractByteCoder implements ByteEncoder {

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            return super.doCode(0, source, true);
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            return super.doCode(0, source, true);
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            return super.doCode(0, source, dest, true);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            return super.doCode(0, source, dest, true);
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
     * Abstract skeletal implementation of {@link ByteDecoder}, see {@link AbstractByteCoder}.
     *
     * @author sunqian
     * @see AbstractByteCoder
     */
    public abstract static class De extends AbstractByteCoder implements ByteDecoder {

        @Override
        public byte[] decode(byte[] data) throws DecodingException {
            return super.doCode(0, data, true);
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            return super.doCode(0, data, true);
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            return super.doCode(0, data, dest, true);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            return super.doCode(0, data, dest, true);
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