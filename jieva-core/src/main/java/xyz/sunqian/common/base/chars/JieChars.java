package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieSystem;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Static utility class for {@code chars} and {@link Charset}.
 *
 * @author sunqian
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

    /**
     * Returns whether the given buffer is null or empty.
     *
     * @param buffer the given buffer
     * @return whether the given buffer is null or empty
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
     * Returns the default charset: {@link #UTF_8}.
     *
     * @return the default charset: {@link #UTF_8}
     */
    public static Charset defaultCharset() {
        return UTF_8;
    }

    /**
     * Returns the latin charset: {@link #ISO_8859_1}.
     *
     * @return the latin charset: {@link #ISO_8859_1}
     */
    public static Charset latinCharset() {
        return ISO_8859_1;
    }

    /**
     * Returns the default charset of this Java virtual machine. It is equivalent to the
     * {@link Charset#defaultCharset()}.
     *
     * @return the default charset of this Java virtual machine
     * @see Charset#defaultCharset()
     */
    public static Charset jvmCharset() {
        return Charset.defaultCharset();
    }

    /**
     * Returns the default charset of current native environment, which is typically the charset of current OS.
     * <p>
     * This method is <b>not</b> equivalent to the {@link #jvmCharset()}, it will search the system properties in the
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
     * Returns the charset with the specified name, may be {@code null} if the search fails.
     *
     * @param name the specified name
     * @return the charset with the specified name
     */
    @Nullable
    public static Charset charset(String name) {
        try {
            return Charset.forName(name);
        } catch (Exception e) {
            return null;
        }
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
}
