package space.sunqian.fs.base.chars;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.system.SystemKeys;
import space.sunqian.fs.io.IORuntimeException;

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
        return Fs.nonnull(nativeCharset(), jvmCharset());
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

    /**
     * Converts the given char to the corresponding Unicode escape string. For example:
     * <ul>
     *    <li>{@code 'a'} -> {@code \\u0061}</li>
     *    <li>{@code 'h'} -> {@code \\u0068}</li>
     *    <li>{@code '中'} -> {@code \\u4E20}</li>
     * </ul>
     *
     * @param c the given char
     * @return the corresponding Unicode escape string
     */
    public static @Nonnull String toUnicode(char c) {
        return toUnicode(c, true);
    }

    /**
     * Converts the given char to the corresponding Unicode escape string. For example:
     * <ul>
     *    <li>{@code 'a'} -> {@code \\u0061}</li>
     *    <li>{@code 'h'} -> {@code \\u0068}</li>
     *    <li>{@code '中'} -> {@code \\u4E20}</li>
     * </ul>
     *
     * @param c         the given char
     * @param uppercase {@code true} to use uppercase letters, {@code false} to use lowercase letters
     * @return the corresponding Unicode escape string
     */
    public static @Nonnull String toUnicode(char c, boolean uppercase) {
        return CharToUnicode.charToUnicode(c, uppercase);
    }

    /**
     * Converts the given char to the corresponding Unicode escape string and appends it to the given appender. For
     * example:
     * <ul>
     *    <li>{@code 'a'} -> {@code \\u0061}</li>
     *    <li>{@code 'h'} -> {@code \\u0068}</li>
     *    <li>{@code '中'} -> {@code \\u4E20}</li>
     * </ul>
     *
     * @param c         the given char
     * @param uppercase {@code true} to use uppercase letters, {@code false} to use lowercase letters
     * @param appender  the appender to append the result
     */
    public static void toUnicode(char c, boolean uppercase, @Nonnull Appendable appender) throws IORuntimeException {
        try {
            CharToUnicode.charToUnicode(c, uppercase, appender);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Converts the Unicode escape sequence to the corresponding character. For example:
     * <ul>
     *    <li>{@code \\u0061} -> {@code 'a'}</li>
     *    <li>{@code \\u0068} -> {@code 'h'}</li>
     *    <li>{@code \\u4E20} -> {@code '中'}</li>
     * </ul>
     *
     * @param c1 the first character of the Unicode escape sequence
     * @param c2 the second character of the Unicode escape sequence
     * @param c3 the third character of the Unicode escape sequence
     * @param c4 the fourth character of the Unicode escape sequence
     * @return the corresponding character
     */
    public static char unicodeToChar(char c1, char c2, char c3, char c4) {
        int digits = unicodeToDigits(c1);
        digits <<= 4;
        digits |= unicodeToDigits(c2);
        digits <<= 4;
        digits |= unicodeToDigits(c3);
        digits <<= 4;
        digits |= unicodeToDigits(c4);
        return (char) digits;
    }

    private static int unicodeToDigits(char c) {
        int digit;
        if (c >= '0' && c <= '9') {
            digit = c - '0';
        } else if (c >= 'A' && c <= 'F') {
            digit = c - 'A' + 10;
        } else if (c >= 'a' && c <= 'f') {
            digit = c - 'a' + 10;
        } else {
            throw new IllegalArgumentException("Illegal hex character: " + c);
        }
        return digit;
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

        @SuppressWarnings("SameParameterValue")
        private static @Nullable Charset search(@Nonnull String @Nonnull ... proName) {
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

    private static final class CharToUnicode {

        private static final char[] UPPERS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        private static final char[] LOWERS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private static String charToUnicode(char ch, boolean uppercase) {
            char[] result = new char[6];
            result[0] = '\\';
            result[1] = 'u';
            char[] dict = uppercase ? UPPERS : LOWERS;
            int code = ch & 0xFFFF;
            result[2] = dict[(code >>> 12) & 0x0F];
            result[3] = dict[(code >>> 8) & 0x0F];
            result[4] = dict[(code >>> 4) & 0x0F];
            result[5] = dict[code & 0x0F];
            return new String(result);
        }

        private static void charToUnicode(char ch, boolean uppercase, @Nonnull Appendable appender) throws Exception {
            char c0 = '\\';
            char c1 = 'u';
            char[] dict = uppercase ? UPPERS : LOWERS;
            int code = ch & 0xFFFF;
            char c2 = dict[(code >>> 12) & 0x0F];
            char c3 = dict[(code >>> 8) & 0x0F];
            char c4 = dict[(code >>> 4) & 0x0F];
            char c5 = dict[code & 0x0F];
            appender.append(c0).append(c1).append(c2).append(c3).append(c4).append(c5);
        }
    }

    private CharsKit() {
    }
}
