package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.ByteStream;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is a static utilities class for {@code Base64} encoding and decoding, provides encoder and decoder
 * implementations: {@link Encoder} and {@link Decoder}. The algorithms are specified in:
 * <ul>
 *     <li>{@code Basic}: <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a></li>
 *     <li>{@code MIME}: <a href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a></li>
 *     <li>{@code PEM}: <a href="http://www.ietf.org/rfc/rfc1421.txt">RFC 1421</a></li>
 * </ul>
 * This class provides 3 types of {@code Base64}:
 * <ul>
 *     <li>
 *         {@code Basic}: Typical Base64 type, if no specified, it is generally refers to this type;
 *     </li>
 *     <li>
 *         {@code URL Safe}: In this type, using '-' and '_' instead of '+' and '/';
 *     </li>
 *     <li>
 *         {@code Separation}: Encoding result is separated by specified separator (such as \r\n). For example:
 *         <ul>
 *             <li>
 *                 {@code MIME}: separated in 76 bytes, no separator added to the last segment;
 *             </li>
 *             <li>
 *                 {@code PEM}: separated in 64 bytes, adding separator to the last segment;
 *             </li>
 *         </ul>
 *     </li>
 * </ul>
 *
 * @author sunqian
 * @see Encoder
 * @see Decoder
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
     * multiple of 3. The {@code MIME} base64 format is separated in 76 bytes, no separator added to the last segment.
     *
     * @return a {@code Base64} encoder in type of {@code MIME}
     */
    public static Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code MIME}, without padding character if the length of source is
     * not a multiple of 3. The {@code MIME} base64 format is separated in 76 bytes, no separator added to the last
     * segment.
     *
     * @return a {@code Base64} encoder in type of {@code MIME}
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code PEM}, with padding character if the length of source is not a
     * multiple of 3. The {@code PEM} base64 format is separated in 64 bytes, adding separator to the last segment.
     *
     * @return a {@code Base64} encoder in type of {@code PEM}
     */
    public static Encoder pemEncoder() {
        return PemEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code PEM}, without padding character if the length of source is not
     * a multiple of 3. The {@code PEM} base64 format is separated in 64 bytes, adding separator to the last segment.
     *
     * @return a {@code Base64} encoder in type of {@code PEM}
     */
    public static Encoder pemEncoder(boolean padding) {
        return padding ? pemEncoder() : PemEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code Block Separation} with specified arguments.
     *
     * @param separationSize   Sets the max size per separation segment, must be a multiple of {@code 4}.
     * @param separator        Sets the separator. The array will be used directly, any modification to array will
     *                         affect the encoding.
     * @param padding          Whether adds padding character at the end if the length of source is not a multiple of
     *                         3.
     * @param addLastSeparator Whether adds the separator at tail (after paddings if padding is true) if the output size
     *                         is not multiple of separation size.
     * @param urlSafe          Whether the base64 dict is in {@code URL Safe}
     * @return a {@code Base64} encoder in type of {@code Block Separation} with specified arguments
     */
    public static Encoder separationEncoder(
        int separationSize,
        byte[] separator,
        boolean padding,
        boolean addLastSeparator,
        boolean urlSafe
    ) throws EncodingException {
        if (separationSize <= 0) {
            throw new EncodingException("Block size must be positive.");
        }
        if (separationSize % 4 != 0) {
            throw new EncodingException("Block size must be multiple of 4.");
        }
        return new SeparationEncoder(separationSize, separator, padding, addLastSeparator, urlSafe);
    }

    /**
     * Returns a {@code Base64} decoder for non-separation decoding.
     *
     * @return a {@code Base64} decoder for non-separation decoding
     */
    public static Decoder decoder() {
        return NonSepaartionDecoder.SINGLETON;
    }

    /**
     * Returns a {@code Base64} decoder with specified separation option: true for separation decoding, false for
     * non-separation decoding.
     *
     * @param separation true for separation decoding, false for non-separation decoding
     * @return a {@code Base64} decoder with specified separation option
     */
    public static Decoder decoder(boolean separation) {
        return separation ? SeparationDecoder.SINGLETON : decoder();
    }

    /**
     * {@code Base64} encoder implementation. {@link #getBlockSize()} and {@link #streamEncoder()} are overridden and
     * require attention.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {

        /**
         * Returns 3. {@code Base64} encoding expects the size of input data is multiple of 3 (although it accepts any
         * size of input data).
         *
         * @return 3
         */
        @Override
        default int getBlockSize() {
            return 3;
        }

        /**
         * Returns a new stream encoder. The encoder is wrapped by
         * {@link ByteStream#roundEncoder(ByteStream.Encoder, int)} to keep size of input data is multiple of 3.
         * Although the encoder accepts any size of input data, it is recommended that sets block size to multiple of 3
         * for a better performance.
         *
         * @return a new stream decoder wrapped by {@link ByteStream#roundEncoder(ByteStream.Encoder, int)}
         */
        @Override
        ByteStream.Encoder streamEncoder();
    }

    /**
     * {@code Base64} decoder implementation. {@link #getBlockSize()} and {@link #streamEncoder()} are overridden and
     * require attention.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {

        /**
         * Returns 4 for un-separation decoder, 1 for separation decoder. Expected data size for {@code Base64} decoding
         * is different for un-separation and separation. Un-separation decoder expects the size of input data is
         * multiple of 4 (although it can decode the data without padding, where the size of the data is not a multiple
         * of 4). Separation decoder will attempt to decode any size of data so that its block size is 1.
         *
         * @return 4 for un-separation decoder, 1 for separation decoder.
         */
        @Override
        int getBlockSize();

        /**
         * Returns a new stream decoder. The decoder is wrapped by
         * {@link ByteStream#roundEncoder(ByteStream.Encoder, int)} for un-separation, and
         * {@link ByteStream#bufferedEncoder(ByteStream.Encoder)} for separation. Thus, it is recommended that sets a
         * multiple of 4 block size for un-separation decoding, or an enough buffered size, such as 1024 for separation
         * decoding, for a better performance.
         *
         * @return a new stream decoder wrapped by {@link ByteStream#roundEncoder(ByteStream.Encoder, int)} or
         * {@link ByteStream#bufferedEncoder(ByteStream.Encoder)}
         */
        @Override
        ByteStream.Encoder streamEncoder();
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
        public int getOutputSize(int inputSize, boolean end) throws EncodingException {
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

        protected char[] dict() {
            return DICT;
        }

        protected int doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
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

    private static class SeparationEncoder extends AbsEncoder {

        private static final byte[] SEPARATOR = new byte[]{'\r', '\n'};

        private final int separationSize;
        private final byte[] separator;
        private final boolean addLastSeparator;
        private final boolean urlSafe;

        private SeparationEncoder(
            int separationSize, byte[] separator, boolean padding, boolean addLastSeparator, boolean urlSafe) {
            super(padding);
            this.separationSize = separationSize;
            this.separator = separator;
            this.addLastSeparator = addLastSeparator;
            this.urlSafe = urlSafe;
        }

        @Override
        protected char[] dict() {
            return urlSafe ? UrlEncoder.DICT : AbsEncoder.DICT;
        }

        @Override
        public int getOutputSize(int inputSize, boolean end) {
            if (end) {
                return getOutputSize0(inputSize, addLastSeparator);
            }
            return getOutputSize0(inputSize, false);
        }

        private int getOutputSize0(int inputSize, boolean doLast) {
            int outputSize = super.getOutputSize(inputSize, true);
            if (!doLast) {
                outputSize += (outputSize - 1) / separationSize * separator.length;
                return outputSize;
            }
            int blockCount = outputSize / separationSize;
            int remainder = outputSize % separationSize;
            if (remainder == 0) {
                outputSize += blockCount * separator.length;
            } else {
                outputSize += (blockCount + 1) * separator.length;
            }
            return outputSize;
        }

        @Override
        public int getBlockSize() {
            return separationSize / 4 * 3;
        }

        @Override
        public ByteStream.Encoder streamEncoder() {
            ByteStream.Encoder encoder = new ByteStream.Encoder() {

                private long startPos = 0;

                @Override
                public ByteBuffer encode(ByteBuffer data, boolean end) {
                    if (startPos > 0) {
                        if (end && !data.hasRemaining()) {
                            if (addLastSeparator) {
                                return JieBytes.copyBuffer(separator);
                            }
                            return JieBytes.emptyBuffer();
                        }
                        ByteBuffer ret = ByteBuffer.allocate(
                            getOutputSize(data.remaining(), end) + separator.length
                        );
                        for (byte b : separator) {
                            ret.put(b);
                        }
                        doCode(startPos, data, ret, end);
                        ret.flip();
                        return ret;
                    }
                    ByteBuffer ret = doCode(startPos, data, end);
                    startPos += ret.remaining();
                    return ret;
                }
            };
            return ByteStream.roundEncoder(encoder, getBlockSize());
        }

        protected int doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
            if (end) {
                return doCode0(src, srcOff, srcEnd, dst, dstOff, addLastSeparator);
            }
            return doCode0(src, srcOff, srcEnd, dst, dstOff, false);
        }

        private int doCode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean doLast) {
            char[] dict = dict();
            int srcPos = srcOff;
            int srcBlock = separationSize / 4 * 3;
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
                if ((writeLen == separationSize && srcPos < srcEnd) || (srcPos == srcEnd && doLast)) {
                    for (byte b : separator) {
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
                if (doLast) {
                    for (byte b : separator) {
                        dst[dstPos++] = b;
                    }
                }
            }
            return dstPos - dstOff;
        }
    }

    private static final class MimeEncoder extends SeparationEncoder {

        private static final int SEPARATION_SIZE = 76;

        private static final MimeEncoder PADDING =
            new MimeEncoder(SEPARATION_SIZE, SeparationEncoder.SEPARATOR, true);
        private static final MimeEncoder NO_PADDING =
            new MimeEncoder(SEPARATION_SIZE, SeparationEncoder.SEPARATOR, false);

        private MimeEncoder(int separationSize, byte[] blockSeparator, boolean padding) {
            super(separationSize, blockSeparator, padding, false, false);
        }
    }

    private static final class PemEncoder extends SeparationEncoder {

        private static final int SEPARATION_SIZE = 64;

        private static final PemEncoder PADDING =
            new PemEncoder(SEPARATION_SIZE, SeparationEncoder.SEPARATOR, true);
        private static final PemEncoder NO_PADDING =
            new PemEncoder(SEPARATION_SIZE, SeparationEncoder.SEPARATOR, false);

        private PemEncoder(int separationSize, byte[] blockSeparator, boolean padding) {
            super(separationSize, blockSeparator, padding, true, false);
        }
    }

    private static abstract class AbsDecoder extends AbsCoder.De implements Decoder {

        protected static final byte[] DICT = new byte[Byte.MAX_VALUE];

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
        public int getOutputSize(int inputSize, boolean end) throws DecodingException {
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
            return 4;
        }

        protected int doCode(long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
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
                    throw new DecodingException(
                        "Invalid base64 char at pos " + (startPos + srcPos - 1) + ": " + ((char) c) + ".");
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

    private static final class NonSepaartionDecoder extends AbsDecoder {

        private static final NonSepaartionDecoder SINGLETON = new NonSepaartionDecoder();

        private NonSepaartionDecoder() {
            super();
        }
    }

    private static final class SeparationDecoder extends AbsDecoder {

        private static final SeparationDecoder SINGLETON = new SeparationDecoder();

        private SeparationDecoder() {
            super();
        }

        @Override
        public int getOutputSize(int inputSize, boolean end) throws DecodingException {
            if (inputSize < 0) {
                throw new DecodingException("Base64 decoding size can not be negative.");
            }
            int remainder = inputSize % 4;
            if (remainder == 0) {
                return inputSize / 4 * 3;
            }
            return inputSize / 4 * 3 + remainder - 1;
        }

        @Override
        public int getBlockSize() {
            return 1;
        }

        @Override
        protected void checkCodingRemaining(int srcRemaining, int dstRemaining) {
            // No checking, because no determine.
        }

        // @Override
        // public ByteStream.Encoder streamEncoder() {
        //     return ByteStream.roundEncoder(this, getBlockSize());
        // }
        //
        // private int decode0(ByteBuffer source, ByteBuffer dest, boolean end) throws EncodingException {
        //     int outputSize = getOutputSize(source.remaining());
        //     if (source.hasArray() && dest.hasArray()) {
        //         doCode(
        //             source.array(),
        //             JieBuffer.getArrayStartIndex(source),
        //             JieBuffer.getArrayEndIndex(source),
        //             dest.array(),
        //             JieBuffer.getArrayStartIndex(dest),
        //             end
        //         );
        //         source.position(source.limit());
        //         dest.position(dest.position() + outputSize);
        //     } else {
        //         ByteBuffer dst = decode0(source, end);
        //         dest.put(dst);
        //     }
        //     return outputSize;
        // }
        //
        // private ByteBuffer decode0(ByteBuffer source, boolean end) throws EncodingException {
        //     int outputSize = getOutputSize(source.remaining());
        //     byte[] dst = new byte[outputSize];
        //     if (source.hasArray()) {
        //         doCode(
        //             source.array(),
        //             JieBuffer.getArrayStartIndex(source),
        //             JieBuffer.getArrayEndIndex(source),
        //             dst,
        //             0,
        //             end
        //         );
        //         source.position(source.limit());
        //     } else {
        //         byte[] s = new byte[source.remaining()];
        //         source.get(s);
        //         doCode(s, 0, s.length, dst, 0, end);
        //     }
        //     return ByteBuffer.wrap(dst);
        // }
        //
        // protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
        //     return doCode(src, srcOff, srcEnd, dst, dstOff, true);
        // }
        //
        // private long doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean end) {
        //     int srcPos = srcOff;
        //     int dstPos = dstOff;
        //     int bits = 0;
        //     int shiftTo = 18;
        //     while (srcPos < srcEnd) {
        //         int c = src[srcPos++] & 0xff;
        //         int b = DICT[c];
        //         if (b < 0) {
        //             if (b == -2) {
        //                 // must be padding end with xx== or xxx=
        //                 // xx==
        //                 if (shiftTo == 6 && srcPos == srcEnd - 1 && src[srcPos] == '=') {
        //                     // find last "="
        //                     while (srcPos < srcEnd) {
        //                         if (src[srcPos] == '=') {
        //                         }
        //                     }
        //                     dst[dstPos++] = (byte) (bits >> 16);
        //                     return dstPos - dstOff;
        //                 }
        //                 // xxx=
        //                 if (shiftTo == 0 && srcPos == srcEnd) {
        //                     dst[dstPos++] = (byte) (bits >> 16);
        //                     dst[dstPos++] = (byte) (bits >> 8);
        //                     return dstPos - dstOff;
        //                 }
        //             }
        //             // ignored
        //             continue;
        //         }
        //         bits |= (b << shiftTo);
        //         shiftTo -= 6;
        //         if (shiftTo < 0) {
        //             dst[dstPos++] = (byte) (bits >> 16);
        //             dst[dstPos++] = (byte) (bits >> 8);
        //             dst[dstPos++] = (byte) (bits);
        //             shiftTo = 18;
        //             bits = 0;
        //         }
        //     }
        //     if (shiftTo == 6) {
        //         dst[dstPos++] = (byte) (bits >> 16);
        //     } else if (shiftTo == 0) {
        //         dst[dstPos++] = (byte) (bits >> 16);
        //         dst[dstPos++] = (byte) (bits >> 8);
        //     } else if (shiftTo != 18) {
        //         throw new DecodingException("Invalid base64 tail without padding, must be 2 or 3 remainder left.");
        //     }
        //     return dstPos - dstOff;
        // }
        //
        // private long mergeCount(int readSize, int writeSize) {
        //     long rs = readSize;
        //     long ws = writeSize;
        //     return (rs << 32) | (0x00000000ffffffffL & ws);
        // }
        //
        // private int getReadSize(long mergeSize) {
        //     return (int) (mergeSize >>> 32);
        // }
        //
        // private int getWriteSize(long mergeSize) {
        //     return (int) mergeSize;
        // }
    }
}
