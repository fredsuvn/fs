package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.BytesProcessor;

/**
 * This is a static utilities class for {@code Hex} encoding and decoding, provides encoder and decoder implementations:
 * {@link Encoder} and {@link Decoder}.
 *
 * @author sunqian
 * @see Encoder
 * @see Decoder
 */
public class JieHex {

    /**
     * Returns a {@code Hex} encoder.
     *
     * @return a {@code Hex} encoder
     */
    public static Encoder encoder() {
        return HexEncoder.SINGLETON;
    }

    /**
     * Returns a {@code Hex} decoder.
     *
     * @return a {@code Hex} decoder
     */
    public static Decoder decoder() {
        return HexDecoder.SINGLETON;
    }

    /**
     * {@code Hex} encoder implementation.
     *
     * @author sunqian
     */
    public interface Encoder extends ByteEncoder.ToLatin {

        /**
         * Returns 1. {@code Hex} encoding is applicable to any size of data so that returns 1.
         *
         * @return 1
         */
        @Override
        default int getBlockSize() {
            return 1;
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
         * @return a {@link BytesProcessor.Encoder} with current hex encoding
         * @see BytesProcessor
         * @see BytesProcessor.Encoder
         */
        @Override
        BytesProcessor.Encoder streamEncoder();
    }

    /**
     * {@code Hex} decoder implementation.
     *
     * @author sunqian
     */
    public interface Decoder extends ByteDecoder.ToLatin {

        /**
         * Returns 2. Data size for {@code Hex} decoding should be even, so that minimal block size for {@code Hex}
         * decoding is 2.
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
         * Returns a new {@link BytesProcessor.Encoder} which encapsulates current hex decoding, supports even size of
         * input data, not thread-safe.
         *
         * @return a {@link BytesProcessor.Encoder} with current hex decoding
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
        public int getOutputSize(int inputSize, boolean end) {
            return inputSize * 2;
        }

        protected long doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            return (srcEnd - srcOff) * 2L;
        }
    }

    private static final class HexDecoder extends AbstractByteCoder.De implements Decoder {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        public int getOutputSize(int inputSize, boolean end) throws DecodingException {
            if (inputSize % 2 != 0) {
                throw new DecodingException("Hex decoding size must be even: " + inputSize + ".");
            }
            return inputSize / 2;
        }

        protected long doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
            int length = srcEnd - srcOff;
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits1 = toDigit((char) src[i], startPos, i);
                i++;
                int bits2 = toDigit((char) src[i], startPos, i);
                i++;
                int bits = ((bits1 << 4) | bits2);
                dst[j++] = (byte) bits;
            }
            return length / 2L;
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
