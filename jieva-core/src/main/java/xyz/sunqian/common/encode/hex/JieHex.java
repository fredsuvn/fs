package xyz.sunqian.common.encode.hex;

import xyz.sunqian.common.encode.*;
import xyz.sunqian.common.io.ByteStream;

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
     * {@code Hex} encoder implementation. {@link #getBlockSize()} and {@link #streamEncoder()} are overridden and
     * require attention.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {

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
         * Returns a singleton thread-safe stream encoder object, accepts any size of input data.
         *
         * @return a singleton thread-safe stream encoder object
         */
        @Override
        ByteStream.Encoder streamEncoder();
    }

    /**
     * {@code Hex} decoder implementation. {@link #getBlockSize()} and {@link #streamEncoder()} are overridden and
     * require attention.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {

        /**
         * Returns 2. Data size for {@code Hex} decoding should be even, so that minimal block size for {@code Hex}
         * decoding is 2.
         *
         * @return 1
         */
        @Override
        default int getBlockSize() {
            return 2;
        }

        /**
         * Returns a new stream decoder. The decoder is wrapped by
         * {@link ByteStream#roundEncoder(ByteStream.Encoder, int)} to keep size of input data is even. Although the
         * decoder accepts any size of input data, it is recommended that sets an even block size for a better
         * performance.
         *
         * @return a new stream decoder wrapped by {@link ByteStream#roundEncoder(ByteStream.Encoder, int)}
         */
        @Override
        ByteStream.Encoder streamEncoder();
    }

    private static final class HexEncoder extends AbstractByteCoder.En implements Encoder {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        public int getOutputSize(int inputSize, boolean end) throws EncodingException {
            if (inputSize < 0) {
                throw new EncodingException("Hex encoding size can not be negative.");
            }
            return inputSize * 2;
        }

        protected long doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            return (srcEnd - srcOff) * 2;
        }
    }

    private static final class HexDecoder extends AbstractByteCoder.De implements Decoder {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        public int getOutputSize(int inputSize, boolean end) throws DecodingException {
            if (inputSize < 0) {
                throw new DecodingException("Hex decoding size can not be negative.");
            }
            if (inputSize % 2 != 0) {
                throw new DecodingException("Hex decoding size must be even.");
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
            return length / 2;
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
