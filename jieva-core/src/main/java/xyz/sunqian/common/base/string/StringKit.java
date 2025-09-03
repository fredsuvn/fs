package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities kit for string related.
 *
 * @author fredsuvn
 */
public class StringKit {

    /**
     * Returns whether the given string is {@code null} or empty.
     *
     * @param str the given string
     * @return whether the given string is {@code null} or empty
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * Returns whether the given string is not {@code null} nor empty.
     *
     * @param str the given string
     * @return whether the given string is not {@code null} nor empty
     */
    public static boolean isNonEmpty(@Nullable CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * Returns whether the given string is blank ({@code null}, empty or whitespace).
     *
     * @param str the given string
     * @return whether the given string is blank
     */
    public static boolean isBlank(@Nullable CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the given string is not blank ({@code null} nor empty nor whitespace).
     *
     * @param str the given string
     * @return whether the given string is not blank
     */
    public static boolean isNonBlank(@Nullable CharSequence str) {
        return !isBlank(str);
    }

    /**
     * Returns {@code true} if any of the given strings is {@code null} or empty, otherwise {@code false}.
     *
     * @param strings the given strings
     * @return {@code true} if any of the given strings is {@code null} or empty, otherwise {@code false}
     */
    public static boolean anyEmpty(@Nullable CharSequence @Nonnull ... strings) {
        for (CharSequence str : strings) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if all the given strings are {@code null} or empty, otherwise {@code false}.
     *
     * @param strings the given strings
     * @return {@code true} if all the given strings are {@code null} or empty, otherwise {@code false}
     */
    public static boolean allEmpty(@Nullable CharSequence @Nonnull ... strings) {
        for (CharSequence str : strings) {
            if (!isEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if any of the given strings is blank, otherwise {@code false}. This method uses
     * {@link #isBlank(CharSequence)} to check whether a string is blank.
     *
     * @param strings the given strings
     * @return {@code true} if any of the given strings is blank, otherwise {@code false}
     */
    public static boolean anyBlank(@Nullable CharSequence @Nonnull ... strings) {
        for (CharSequence str : strings) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if all the given strings are blank, otherwise {@code false}. This method uses
     * {@link #isBlank(CharSequence)} to check whether a string is blank.
     *
     * @param strings the given strings
     * @return {@code true} if all the given strings are blank, otherwise {@code false}
     */
    public static boolean allBlank(@Nullable CharSequence @Nonnull ... strings) {
        for (CharSequence str : strings) {
            if (!isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Encodes the given char array into a new byte array using {@link CharsKit#defaultCharset()}.
     *
     * @param chars the given char array
     * @return a new byte array contains the encoded bytes
     */
    public static byte @Nonnull [] getBytes(char @Nonnull [] chars) {
        return getBytes(chars, CharsKit.defaultCharset());
    }

    /**
     * Encodes the given char array into a new byte array using the specified charset.
     *
     * @param chars   the given char array
     * @param charset the specified charset
     * @return a new byte array contains the encoded bytes
     */
    public static byte @Nonnull [] getBytes(char @Nonnull [] chars, Charset charset) {
        return new String(chars).getBytes(charset);
    }

    /**
     * Encodes the given char sequence into a new byte array using {@link CharsKit#defaultCharset()}.
     *
     * @param chars the given char sequence
     * @return a new byte array contains the encoded bytes
     */
    public static byte @Nonnull [] getBytes(@Nonnull CharSequence chars) {
        return getBytes(chars, CharsKit.defaultCharset());
    }

    /**
     * Encodes the given char sequence into a new byte array using the specified charset.
     *
     * @param chars   the given char sequence
     * @param charset the specified charset
     * @return a new byte array contains the encoded bytes
     */
    public static byte @Nonnull [] getBytes(@Nonnull CharSequence chars, Charset charset) {
        return chars.toString().getBytes(charset);
    }

    /**
     * Returns whether all chars of the given string are upper case. Note if the string is empty, returns {@code true}.
     *
     * @param str the given string
     * @return whether all chars of the given string are upper case
     */
    public static boolean allUpperCase(@Nonnull CharSequence str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all chars of the given string are lower case. Note if the string is empty, returns {@code true}.
     *
     * @param str the given string
     * @return whether all chars of the given string are lower case
     */
    public static boolean allLowerCase(@Nonnull CharSequence str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLowerCase(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Capitalizes the given string.
     *
     * @param str the given string
     * @return the capitalized string
     */
    public static @Nonnull String capitalize(@Nonnull CharSequence str) {
        if (str.length() == 0) {
            return str.toString();
        }
        if (str.length() == 1) {
            char c = str.charAt(0);
            if (Character.isLowerCase(c)) {
                return String.valueOf(Character.toUpperCase(c));
            }
        }
        char c = str.charAt(0);
        if (Character.isLowerCase(c)) {
            StringBuilder sb = new StringBuilder(str.length());
            sb.append(Character.toUpperCase(c));
            sb.append(str, 1, str.length());
            return sb.toString();
        }
        return str.toString();
    }

    /**
     * Uncapitalizes the given string. Specifically, if the given string's length {@code >= 2} and its chars are all
     * upper case, the original string is also returned.
     *
     * @param str the given string
     * @return the uncapitalized string
     */
    public static @Nonnull String uncapitalize(@Nonnull CharSequence str) {
        if (str.length() == 1) {
            char c = str.charAt(0);
            if (Character.isUpperCase(c)) {
                return String.valueOf(Character.toLowerCase(c));
            }
        }
        if (allUpperCase(str)) {
            return str.toString();
        }
        char c = str.charAt(0);
        if (Character.isUpperCase(c)) {
            StringBuilder sb = new StringBuilder(str.length());
            sb.append(Character.toLowerCase(c));
            sb.append(str, 1, str.length());
            return sb.toString();
        }
        return str.toString();
    }

    /**
     * Returns a String of which content is upper case of the given string.
     *
     * @param str the given string
     * @return the converted string
     */
    public static @Nonnull String upperCase(@Nonnull CharSequence str) {
        if (str.length() == 0) {
            return str.toString();
        }
        char[] cs = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            cs[i] = Character.toUpperCase(str.charAt(i));
        }
        return new String(cs);
    }

    /**
     * Returns a String of which content is lower case of the given string.
     *
     * @param str the given string
     * @return the converted string
     */
    public static @Nonnull String lowerCase(@Nonnull CharSequence str) {
        if (str.length() == 0) {
            return str.toString();
        }
        char[] cs = new char[str.length()];
        for (int i = 0; i < str.length(); i++) {
            cs[i] = Character.toLowerCase(str.charAt(i));
        }
        return new String(cs);
    }

    /**
     * Returns the first index of the specified char in the specified string. The returned value is the smallest value
     * {@code k} such that:
     * <pre>{@code
     * (str.charAt(k) == ch) && (k >= 0)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     *
     * @param str the specified string
     * @param c   the specified char
     * @return the first index of the specified char in the specified string, or {@code -1} if not found
     */
    public static int indexOf(CharSequence str, char c) {
        return indexOf(str, c, 0);
    }

    /**
     * Returns the first index of the specified char in the specified string, starting at the specified index. The
     * returned value is the smallest value {@code k} such that:
     * <pre>{@code
     * (str.charAt(k) == ch) && (k >= index)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * There is no restriction on the {@code index}. If it is negative, it has the same effect as if it were {@code 0};
     * if it is greater than {@code str.length()}, it has the same effect as if it were {@code str.length()} ({@code -1}
     * is returned).
     *
     * @param str   the specified string
     * @param c     the specified char
     * @param index the specified index
     * @return the first index of the specified char in the specified string, starting at the specified index, or
     * {@code -1} if not found
     */
    public static int indexOf(CharSequence str, char c, int index) {
        if (str instanceof String) {
            return ((String) str).indexOf(c, index);
        }
        if (index >= str.length()) {
            return -1;
        }
        int s = Math.max(0, index);
        for (int i = s; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the specified char in the specified string, searching backward. The returned value is
     * the largest value {@code k} such that:
     * <pre>{@code
     * (str.charAt(k) == ch) && (k <= str.length() - 1)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     *
     * @param str the specified string
     * @param c   the specified char
     * @return the last index of the specified char in the specified string, searching backward, or {@code -1} if not
     * found
     */
    public static int lastIndexOf(CharSequence str, char c) {
        return lastIndexOf(str, c, str.length() - 1);
    }

    /**
     * Returns the last index of the specified char in the specified string, searching backward starting at the
     * specified index. The returned value is the largest value {@code k} such that:
     * <pre>{@code
     * (str.charAt(k) == ch) && (k <= index)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * There is no restriction on the {@code index}. If it is negative, it has the same effect as if it were {@code -1}
     * ({@code -1} is returned); if it is greater than or equal to {@code str.length()}, it has the same effect as if it
     * were {@code str.length() - 1}.
     *
     * @param str   the specified string
     * @param c     the specified char
     * @param index the specified index
     * @return the last index of the specified char in the specified string, searching backward starting at the
     * specified index, or {@code -1} if not found
     */
    public static int lastIndexOf(CharSequence str, char c, int index) {
        if (str instanceof String) {
            return ((String) str).lastIndexOf(c, index);
        }
        if (index < 0) {
            return -1;
        }
        int s = Math.min(str.length() - 1, index);
        for (int i = s; i >= 0; i--) {
            if (str.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the first index of the specified substring in the specified string. The returned value is the smallest
     * value {@code k} for which:
     * <pre>{@code
     * (k >= 0) && str.startsWith(sub, k)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * The behavior of this method is the same as {@link String#indexOf(String)}.
     *
     * @param str the specified string
     * @param sub the specified substring
     * @return the first index of the specified substring in the specified string, or {@code -1} if not found
     */
    public static int indexOf(CharSequence str, CharSequence sub) {
        return indexOf(str, sub, 0);
    }

    /**
     * Returns the first index of the specified substring in the specified string, starting at the specified index. The
     * returned value is the smallest value {@code k} for which:
     * <pre>{@code
     * (k >= index) && str.startsWith(sub, k)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * The behavior of this method is the same as {@link String#indexOf(String, int)}.
     *
     * @param str   the specified string
     * @param sub   the specified substring
     * @param index the specified index
     * @return the first index of the specified substring in the specified string, starting at the specified index, or
     * {@code -1} if not found
     */
    public static int indexOf(CharSequence str, CharSequence sub, int index) {
        if ((str instanceof String) && (sub instanceof String)) {
            return ((String) str).indexOf((String) sub, index);
        }
        int maxIndex = str.length() - sub.length();
        if (index > maxIndex) {
            return (sub.length() == 0 ? str.length() : -1);
        }
        int s = Math.max(index, 0);
        if (sub.length() == 0) {
            return s;
        }
        for (int i = s; i <= maxIndex; i++) {
            if (startsWith(str, sub, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the last index of the specified substring in the specified string, searching backward. The returned value
     * is the smallest value {@code k} for which:
     * <pre>{@code
     * (k <= str.length() - 1) && str.startsWith(sub, k)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * The behavior of this method is the same as {@link String#lastIndexOf(String)}.
     *
     * @param str the specified string
     * @param sub the specified substring
     * @return the last index of the specified substring in the specified string, searching backward, or {@code -1} if
     * not found
     */
    public static int lastIndexOf(CharSequence str, CharSequence sub) {
        return lastIndexOf(str, sub, str.length());
    }

    /**
     * Returns the last index of the specified substring in the specified string, searching backward starting at the
     * specified index. The returned value is the largest value {@code k} for which:
     * <pre>{@code
     * (k <= index) && str.startsWith(sub, k)
     * }</pre>
     * If no such {@code k} is found, returns {@code -1}.
     * <p>
     * The behavior of this method is the same as {@link String#lastIndexOf(String, int)}.
     *
     * @param str   the specified string
     * @param sub   the specified substring
     * @param index the specified index
     * @return the last index of the specified substring in the specified string, searching backward starting at the
     * specified index, or {@code -1} if not found
     */
    public static int lastIndexOf(CharSequence str, CharSequence sub, int index) {
        if ((str instanceof String) && (sub instanceof String)) {
            return ((String) str).lastIndexOf((String) sub, index);
        }
        if (index < 0) {
            return -1;
        }
        int maxIndex = str.length() - sub.length();
        int s = Math.min(index, maxIndex);
        if (sub.length() == 0) {
            return s;
        }
        for (int i = s; i >= 0; i--) {
            if (startsWith(str, sub, i)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns whether the specified string is starts with the specified substring.
     * <p>
     * The behavior of this method is the same as {@link String#startsWith(String)}.
     *
     * @param str the specified string
     * @param sub the specified substring
     * @return whether the specified string is starts with the specified substring
     */
    public static boolean startsWith(@Nonnull CharSequence str, @Nonnull CharSequence sub) {
        return startsWith(str, sub, 0);
    }

    /**
     * Returns whether the specified string is starts with the specified substring, the comparing starting at the
     * specified index.
     * <p>
     * The behavior of this method is the same as {@link String#startsWith(String, int)}.
     *
     * @param str   the specified string
     * @param sub   the specified substring
     * @param index the specified index
     * @return whether the specified string is starts with the specified substring
     */
    public static boolean startsWith(@Nonnull CharSequence str, @Nonnull CharSequence sub, int index) {
        int strLen = str.length();
        int subLen = sub.length();
        if (index < 0 || index > strLen - subLen) {
            return false;
        }
        int l = subLen + index;
        for (int i = index, j = 0; i < l; i++, j++) {
            if (str.charAt(i) != sub.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether the specified string is ends with the specified substring.
     * <p>
     * The behavior of this method is the same as {@link String#endsWith(String)}.
     *
     * @param str the specified string
     * @param sub the specified substring
     * @return whether the specified string is ends with the specified substring
     */
    public static boolean endsWith(@Nonnull CharSequence str, @Nonnull CharSequence sub) {
        return startsWith(str, sub, str.length() - sub.length());
    }

    /**
     * Returns the {@code toString} of the given object. If the given object is array, uses {@code Arrays.toString} or
     * {@link Arrays#deepToString(Object[])} if necessary.
     * <p>
     * This method is equivalent to ({@link #toStringWith(Object, boolean, boolean)}):
     * {@code toStringWith(obj, true, true)}.
     *
     * @param obj the given object
     * @return the {@code toString} of the given object
     */
    public static @Nonnull String toString(@Nullable Object obj) {
        return toStringWith(obj, true, true);
    }

    /**
     * Returns the {@code toString} of the given objects via {@link Arrays#deepToString(Object[])}.
     *
     * @param objs the given objects
     * @return the {@code toString} of the given objects via {@link Arrays#deepToString(Object[])}
     */
    public static @Nonnull String toStringAll(@Nullable Object @Nonnull ... objs) {
        return Arrays.deepToString(objs);
    }

    /**
     * Returns the {@code toString} of the given object. This method follows the following logic:
     * <ul>
     *     <li>
     *         If the given object is not an array, returns {@link Objects#toString(Object)}.
     *     </li>
     *     <li>
     *         If the {@code arrayToString} is {@code true}:
     *         <ul>
     *             <li>
     *                 If the {@code deep} is {@code true}, uses {@link Arrays#deepToString(Object[])} for them.
     *                 Otherwise, uses {@code Arrays.toString}.
     *             </li>
     *         </ul>
     *     </li>
     *     <li>
     *         Returns {@link Objects#toString(Object)} otherwise.
     *     </li>
     * </ul>
     *
     * @param obj           the given object
     * @param arrayToString the arrayToString option
     * @param deep          the deep option
     * @return the {@code toString} of the given object
     */
    public static @Nonnull String toStringWith(@Nullable Object obj, boolean arrayToString, boolean deep) {
        if (obj == null || !arrayToString) {
            return Objects.toString(obj);
        }
        Class<?> cls = obj.getClass();
        if (cls.isArray()) {
            return toStringArray(obj, deep);
        }
        return obj.toString();
    }

    private static @Nonnull String toStringArray(@Nonnull Object obj, boolean deep) {
        if (obj instanceof Object[]) {
            return deep ? Arrays.deepToString((Object[]) obj) : Arrays.toString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        throw new UnknownArrayTypeException(obj.getClass());
    }

    /**
     * Compares the given two {@link CharSequence} instances, returns {@code true} if they have same length and two
     * chars at the same index in two sequences are equal, otherwise {@code false}.
     *
     * @param cs1 the first {@link CharSequence} to compare
     * @param cs2 the second {@link CharSequence} to compare
     * @return {@code true} if they have same length and two chars at the same index in two sequences are equal,
     * otherwise {@code false}
     */
    public static boolean charEquals(CharSequence cs1, CharSequence cs2) {
        if (cs1.length() != cs2.length()) {
            return false;
        }
        for (int i = 0; i < cs1.length(); i++) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Copies chars from the given string into the specified destination char array. The chars copied starting at the
     * specified start index inclusive, and ending at the specified end index exclusive. The destination array receives
     * starting at the specified offset.
     * <p>
     * The behavior of this method is the same as {@link String#getChars(int, int, char[], int)}.
     *
     * @param str   the given string of which chars will be copied
     * @param start the specified start index, inclusive
     * @param end   the specified end index, exclusive
     * @param dst   the specified destination char array
     * @param off   the specified offset
     * @throws IndexOutOfBoundsException if there exists an arguments is out of bounds
     */
    public static void charsCopy(
        @Nonnull CharSequence str, int start, int end, char @Nonnull [] dst, int off
    ) throws IndexOutOfBoundsException {
        if (str instanceof String) {
            ((String) str).getChars(start, end, dst, off);
        } else {
            CheckKit.checkRangeInBounds(start, end, 0, str.length());
            CheckKit.checkRangeInBounds(off, off + end - start, 0, dst.length);
            if (start == end) {
                return;
            }
            for (int i = 0; i < end - start; i++) {
                dst[off + i] = str.charAt(start + i);
            }
        }
    }

    /**
     * Copies the specified length of chars from the given source, starting at the specified source offset, to the given
     * destination array, starting at the specified destination offset.
     * <p>
     * The behavior of this method is the same as {@link System#arraycopy(Object, int, Object, int, int)}.
     *
     * @param src    the given source
     * @param srcOff the specified source offset
     * @param dst    the given destination array
     * @param dstOff the specified destination offset
     * @param len    the specified copy length
     * @throws IndexOutOfBoundsException if there exists an arguments is out of bounds
     */
    public static void charsCopy(
        @Nonnull CharSequence src,
        int srcOff,
        char @Nonnull [] dst,
        int dstOff,
        int len
    ) throws IndexOutOfBoundsException {
        if (src instanceof String) {
            ((String) src).getChars(srcOff, srcOff + len, dst, dstOff);
        } else {
            CheckKit.checkOffLen(srcOff, len, src.length());
            CheckKit.checkOffLen(dstOff, len, dst.length);
            if (len == 0) {
                return;
            }
            for (int i = 0; i < len; i++) {
                dst[dstOff + i] = src.charAt(srcOff + i);
            }
        }
    }

    /**
     * Converts the given string to the specified number type. Supported number types are:
     * <ul>
     *     <li>{@link Byte}</li>
     *     <li>{@link Short}</li>
     *     <li>{@link Character} (Converts to {@link Integer} first, then to {@link Character})</li>
     *     <li>{@link Integer}</li>
     *     <li>{@link Long}</li>
     *     <li>{@link Float}</li>
     *     <li>{@link Double}</li>
     *     <li>{@link BigInteger}</li>
     *     <li>{@link BigDecimal}</li>
     * </ul>
     *
     * @param str     the given string
     * @param numType the specified number type
     * @param <T>     the number type
     * @return the converted number object
     * @throws NumberFormatException         if the given string can't be converted to the specified number type
     * @throws UnsupportedOperationException if the given number type is not supported
     */
    public static <T> @Nonnull T toNumber(
        @Nonnull CharSequence str, Class<T> numType
    ) throws NumberFormatException, UnsupportedOperationException {
        if (Byte.class.equals(numType)) {
            return Jie.as(Byte.parseByte(str.toString()));
        }
        if (Short.class.equals(numType)) {
            return Jie.as(Short.parseShort(str.toString()));
        }
        if (Character.class.equals(numType)) {
            int i = Integer.parseInt(str.toString());
            char c = (char) i;
            return Jie.as(c);
        }
        if (Integer.class.equals(numType)) {
            return Jie.as(Integer.parseInt(str.toString()));
        }
        if (Long.class.equals(numType)) {
            return Jie.as(Long.parseLong(str.toString()));
        }
        if (Float.class.equals(numType)) {
            return Jie.as(Float.parseFloat(str.toString()));
        }
        if (Double.class.equals(numType)) {
            return Jie.as(Double.parseDouble(str.toString()));
        }
        if (BigInteger.class.equals(numType)) {
            return Jie.as(new BigInteger(str.toString()));
        }
        if (BigDecimal.class.equals(numType)) {
            return Jie.as(new BigDecimal(str.toString()));
        }
        throw new UnsupportedOperationException("Unsupported number type: " + numType);
    }
}
