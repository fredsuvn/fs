package xyz.sunqian.common.encode;

import xyz.sunqian.common.io.ByteStream;
import xyz.sunqian.common.io.JieBuffer;

import java.nio.ByteBuffer;

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

    private static final class HexEncoder implements Encoder, ByteStream.Encoder {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        public byte[] encode(byte[] source) throws EncodingException {
            int outputSize = getOutputSize(source.length);
            byte[] dst = new byte[outputSize];
            encode0(source, 0, source.length, dst, 0);
            return dst;
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws EncodingException {
            int outputSize = getOutputSize(source.remaining());
            byte[] dst = new byte[outputSize];
            ByteBuffer ret = ByteBuffer.wrap(dst);
            if (source.hasArray()) {
                encode0(
                    source.array(),
                    JieBuffer.getArrayStartIndex(source),
                    JieBuffer.getArrayEndIndex(source),
                    dst,
                    0
                );
                source.position(source.limit());
            } else {
                byte[] s = new byte[source.remaining()];
                source.get(s);
                encode0(s, 0, s.length, dst, 0);
            }
            return ret;
        }

        @Override
        public int encode(byte[] source, byte[] dest) throws EncodingException {
            int outputSize = getOutputSize(source.length);
            EncodeMisc.checkEncodingRemaining(outputSize, dest.length);
            return encode0(source, 0, source.length, dest, 0);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws EncodingException {
            int outputSize = getOutputSize(source.remaining());
            EncodeMisc.checkEncodingRemaining(outputSize, dest.remaining());
            if (source.hasArray() && dest.hasArray()) {
                encode0(
                    source.array(),
                    JieBuffer.getArrayStartIndex(source),
                    JieBuffer.getArrayEndIndex(source),
                    dest.array(),
                    JieBuffer.getArrayStartIndex(dest)
                );
                source.position(source.limit());
                dest.position(dest.position() + outputSize);
            } else {
                ByteBuffer dst = encode(source);
                dest.put(dst);
            }
            return outputSize;
        }

        @Override
        public int getOutputSize(int inputSize) {
            return inputSize * 2;
        }

        @Override
        public int getBlockSize() {
            return 1024;
        }

        @Override
        public ByteStream.Encoder toStreamEncoder() {
            return this;
        }

        @Override
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            return encode(data);
        }

        private int encode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            for (int i = srcOff, j = dstOff; i < srcEnd; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            return (srcEnd - srcOff) * 2;
        }
    }

    private static final class HexDecoder implements Decoder, ByteStream.Encoder {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        public byte[] decode(byte[] data) throws DecodingException {
            int outputSize = getOutputSize(data.length);
            byte[] dst = new byte[outputSize];
            decode0(data, 0, data.length, dst, 0);
            return dst;
        }

        @Override
        public ByteBuffer decode(ByteBuffer data) throws DecodingException {
            int outputSize = getOutputSize(data.remaining());
            byte[] dst = new byte[outputSize];
            ByteBuffer ret = ByteBuffer.wrap(dst);
            if (data.hasArray()) {
                decode0(
                    data.array(),
                    JieBuffer.getArrayStartIndex(data),
                    JieBuffer.getArrayEndIndex(data),
                    dst,
                    0
                );
                data.position(data.limit());
            } else {
                byte[] s = new byte[data.remaining()];
                data.get(s);
                decode0(s, 0, s.length, dst, 0);
            }
            return ret;
        }

        @Override
        public int decode(byte[] data, byte[] dest) throws DecodingException {
            int outputSize = getOutputSize(data.length);
            EncodeMisc.checkDecodingRemaining(outputSize, dest.length);
            return decode0(data, 0, data.length, dest, 0);
        }

        @Override
        public int decode(ByteBuffer data, ByteBuffer dest) throws DecodingException {
            int outputSize = getOutputSize(data.remaining());
            EncodeMisc.checkDecodingRemaining(outputSize, dest.remaining());
            if (data.hasArray() && dest.hasArray()) {
                decode0(
                    data.array(),
                    JieBuffer.getArrayStartIndex(data),
                    JieBuffer.getArrayEndIndex(data),
                    dest.array(),
                    JieBuffer.getArrayStartIndex(dest)
                );
                data.position(data.limit());
                dest.position(dest.position() + outputSize);
            } else {
                ByteBuffer dst = decode(data);
                dest.put(dst);
            }
            return outputSize;
        }

        @Override
        public int getOutputSize(int inputSize) {
            return inputSize / 2;
        }

        @Override
        public int getBlockSize() {
            return 1024;
        }

        @Override
        public ByteStream.Encoder toStreamEncoder() {
            return this;
        }

        @Override
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            return decode(data);
        }

        private int decode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
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
