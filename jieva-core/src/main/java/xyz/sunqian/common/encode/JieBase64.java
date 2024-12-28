package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.BytesProcessor;
import xyz.sunqian.common.io.JieIO;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is a static utilities class for {@code base64} encoding and decoding, provides encoder and decoder
 * implementations: {@link JieBase64.Encoder} and {@link JieBase64.Decoder}. The algorithms are specified in:
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
 *         {@code Line Separated}: Encoding result is line-separated by specified separator (such as \r\n). For example:
 *         <ul>
 *             <li>
 *                 {@code MIME}: separated in 76 bytes, no separator added to the last line;
 *             </li>
 *             <li>
 *                 {@code PEM}: separated in 64 bytes, adding separators to the last line;
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
     * Returns a {@code basic base64} encoder with padding characters as necessary.
     *
     * @return a {@code basic base64} encoder with padding characters as necessary
     */
    public static Encoder encoder() {
        return encoder(true);
    }

    /**
     * Returns a {@code basic base64} encoder.
     *
     * @param padding whether the encoder with padding characters as necessary
     * @return a {@code basic base64} encoder
     */
    public static Encoder encoder(boolean padding) {
        return padding ? BasicEncoder.PADDING : BasicEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code url-safe base64} encoder with padding characters as necessary.
     *
     * @return a {@code url-safe base64} encoder with padding characters as necessary
     */
    public static Encoder urlEncoder() {
        return urlEncoder(true);
    }

    /**
     * Returns a {@code url-safe base64} encoder.
     *
     * @param padding whether the encoder with padding characters as necessary
     * @return a {@code url-safe base64} encoder
     */
    public static Encoder urlEncoder(boolean padding) {
        return padding ? UrlEncoder.PADDING : UrlEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in {@code MIME} format, which is a format of {@code Line Separated} type,
     * separated in 76 bytes, no separator added to the last line, without padding and url-safe.
     *
     * @return a {@code Base64} encoder in {@code MIME} format
     */
    public static Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in {@code MIME} format, which is a format of {@code Line Separated} type,
     * separated in 76 bytes, no separator added to the last line, without url-safe.
     *
     * @param padding whether adds padding characters if the length of source is not a multiple of 3
     * @return a {@code Base64} encoder in {@code MIME} format
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in {@code PEM} format, which is a format of {@code Line Separated} type,
     * separated in 64 bytes, adding separators to the last line, without padding and url-safe.
     *
     * @return a {@code Base64} encoder in {@code PEM} format
     */
    public static Encoder pemEncoder() {
        return PemEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in {@code PEM} format, which is a format of {@code Line Separated} type,
     * separated in 64 bytes, adding separators to the last line, without url-safe.
     *
     * @param padding whether adds padding characters if the length of source is not a multiple of 3, the paddings are
     *                placed before the separators
     * @return a {@code Base64} encoder in {@code PEM} format
     */
    public static Encoder pemEncoder(boolean padding) {
        return padding ? pemEncoder() : PemEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder in type of {@code Line Separated} with specified arguments.
     *
     * @param lineSize          Sets the max line size, must be a multiple of {@code 4}.
     * @param separator         Sets the line separator. The array will be used directly, any modification to array will
     *                          affect the encoding.
     * @param padding           Whether adds padding characters if the length of source is not a multiple of 3.
     * @param lastLineSeparator Whether adds the separators to the last line
     * @param urlSafe           Whether the base64 dict is in {@code URL Safe}
     * @return a {@code Base64} encoder in type of {@code Line Separated} with specified arguments
     */
    public static Encoder lineEncoder(
        int lineSize,
        byte[] separator,
        boolean padding,
        boolean lastLineSeparator,
        boolean urlSafe
    ) throws EncodingException {
        if (lineSize <= 0) {
            throw new EncodingException("Block size must be positive.");
        }
        if (lineSize % 4 != 0) {
            throw new EncodingException("Block size must be multiple of 4.");
        }
        return new LineEncoder(lineSize, separator, padding, lastLineSeparator, urlSafe);
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
     * The implementation of {@link ByteEncoder} for {@code base64} encoding.
     *
     * @author sunqian
     */
    public interface Encoder extends ByteEncoder.ToLatin {

        /**
         * Returns -1. The {@code base64} doesn't require encoding in blocks.
         *
         * @return -1
         */
        @Override
        default int getBlockSize() {
            return -1;
        }

        /**
         * Returns output size of base64 encoding.
         *
         * @param inputSize specified input size
         * @return output size of base64 encoding
         * @throws EncodingException if input size is illegal
         */
        @Override
        int getOutputSize(int inputSize) throws EncodingException;

        /**
         * Returns a new {@link BytesProcessor.Encoder} which encapsulates current base64 encoding, supports any size of
         * input data, not thread-safe.
         *
         * @return a {@link BytesProcessor.Encoder} with current base64 encoding logic
         * @see BytesProcessor
         * @see BytesProcessor.Encoder
         */
        @Override
        BytesProcessor.Encoder streamEncoder();
    }

    /**
     * The implementation of {@link ByteEncoder} for {@code base64} decoding.
     *
     * @author sunqian
     */
    public interface Decoder extends ByteDecoder.ToLatin {

        /**
         * Returns -1. The {@code base64} decoding may not determine block size.
         *
         * @return -1
         */
        @Override
        default int getBlockSize() {
            return -1;
        }

        /**
         * Returns maximum output size of base64 decoding.
         *
         * @param inputSize specified input size
         * @return maximum output size of base64 decoding
         * @throws DecodingException if input size is illegal
         */
        @Override
        int getOutputSize(int inputSize) throws DecodingException;

        /**
         * Returns a new {@link BytesProcessor.Encoder} which encapsulates current base64 decoding, supports any size of
         * input data, not thread-safe.
         *
         * @return a {@link BytesProcessor.Encoder} with current base64 decoding logic
         * @see BytesProcessor
         * @see BytesProcessor.Encoder
         */
        @Override
        BytesProcessor.Encoder streamEncoder();
    }

    private static abstract class AbsEncoder extends AbstractByteCoder.En implements Encoder {

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
            if (end) {
                return getOutputSizeEnd(inputSize);
            }
            return inputSize / 3 * 4;
        }

        private int getOutputSizeEnd(int inputSize) {
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

        protected long doCode(
            long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, boolean end
        ) {
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
            if (end && srcPos < srcEnd) {
                int b0 = src[srcPos++] & 0xff;
                dst[dstPos++] = (byte) dict[b0 >> 2];
                if (srcPos == srcEnd) {
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                        dst[dstPos++] = '=';
                    }
                } else {
                    int b1 = src[srcPos++] & 0xff;
                    dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[dstPos++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[dstPos++] = '=';
                    }
                }
            }
            return buildDoCodeResult(srcPos - srcOff, dstPos - dstOff);
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

    private static class LineEncoder extends AbsEncoder {

        private static final byte[] SEPARATOR = new byte[]{'\r', '\n'};

        private final int lineSize;
        private final byte[] separator;
        private final boolean lastLineSeparator;
        private final boolean urlSafe;

        private LineEncoder(
            int lineSize, byte[] separator, boolean padding, boolean lastLineSeparator, boolean urlSafe
        ) {
            super(padding);
            this.lineSize = lineSize;
            this.separator = separator;
            this.lastLineSeparator = lastLineSeparator;
            this.urlSafe = urlSafe;
        }

        @Override
        protected char[] dict() {
            return urlSafe ? UrlEncoder.DICT : AbsEncoder.DICT;
        }

        @Override
        public int getOutputSize(int inputSize, boolean end) {
            int sourceLineSize = getSourceLineSize();
            int portion = inputSize / sourceLineSize;
            int portionSize = portion * lineSize + (portion > 1 ? (portion - 1) * separator.length : 0);
            if (!end) {
                return portionSize;
            }
            int portionRemainder = inputSize % sourceLineSize;
            if (portionRemainder == 0) {
                return portionSize + (lastLineSeparator ? separator.length : 0);
            }
            return portionSize + separator.length + portionRemainder + (lastLineSeparator ? separator.length : 0);
        }

        private int getSourceLineSize() {
            return lineSize / 4 * 3;
        }

        @Override
        public int getBlockSize() {
            return lineSize / 4 * 3;
        }

        @Override
        public BytesProcessor.Encoder streamEncoder() {
            BytesProcessor.Encoder encoder = new BytesProcessor.Encoder() {

                private long startPos = 0;

                @Override
                public ByteBuffer encode(ByteBuffer data, boolean end) {
                    int pos = data.position();
                    if (startPos > 0) {
                        if (end && !data.hasRemaining()) {
                            if (lastLineSeparator) {
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
                        // doCode(startPos, data, ret, end);
                        ret.flip();
                        startPos += (data.position() - pos);
                        return ret;
                    }
                    ByteBuffer ret = null;// doCode(startPos, data, end);
                    startPos += (data.position() - pos);
                    return ret;
                }
            };
            return JieIO.roundEncoder(getBlockSize(), encoder);
        }

        protected long doCode(
            long startPos, byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, boolean end
        ) {
            if (end) {
                return doCode0(src, srcOff, srcEnd, dst, dstOff, lastLineSeparator);
            }
            return doCode0(src, srcOff, srcEnd, dst, dstOff, false);
        }

        private int doCode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, boolean doLast) {
            char[] dict = dict();
            int srcPos = srcOff;
            int srcBlock = lineSize / 4 * 3;
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
                if ((writeLen == lineSize && srcPos < srcEnd) || (srcPos == srcEnd && doLast)) {
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

    private static final class MimeEncoder extends LineEncoder {

        private static final int SEPARATION_SIZE = 76;

        private static final MimeEncoder PADDING =
            new MimeEncoder(SEPARATION_SIZE, LineEncoder.SEPARATOR, true);
        private static final MimeEncoder NO_PADDING =
            new MimeEncoder(SEPARATION_SIZE, LineEncoder.SEPARATOR, false);

        private MimeEncoder(int separationSize, byte[] blockSeparator, boolean padding) {
            super(separationSize, blockSeparator, padding, false, false);
        }
    }

    private static final class PemEncoder extends LineEncoder {

        private static final int SEPARATION_SIZE = 64;

        private static final PemEncoder PADDING =
            new PemEncoder(SEPARATION_SIZE, LineEncoder.SEPARATOR, true);
        private static final PemEncoder NO_PADDING =
            new PemEncoder(SEPARATION_SIZE, LineEncoder.SEPARATOR, false);

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
