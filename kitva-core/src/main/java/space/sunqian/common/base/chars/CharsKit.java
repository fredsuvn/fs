package space.sunqian.common.base.chars;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.common.Kit;
import space.sunqian.common.base.system.SystemKeys;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Utilities for {@code char}, {@code char array} and {@link Charset}.
 *
 * @author sunqian
 */
public class CharsKit {

    /**
     * Charset: UTF-8.
     */
    public static final @Nonnull Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Charset: ISO-8859-1.
     */
    public static final @Nonnull Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    private static final char @Nonnull [] EMPTY = {};
    private static final @Nonnull CharBuffer EMPTY_BUFFER = CharBuffer.wrap(EMPTY);

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
    public static char @Nonnull [] empty() {
        return EMPTY;
    }

    /**
     * Returns an empty char buffer.
     *
     * @return an empty char buffer
     */
    public static @Nonnull CharBuffer emptyBuffer() {
        return EMPTY_BUFFER;
    }

    /**
     * Returns the default charset: {@link #UTF_8}.
     *
     * @return the default charset: {@link #UTF_8}
     */
    public static @Nonnull Charset defaultCharset() {
        return UTF_8;
    }

    /**
     * Returns the latin charset: {@link #ISO_8859_1}.
     *
     * @return the latin charset: {@link #ISO_8859_1}
     */
    public static @Nonnull Charset latinCharset() {
        return ISO_8859_1;
    }

    /**
     * If the {@link #nativeCharset()} is not {@code null}, returns {@link #nativeCharset()}, otherwise returns
     * {@link #jvmCharset()}.
     *
     * @return if the {@link #nativeCharset()} is not {@code null}, returns {@link #nativeCharset()}, otherwise returns
     * {@link #jvmCharset()}.
     */
    public static @Nonnull Charset localCharset() {
        return Kit.nonnull(nativeCharset(), jvmCharset());
    }

    /**
     * Returns the default charset of the JVM. It is equivalent to the {@link Charset#defaultCharset()}.
     *
     * @return the default charset of the JVM
     * @see Charset#defaultCharset()
     */
    public static @Nonnull Charset jvmCharset() {
        return Charset.defaultCharset();
    }

    /**
     * Returns the charset from the host environment, which is typically the charset of current OS.
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
     * @return the charset from the host environment, which is typically the charset of current OS
     */
    public static @Nullable Charset nativeCharset() {
        return Natives.NATIVE_CHARSET;
    }

    /**
     * Returns the charset with the specified name, may be {@code null} if searching fails.
     *
     * @param name the specified name
     * @return the charset with the specified name
     */
    public static @Nullable Charset charset(String name) {
        try {
            return Charset.forName(name);
        } catch (Exception e) {
            return null;
        }
    }

    private static final class Natives {

        private static final @Nullable Charset NATIVE_CHARSET = searchNativeCharset();

        private static @Nullable Charset searchNativeCharset() {
            return search(
                SystemKeys.NATIVE_ENCODING,
                "sun.jnu.encoding",
                SystemKeys.FILE_ENCODING
            );
        }

        private static @Nullable Charset search(String... proName) {
            for (String s : proName) {
                String prop = System.getProperty(s);
                @Nullable Charset charset = charset(prop);
                if (charset != null) {
                    return charset;
                }
            }
            return null;
        }
    }

    private CharsKit() {
    }
}
