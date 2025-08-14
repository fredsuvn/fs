package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.bytes.BytesKit;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteArrayOperator;
import xyz.sunqian.common.io.ByteTransformer;

import java.nio.ByteBuffer;

/**
 * Utilities kit for Hex.
 *
 * @author sunqian
 */
public class HexKit {

    /**
     * Returns a {@code hex} encoder. The encoder and its {@link ByteEncoder#asTransformer()} are stateless and
     * thread-safe.
     *
     * @return a {@code hex} encoder
     */
    public static @ThreadSafe ByteEncoder encoder() {
        return HexEncoder.SINGLETON;
    }

    /**
     * Returns a {@code hex} decoder. Note its {@link ByteEncoder#asTransformer()} are stateful and not thread-safe.
     *
     * @return a {@code hex} decoder
     */
    public static ByteDecoder decoder() {
        return HexDecoder.SINGLETON;
    }

    private static final class HexEncoder implements ByteEncoder, ByteArrayOperator, ByteTransformer {

        private static final HexEncoder SINGLETON = new HexEncoder();

        private static final char[] DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        @Override
        public byte @Nonnull [] encode(byte @Nonnull [] bytes) throws ByteEncodingException {
            byte[] dst = new byte[bytes.length * 2];
            process(bytes, 0, dst, 0, bytes.length);
            return dst;
        }

        @Override
        public @Nonnull ByteBuffer encode(@Nonnull ByteBuffer bytes) throws ByteEncodingException {
            ByteBuffer dst = ByteBuffer.allocate(bytes.remaining() * 2);
            BufferKit.process(bytes, dst, this);
            dst.flip();
            return dst;
        }

        @Override
        public int encode(@Nonnull ByteBuffer in, @Nonnull ByteBuffer out) throws ByteEncodingException {
            return BufferKit.process(in, out, this);
        }

        @Override
        public @Nonnull ByteTransformer asTransformer() {
            return this;
        }

        @Override
        public int process(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) {
            for (int i = srcOff, j = dstOff; i < srcOff + len; ) {
                int bits = src[i++];
                dst[j++] = (byte) DICT[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) DICT[(bits & 0x0f)];
            }
            return len * 2;
        }

        @Override
        public @Nonnull ByteBuffer transform(@Nonnull ByteBuffer data, boolean end) {
            return encode(data);
        }
    }

    private static final class HexDecoder implements ByteDecoder, ByteArrayOperator {

        private static final HexDecoder SINGLETON = new HexDecoder();

        @Override
        public byte @Nonnull [] decode(byte @Nonnull [] bytes) throws ByteDecodingException {
            byte[] dst = new byte[bytes.length * 2];
            process(bytes, 0, dst, 0, bytes.length);
            return dst;
        }

        @Override
        public @Nonnull ByteBuffer decode(@Nonnull ByteBuffer bytes) throws ByteDecodingException {
            ByteBuffer dst = ByteBuffer.allocate(bytes.remaining() * 2);
            BufferKit.process(bytes, dst, this);
            dst.flip();
            return dst;
        }

        @Override
        public int decode(@Nonnull ByteBuffer in, @Nonnull ByteBuffer out) throws ByteDecodingException {
            return BufferKit.process(in, out, this);
        }

        @Override
        public @Nonnull ByteTransformer asTransformer() {
            return new ByteTransformer() {

                private long count = 0;
                private int bits1 = 0;

                @Override
                public @Nonnull ByteBuffer transform(@Nonnull ByteBuffer data, boolean end) throws Exception {
                    int totalLen = data.remaining() + (bits1 == 0 ? 0 : 1);
                    ByteBuffer dst = null;

                    try {
                        // decodes last char
                        if (bits1 != 0) {
                            if (!data.hasRemaining()) {
                                if (end) {
                                    throw new ByteDecodingException("Invalid hex length: " + (count + 1) + ".");
                                }
                                return BytesKit.emptyBuffer();
                            }
                            int bits2 = toDigit((char) data.get(), 0);
                            int bits = ((bits1 << 4) | bits2);
                            dst = ByteBuffer.allocate(totalLen / 2);
                            dst.put((byte) bits);
                        }

                        // decodes data
                        int deLen = data.remaining() / 2 * 2;
                        ByteBuffer deData;
                        if (data.remaining() == deLen) {
                            deData = data;
                        } else {
                            deData = BufferKit.slice(data, deLen);
                            data.position(data.position() + deLen);
                        }
                        if (dst == null) {
                            dst = ByteBuffer.allocate(deLen / 2);
                        }
                        BufferKit.process(deData, dst, HexDecoder.this);

                        // record last char
                        if (data.hasRemaining()) {
                            bits1 = toDigit((char) data.get(), totalLen - 1);
                        } else {
                            bits1 = 0;
                        }
                        count += dst.capacity() * 2L;
                        dst.flip();
                        return dst;
                    } catch (HexDecodingException e) {
                        throw new ByteDecodingException(count + e.position());
                    }
                }
            };
        }

        @Override
        public int process(
            byte @Nonnull [] src, final int srcOff, byte @Nonnull [] dst, final int dstOff, final int len
        ) {
            for (int i = 0, j = dstOff; i < len; ) {
                int bits1 = toDigit((char) src[i + srcOff], i);
                i++;
                int bits2 = toDigit((char) src[i + srcOff], i);
                i++;
                int bits = ((bits1 << 4) | bits2);
                dst[j++] = (byte) bits;
            }
            return len / 2;
        }

        private int toDigit(char c, long pos) throws HexDecodingException {
            if (c >= '0' && c <= '9') {
                return c - '0';
            }
            if (c >= 'a' && c <= 'f') {
                return c - 'a' + 10;
            }
            if (c >= 'A' && c <= 'F') {
                return c - 'A' + 10;
            }
            throw new HexDecodingException(pos);
        }

        private static final class HexDecodingException extends ByteDecodingException {

            private HexDecodingException(long pos) {
                super(pos);
            }
        }
    }
}
