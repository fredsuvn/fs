package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Nullable;

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

    private static final class Natives {

        private static final Charset NATIVE_CHARSET = searchNativeCharset();

        @Nullable
        private static Charset searchNativeCharset() {
            Charset result = charset(JieSystem.getNativeEncoding());
            if (result != null) {
                return result;
            }
            result = charset(System.getProperty("sun.jnu.encoding"));
            if (result != null) {
                return result;
            }
            return charset(JieSystem.getFileEncoding());
        }
    }
}
