package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.ByteStream;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is a static utilities class provides implementations and utilities for {@code Base64} encoder and decoder,
 * mainly specified in <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a>,
 * <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a> for {@code MIME}, and
 * <a href="http://www.ietf.org/rfc/rfc1421.txt">RFC 1421</a> for {@code PEM}.
 * <h2>Types</h2>
 * <p>
 * There are 3 types of {@code Base64}:
 * <ul>
 *     <li>
 *         {@code Basic}: The typical Base64 type, if no specified, it is generally refers to this type;
 *     </li>
 *     <li>
 *         {@code URL and Filename safe}: In this type, using '-' and '_' instead of '+' and '/';
 *     </li>
 *     <li>
 *         {@code Block Mode}: Result of encoding is separated by specified separator (such as \r\n). Common block
 *         types include {@code MIME} (output block size is 76, no separator added to the last block) and {@code PEM} (
 *         output block size is 64, adding separator to the last block);
 *     </li>
 * </ul>
 * <h2>Stream Encoder and Block Size</h2>
 * <p>
 * {@link Encoder#streamEncoder()} always returns a new encoder wrapped by
 * {@link ByteStream#roundEncoder(ByteStream.Encoder, int)}, and it can process any size of data.
 * Even though {@link Encoder#getBlockSize()} returns 3, it's best to set {@link ByteStream#blockSize(int)} to a
 * reasonable value, such as {@code 384 * 3}, for better performance.
 *
 * @author sunqian
 */
public class JieBase64 {

    /**
     * Returns a {@code Base64} encoder in type of {@code Basic}, with padding character if the length of source is not
     * a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code Basic}
     */
    public static Encoder encoder() {
        return BasicEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code Basic}. without padding character if the length of source is
     * not a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code Basic}
     */
    public static Encoder encoder(boolean padding) {
        return padding ? encoder() : BasicEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code URL and Filename safe}, with padding character if the length
     * of source is not a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder() {
        return UrlEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code URL and Filename safe}, without padding character if the
     * length of source is not a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder(boolean padding) {
        return padding ? urlEncoder() : UrlEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code MIME}, with padding character if the length of source is not a
     * multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code MIME}
     */
    public static Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code MIME}, without padding character if the length of source is
     * not a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code MIME}
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code PEM}, with padding character if the length of source is not a
     * multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code PEM}
     */
    public static Encoder pemEncoder() {
        return PemEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code PEM}, without padding character if the length of source is not
     * a multiple of 3.
     *
     * @return a {@code Base64} encoder in type of {@code PEM}
     */
    public static Encoder pemEncoder(boolean padding) {
        return padding ? pemEncoder() : PemEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code Block Mode}, with specified arguments.
     *
     * @param blockSize      Sets the max block size, must be a multiple of {@code 4}.
     * @param blockSeparator Sets the block separator. The array will be used directly, any modification to array will
     *                       affect the encoding.
     * @param padding        Whether add padding character at the end if the length of source is not a multiple of 3.
     * @param lastSeparator  Whether add block separator at tail if the output size is not multiple of block size.
     * @return a {@code Base64} encoder in type of {@code Block Mode}
     */
    public static Encoder blockEncoder(
        int blockSize, byte[] blockSeparator, boolean padding, boolean lastSeparator) throws EncodingException {
        if (blockSize <= 0) {
            throw new EncodingException("Block size must be positive.");
        }
        if (blockSize % 4 != 0) {
            throw new EncodingException("Block size must be multiple of 4.");
        }
        return new BlockEncoder(blockSize, blockSeparator, padding, lastSeparator);
    }

    /**
     * Returns a {@code Base64} decoder in type of {@code Basic}, supports both {@code padding} or {@code no-padding}.
     *
     * @return a {@code Base64} encoder in type of {@code Basic}
     */
    public static Decoder decoder() {
        return BasicDecoder.SINGLETON;
    }

    /**
     * {@code Base64} encoder.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {

        /**
         * Returns 3 for {@code Base64} encoding.
         *
         * @return 3
         */
        @Override
        default int getBlockSize() {
            return 3;
        }
    }

    /**
     * {@code Base64} decoder.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {

        /**
         * Returns 4 for {@code Base64} decoding.
         *
         * @return 3
         */
        @Override
        default int getBlockSize() {
            return 4;
        }
    }

    private static abstract class AbsEncoder extends AbsCoder.En implements Encoder {

        private static final char[] DICT = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        protected final boolean padding;

        protected AbsEncoder(boolean padding) {
            this.padding = padding;
        }

        @Override
        public int getOutputSize(int inputSize) throws EncodingException {
            if (inputSize < 0) {
                throw new EncodingException("Base64 encoding size can not be negative.");
            }
            if (padding) {
                return ((inputSize + 2) / 3) * 4;
            }
            int remainder = inputSize % 3;
            if (remainder == 0) {
                return inputSize / 3 * 4;
            }
            return inputSize / 3 * 4 + remainder + 1;
        }

        @Override
        public ByteStream.Encoder streamEncoder() {
            return ByteStream.roundEncoder(this, getBlockSize());
        }

        protected char[] dict() {
            return DICT;
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int dstPos = dstOff;
            int roundLen = (srcEnd - srcOff) / 3 * 3;
            int roundEnd = srcPos + roundLen;
            for (int i = srcPos, j = dstPos; i < roundEnd; ) {
                int bits = (src[i++] & 0xff) << 16 | (src[i++] & 0xff) << 8 | (src[i++] & 0xff);
                dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                dst[j++] = (byte) dict[bits & 0x3f];
            }
            srcPos = roundEnd;
            dstPos += roundLen / 3 * 4;
            // 1 or 2 leftover bytes
            if (srcPos < srcEnd) {
                int b0 = src[srcPos++] & 0xff;
                dst[dstPos++] = (byte) dict[b0 >> 2];
                if (srcPos == srcEnd) {
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                        dst[dstPos++] = '=';
                    }
                } else {
                    int b1 = src[srcPos] & 0xff;
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dstPos++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                    }
                }
            }
            return dstPos - dstOff;
        }
    }

    private static final class BasicEncoder extends AbsEncoder {

        private static final BasicEncoder PADDING = new BasicEncoder(true);
        private static final BasicEncoder NO_PADDING = new BasicEncoder(false);

        private BasicEncoder(boolean padding) {
            super(padding);
        }
    }

    private static final class UrlEncoder extends AbsEncoder {

        private static final char[] DICT = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        };

        private static final UrlEncoder PADDING = new UrlEncoder(true);
        private static final UrlEncoder NO_PADDING = new UrlEncoder(false);

        private UrlEncoder(boolean padding) {
            super(padding);
        }

        @Override
        protected char[] dict() {
            return DICT;
        }
    }

    private static class BlockEncoder extends AbsEncoder {

        private static final byte[] BLOCK_SEPARATOR = new byte[]{'\r', '\n'};

        private final int blockSize;
        private final byte[] blockSeparator;
        private final boolean lastSeparator;

        private BlockEncoder(int blockSize, byte[] blockSeparator, boolean padding, boolean lastSeparator) {
            super(padding);
            this.blockSize = blockSize;
            this.blockSeparator = blockSeparator;
            this.lastSeparator = lastSeparator;
        }

        @Override
        public int getOutputSize(int inputSize) {
            int outputSize = super.getOutputSize(inputSize);
            if (!lastSeparator) {
                outputSize += (outputSize - 1) / blockSize * blockSeparator.length;
                return outputSize;
            }
            int blockCount = outputSize / blockSize;
            int remainder = outputSize % blockSize;
            if (remainder == 0) {
                outputSize += blockCount * blockSeparator.length;
            } else {
                outputSize += (blockCount + 1) * blockSeparator.length;
            }
            return outputSize;
        }

        @Override
        public int getBlockSize() {
            return blockSize / 4 * 3 * 20;
        }

        @Override
        public ByteStream.Encoder streamEncoder() {
            ByteStream.Encoder encoder = new ByteStream.Encoder() {

                private boolean hasPrev = false;

                @Override
                public ByteBuffer encode(ByteBuffer data, boolean end) {
                    if (hasPrev) {
                        if (end && !data.hasRemaining()) {
                            return JieBytes.emptyBuffer();
                        }
                        ByteBuffer ret = ByteBuffer.allocate(getOutputSize(data.remaining()) + blockSeparator.length);
                        for (byte b : blockSeparator) {
                            ret.put(b);
                        }
                        BlockEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                    hasPrev = true;
                    return BlockEncoder.this.encode(data);
                }
            };
            return ByteStream.roundEncoder(encoder, getBlockSize());
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int srcBlock = blockSize / 4 * 3;
            int roundEnd = srcOff + ((srcEnd - srcOff) / 3 * 3);
            int dstPos = dstOff;
            while (srcPos < roundEnd) {
                int readEnd = Math.min(srcPos + srcBlock, roundEnd);
                for (int i = srcPos, j = dstPos; i < readEnd; ) {
                    int bits = (src[i++] & 0xff) << 16 | (src[i++] & 0xff) << 8 | (src[i++] & 0xff);
                    dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                    dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                    dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                    dst[j++] = (byte) dict[bits & 0x3f];
                }
                int writeLen = (readEnd - srcPos) / 3 * 4;
                dstPos += writeLen;
                srcPos = readEnd;
                if (writeLen == blockSize && (lastSeparator || srcPos < srcEnd)) {
                    for (byte b : blockSeparator) {
                        dst[dstPos++] = b;
                    }
                }
            }
            // 1 or 2 leftover bytes
            if (srcPos < srcEnd) {
                int b0 = src[srcPos++] & 0xff;
                dst[dstPos++] = (byte) dict[b0 >> 2];
                if (srcPos == srcEnd) {
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                        dst[dstPos++] = '=';
                    }
                } else {
                    int b1 = src[srcPos] & 0xff;
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dstPos++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                    }
                }
                if (lastSeparator) {
                    for (byte b : blockSeparator) {
                        dst[dstPos++] = b;
                    }
                }
            }
            return dstPos - dstOff;
        }
    }

    private static final class MimeEncoder extends BlockEncoder {

        private static final int BLOCK_SIZE = 76;

        private static final MimeEncoder PADDING = new MimeEncoder(BLOCK_SIZE, BlockEncoder.BLOCK_SEPARATOR, true);
        private static final MimeEncoder NO_PADDING = new MimeEncoder(BLOCK_SIZE, BlockEncoder.BLOCK_SEPARATOR, false);

        private MimeEncoder(int blockSize, byte[] blockSeparator, boolean padding) {
            super(blockSize, blockSeparator, padding, false);
        }
    }

    private static final class PemEncoder extends BlockEncoder {

        private static final int BLOCK_SIZE = 64;

        private static final PemEncoder PADDING = new PemEncoder(BLOCK_SIZE, BlockEncoder.BLOCK_SEPARATOR, true);
        private static final PemEncoder NO_PADDING = new PemEncoder(BLOCK_SIZE, BlockEncoder.BLOCK_SEPARATOR, false);

        private PemEncoder(int blockSize, byte[] blockSeparator, boolean padding) {
            super(blockSize, blockSeparator, padding, false);
        }
    }

    private static abstract class AbsDecoder extends AbsCoder.De implements Decoder {

        private static final byte[] DICT = new byte[Byte.MAX_VALUE];

        static {
            Arrays.fill(DICT, (byte) -1);
            for (int i = 0; i < AbsEncoder.DICT.length; i++) {
                int c = AbsEncoder.DICT[i];
                DICT[c & 0xff] = (byte) i;
            }
            DICT['-'] = DICT['+'];
            DICT['_'] = DICT['/'];
            DICT['='] = -2;
        }

        protected AbsDecoder() {
        }

        @Override
        public int getOutputSize(int inputSize) throws DecodingException {
            if (inputSize < 0) {
                throw new DecodingException("Base64 decoding size can not be negative.");
            }
            int remainder = inputSize % 4;
            if (remainder == 0) {
                return inputSize / 4 * 3;
            }
            if (remainder == 1) {
                throw new DecodingException("Base64 decoding will not has 1 remainder left.");
            }
            return inputSize / 4 * 3 + remainder - 1;
        }

        @Override
        public int getBlockSize() {
            return 384 * 4;
        }

        @Override
        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            // No checking, because no determine.
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            int srcPos = srcOff;
            int dstPos = dstOff;
            int bits = 0;
            int shiftTo = 18;
            while (srcPos < srcEnd) {
                int c = src[srcPos++] & 0xff;
                int b = DICT[c];
                if (b < 0) {
                    if (b == -2) {
                        // must be padding end with xx== or xxx=
                        // xx==
                        if (shiftTo == 6 && srcPos == srcEnd - 1 && src[srcPos] == '=') {
                            dst[dstPos++] = (byte) (bits >> 16);
                            return dstPos - dstOff;
                        }
                        // xxx=
                        if (shiftTo == 0 && srcPos == srcEnd) {
                            dst[dstPos++] = (byte) (bits >> 16);
                            dst[dstPos++] = (byte) (bits >> 8);
                            return dstPos - dstOff;
                        }
                    }
                    throw new DecodingException("Invalid base64 char: " + ((char) c) + ".");
                }
                bits |= (b << shiftTo);
                shiftTo -= 6;
                if (shiftTo < 0) {
                    dst[dstPos++] = (byte) (bits >> 16);
                    dst[dstPos++] = (byte) (bits >> 8);
                    dst[dstPos++] = (byte) (bits);
                    shiftTo = 18;
                    bits = 0;
                }
            }
            if (shiftTo == 6) {
                dst[dstPos++] = (byte) (bits >> 16);
            } else if (shiftTo == 0) {
                dst[dstPos++] = (byte) (bits >> 16);
                dst[dstPos++] = (byte) (bits >> 8);
            } else if (shiftTo != 18) {
                throw new DecodingException("Invalid base64 tail without padding, must be 2 or 3 remainder left.");
            }
            return dstPos - dstOff;
        }
    }

    private static final class BasicDecoder extends AbsDecoder {

        private static final BasicDecoder SINGLETON = new BasicDecoder();

        private BasicDecoder() {
            super();
        }
    }

    private static final class MimeDecoder extends AbsDecoder {

        private static final MimeDecoder SINGLETON = new MimeDecoder();

        private MimeDecoder() {
            super();
        }
    }
}
