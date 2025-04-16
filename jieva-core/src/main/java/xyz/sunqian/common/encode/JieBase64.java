package xyz.sunqian.common.encode;

import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.UnreachablePointException;
import xyz.sunqian.common.base.bytes.ByteEncoder;
import xyz.sunqian.common.base.bytes.ByteProcessor;

import java.util.Arrays;

/**
 * This is a static utilities class for {@code base64} encoding and decoding, provides encoder and decoder
 * implementations: {@link JieBase64.Encoder} and {@link JieBase64.Decoder}. All implementations are thread-safe. The
 * algorithms are specified in:
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
 *                 {@code PEM}: separated in 64 bytes, adding line separator to the last line;
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
     * Returns a {@code line-separated base64} encoder in {@code MIME} format, of which encoding result is separated in
     * 76 bytes, no separator added to the last line, with padding characters as necessary.
     *
     * @return a {@code line-separated base64} encoder in {@code MIME} format
     */
    public static Encoder mimeEncoder() {
        return mimeEncoder(true);
    }

    /**
     * Returns a {@code line-separated base64} encoder in {@code MIME} format, of which encoding result is separated in
     * 76 bytes, no separator added to the last line.
     *
     * @param padding whether the encoder with padding characters as necessary
     * @return a {@code line-separated base64} encoder in {@code MIME} format
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? MimeEncoder.PADDING : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code line-separated base64} encoder in {@code PEM} format, of which encoding result is separated in
     * 64 bytes, adding line separator to the last line, with padding characters as necessary.
     *
     * @return a {@code line-separated base64} encoder in {@code PEM} format
     */
    public static Encoder pemEncoder() {
        return pemEncoder(true);
    }

    /**
     * Returns a {@code line-separated base64} encoder in {@code PEM} format, of which encoding result is separated in
     * 64 bytes, adding line separator to the last line.
     *
     * @param padding whether the encoder with padding characters as necessary
     * @return a {@code line-separated base64} encoder in {@code PEM} format
     */
    public static Encoder pemEncoder(boolean padding) {
        return padding ? PemEncoder.PADDING : PemEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code line-separated base64} encoder with specified arguments.
     *
     * @param lineSize          Sets the max line size, must be a multiple of {@code 4}.
     * @param separator         Sets the line separator. The array will be used directly, any modification to array will
     *                          affect the encoding.
     * @param padding           Whether the encoder with padding characters as necessary.
     * @param lastLineSeparator Whether adds the line separator to the last line
     * @param urlSafe           Whether the base64 dict is {@code url-safe}
     * @return a {@code line-separated base64} encoder with specified arguments
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
     * Returns a {@code base64} decoder for all types including {@code basic}, {@code url-safe} and
     * {@code line-separated}.
     *
     * @return a {@code base64} decoder for all types including {@code basic}, {@code url-safe} and
     * {@code line-separated}
     */
    public static Decoder decoder() {
        return BasicDecoder.SINGLETON;
    }

    /**
     * The implementation of {@link DataEncoder} for {@code base64} encoding, thread-safe.
     *
     * @author sunqian
     */
    @ThreadSafe
    public interface Encoder extends DataEncoder.ToLatin {

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
         * Returns a new {@link ByteEncoder} which encapsulates current base64 encoding, supports any size of input
         * data, not thread-safe.
         *
         * @return a {@link ByteEncoder} with current base64 encoding logic
         * @see ByteProcessor
         * @see ByteEncoder
         */
        @Override
        ByteEncoder streamEncoder();
    }

    /**
     * The implementation of {@link DataEncoder} for {@code base64} decoding, thread-safe.
     *
     * @author sunqian
     */
    @ThreadSafe
    public interface Decoder extends DataDecoder.FromLatin {

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
         * Returns a new {@link ByteEncoder} which encapsulates current base64 decoding, supports any size of input
         * data, not thread-safe.
         *
         * @return a {@link ByteEncoder} with current base64 decoding logic
         * @see ByteProcessor
         * @see ByteEncoder
         */
        @Override
        ByteEncoder streamEncoder();
    }

    private static abstract class AbsEncoder extends AbstractBaseDataEncoder.En implements Encoder {

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
        protected int getOutputSize(int inputSize, long startPos, boolean end) throws EncodingException {
            if (inputSize == 0) {
                return 0;
            }
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
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
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
            if (!end) {
                return buildDoCodeResult(srcPos - srcOff, dstPos - dstOff);
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
        protected int getOutputSize(int inputSize, long startPos, boolean end) throws EncodingException {
            if (inputSize == 0) {
                if (!end) {
                    throw new EncodingException(new UnreachablePointException());
                }
                if (startPos == 0) {
                    return 0;
                }
                return getSuffixSize();
            }
            int sourceLineSize = getSourceLineSize();
            if (inputSize < sourceLineSize) {
                if (!end) {
                    return 0;
                }
                return getPrefixSize(startPos) +
                    super.getOutputSize(inputSize, startPos, true) +
                    getSuffixSize();
            }
            int portion = inputSize / sourceLineSize;
            int portionSize = portion * lineSize + getPortionMiddleSeparatorSize(portion);
            if (!end) {
                return getPrefixSize(startPos) + portionSize;
            }
            int portionRemainder = inputSize % sourceLineSize;
            if (portionRemainder == 0) {
                return getPrefixSize(startPos) + portionSize + getSuffixSize();
            }
            return getPrefixSize(startPos) +
                portionSize +
                separator.length +
                super.getOutputSize(portionRemainder, startPos, true) +
                getSuffixSize();
        }

        private int getSourceLineSize() {
            return lineSize / 4 * 3;
        }

        private int getPrefixSize(long startPos) {
            return startPos > 0 ? separator.length : 0;
        }

        private int getSuffixSize() {
            return lastLineSeparator ? separator.length : 0;
        }

        private int getPortionMiddleSeparatorSize(int portion) {
            if (portion < 2) {
                return 0;
            }
            return (portion - 1) * separator.length;
        }

        protected long doCode(
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
        ) {
            int dstPos = dstOff;
            if (startPos > 0) {
                for (byte b : separator) {
                    dst[dstPos++] = b;
                }
            }
            char[] dict = dict();
            int srcPos = srcOff;
            int srcLineSize = getSourceLineSize();
            int srcLineEnd = srcOff + (srcEnd - srcOff) / srcLineSize * srcLineSize;
            if (srcPos < srcLineEnd) {
                while (true) {
                    int readEnd = srcPos + srcLineSize;
                    for (int i = srcPos, j = dstPos; i < readEnd; ) {
                        int bits = (src[i++] & 0xff) << 16 | (src[i++] & 0xff) << 8 | (src[i++] & 0xff);
                        dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                        dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                        dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                        dst[j++] = (byte) dict[bits & 0x3f];
                    }
                    dstPos += lineSize;
                    srcPos = readEnd;
                    if (srcPos < srcLineEnd) {
                        for (byte b : separator) {
                            dst[dstPos++] = b;
                        }
                    } else {
                        break;
                    }
                }
            }
            if (!end) {
                return buildDoCodeResult(srcPos - srcOff, dstPos - dstOff);
            }
            int remainder = srcEnd - srcPos;
            if (remainder > 0) {
                if (srcPos - srcOff > 0) {
                    for (byte b : separator) {
                        dst[dstPos++] = b;
                    }
                }
                int roundLen = remainder / 3 * 3;
                int roundEnd = srcPos + roundLen;
                while (srcPos < roundEnd) {
                    int bits = (src[srcPos++] & 0xff) << 16 | (src[srcPos++] & 0xff) << 8 | (src[srcPos++] & 0xff);
                    dst[dstPos++] = (byte) dict[(bits >>> 18) & 0x3f];
                    dst[dstPos++] = (byte) dict[(bits >>> 12) & 0x3f];
                    dst[dstPos++] = (byte) dict[(bits >>> 6) & 0x3f];
                    dst[dstPos++] = (byte) dict[bits & 0x3f];
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
                        int b1 = src[srcPos++] & 0xff;
                        dst[dstPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                        dst[dstPos++] = (byte) dict[(b1 << 2) & 0x3f];
                        if (padding) {
                            dst[dstPos++] = '=';
                        }
                    }
                }
            }
            if (lastLineSeparator && srcPos > srcOff) {
                for (byte b : separator) {
                    dst[dstPos++] = b;
                }
            }
            return buildDoCodeResult(srcPos - srcOff, dstPos - dstOff);
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

    private static abstract class AbsDecoder extends AbstractBaseDataEncoder.De implements Decoder {

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
        protected int getOutputSize(int inputSize, long startPos, boolean end) throws DecodingException {
            switch (inputSize) {
                case 0:
                    return 0;
                case 1:
                    if (end) {
                        throw new DecodingException("Invalid Base64 decoding size: " + inputSize + ".");
                    }
                    return 0;
                case 2:
                    return 1;
                case 3:
                    return 2;
                default:
                    return getOutputSizeEnd(inputSize);
            }
        }

        private int getOutputSizeEnd(int inputSize) throws DecodingException {
            int remainder = inputSize % 4;
            if (remainder == 0) {
                return inputSize / 4 * 3;
            }
            return inputSize / 4 * 3 + remainder - 1;
        }

        @Override
        protected void checkRemainingSpace(int srcRemaining, int dstRemaining) {
            // do not check!
        }

        @Override
        protected long doCode(
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
        ) {
            try {
                return doCode0(src, srcOff, srcEnd, dst, dstOff, dstEnd, startPos, end);
            } catch (DecodingException e) {
                throw e;
            } catch (Exception e) {
                throw new DecodingException(e);
            }
        }

        private long doCode0(
            byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff, int dstEnd, long startPos, boolean end
        ) {
            int srcPos = srcOff;
            int dstPos = dstOff;
            int bits = 0;
            int shiftTo = 18;// must be 18, 12, 6, 0, -6.
            int endState = 0;
            int bitsStart = srcPos;
            while (srcPos < srcEnd) {
                int c = src[srcPos++] & 0xff;
                int b = DICT[c];
                if (b >= 0) {
                    if (endState != 0) {
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
                        bitsStart = srcPos;
                    }
                    continue;
                }
                if (b == -2) {
                    // must be padding end with xx== or xxx=
                    if (shiftTo == 6) {
                        if (endState == 0) {
                            // xx= -> endState = 1;
                            endState = 1;
                            continue;
                        }
                        // (endState == 1 && xx==) -> endState = 2;
                        endState = 2;
                        dst[dstPos++] = (byte) (bits >> 16);
                        bitsStart = srcPos;
                        continue;
                    } else if (shiftTo == 0) {
                        // (endState == 0 && xxx=) -> endState = 2;
                        endState = 2;
                        dst[dstPos++] = (byte) (bits >> 16);
                        dst[dstPos++] = (byte) (bits >> 8);
                        bitsStart = srcPos;
                        continue;
                    }
                    throw new DecodingException(
                        "Invalid base64 char at pos " + (startPos + srcPos - 1) + ": " + ((char) c) + ".");
                }
                // Otherwise: -1
            }
            if (!end) {
                return buildDoCodeResult(bitsStart - srcOff, dstPos - dstOff);
            }
            if (endState == 0 && shiftTo == 6) {
                // xx
                dst[dstPos++] = (byte) (bits >> 16);
            } else if (endState == 0 && shiftTo == 0) {
                // xxx
                dst[dstPos++] = (byte) (bits >> 16);
                dst[dstPos++] = (byte) (bits >> 8);
            } else if (endState != 2 && endState != 0) {
                throw new DecodingException("Invalid base64 tail, must be xx, xxx, xx== or xxx=.");
            }
            return buildDoCodeResult(srcPos - srcOff, dstPos - dstOff);
        }
    }

    private static final class BasicDecoder extends AbsDecoder {

        private static final BasicDecoder SINGLETON = new BasicDecoder();

        private BasicDecoder() {
            super();
        }
    }
}
