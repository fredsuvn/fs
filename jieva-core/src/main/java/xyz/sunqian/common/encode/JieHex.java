package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;

/**
 * This is a static utilities class provides implementations and utilities for {@code Hex} encoder and decoder.
 * <h2>Encoder</h2>
 * <p>
 * {@link ByteEncoder#toStreamEncoder()} always returns a singleton thread-safe object, the best block size for
 * {@link ByteStream#blockSize(int)} is {@link ByteEncoder#getBlockSize()} or multiples of it, but any legal size
 * (&gt;0) are permitted.
 * <h2>Decoder</h2>
 * <p>
 * {@link ByteDecoder#toStreamEncoder()} always returns a singleton thread-safe object, the best block size for
 * {@link ByteStream#blockSize(int)} is {@link ByteDecoder#getBlockSize()} or multiples of it, or must be multiples of 2
 * (even).
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
     * {@code Hex} encoder, extends {@link ToCharEncoder}.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {
    }

    /**
     * {@code Hex} decoder, extends {@link ToCharDecoder}.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {
    }

    private static final class HexEncoder extends AbsCoder.En implements Encoder {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        public int getOutputSize(int inputSize) {
            return inputSize * 2;
        }

        @Override
        public int getBlockSize() {
            return 1024;
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
        public int getOutputSize(int inputSize) {
            return inputSize / 2;
        }

        @Override
        public int getBlockSize() {
            return 1024;
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            int length = srcEnd - srcOff;
            if (length % 2 != 0) {
                throw new DecodingException("Invalid hex string: length must be even.");
            }
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
