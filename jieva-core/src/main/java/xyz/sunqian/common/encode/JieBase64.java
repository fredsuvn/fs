package xyz.sunqian.common.encode;

import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.io.ByteStream;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * This is a static utilities class provides implementations and utilities for {@code Base64} encoder and decoder,
 * specified in <a href="http://www.ietf.org/rfc/rfc4648.txt">RFC 4648</a> and <a
 * href="http://www.ietf.org/rfc/rfc2045.txt">RFC 2045</a>.
 * <h2>Types</h2>
 * <p>
 * There are 3 types of {@code Base64}:
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
 * <h2>Encoder</h2>
 * <p>
 * For {@link ByteEncoder#toStreamEncoder()}, the best block size for {@link ByteStream#blockSize(int)} is
 * {@link ByteEncoder#getBlockSize()} or multiples of it, and:
 * <ul>
 *     <li>
 *         {@code Basic} and {@code URL and Filename safe}: the {@link ByteStream#blockSize(int)} should be set to
 *         multiples of 3, and {@link ByteEncoder#toStreamEncoder()} returns singleton thread-safe instance for each
 *         calling;
 *     </li>
 *     <li>
 *         {@code MIME}: the {@link ByteStream#blockSize(int)} should be set to multiples of ({@code lineMax / 4 * 3}),
 *         and {@link ByteEncoder#toStreamEncoder()} returns a new un-thread-safe instance for each calling;
 *     </li>
 * </ul>
 * <h2>Decoder</h2>
 * <p>
 * For {@link ByteDecoder#toStreamEncoder()}, the best block size for {@link ByteStream#blockSize(int)} is
 * {@link ByteDecoder#getBlockSize()} or multiples of it, and:
 * <ul>
 *     <li>
 *         {@code Basic} and {@code URL and Filename safe}: the {@link ByteStream#blockSize(int)} should be set to
 *         multiples of 4, and {@link ByteDecoder#toStreamEncoder()} returns singleton thread-safe instance for each
 *         calling;
 *     </li>
 *     <li>
 *         {@code MIME}: the {@link ByteStream#blockSize(int)} should be set to multiples of ({@code lineMax}), and
 *         {@link ByteDecoder#toStreamEncoder()} returns a new un-thread-safe instance for each calling;
 *     </li>
 * </ul>
 *
 * @author sunqian
 */
public class JieBase64 {

    /**
     * Returns a {@code Base64} encoder  in type of {@code Basic}, with padding character if the length of source is not
     * a multiple of
     * 3.
     *
     * @return a {@code Base64} encoder  in type of {@code Basic}
     */
    public static Encoder encoder() {
        return BasicEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code Basic}. without padding character if the length of source is
     * not a multiple of 3.
     *
     * @return a {@code Base64} encoder  in type of {@code Basic}
     */
    public static Encoder encoder(boolean padding) {
        return padding ? encoder() : BasicEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code URL and Filename safe}, with padding character if the length
     * of source is not a multiple of 3.
     *
     * @return a {@code Base64} encoder  in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder() {
        return UrlEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code URL and Filename safe}, without padding character if the
     * length of source is not a multiple of 3.
     *
     * @return a {@code Base64} encoder  in type of {@code URL and Filename safe}
     */
    public static Encoder urlEncoder(boolean padding) {
        return padding ? urlEncoder() : UrlEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code MIME}, with padding character if the length of source is not
     * a multiple of
     * 3.
     *
     * @return a {@code Base64} encoder  in type of {@code MIME}
     */
    public static Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code MIME}, without padding character if the length of source is
     * not a multiple of 3.
     *
     * @return a {@code Base64} encoder  in type of {@code MIME}
     */
    public static Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a {@code Base64} encoder  in type of {@code MIME}, with specified arguments.
     *
     * @param lineMax sets the max length per line, must be a multiple of {@code 4}
     * @param newLine sets the line separator. The array will be used directly, any modification to array will affect
     *                the encoding.
     * @param padding whether add padding character at the end if the length of source is not a multiple of 3.
     * @return a {@code Base64} encoder  in type of {@code MIME}
     */
    public static Encoder mimeEncoder(int lineMax, byte[] newLine, boolean padding) {
        return new MimeEncoder(padding, lineMax, newLine);
    }

    /**
     * {@code Base64} encoder, extends {@link ToCharEncoder}.
     *
     * @author sunqian
     */
    public interface Encoder extends ToCharEncoder {
    }

    /**
     * {@code Base64} decoder, extends {@link ToCharDecoder}.
     *
     * @author sunqian
     */
    public interface Decoder extends ToCharDecoder {
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
            return 384 * 3;
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

        /*
        protected class ByteEncoder implements ByteStream.Encoder {

            protected ByteBuffer buf;

            @Override
            public ByteBuffer encode(ByteBuffer data, boolean end) {
                if (noBuf()) {
                    if (end) {
                        ByteBuffer ret = newDest(data.remaining());
                        AbsEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                    int multiLen = data.remaining() / 3 * 3;
                    if (multiLen == data.remaining()) {
                        ByteBuffer ret = newDest(multiLen);
                        AbsEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                }
                int blockSize = AbsEncoder.this.getBlockSize();
                if (buf == null) {
                    buf = ByteBuffer.allocate(blockSize);
                }
                int totalSize = buf.position() + data.remaining();
                if (totalSize < blockSize) {
                    fillBuffer(data);
                    if (end) {
                        buf.flip();
                        ByteBuffer ret = newDest(buf.remaining());
                        AbsEncoder.this.encode(buf, ret);
                        ret.flip();
                        return ret;
                    } else {
                        return JieBytes.emptyBuffer();
                    }
                }
                int multiSize = totalSize / 3 * 3;
                ByteBuffer ret;
                if (end) {
                    ret = newDest(totalSize);
                } else {
                    ret = newDest(multiSize);
                }
                while (data.hasRemaining()) {
                    fillBuffer(data);
                    if (buf.hasRemaining()) {
                        break;
                    }
                    buf.flip();
                    AbsEncoder.this.encode(buf, ret);
                    buf.compact();
                }
                if (end) {
                    buf.flip();
                    AbsEncoder.this.encode(buf, ret);
                }
                ret.flip();
                return ret;
            }

            protected ByteBuffer newDest(int inSize) {
                return ByteBuffer.allocate(AbsEncoder.this.getOutputSize(inSize));
            }

            protected boolean noBuf() {
                return buf == null || buf.position() == 0;
            }

            protected void fillBuffer(ByteBuffer data) {
                JieBytes.putBuffer(data, buf, Math.min(buf.remaining(), data.remaining()));
            }
        }
        public ByteStream.Encoder toStreamEncoder2() {
            return new ByteStream.Encoder() {

                private ByteBuffer buf;

                @Override
                public ByteBuffer encode(ByteBuffer data, boolean end) {
                    int multiLen = 0;
                    if (noBuf()) {
                        if (end) {
                            return AbsEncoder.this.encode(data);
                        }
                        multiLen = data.remaining() / 3 * 3;
                        if (multiLen == data.remaining()) {
                            return AbsEncoder.this.encode(data);
                        }
                    }
                    if (buf == null) {
                        buf = ByteBuffer.allocate(3);
                    }
                    int totalLen = buf.position() + data.remaining();
                    if (end) {
                        ByteBuffer ret = ByteBuffer.allocate(getOutputSize(totalLen));
                        fillLeftBuffer(buf, data);
                        buf.flip();
                        AbsEncoder.this.encode(buf, ret);
                        AbsEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                    if (buf.position() > 0) {
                        fillLeftBuffer(buf, data);
                        if (buf.position() < 3) {
                            return JieBytes.emptyBuffer();
                        }
                        multiLen = data.remaining() / 3 * 3;
                        int remainder = data.remaining() - multiLen;
                        ByteBuffer ret = ByteBuffer.allocate(getOutputSize(multiLen + 3));
                        buf.flip();
                        AbsEncoder.this.encode(buf, ret);
                        buf.flip();
                        if (remainder > 0) {
                            ByteBuffer slice = JieBytes.slice(data, 0, multiLen);
                            data.position(data.position() + multiLen);
                            AbsEncoder.this.encode(slice, ret);
                            fillLeftBuffer(buf, data);
                        } else {
                            AbsEncoder.this.encode(data, ret);
                        }
                        ret.flip();
                        return ret;
                    } else {
                        ByteBuffer ret = ByteBuffer.allocate(getOutputSize(multiLen));
                        ByteBuffer slice = JieBytes.slice(data, 0, multiLen);
                        data.position(data.position() + multiLen);
                        AbsEncoder.this.encode(slice, ret);
                        fillLeftBuffer(buf, data);
                        ret.flip();
                        return ret;
                    }
                }

                private boolean noBuf() {
                    return buf == null || buf.position() == 0;
                }

                private void fillLeftBuffer(ByteBuffer buf, ByteBuffer data) {
                    while (buf.hasRemaining() && data.hasRemaining()) {
                        buf.put(data.get());
                    }
                }
            };
        }
         */
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
            return new ByteStream.Encoder() {

                private boolean hasPrev = false;

                @Override
                public ByteBuffer encode(ByteBuffer data, boolean end) {
                    if (hasPrev) {
                        if (end && !data.hasRemaining()) {
                            return JieBytes.emptyBuffer();
                        }
                        ByteBuffer ret = ByteBuffer.allocate(getOutputSize(data.remaining()) + newLine.length);
                        for (byte b : newLine) {
                            ret.put(b);
                        }
                        MimeEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                    hasPrev = true;
                    return MimeEncoder.this.encode(data);
                }
            };
        }

        protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int lineSize = lineMax / 4 * 3;
            int roundEnd = srcOff + ((srcEnd - srcOff) / 3 * 3);
            int destPos = dstOff;
            while (srcPos < roundEnd) {
                int readEnd = Math.min(srcPos + lineSize, roundEnd);
                for (int i = srcPos, j = destPos; i < readEnd; ) {
                    int bits = (src[i++] & 0xff) << 16 | (src[i++] & 0xff) << 8 | (src[i++] & 0xff);
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

        /*
        private class ByteEncoder extends AbsEncoder.ByteEncoder {

            private boolean hasPrev = false;

            @Override
            public ByteBuffer encode(ByteBuffer data, boolean end) {
                if (noBuf()) {
                    if (end) {
                        ByteBuffer ret = newDest(data.remaining(), true);
                        MimeEncoder.this.encode(data, ret);
                        ret.flip();
                        return ret;
                    }
                }
                int blockSize = MimeEncoder.this.getBlockSize();
                if (buf == null) {
                    buf = ByteBuffer.allocate(blockSize);
                }
                int totalSize = buf.position() + data.remaining();
                if (totalSize < blockSize) {
                    fillBuffer(data);
                    if (end) {
                        buf.flip();
                        ByteBuffer ret = newDest(buf.remaining(), true);
                        MimeEncoder.this.encode(buf, ret);
                        ret.flip();
                        return ret;
                    } else {
                        return JieBytes.emptyBuffer();
                    }
                }
                int multiSize = totalSize / blockSize * blockSize;
                ByteBuffer ret;
                if (end) {
                    ret = newDest(totalSize, false);
                } else {
                    ret = newDest(multiSize, false);
                }
                while (data.hasRemaining()) {
                    fillBuffer(data);
                    if (buf.hasRemaining()) {
                        break;
                    }
                    buf.flip();
                    if (hasPrev) {
                        fillLineSeparator(ret);
                    }
                    MimeEncoder.this.encode(buf, ret);
                    hasPrev = true;
                    buf.compact();
                }
                if (end) {
                    buf.flip();
                    if (buf.hasRemaining()) {
                        for (byte b : newLine) {
                            ret.put(b);
                        }
                        MimeEncoder.this.encode(buf, ret);
                    }
                }
                ret.flip();
                return ret;
            }

            protected ByteBuffer newDest(int inSize, boolean fillSeparator) {
                if (inSize == 0) {
                    return JieBytes.emptyBuffer();
                }
                int outputSize = hasPrev ? newLine.length : 0;
                outputSize += MimeEncoder.this.getOutputSize(inSize);
                ByteBuffer ret = ByteBuffer.allocate(outputSize);
                if (fillSeparator && hasPrev) {
                    fillLineSeparator(ret);
                }
                return ret;
            }

            private void fillLineSeparator(ByteBuffer ret) {
                for (byte b : newLine) {
                    ret.put(b);
                }
            }
        }
         */
    }

    // private static abstract class AbsDecoder extends AbsCoder.De implements Decoder {
    //
    //     private static final byte[] DICT = new byte[Byte.MAX_VALUE];
    //
    //     static {
    //         Arrays.fill(DICT, (byte) -1);
    //         for (int i = 0; i < AbsEncoder.DICT.length; i++) {
    //             int c = AbsEncoder.DICT[i];
    //             DICT[c & 0xff] = (byte) i;
    //         }
    //         DICT['-'] = DICT['+'];
    //         DICT['_'] = DICT['/'];
    //         DICT['='] = -2;
    //     }
    //
    //     protected final boolean padding;
    //
    //     protected AbsDecoder(boolean padding) {
    //         this.padding = padding;
    //     }
    //
    //     @Override
    //     public int getOutputSize(int inputSize) {
    //         if (padding) {
    //             return inputSize / 4 * 3;
    //         }
    //         int remainder = inputSize % 4;
    //         if (remainder == 0) {
    //             return inputSize / 4 * 3;
    //         }
    //         if (remainder == 1) {
    //             throw new DecodingException("Illegal input size: " + inputSize + ".");
    //         }
    //         return inputSize / 4 * 3 + remainder - 1;
    //     }
    //
    //     @Override
    //     public int getBlockSize() {
    //         return 384 * 4;
    //     }
    //
    //
    //
    //     protected int doCode(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
    //         int srcPos= srcOff;
    //         int dstPos = dstOff;
    //         int totalLen = srcEnd - srcOff;
    //         int roundLen = totalLen / 4 * 4;
    //         int bits = 0;
    //         int shiftTo = 18;
    //         while (srcPos < srcOff + roundLen) {
    //             char c = (char) (src[srcPos++] & 0xff);
    //             int b = DICT[c];
    //             if (b < 0) {
    //                 if (b == -2) {
    //                     // must be padding end with xx== or xxx=
    //                     if (!padding) {
    //                         throw new DecodingException("Invalid base64 char: " + c + ".");
    //                     }
    //                     if (shiftTo == 6) {
    //                         // xx==
    //                         char nextC = (char) (src[srcPos++] & 0xff);
    //                         if (nextC != '=' || srcPos != srcEnd) {
    //                             throw new DecodingException("Invalid base64 char: " + c + ".");
    //                         } else {
    //
    //                         }
    //                     }
    //                 }
    //                 throw new DecodingException("Invalid base64 char: " + c + ".");
    //             }
    //             bits |= (b << shiftTo);
    //             shiftTo -= 6;
    //             if (shiftTo < 0) {
    //                 dst[dstPos++] = (byte) (bits >> 16);
    //                 dst[dstPos++] = (byte) (bits >> 8);
    //                 dst[dstPos++] = (byte) (bits);
    //                 shiftTo = 18;
    //                 bits = 0;
    //             }
    //         }
    //         // left
    //         while (srcPos < srcOff) {
    //
    //         }
    //
    //
    //
    //
    //         int[] base64 = isURL ? fromBase64URL : fromBase64;
    //         int dp = 0;
    //         int bits = 0;
    //         int shiftto = 18;       // pos of first byte of 4-byte atom
    //         while (sp < sl) {
    //             int b = src[sp++] & 0xff;
    //             if ((b = base64[b]) < 0) {
    //                 if (b == -2) {         // padding byte '='
    //                     // =     shiftto==18 unnecessary padding
    //                     // x=    shiftto==12 a dangling single x
    //                     // x     to be handled together with non-padding case
    //                     // xx=   shiftto==6&&sp==sl missing last =
    //                     // xx=y  shiftto==6 last is not =
    //                     if (shiftto == 6 && (sp == sl || src[sp++] != '=') ||
    //                         shiftto == 18) {
    //                         throw new IllegalArgumentException(
    //                             "Input byte array has wrong 4-byte ending unit");
    //                     }
    //                     break;
    //                 }
    //                 if (isMIME)    // skip if for rfc2045
    //                     continue;
    //                 else
    //                     throw new IllegalArgumentException(
    //                         "Illegal base64 character " +
    //                             Integer.toString(src[sp - 1], 16));
    //             }
    //             bits |= (b << shiftto);
    //             shiftto -= 6;
    //             if (shiftto < 0) {
    //                 dst[dp++] = (byte) (bits >> 16);
    //                 dst[dp++] = (byte) (bits >> 8);
    //                 dst[dp++] = (byte) (bits);
    //                 shiftto = 18;
    //                 bits = 0;
    //             }
    //         }
    //         // reached end of byte array or hit padding '=' characters.
    //         if (shiftto == 6) {
    //             dst[dp++] = (byte) (bits >> 16);
    //         } else if (shiftto == 0) {
    //             dst[dp++] = (byte) (bits >> 16);
    //             dst[dp++] = (byte) (bits >> 8);
    //         } else if (shiftto == 12) {
    //             // dangling single "x", incorrectly encoded.
    //             throw new IllegalArgumentException(
    //                 "Last unit does not have enough valid bits");
    //         }
    //         // anything left is invalid, if is not MIME.
    //         // if MIME, ignore all non-base64 character
    //         while (sp < sl) {
    //             if (isMIME && base64[src[sp++]] < 0)
    //                 continue;
    //             throw new IllegalArgumentException(
    //                 "Input byte array has incorrect ending byte at " + sp);
    //         }
    //         return dp;
    //     }
    // }
}
