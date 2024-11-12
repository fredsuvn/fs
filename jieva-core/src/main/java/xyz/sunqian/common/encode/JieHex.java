package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;

/**
 * This is a static utilities class provides implementations and utilities for {@code Hex} encoder and decoder.
 * <p>
 * {@link Encoder#streamEncoder()} always returns a singleton thread-safe object, and it can process any size of data.
 * Even though {@link Encoder#getBlockSize()} returns 1, it's best to set {@link ByteStream#blockSize(int)} to a
 * reasonable value, such as {@code 1024}, for better performance.
 * <p>
 * {@link Decoder#streamEncoder()} always returns a new decoder wrapped by
 * {@link ByteStream#bufferedEncoder(ByteStream.Encoder)}. Although it accepts both odd and even size of data at per
 * decoding, it's best to set even block size for better performance. {@link Decoder#getBlockSize()} returns minimal
 * block size: 2, but like the encoder, set a reasonable block size such as {@code 1024}.
 *
 * @author sunqian
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
     * {@code Hex} encoder.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {

        /**
         * Returns 1 because {@code Hex} encoding is applicable to any size of data.
         *
         * @return 1
         */
        @Override
        default int getBlockSize() {
            return 1;
        }
    }

    /**
     * {@code Hex} decoder.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {

        /**
         * Returns 2 because data size for {@code Hex} decoding should be even.
         *
         * @return 1
         */
        @Override
        default int getBlockSize() {
            return 2;
        }
    }

    private static final class HexEncoder extends AbsCoder.En implements Encoder {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        public int getOutputSize(int inputSize) throws EncodingException {
            if (inputSize < 0) {
                throw new EncodingException("Hex encoding size can not be negative.");
            }
            return inputSize * 2;
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            return (srcEnd - srcOff) * 2;
        }
    }

    private static final class HexDecoder extends AbsCoder.De implements Decoder {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        public int getOutputSize(int inputSize) throws DecodingException {
            if (inputSize < 0) {
                throw new DecodingException("Hex decoding size can not be negative.");
            }
            if (inputSize % 2 != 0) {
                throw new DecodingException("Hex decoding size must be even.");
            }
            return inputSize / 2;
        }

        @Override
        public ByteStream.Encoder streamEncoder() {
            return ByteStream.roundEncoder(this, getBlockSize());
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            int length = srcEnd - srcOff;
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits1 = toDigit((char) src[i++]);
                int bits2 = toDigit((char) src[i++]);
                int bits = ((bits1 << 4) | bits2);
                dst[j++] = (byte) bits;
            }
            return length / 2;
        }

        private int toDigit(char c) {
            if (c >= '0' && c <= '9') {
                return c - '0';
            }
            if (c >= 'a' && c <= 'f') {
                return c - 'a' + 10;
            }
            if (c >= 'A' && c <= 'F') {
                return c - 'A' + 10;
            }
            throw new DecodingException("Invalid hex char: " + c + ".");
        }
    }
}
