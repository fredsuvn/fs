package xyz.fslabo.common.encode;

import xyz.fslabo.common.base.JieBytes;
import xyz.fslabo.common.codec.CodecException;
import xyz.fslabo.common.io.ByteStream;
import xyz.fslabo.common.io.JieBuffer;

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
 * {@link Encoder#toStreamEncoder()} returns a stateful and non-thread-safe object, it is the recommended that
 * set {@link ByteStream#blockSize(int)} to be {@link Encoder#getBlockSize()} or a multiple of it (other legal values
 * are permitted but may be low performance).
 *
 * @author sunqian
 */
public class JieBase64 {

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code Basic}, with padding character if the length of
     * source is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code Basic}
     */
    public static Base64Encoder encoder() {
        return BasicEncoder.PADDING;
    }

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code Basic}. without padding character if the length of
     * source is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code Basic}
     */
    public static Base64Encoder encoder(boolean padding) {
        return padding ? encoder() : BasicEncoder.NO_PADDING;
    }

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code URL and Filename safe}, with padding character if the
     * length of source is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code URL and Filename safe}
     */
    public static Base64Encoder urlEncoder() {
        return UrlEncoder.PADDING;
    }

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code URL and Filename safe}, without padding character if
     * the length of source is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code URL and Filename safe}
     */
    public static Base64Encoder urlEncoder(boolean padding) {
        return padding ? urlEncoder() : UrlEncoder.NO_PADDING;
    }

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code MIME}, with padding character if the length of source
     * is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code MIME}
     */
    public static Base64Encoder mimeEncoder() {
        return MimeEncoder.PADDING;
    }

    /**
     * Returns an {@link Base64Encoder} instance in type of {@code MIME}, without padding character if the length of
     * source is not a multiple of 3.
     *
     * @return an {@link Base64Encoder} instance in type of {@code MIME}
     */
    public static Base64Encoder mimeEncoder(boolean padding) {
        return padding ? mimeEncoder() : MimeEncoder.NO_PADDING;
    }

    /**
     * Returns a new {@link Base64Encoder} in type of {@code MIME}, with specified arguments.
     *
     * @param lineMax sets the max length per line, must be a multiple of {@code 4}
     * @param newLine sets the line separator. The array will be used directly, any modification to array will affect
     *                the encoding.
     * @param padding whether add padding character at the end if the length of source is not a multiple of 3.
     */
    public static Base64Encoder mimeEncoder(int lineMax, byte[] newLine, boolean padding) {
        return new MimeEncoder(padding, lineMax, newLine);
    }

    private static void checkRemaining(int srcRemaining, int dstRemaining) {
        if (srcRemaining > dstRemaining) {
            throw new CodecException("Remaining of destination is not enough.");
        }
    }

    private static abstract class AbsEncoder implements Base64Encoder {

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

        @Override
        public ByteStream.Encoder toStreamEncoder() {
            return new ByteEncoder();
        }

        protected char[] dict() {
            return DICT;
        }

        protected int encode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
            char[] dict = dict();
            int srcPos = srcOff;
            int dstPos = dstOff;
            int roundLen = (srcEnd - srcOff) / 3 * 3;
            int roundEnd = srcPos + roundLen;
            for (int i = srcPos, j = dstPos; i < roundEnd; ) {
                int bits = (src[i++] & 0xff) << 16 |
                    (src[i++] & 0xff) << 8 |
                    (src[i++] & 0xff);
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

        /*
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
            return new ByteEncoder();
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
    }

    // private static abstract class AbsDecoder implements Base64Decoder, ByteStream.Encoder {
    //
    //     private static final char[] DICT = {
    //         'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    //         'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
    //         'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    //         'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    //         '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    //     };
    //
    //     protected final boolean padding;
    //
    //     protected AbsDecoder(boolean padding) {
    //         this.padding = padding;
    //     }
    //
    //     @Override
    //     public byte[] decode(byte[] data) throws CodecException {
    //         int len = getOutputSize(data.length);
    //         byte[] dst = new byte[len];
    //         decode0(data, 0, data.length, dst, 0);
    //         return dst;
    //     }
    //
    //     @Override
    //     public ByteBuffer decode(ByteBuffer data) throws CodecException {
    //         int len = getOutputSize(data.remaining());
    //         byte[] dst = new byte[len];
    //         ByteBuffer ret = ByteBuffer.wrap(dst);
    //         if (data.hasArray()) {
    //             decode0(
    //                 data.array(),
    //                 JieBuffer.getArrayStartIndex(data),
    //                 JieBuffer.getArrayEndIndex(data),
    //                 dst,
    //                 0
    //             );
    //             data.position(data.limit());
    //         } else {
    //             byte[] s = new byte[data.remaining()];
    //             data.get(s);
    //             decode0(s, 0, s.length, dst, 0);
    //         }
    //         return ret;
    //     }
    //
    //     @Override
    //     public int decode(byte[] data, byte[] dest) throws CodecException {
    //         int outputSize = getOutputSize(data.length);
    //         checkRemaining(outputSize, dest.length);
    //         return decode0(data, 0, data.length, dest, 0);
    //     }
    //
    //     @Override
    //     public int decode(ByteBuffer data, ByteBuffer dest) throws CodecException {
    //         int outputSize = getOutputSize(data.remaining());
    //         checkRemaining(outputSize, dest.remaining());
    //         if (data.hasArray() && dest.hasArray()) {
    //             decode0(
    //                 data.array(),
    //                 JieBuffer.getArrayStartIndex(data),
    //                 JieBuffer.getArrayEndIndex(data),
    //                 dest.array(),
    //                 JieBuffer.getArrayStartIndex(dest)
    //             );
    //             data.position(data.limit());
    //             dest.position(dest.position() + outputSize);
    //         } else {
    //             ByteBuffer dst = decode(data);
    //             dest.put(dst);
    //         }
    //         return outputSize;
    //     }
    //
    //     @Override
    //     public int getOutputSize(int inputSize) {
    //         if (padding) {
    //             return inputSize / 4 * 3;
    //         }
    //         int remainder = inputSize % 3;
    //         if (remainder == 0) {
    //             return inputSize / 3 * 4;
    //         }
    //         return inputSize / 3 * 4 + remainder + 1;
    //     }
    //
    //     @Override
    //     public int getBlockSize() {
    //         return 1024 * 3;
    //     }
    //
    //     @Override
    //     public ByteStream.Encoder toStreamEncoder() {
    //         return this;
    //     }
    //
    //     @Override
    //     public ByteBuffer encode(ByteBuffer source, boolean end) throws CodecException {
    //         return decode(source);
    //     }
    //
    //     protected int decode0(byte[] src, int srcOff, int srcEnd, byte[] dst, int dstOff) {
    //         return 0;
    //     }
    // }
}
