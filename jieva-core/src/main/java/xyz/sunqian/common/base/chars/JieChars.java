package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.JieSystem;
import xyz.sunqian.common.base.bytes.JieBytes;

import java.io.Reader;
import java.nio.BufferOverflowException;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * This is a static utilities class provides utilities for {@code character}/{@link Charset}.
 *
 * @author fredsuvn
 */
public class JieChars {

    /**
     * Charset: UTF-8.
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Charset: ISO-8859-1.
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    private static final char[] EMPTY_CHARS = {};
    private static final CharBuffer EMPTY_BUFFER = CharBuffer.wrap(EMPTY_CHARS);

    //---------------- Common Begin ----------------//

    /**
     * Returns default charset: {@link #UTF_8}.
     *
     * @return default charset: {@link #UTF_8}
     */
    public static Charset defaultCharset() {
        return UTF_8;
    }

    /**
     * Returns latin charset: {@link #ISO_8859_1}.
     *
     * @return latin charset: {@link #ISO_8859_1}
     */
    public static Charset latinCharset() {
        return ISO_8859_1;
    }

    /**
     * Returns the default charset of this Java virtual machine. It is equivalent to {@link Charset#defaultCharset()}.
     *
     * @return the default charset of this Java virtual machine
     * @see Charset#defaultCharset()
     */
    public static Charset jvmCharset() {
        return Charset.defaultCharset();
    }

    /**
     * Returns the default charset of current native environment, which is typically the current OS.
     * <p>
     * This method is <b>not</b> equivalent to {@link #jvmCharset()}, it will search the system properties in the
     * following order:
     * <ul>
     *     <li>native.encoding</li>
     *     <li>sun.jnu.encoding</li>
     *     <li>file.encoding</li>
     * </ul>
     * It may return {@code null} if not found.
     *
     * @return the default charset of current native environment
     */
    @Nullable
    public static Charset nativeCharset() {
        return Natives.NATIVE_CHARSET;
    }

    /**
     * Returns the charset with specified charset name, may be {@code null} if the search fails.
     *
     * @param name specified charset name
     * @return the charset with specified charset name
     */
    @Nullable
    public static Charset charset(String name) {
        try {
            return Charset.forName(name);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns whether given buffer is null or empty.
     *
     * @param buffer given buffer
     * @return whether given buffer is null or empty
     */
    public static boolean isEmpty(@Nullable CharBuffer buffer) {
        return buffer == null || !buffer.hasRemaining();
    }

    /**
     * Returns an empty char array.
     *
     * @return an empty char array
     */
    public static char[] emptyChars() {
        return EMPTY_CHARS;
    }

    /**
     * Returns an empty char buffer.
     *
     * @return an empty char buffer
     */
    public static CharBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Returns a new buffer (not direct) of which content copied from given data. This method is equivalent to
     * ({@link #copyBuffer(char[], boolean)}):
     * <pre>
     *     return copy(data, false);
     * </pre>
     * The new buffer's position will be 0, limit and capacity will be length of given data, and it is not read-only.
     *
     * @param data given data
     * @return a new buffer (not direct) of which content copied from given data
     * @see #copyBuffer(char[], boolean)
     */
    public static CharBuffer copyBuffer(char[] data) {
        return copyBuffer(data, false);
    }

    /**
     * Returns a new buffer of which content copied from given data. The buffer will be direct if specified direct
     * option is {@code true}, otherwise be not. The new buffer's position will be 0, limit and capacity will be length
     * of given data, and it is not read-only.
     *
     * @param data   given data
     * @param direct specified direct option
     * @return a new buffer of which content copied from given data
     */
    public static CharBuffer copyBuffer(char[] data, boolean direct) {
        if (direct) {
            byte[] bytes = toBytes(data);
            return JieBytes.copyBuffer(bytes, true).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
        }
        CharBuffer buffer = CharBuffer.allocate(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static byte[] toBytes(char[] data) {
        byte[] bytes = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            bytes[i * 2] = (byte) (data[i] >> 8);
            bytes[i * 2 + 1] = (byte) data[i];
        }
        return bytes;
    }

    /**
     * Returns a new buffer of which content copied from given data. The buffer will be direct if given data is direct,
     * otherwise be not. The position of given data will not be changed, rather than incremented by its remaining. The
     * new buffer's position will be 0, limit and capacity will be length of given data, and it is not read-only.
     *
     * @param data given data
     * @return a new buffer (not direct) of which content copied from given data
     */
    public static CharBuffer copyBuffer(CharBuffer data) {
        if (data.isDirect()) {
            byte[] bytes = toBytes(data);
            return JieBytes.copyBuffer(bytes, true).order(ByteOrder.BIG_ENDIAN).asCharBuffer();
        }
        CharBuffer buffer = CharBuffer.allocate(data.remaining());
        int pos = data.position();
        buffer.put(data);
        data.position(pos);
        buffer.flip();
        return buffer;
    }

    private static byte[] toBytes(CharBuffer data) {
        int size = data.remaining();
        byte[] bytes = new byte[size * 2];
        for (int i = 0; i < size; i++) {
            bytes[i * 2] = (byte) (data.charAt(i) >> 8);
            bytes[i * 2 + 1] = (byte) data.charAt(i);
        }
        return bytes;
    }

    /**
     * Returns a new array of which content copied from given data. The position of given data will not be changed,
     * rather than incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content copied from given data
     */
    public static char[] copyChars(CharBuffer data) {
        int pos = data.position();
        char[] chars = new char[data.remaining()];
        data.get(chars);
        data.position(pos);
        return chars;
    }

    /**
     * Reads given data into a new array then returns. The position of given data will be incremented by its remaining.
     *
     * @param data given data
     * @return a new array of which content read from given data
     */
    public static char[] getChars(CharBuffer data) {
        char[] bytes = new char[data.remaining()];
        data.get(bytes);
        return bytes;
    }

    /**
     * Puts content of specified length from given source into destination. The positions of two buffers will be
     * incremented by specified length.
     *
     * @param source given source
     * @param dest   given destination
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on length do not hold
     * @throws IllegalArgumentException If there is insufficient space in the destination
     */
    public static void putBuffer(CharBuffer source, CharBuffer dest, int length)
        throws IllegalArgumentException, BufferOverflowException {
        CharBuffer slice = slice(source, 0, length);
        dest.put(slice);
        source.position(source.position() + length);
    }

    /**
     * Returns a new buffer whose content is a shared subsequence of given buffer's content. The content of the new
     * buffer will start at specified offset from given buffer's current position, up to specified length. Changes to
     * given buffer's content will be visible in the new buffer, and vice versa.
     * <p>
     * The two buffers' position, limit, and mark values will be independent. The new buffer's position will be zero,
     * its capacity and its limit will be the specified length, and its mark will be undefined. The new buffer will be
     * direct if, and only if, given buffer is direct, and it will be read-only if, and only if, given buffer is
     * read-only. The position of given buffer will not be changed.
     * <p>
     * Specially if specified length is {@code 0}, returns {@link #emptyBuffer()}.
     *
     * @param buffer given buffer
     * @param offset specified offset to {@code position}
     * @param length specified length
     * @throws IllegalArgumentException if the preconditions on offset and length do not hold
     */
    public static CharBuffer slice(CharBuffer buffer, int offset, int length) throws IllegalArgumentException {
        if (length == 0) {
            return emptyBuffer();
        }
        int pos = buffer.position();
        int limit = buffer.limit();
        buffer.position(pos + offset);
        buffer.limit(pos + offset + length);
        CharBuffer slice = buffer.slice();
        buffer.position(pos);
        buffer.limit(limit);
        return slice;
    }

    private static final class Natives {

        private static final Charset NATIVE_CHARSET = searchNativeCharset();

        @Nullable
        private static Charset searchNativeCharset() {
            return search(
                JieSystem.KEY_OF_NATIVE_ENCODING,
                "sun.jnu.encoding",
                JieSystem.KEY_OF_FILE_ENCODING
            );
        }

        @Nullable
        private static Charset search(String... proName) {
            for (String s : proName) {
                String prop = System.getProperty(s);
                Charset charset = charset(prop);
                if (charset != null) {
                    return charset;
                }
            }
            return null;
        }
    }

    //---------------- Common End ----------------//

    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
    // XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

    //---------------- Processors Begin ----------------//

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processor(Reader data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processor(char[] data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data from the specified offset up to the specified
     * length.
     *
     * @param data   the specified data
     * @param offset the specified offset
     * @param length the specified length
     * @return a new {@link CharsProcessor}
     * @throws IndexOutOfBoundsException if an index is out of bounds
     */
    public static CharsProcessor processor(char[] data, int offset, int length) throws IndexOutOfBoundsException {
        JieCheck.checkOffsetLength(data, offset, length);
        if (offset == 0 && length == data.length) {
            return processor(data);
        }
        CharBuffer buffer = CharBuffer.wrap(data, offset, length);
        return processor(buffer);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processor(CharBuffer data) {
        return new CharsProcessorImpl(data);
    }

    /**
     * Returns a new {@link CharsProcessor} to process the specified data.
     *
     * @param data the specified data
     * @return a new {@link CharsProcessor}
     */
    public static CharsProcessor processor(CharSequence data) {
        return new CharsProcessorImpl(data);
    }


    /**
     * Returns a {@link CharsProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks. The
     * returned encoder splits incoming data into blocks of the specified block size, and for each block, it
     * sequentially calls the given encoder, passing the block as the data parameter. If incoming data is insufficient
     * to form a full block, it is buffered until enough data is received to form a full block.
     * <p>
     * In the last invocation (when {@code end == true}) of the returned encoder, even if the remainder data after
     * splitting is insufficient to form a full block, it will still be passed to the given encoder as the last block,
     * and this call is the given encoder's last invocation.
     *
     * @param size    the specified block size
     * @param encoder the given encoder
     * @return a new {@link CharsProcessor.Encoder} that wraps the given encoder to encode data in fixed-size blocks
     */
    public static CharsProcessor.Encoder fixedSizeEncoder(int size, CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.FixedSizeEncoder(encoder, size);
    }

    /**
     * Returns a {@link CharsProcessor.Encoder} to round down incoming data for the given encoder, it is typically used
     * for the encoder which requires consuming data in multiples of the specified size. The returned encoder rounds
     * down incoming data to the largest multiple of the specified size and passes the rounded data to the given
     * encoder. The remainder data will be buffered until enough data is received to round.
     * <p>
     * However, in the last invocation (when {@code end == true}), all remaining data will be passed directly to the
     * given encoder.
     *
     * @param size    the specified size
     * @param encoder the given encoder
     * @return a {@link CharsProcessor.Encoder} to round down incoming data for the given encoder
     */
    public static CharsProcessor.Encoder roundEncoder(int size, CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.RoundEncoder(encoder, size);
    }

    /**
     * Returns a {@link CharsProcessor.Encoder} that buffers unconsumed data of the given encoder, it is typically used
     * for the encoder which may not fully consume the passed data, requires buffering and consuming data in next
     * invocation. This encoder passes incoming data to the given encoder. The unconsumed remaining data after encoding
     * of the given encoder will be buffered and used in the next invocation.
     * <p>
     * However, in the last invocation (when {@code end == true}), no data will be buffered.
     *
     * @param encoder the given encoder
     * @return a {@link CharsProcessor.Encoder} that buffers unconsumed data of the given encoder
     */
    public static CharsProcessor.Encoder bufferedEncoder(CharsProcessor.Encoder encoder) {
        return new CharsProcessorImpl.BufferedEncoder(encoder);
    }

    //---------------- Processors End ----------------//
}
