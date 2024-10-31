package xyz.fslabo.common.codec;

import xyz.fslabo.common.io.ByteStream;

import java.nio.ByteBuffer;

/**
 * This is a static utilities class provides implementations and utilities for {@code Base64} encoder and decoder,
 * specified in <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a> and <a
 * href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>. There are 3 types of {@code Base64}:
 * <ul>
 *     <li>
 *         {@code Basic}: The most common Base64 type, if no specified, it is typically refers to this type;
 *     </li>
 *     <li>
 *         {@code URL and Filename safe}: In this type, using '-' and '_' instead of '+' and '/';
 *     </li>
 *     <li>
 *         {@code MIME}: The encoded output must be split into lines, typically no more than 76 chars per line, and
 *         '\r\n' as the line separator. Note no line separator is added to the end of the output;
 *     </li>
 * </ul>
 * When using the {@link Encoder#toStreamEncoder()} method, the {@link ByteStream#blockSize(int)} needs to be set to a
 * correct value, which can be obtained by {@link Encoder#getBlockSize()}. To specifies a custom block size, for
 * {@code Basic} and {@code URL and Filename safe}, the block size must be a multiple of 3; for {@code MIME}, the block
 * size must be a multiple of (line size / 4 * 3) and at least twice this value (if line size is 76, the block size can
 * be (76 / 4 * 3) * 2 = 114).
 *
 * @author sunqian
 */
public class JieBase64 {

    /**
     * Returns an {@link Encoder} instance in type of {@code Basic}, with padding character if the length of source is
     * not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code Basic}
     */
    public static Encoder encoder() {
        return BasicEncoder.PADDING;
    }

    /**
     * Returns an {@link Encoder} instance in type of {@code Basic}. without padding character if the length of source
     * is not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code Basic}
     */
    public static Encoder encoder(boolean padding) {
        return padding ? encoder() : BasicEncoder.NO_PADDING;
    }

    /**
     * Returns an {@link Encoder} instance in type of {@code URL and Filename safe}, with padding character if the
     * length of source is not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder() {
        return UrlEncoder.PADDING;
    }

    /**
     * Returns an {@link Encoder} instance in type of {@code URL and Filename safe}, without padding character if the
     * length of source is not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder(boolean padding) {
        return padding ? urlEncoder() : UrlEncoder.NO_PADDING;
    }

    /**
     * Returns an {@link Encoder} instance in type of {@code MIME}, with padding character if the length of source is
     * not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code MIME}
     */
    public static Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns an {@link Encoder} instance in type of {@code MIME}, without padding character if the length of source is
     * not a multiple of 3.
     *
     * @return an {@link Encoder} instance in type of {@code MIME}
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a new {@link Encoder} in type of {@code MIME}, with specified arguments.
     *
     * @param lineMax sets the max length per line, must be a multiple of {@code 4}
     * @param newLine sets the line separator. The array will be used directly, any modification to array will affect
     *                the encoding.
     * @param padding whether add padding character at the end if the length of source is not a multiple of 3.
     */
    public static Encoder mimeEncoder(int lineMax, byte[] newLine, boolean padding) {
        return new MimeEncoder(padding, lineMax, newLine);
    }

    private static abstract class AbsEncoder implements Encoder, ByteStream.Encoder {

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
        public byte[] encode(byte[] source) throws CodecException {
            int len = getOutputSize(source.length);
            byte[] dst = new byte[len];
            encode0(source, 0, source.length, dst, 0);
            return dst;
        }

        @Override
        public ByteBuffer encode(ByteBuffer source) throws CodecException {
            int len = getOutputSize(source.remaining());
            byte[] dst = new byte[len];
            ByteBuffer ret = ByteBuffer.wrap(dst);
            if (source.hasArray()) {
                encode0(
                    source.array(),
                    source.arrayOffset() + source.position(),
                    source.arrayOffset() + source.limit(),
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
        public int encode(byte[] source, byte[] dest) throws CodecException {
            int outputSize = getOutputSize(source.length);
            checkRemaining(outputSize, dest.length);
            return encode0(source, 0, source.length, dest, 0);
        }

        @Override
        public int encode(ByteBuffer source, ByteBuffer dest) throws CodecException {
            int outputSize = getOutputSize(source.remaining());
            checkRemaining(outputSize, dest.remaining());
            if (source.hasArray() && dest.hasArray()) {
                encode0(
                    source.array(),
                    source.arrayOffset() + source.position(),
                    source.arrayOffset() + source.position() + source.remaining(),
                    dest.array(),
                    dest.arrayOffset() + dest.position()
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
        public int getBlockSize() {
            return 1024 * 3;
        }

        @Override
        public ByteStream.Encoder toStreamEncoder() {
            return this;
        }

        @Override
        public ByteBuffer encode(ByteBuffer source, boolean end) throws CodecException {
            return encode(source);
        }

        protected void checkRemaining(int srcRemaining, int dstRemaining) {
            if (srcRemaining > dstRemaining) {
                throw new CodecException("Remaining of destination is not enough.");
            }
        }

        protected char[] dict() {
            return DICT;
        }

        protected int encode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int destPos = dstOff;
            int roundLen = (srcEnd - srcOff) / 3 * 3;
            int roundEnd = srcPos + roundLen;
            for (int i = srcPos, j = destPos; i < roundEnd; ) {
                int bits = (src[i++] & 0xff) << 16 |
                    (src[i++] & 0xff) << 8 |
                    (src[i++] & 0xff);
                dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                dst[j++] = (byte) dict[bits & 0x3f];
            }
            srcPos = roundEnd;
            destPos += roundLen / 3 * 4;
            // 1 or 2 leftover bytes
            if (srcPos < srcEnd) {
                int b0 = src[srcPos++] & 0xff;
                dst[destPos++] = (byte) dict[b0 >> 2];
                if (srcPos == srcEnd) {
                    dst[destPos++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[destPos++] = '=';
                        dst[destPos++] = '=';
                    }
                } else {
                    int b1 = src[srcPos] & 0xff;
                    dst[destPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[destPos++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[destPos++] = '=';
                    }
                }
            }
            return destPos - dstOff;
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

    private static final class MimeEncoder extends AbsEncoder {

        private static final int MIME_LINE_MAX = 76;
        private static final byte[] CRLF = new byte[]{'\r', '\n'};

        private static final MimeEncoder PADDING = new MimeEncoder(true, MIME_LINE_MAX, CRLF);
        private static final MimeEncoder NO_PADDING = new MimeEncoder(false, MIME_LINE_MAX, CRLF);

        private final int lineMax;
        private final byte[] newLine;

        private MimeEncoder(boolean padding, int lineMax, byte[] newLine) {
            super(padding);
            this.lineMax = lineMax;
            this.newLine = newLine;
        }

        @Override
        public int getOutputSize(int inputSize) {
            int outputSize = super.getOutputSize(inputSize);
            outputSize += (outputSize - 1) / lineMax * newLine.length;
            return outputSize;
        }

        @Override
        public int getBlockSize() {
            return lineMax / 4 * 3 * 20;
        }

        @Override
        public ByteStream.Encoder toStreamEncoder() {
            boolean[] hasPrev = {false};
            return (data, end) -> {
                int outputSize = getOutputSize(data.remaining());
                if (hasPrev[0]) {
                    ByteBuffer ret = ByteBuffer.allocate(outputSize + newLine.length);
                    for (byte b : newLine) {
                        ret.put(b);
                    }
                    encode(data, ret);
                    ret.flip();
                    return ret;
                }
                hasPrev[0] = true;
                return encode(data);
            };
        }

        protected int encode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int lineSize = lineMax / 4 * 3;
            int roundEnd = srcOff + ((srcEnd - srcOff) / 3 * 3);
            int destPos = dstOff;
            while (srcPos < roundEnd) {
                int readEnd = Math.min(srcPos + lineSize, roundEnd);
                for (int i = srcPos, j = destPos; i < readEnd; ) {
                    int bits = (src[i++] & 0xff) << 16 |
                        (src[i++] & 0xff) << 8 |
                        (src[i++] & 0xff);
                    dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                    dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                    dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                    dst[j++] = (byte) dict[bits & 0x3f];
                }
                int writeLen = (readEnd - srcPos) / 3 * 4;
                destPos += writeLen;
                srcPos = readEnd;
                if (writeLen == lineMax && srcPos < srcEnd) {
                    for (byte b : newLine) {
                        dst[destPos++] = b;
                    }
                }
            }
            // 1 or 2 leftover bytes
            if (srcPos < srcEnd) {
                int b0 = src[srcPos++] & 0xff;
                dst[destPos++] = (byte) dict[b0 >> 2];
                if (srcPos == srcEnd) {
                    dst[destPos++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[destPos++] = '=';
                        dst[destPos++] = '=';
                    }
                } else {
                    int b1 = src[srcPos] & 0xff;
                    dst[destPos++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[destPos++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[destPos++] = '=';
                    }
                }
            }
            return destPos - dstOff;
        }
    }
}
