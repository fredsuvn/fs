package space.sunqian.common.codec;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.base.chars.CharsKit;
import space.sunqian.common.base.exception.FsRuntimeException;
import space.sunqian.common.io.BufferKit;
import space.sunqian.common.io.ByteArrayOperator;
import space.sunqian.common.io.IORuntimeException;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Utilities for Hex.
 *
 * @author sunqian
 */
public class HexKit {

    /**
     * Returns a {@code hex} encoder encoding in upper case. This method is equivalent to {@code encoder(true)}.
     *
     * @return a {@code hex} encoder encoding in upper case
     */
    public static Encoder encoder() {
        return encoder(true);
    }

    /**
     * Returns a {@code hex} encoder. The {@code upper} parameter specifies whether the encoder uses upper case for
     * encoding ({@code true} for {@code A-F} and {@code false} for {@code a-f}).
     *
     * @param upper {@code true} for upper case, otherwise {@code false}
     * @return a {@code hex} encoder
     */
    public static Encoder encoder(boolean upper) {
        return upper ? EncoderImpl.UPPER : EncoderImpl.LOWER;
    }

    /**
     * Returns a {@code hex} decoder with the strict mode. This method is equivalent to {@code decoder(true)}.
     *
     * @return a {@code hex} decoder with the strict mod
     */
    public static Decoder decoder() {
        return decoder(true);
    }

    /**
     * Returns a {@code hex} decoder. The {@code strict} parameter specifies whether the decoder is strict or not, a
     * strict decoder will throw an exception if it encounters an invalid hex character, and an un-strict decoder will
     * ignore the invalid hex characters.
     *
     * @param strict {@code true} for strict, otherwise {@code false}
     * @return a {@code hex} decoder with the specified mod
     */
    public static Decoder decoder(boolean strict) {
        return strict ? DecoderImpl.STRICT : DecoderImpl.LOOSE;
    }

    private static final class EncoderImpl implements Encoder, ByteArrayOperator {

        private static final @Nonnull EncoderImpl UPPER = new EncoderImpl(true);
        private static final @Nonnull EncoderImpl LOWER = new EncoderImpl(false);

        private static final char @Nonnull [] UPPER_DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        private static final char @Nonnull [] LOWER_DICT = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };

        private final boolean upper;

        private EncoderImpl(boolean upper) {
            this.upper = upper;
        }

        @Override
        public byte @Nonnull [] encode(byte @Nonnull [] bytes) throws HexException {
            byte[] dst = new byte[bytes.length * 2];
            process(bytes, 0, dst, 0, bytes.length);
            return dst;
        }

        @Override
        public byte @Nonnull [] encode(@Nonnull ByteBuffer buffer) throws HexException {
            byte[] dst = new byte[buffer.remaining() * 2];
            ByteBuffer dstBuf = ByteBuffer.wrap(dst);
            BufferKit.process(buffer, dstBuf, this);
            return dst;
        }

        @Override
        public int process(byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len) {
            char[] dict = upper ? UPPER_DICT : LOWER_DICT;
            for (int i = srcOff, j = dstOff; i < srcOff + len; ) {
                int bits = src[i++];
                dst[j++] = (byte) dict[((bits >> 4) & 0x0f)];
                dst[j++] = (byte) dict[(bits & 0x0f)];
            }
            return len * 2;
        }
    }

    private static final class DecoderImpl implements Decoder, ByteArrayOperator {

        private static final @Nonnull DecoderImpl STRICT = new DecoderImpl(true);
        private static final @Nonnull DecoderImpl LOOSE = new DecoderImpl(false);

        private final boolean strict;

        private DecoderImpl(boolean strict) {
            this.strict = strict;
        }

        @Override
        public byte @Nonnull [] decode(byte @Nonnull [] bytes) throws HexException {
            checkLen(bytes.length);
            byte[] dst = new byte[bytes.length / 2];
            int actualLen = process(bytes, 0, dst, 0, bytes.length);
            return actualLen == dst.length ? dst : Arrays.copyOfRange(dst, 0, actualLen);
        }

        @Override
        public byte @Nonnull [] decode(@Nonnull ByteBuffer buffer) throws HexException {
            checkLen(buffer.remaining());
            byte[] dst = new byte[buffer.remaining() / 2];
            ByteBuffer dstBuf = ByteBuffer.wrap(dst);
            try {
                int actualLen = BufferKit.process(buffer, dstBuf, this);
                return actualLen == dst.length ? dst : Arrays.copyOfRange(dst, 0, actualLen);
            } catch (IORuntimeException e) {
                throw (HexException) e.getCause();
            }
        }

        @Override
        public int process(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws HexException {
            if (strict) {
                return processStrict(src, srcOff, dst, dstOff, len);
            } else {
                return processLoose(src, srcOff, dst, dstOff, len);
            }
        }

        private int processStrict(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws HexException {
            for (int i = 0, j = dstOff; i < len; ) {
                int bits1 = toDigit((char) src[i + srcOff]);
                if (bits1 < 0) {
                    throw new HexException(i, "The hex string contains invalid character at position: " + i + ".");
                }
                i++;
                int bits2 = toDigit((char) src[i + srcOff]);
                if (bits2 < 0) {
                    throw new HexException(i, "The hex string contains invalid character at position: " + i + ".");
                }
                i++;
                int bits = ((bits1 << 4) | bits2);
                dst[j++] = (byte) bits;
            }
            return len / 2;
        }

        public int processLoose(
            byte @Nonnull [] src, int srcOff, byte @Nonnull [] dst, int dstOff, int len
        ) throws HexException {
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
                throw new HexException("The valid hex string is not a multiple of 2.");
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

        private void checkLen(int len) throws HexException {
            if (strict) {
                if (len % 2 != 0) {
                    throw new HexException("The length of hex string is not a multiple of 2.");
                }
            }
        }
    }

    /**
     * Encoder for Hex. The implementation should be immutable and thread-safe.
     */
    public interface Encoder {

        /**
         * Encodes the given bytes to hex string as byte array.
         *
         * @param bytes the given bytes to encode
         * @return the hex string as byte array
         * @throws HexException if any error occurs
         */
        byte @Nonnull [] encode(byte @Nonnull [] bytes) throws HexException;

        /**
         * Encodes the given buffer to hex string as byte array. The position of the given buffer will increment to its
         * limit.
         *
         * @param buffer the given buffer to encode
         * @return the hex string as byte array
         * @throws HexException if any error occurs
         */
        byte @Nonnull [] encode(@Nonnull ByteBuffer buffer) throws HexException;

        /**
         * Encodes the given bytes to hex string.
         *
         * @param bytes the given bytes to encode
         * @return the hex string
         * @throws HexException if any error occurs
         */
        default @Nonnull String encodeToString(byte @Nonnull [] bytes) throws HexException {
            byte[] dst = encode(bytes);
            return new String(dst, StandardCharsets.ISO_8859_1);
        }

        /**
         * Encodes the given buffer to hex string. The position of the given buffer will increment to its limit.
         *
         * @param buffer the given buffer to encode
         * @return the hex string
         * @throws HexException if any error occurs
         */
        default @Nonnull String encodeToString(@Nonnull ByteBuffer buffer) throws HexException {
            byte[] dst = encode(buffer);
            return new String(dst, StandardCharsets.ISO_8859_1);
        }
    }

    /**
     * Decoder for Hex. The implementation should be immutable and thread-safe.
     */
    public interface Decoder {

        /**
         * Decodes the given bytes of hex string to the original bytes.
         *
         * @param bytes the given bytes of hex string
         * @return the original bytes
         * @throws HexException if any error occurs
         */
        byte @Nonnull [] decode(byte @Nonnull [] bytes) throws HexException;

        /**
         * Decodes the given buffer of hex string to the original bytes. The position of the given buffer will increment
         * to its limit.
         *
         * @param buffer the given buffer of hex string
         * @return the original bytes
         * @throws HexException if any error occurs
         */
        byte @Nonnull [] decode(@Nonnull ByteBuffer buffer) throws HexException;

        /**
         * Decodes the given hex string to the original bytes.
         *
         * @param hex the given hex string
         * @return the original bytes
         * @throws HexException if any error occurs
         */
        default byte @Nonnull [] decode(@Nonnull String hex) throws HexException {
            byte[] src = hex.getBytes(StandardCharsets.ISO_8859_1);
            return decode(src);
        }

        /**
         * Decodes the given hex string to the original bytes, and then convert to string with the given charset.
         *
         * @param hex     the given hex string
         * @param charset the charset to use
         * @return the original string
         * @throws HexException if any error occurs
         */
        default @Nonnull String decodeToString(@Nonnull String hex, @Nonnull Charset charset) throws HexException {
            byte[] src = decode(hex);
            return new String(src, charset);
        }

        /**
         * Decodes the given hex string to the original bytes, and then convert to string with the
         * {@link CharsKit#defaultCharset()}.
         *
         * @param hex the given hex string
         * @return the original string decoded with the {@link CharsKit#defaultCharset()}
         * @throws HexException if any error occurs
         */
        default @Nonnull String decodeToString(@Nonnull String hex) throws HexException {
            return decodeToString(hex, CharsKit.defaultCharset());
        }
    }

    /**
     * Exception for hex encoding/decoding. The {@link #position()} returns the position where this exception occurs.
     *
     * @author sunqian
     */
    public static class HexException extends FsRuntimeException {

        private final long position;

        /**
         * Empty constructor.
         */
        public HexException() {
            super();
            this.position = -1;
        }

        /**
         * Constructs with the message.
         *
         * @param message the message
         */
        public HexException(@Nullable String message) {
            super(message);
            this.position = -1;
        }

        /**
         * Constructs with the message and cause.
         *
         * @param message the message
         * @param cause   the cause
         */
        public HexException(@Nullable String message, @Nullable Throwable cause) {
            super(message, cause);
            this.position = -1;
        }

        /**
         * Constructs with the cause.
         *
         * @param cause the cause
         */
        public HexException(@Nullable Throwable cause) {
            super(cause);
            this.position = -1;
        }

        /**
         * Empty with the position.
         *
         * @param position the position where this exception occurs
         */
        public HexException(long position) {
            super();
            this.position = position;
        }

        /**
         * Constructs with the position and message.
         *
         * @param position the position where this exception occurs
         * @param message  the message
         */
        public HexException(long position, @Nullable String message) {
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
        public HexException(long position, @Nullable String message, @Nullable Throwable cause) {
            super(message, cause);
            this.position = position;
        }

        /**
         * Constructs with the position and cause.
         *
         * @param position the position where this exception occurs
         * @param cause    the cause
         */
        public HexException(long position, @Nullable Throwable cause) {
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

    private HexKit() {
    }
}
