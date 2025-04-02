package xyz.sunqian.common.encode;

import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.bytes.BytesProcessor;

/**
 * This is a static utilities class for {@code hex} encoding and decoding, provides encoder and decoder implementations:
 * {@link Encoder} and {@link Decoder}. All implementations are thread-safe.
 *
 * @author sunqian
 * @see Encoder
 * @see Decoder
 */
public class JieHex {

    /**
     * Returns a {@code hex} encoder.
     *
     * @return a {@code hex} encoder
     */
    public static Encoder encoder() {
        return HexEncoder.SINGLETON;
    }

    /**
     * Returns a {@code hex} decoder.
     *
     * @return a {@code hex} decoder
     */
    public static Decoder decoder() {
        return HexDecoder.SINGLETON;
    }

    /**
     * The implementation of {@link ByteEncoder} for {@code hex} encoding, thread-safe.
     *
     * @author sunqian
     */
    @ThreadSafe
    public interface Encoder extends ByteEncoder.ToLatin {

        /**
         * Returns -1. The {@code hex} doesn't require encoding in blocks.
         *
         * @return -1
         */
        @Override
        default int getBlockSize() {
            return -1;
        }

        /**
         * Returns twice the specified input size.
         *
         * @param inputSize specified input size
         * @return twice the specified input size
         * @throws EncodingException if input size is illegal
         */
        @Override
        int getOutputSize(int inputSize) throws EncodingException;

        /**
         * Returns a new {@link BytesProcessor.Encoder} which encapsulates current hex encoding, supports any size of
         * input data, not thread-safe.
         *
         * @return a {@link BytesProcessor.Encoder} with current hex encoding logic
         * @see BytesProcessor
         * @see BytesProcessor.Encoder
         */
        @Override
        BytesProcessor.Encoder streamEncoder();
    }

    /**
     * The implementation of {@link ByteDecoder} for {@code hex} decoding, thread-safe.
     *
     * @author sunqian
     */
    @ThreadSafe
    public interface Decoder extends ByteDecoder.FromLatin {

        /**
         * Returns 2. The size of {@code hex} data is even.
         *
         * @return 2
         */
        @Override
        default int getBlockSize() {
            return 2;
        }

        /**
         * Returns half of the specified input size.
         *
         * @param inputSize specified input size
         * @return half of the specified input size
         * @throws DecodingException if input size is illegal
         */
        @Override
        int getOutputSize(int inputSize) throws DecodingException;

        /**
         * Returns a new {@link BytesProcessor.Encoder} which encapsulates current hex decoding, supports any size of
         * input data, not thread-safe.
         *
         * @return a {@link BytesProcessor.Encoder} with current hex decoding logic
         * @see BytesProcessor
         * @see BytesProcessor.Encoder
         */
        @Override
        BytesProcessor.Encoder streamEncoder();
    }

    private static final class HexEncoder extends AbstractByteCoder.En implements Encoder {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        protected int getOutputSize(int inputSize, long startPos, boolean end) {
            return inputSize * 2;
        }

        protected long doCode(
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
        ) {
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            int readSize = srcEnd - srcOff;
            return buildDoCodeResult(readSize, readSize * 2);
        }
    }

    private static final class HexDecoder extends AbstractByteCoder.De implements Decoder {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        protected int getOutputSize(int inputSize, long startPos, boolean end) throws DecodingException {
            if (end && inputSize % 2 != 0) {
                throw new DecodingException("Hex decoding size must be even: " + inputSize + ".");
            }
            return inputSize / 2;
        }

        protected long doCode(
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
        ) {
            for (int i = dstOff, j = srcOff; i < dstEnd; i++) {
                int bits1 = toDigit((char) src[j], startPos, j);
                j++;
                int bits2 = toDigit((char) src[j], startPos, j);
                j++;
                int bits = ((bits1 << 4) | bits2);
                dst[i] = (byte) bits;
            }
            int writeSize = dstEnd - dstOff;
            return buildDoCodeResult(writeSize * 2, writeSize);
        }

        private int toDigit(char c, long startPos, int index) {
            if (c >= '0' && c <= '9') {
                return c - '0';
            }
            if (c >= 'a' && c <= 'f') {
                return c - 'a' + 10;
            }
            if (c >= 'A' && c <= 'F') {
                return c - 'A' + 10;
            }
            throw new DecodingException("Invalid hex char at pos " + (startPos + index) + ": " + c + ".");
        }
    }
}
