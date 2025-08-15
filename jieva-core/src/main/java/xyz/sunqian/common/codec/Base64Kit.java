package xyz.sunqian.common.codec;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.exception.JieRuntimeException;
import xyz.sunqian.common.io.BufferKit;
import xyz.sunqian.common.io.ByteArrayOperator;
import xyz.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utilities kit for Base64.
 *
 * @author sunqian
 */
public class Base64Kit {

    /**
     * Returns a {@code base64} encoder encoding in upper case. This method is equivalent to {@code encoder(true)}.
     *
     * @return a {@code base64} encoder encoding in upper case
     */
    public static Encoder encoder() {
        return encoder(false, true);
    }

    /**
     * Returns a {@code base64} encoder. The {@code url} parameter specifies whether the encoder is URL-Safe, and the
     * {@code padding} parameter specifies whether the encoder will add padding character at tail.
     *
     * @param url
     * @return a {@code base64} encoder
     */
    public static Encoder encoder(boolean url, boolean padding) {
        return url ?
            (padding ? EncoderImpl.URL_PADDING : EncoderImpl.URL_NO_PADDING)
            : (padding ? EncoderImpl.BASE_PADDING : EncoderImpl.BASE_NO_PADDING);
    }

    /**
     * Returns a {@code base64} decoder with the strict mode. This method is equivalent to {@code decoder(true)}.
     *
     * @return a {@code base64} decoder with the strict mod
     */
    public static Decoder decoder() {
        return decoder(true);
    }

    /**
     * Returns a {@code base64} decoder. The {@code strict} parameter specifies whether the decoder is strict or not, a
     * strict decoder will throw an exception if it encounters a valid base64 character, and an un-strict decoder will
     * ignore the valid base64 characters.
     *
     * @param strict {@code true} for strict, otherwise {@code false}
     * @return a {@code base64} decoder with the specified mod
     */
    public static Decoder decoder(boolean strict) {
        return strict ? DecoderImpl.STRICT_SINGLETON : DecoderImpl.LOOSE_SINGLETON;
    }

    private static final class EncoderImpl implements Encoder, ByteArrayOperator {

        private static final @Nonnull EncoderImpl BASE_PADDING = new EncoderImpl(false, true);
        private static final @Nonnull EncoderImpl BASE_NO_PADDING = new EncoderImpl(false, false);
        private static final @Nonnull EncoderImpl URL_PADDING = new EncoderImpl(true, true);
        private static final @Nonnull EncoderImpl URL_NO_PADDING = new EncoderImpl(true, false);

        private static final char @Nonnull [] BASE_DICT = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        private static final char @Nonnull [] URL_DICT = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'
        };

        private final boolean url;
        private final boolean padding;

        private EncoderImpl(boolean url, boolean padding) {
            this.url = url;
            this.padding = padding;
        }

        @Override
        public byte @Nonnull [] encode(byte @Nonnull [] bytes) throws Base64Exception {
            byte[] dst = new byte[bytes.length * 2];
            process(bytes, 0, dst, 0, bytes.length);
            return dst;
        }

        @Override
        public byte @Nonnull [] encode(@Nonnull ByteBuffer buffer) throws Base64Exception {
            byte[] dst = new byte[buffer.remaining() * 2];
            ByteBuffer dstBuf = ByteBuffer.wrap(dst);
            BufferKit.process(buffer, dstBuf, this);
            return dst;
        }

        @Override
        public int process(byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len) {
            char[] dict = url ? URL_DICT : BASE_DICT;
            int roundLen = len / 3 * 3;
            int i = srcOff, j = dstOff;
            while (i < srcOff + roundLen) {
                int bits = (src[i++] & 0xff) << 16 | (src[i++] & 0xff) << 8 | (src[i++] & 0xff);
                dst[j++] = (byte) dict[(bits >>> 18) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 12) & 0x3f];
                dst[j++] = (byte) dict[(bits >>> 6) & 0x3f];
                dst[j++] = (byte) dict[bits & 0x3f];
            }
            // 1 or 2 leftover bytes
            if (roundLen < len) {
                int b0 = src[i++] & 0xff;
                dst[j++] = (byte) dict[b0 >> 2];
                if (i == srcOff + len) {
                    dst[j++] = (byte) dict[(b0 << 4) & 0x3f];
                    if (padding) {
                        dst[j++] = '=';
                        dst[j++] = '=';
                    }
                } else {
                    int b1 = src[j++] & 0xff;
                    dst[j++] = (byte) dict[(b0 << 4) & 0x3f | (b1 >> 4)];
                    dst[j++] = (byte) dict[(b1 << 2) & 0x3f];
                    if (padding) {
                        dst[j++] = '=';
                    }
                }
            }
            return j - dstOff;
        }
    }

    private static final class DecoderImpl implements Decoder, ByteArrayOperator {

        private static final @Nonnull DecoderImpl STRICT_SINGLETON = new DecoderImpl(true);
        private static final @Nonnull DecoderImpl LOOSE_SINGLETON = new DecoderImpl(false);

        private final boolean strict;

        private DecoderImpl(boolean strict) {
            this.strict = strict;
        }

        @Override
        public byte @Nonnull [] decode(byte @Nonnull [] bytes) throws Base64Exception {
            checkLen(bytes.length);
            byte[] dst = new byte[bytes.length / 2];
            int actualLen = process(bytes, 0, dst, 0, bytes.length);
            return actualLen == dst.length ? dst : Arrays.copyOfRange(dst, 0, actualLen);
        }

        @Override
        public byte @Nonnull [] decode(@Nonnull ByteBuffer buffer) throws Base64Exception {
            checkLen(buffer.remaining());
            byte[] dst = new byte[buffer.remaining() / 2];
            ByteBuffer dstBuf = ByteBuffer.wrap(dst);
            try {
                int actualLen = BufferKit.process(buffer, dstBuf, this);
                return actualLen == dst.length ? dst : Arrays.copyOfRange(dst, 0, actualLen);
            } catch (IORuntimeException e) {
                throw (Base64Exception) e.getCause();
            }
        }

        @Override
        public int process(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws Base64Exception {
            if (strict) {
                return processStrict(src, srcOff, dst, dstOff, len);
            } else {
                return processLoose(src, srcOff, dst, dstOff, len);
            }
        }

        private int processStrict(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws Base64Exception {
            for (int i = 0, j = dstOff; i < len; ) {
                int bits1 = toDigit((char) src[i + srcOff]);
                if (bits1 < 0) {
                    throw new Base64Exception(i, "The base64 string contains invalid character at position: " + i + ".");
                }
                i++;
                int bits2 = toDigit((char) src[i + srcOff]);
                if (bits2 < 0) {
                    throw new Base64Exception(i, "The base64 string contains invalid character at position: " + i + ".");
                }
                i++;
                int bits = ((bits1 << 4) | bits2);
                dst[j++] = (byte) bits;
            }
            return len / 2;
        }

        public int processLoose(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws Base64Exception {
            int i = 0, j = dstOff, count = 0;
            int bits1 = -1;
            while (i < len) {
                int bits = toDigit((char) src[i + srcOff]);
                if (bits < 0) {
                    i++;
                    continue;
                }
                if (bits1 < 0) {
                    bits1 = bits;
                } else {
                    int c = ((bits1 << 4) | bits);
                    dst[j++] = (byte) c;
                    count++;
                    bits1 = -1;
                }
                i++;
            }
            if (bits1 >= 0) {
                throw new Base64Exception("The valid base64 string is not a multiple of 2.");
            }
            return count;
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
            return -1;
        }

        private void checkLen(int len) throws Base64Exception {
            if (strict) {
                if (len % 2 != 0) {
                    throw new Base64Exception("The length of base64 string is not a multiple of 2.");
                }
            }
        }
    }

    /**
     * Encoder for Base64. The implementation should be immutable and thread-safe.
     */
    public interface Encoder {

        /**
         * Encodes the given bytes to base64 string as byte array.
         *
         * @param bytes the given bytes to encode
         * @return the base64 string as byte array
         * @throws Base64Exception if any error occurs
         */
        byte @Nonnull [] encode(byte @Nonnull [] bytes) throws Base64Exception;

        /**
         * Encodes the given buffer to base64 string as byte array. The position of the given buffer will increment to
         * its limit.
         *
         * @param buffer the given buffer to encode
         * @return the base64 string as byte array
         * @throws Base64Exception if any error occurs
         */
        byte @Nonnull [] encode(@Nonnull ByteBuffer buffer) throws Base64Exception;

        /**
         * Encodes the given bytes to base64 string.
         *
         * @param bytes the given bytes to encode
         * @return the base64 string
         * @throws Base64Exception if any error occurs
         */
        default @Nonnull String encodeToString(byte @Nonnull [] bytes) throws Base64Exception {
            byte[] dst = encode(bytes);
            return new String(dst, StandardCharsets.ISO_8859_1);
        }

        /**
         * Encodes the given buffer to base64 string. The position of the given buffer will increment to its limit.
         *
         * @param buffer the given buffer to encode
         * @return the base64 string
         * @throws Base64Exception if any error occurs
         */
        default @Nonnull String encodeToString(@Nonnull ByteBuffer buffer) throws Base64Exception {
            byte[] dst = encode(buffer);
            return new String(dst, StandardCharsets.ISO_8859_1);
        }
    }

    /**
     * Decoder for Base64. The implementation should be immutable and thread-safe.
     */
    public interface Decoder {

        /**
         * Decodes the given bytes of base64 string to the original bytes.
         *
         * @param bytes the given bytes of base64 string
         * @return the original bytes
         * @throws Base64Exception if any error occurs
         */
        byte @Nonnull [] decode(byte @Nonnull [] bytes) throws Base64Exception;

        /**
         * Decodes the given buffer of base64 string to the original bytes. The position of the given buffer will
         * increment to its limit.
         *
         * @param buffer the given buffer of base64 string
         * @return the original bytes
         * @throws Base64Exception if any error occurs
         */
        byte @Nonnull [] decode(@Nonnull ByteBuffer buffer) throws Base64Exception;

        /**
         * Decodes the given base64 string to the original bytes.
         *
         * @param base64 the given base64 string
         * @return the original bytes
         * @throws Base64Exception if any error occurs
         */
        default byte @Nonnull [] decode(@Nonnull String base64) throws Base64Exception {
            byte[] src = base64.getBytes(StandardCharsets.ISO_8859_1);
            return decode(src);
        }
    }

    /**
     * Exception for base64 encoding/decoding. The {@link #position()} returns the position where this exception
     * occurs.
     *
     * @author sunqian
     */
    public static class Base64Exception extends JieRuntimeException {

        private final long position;

        /**
         * Empty constructor.
         */
        public Base64Exception() {
            super();
            this.position = -1;
        }

        /**
         * Constructs with the message.
         *
         * @param message the message
         */
        public Base64Exception(@Nullable String message) {
            super(message);
            this.position = -1;
        }

        /**
         * Constructs with the message and cause.
         *
         * @param message the message
         * @param cause   the cause
         */
        public Base64Exception(@Nullable String message, @Nullable Throwable cause) {
            super(message, cause);
            this.position = -1;
        }

        /**
         * Constructs with the cause.
         *
         * @param cause the cause
         */
        public Base64Exception(@Nullable Throwable cause) {
            super(cause);
            this.position = -1;
        }

        /**
         * Empty with the position.
         *
         * @param position the position where this exception occurs
         */
        public Base64Exception(long position) {
            super();
            this.position = position;
        }

        /**
         * Constructs with the position and message.
         *
         * @param position the position where this exception occurs
         * @param message  the message
         */
        public Base64Exception(long position, @Nullable String message) {
            super(message);
            this.position = position;
        }

        /**
         * Constructs with the position, message and cause.
         *
         * @param position the position where this exception occurs
         * @param message  the message
         * @param cause    the cause
         */
        public Base64Exception(long position, @Nullable String message, @Nullable Throwable cause) {
            super(message, cause);
            this.position = position;
        }

        /**
         * Constructs with the position and cause.
         *
         * @param position the position where this exception occurs
         * @param cause    the cause
         */
        public Base64Exception(long position, @Nullable Throwable cause) {
            super(cause);
            this.position = position;
        }

        /**
         * Returns the position where this exception occurs, may be {@code -1} if the position is unknown.
         *
         * @return the position where this exception occurs, may be {@code -1} if the position is unknown
         */
        public long position() {
            return position;
        }
    }
}
