package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.exception.UnknownArrayTypeException;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Utilities kit for string related.
 *
 * @author fredsuvn
 */
public class StringKit {

    /**
     * Returns string decoding from given bytes with {@link CharsKit#defaultCharset()}.
     *
     * @param bytes given bytes
     * @return string decoding from given bytes and charset
     */
    public static String of(byte[] bytes) {
        return of(bytes, CharsKit.defaultCharset());
    }

    /**
     * Returns string decoding from given bytes (from given offset to specified length) with
     * {@link CharsKit#defaultCharset()}.
     *
     * @param bytes  given bytes
     * @param offset given offset
     * @param length specified length
     * @return string decoding from given bytes and charset
     */
    public static String of(byte[] bytes, int offset, int length) {
        return of(bytes, offset, length, CharsKit.defaultCharset());
    }

    /**
     * Returns string decoding from given bytes and charset.
     *
     * @param bytes   given bytes
     * @param charset given charset
     * @return string decoding from given bytes and charset
     */
    public static String of(byte[] bytes, Charset charset) {
        return new String(bytes, charset);
    }

    /**
     * Returns string decoding from given bytes (from given offset to specified length) and charset.
     *
     * @param bytes   given bytes
     * @param offset  given offset
     * @param length  specified length
     * @param charset given charset
     * @return string decoding from given bytes and charset
     */
    public static String of(byte[] bytes, int offset, int length, Charset charset) {
        return new String(bytes, offset, length, charset);
    }

    /**
     * Encodes given chars into a new byte array using {@link CharsKit#defaultCharset()}.
     *
     * @param chars given chars
     * @return a new byte array encoded given chars
     */
    public static byte[] getBytes(char[] chars) {
        return getBytes(chars, CharsKit.defaultCharset());
    }

    /**
     * Encodes given chars into a new byte array using the given charset.
     *
     * @param chars   given chars
     * @param charset given charset
     * @return a new byte array encoded given chars
     */
    public static byte[] getBytes(char[] chars, Charset charset) {
        return new String(chars).getBytes(charset);
    }

    /**
     * Encodes given chars into a new byte array using {@link CharsKit#defaultCharset()}.
     *
     * @param chars given chars
     * @return a new byte array encoded given chars
     */
    public static byte[] getBytes(CharSequence chars) {
        return getBytes(chars, CharsKit.defaultCharset());
    }

    /**
     * Encodes given chars into a new byte array using the given charset.
     *
     * @param chars   given chars
     * @param charset given charset
     * @return a new byte array encoded given chars
     */
    public static byte[] getBytes(CharSequence chars, Charset charset) {
        return chars.toString().getBytes(charset);
    }

    /**
     * Returns whether given chars is null or empty.
     *
     * @param chars given chars
     * @return whether given chars is null or empty
     */
    public static boolean isEmpty(@Nullable CharSequence chars) {
        return chars == null || chars.length() == 0;
    }

    /**
     * Returns whether given chars is not null and empty.
     *
     * @param chars given chars
     * @return whether given chars is not null and empty
     */
    public static boolean isNotEmpty(@Nullable CharSequence chars) {
        return !isEmpty(chars);
    }

    /**
     * Returns whether given chars is blank (null, empty or whitespace).
     *
     * @param chars given chars
     * @return whether given chars is blank
     */
    public static boolean isBlank(@Nullable CharSequence chars) {
        if (chars == null || chars.length() == 0) {
            return true;
        }
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether given chars is not blank (null, empty or whitespace).
     *
     * @param chars given chars
     * @return whether given chars is not blank
     */
    public static boolean isNotBlank(@Nullable CharSequence chars) {
        return !isBlank(chars);
    }

    /**
     * Returns whether given chars can match given regex.
     *
     * @param regex given regex
     * @param chars given chars
     * @return whether given chars can match given regex
     */
    public static boolean matches(CharSequence regex, @Nullable CharSequence chars) {
        if (chars == null) {
            return false;
        }
        return chars.toString().matches(regex.toString());
    }

    /**
     * Returns ture if any given chars is empty, otherwise false.
     *
     * @param chars given chars
     * @return ture if any given chars is empty, otherwise false
     */
    public static boolean anyEmpty(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isEmpty(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if any given chars is blank (null, empty or whitespace), otherwise false.
     *
     * @param chars given chars
     * @return ture if any given chars is blank, otherwise false
     */
    public static boolean anyBlank(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isBlank(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if any given chars can match given regex, otherwise false.
     *
     * @param chars given chars
     * @param regex given regex
     * @return ture if any given chars can match given regex, otherwise false
     */
    public static boolean anyMatches(CharSequence regex, CharSequence... chars) {
        for (CharSequence c : chars) {
            if (matches(regex, c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if all given chars is empty, otherwise false.
     *
     * @param chars given chars
     * @return ture if all given chars is empty, otherwise false
     */
    public static boolean allEmpty(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isNotEmpty(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns ture if all given chars is blank (null, empty or whitespace), otherwise false.
     *
     * @param chars given chars
     * @return ture if all given chars is blank, otherwise false
     */
    public static boolean allBlank(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isNotBlank(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns ture if all given chars can match given regex, otherwise false.
     *
     * @param chars given chars
     * @param regex given regex
     * @return ture if all given chars can match given regex, otherwise false
     */
    public static boolean allMatches(CharSequence regex, CharSequence... chars) {
        for (CharSequence c : chars) {
            if (!matches(regex, c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all chars of given chars are upper case.
     *
     * @param chars given chars
     * @return whether all chars of given chars are upper case
     */
    public static boolean allUpperCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return false;
        }
        for (int i = 0; i < chars.length(); i++) {
            if (!Character.isUpperCase(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all chars of given chars are lower case.
     *
     * @param chars given chars
     * @return whether all chars of given chars are lower case
     */
    public static boolean allLowerCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return false;
        }
        for (int i = 0; i < chars.length(); i++) {
            if (!Character.isLowerCase(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Capitalizes given chars, equivalent to {@code firstCase(chars, true)}.
     *
     * @param chars given chars
     * @return capitalized string
     */
    public static String capitalize(CharSequence chars) {
        return firstCase(chars, true);
    }

    /**
     * Uncapitalizes given chars, equivalent to {@code firstCase(chars, false)}.
     *
     * @param chars given chars
     * @return uncapitalized string
     */
    public static String uncapitalize(CharSequence chars) {
        return firstCase(chars, false);
    }

    /**
     * Returns a String of which first char is upper or lower (according to given upper) of first char of given chars,
     * and the rest chars are unchanged.
     *
     * @param chars given chars
     * @param upper given upper
     * @return converted string
     */
    public static String firstCase(CharSequence chars, boolean upper) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        if (Character.isUpperCase(chars.charAt(0)) == upper) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        cs[0] = upper ? Character.toUpperCase(chars.charAt(0)) : Character.toLowerCase(chars.charAt(0));
        for (int i = 1; i < chars.length(); i++) {
            cs[i] = chars.charAt(i);
        }
        return new String(cs);
    }

    /**
     * Returns a String of which content is upper case of given chars.
     *
     * @param chars given chars
     * @return converted string
     */
    public static String upperCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            cs[i] = Character.toUpperCase(chars.charAt(i));
        }
        return new String(cs);
    }

    /**
     * Returns a String of which content is lower case of given chars.
     *
     * @param chars given chars
     * @return converted string
     */
    public static String lowerCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            cs[i] = Character.toLowerCase(chars.charAt(i));
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
            CheckKit.checkRangeInBounds(off, end - start, 0, dst.length);
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
            CheckKit.checkOffsetLength(src.length(), srcOff, len);
            CheckKit.checkOffsetLength(dst.length, dstOff, len);
            for (int i = 0; i < len; i++) {
                dst[dstOff + i] = src.charAt(srcOff + i);
            }
        }
    }

    /**
     * Returns a string doesn't start with given start string. If given source string starts with given start string,
     * remove the start chars and return; else return source string.
     *
     * @param src   given source string
     * @param start given start string
     * @return removed string
     */
    public static String removeStart(CharSequence src, CharSequence start) {
        if (src.length() < start.length()) {
            return src.toString();
        }
        for (int i = 0; i < start.length(); i++) {
            if (src.charAt(i) != start.charAt(i)) {
                return src.toString();
            }
        }
        return src.subSequence(start.length(), src.length()).toString();
    }

    /**
     * Returns a string doesn't end with given end string. If given source string ends with given end string, remove the
     * end chars and return; else return source string.
     *
     * @param src given source string
     * @param end given end string
     * @return removed string
     */
    public static String removeEnd(CharSequence src, CharSequence end) {
        if (src.length() < end.length()) {
            return src.toString();
        }
        for (int i = src.length() - 1, j = end.length() - 1; j >= 0; i--, j--) {
            if (src.charAt(i) != end.charAt(j)) {
                return src.toString();
            }
        }
        return src.subSequence(0, src.length() - end.length()).toString();
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     * @return concatenated string
     */
    public static String concat(Object... args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     * @return concatenated string
     */
    public static String concat(Iterable<?> args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     * @return joined string
     */
    public static String join(CharSequence separator, Object... args) {
        StringJoiner joiner = new StringJoiner(separator);
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     * @return joined string
     */
    public static String join(CharSequence separator, Iterable<?> args) {
        StringJoiner joiner = new StringJoiner(separator);
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Converts given chars to int, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     * @return int from chars
     */
    public static int toInt(@Nullable CharSequence chars) {
        return toInt(chars, 0);
    }

    /**
     * Converts given chars to int, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     * @return int from chars
     */
    public static int toInt(@Nullable CharSequence chars, int defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to long, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     * @return long from chars
     */
    public static long toLong(@Nullable CharSequence chars) {
        return toLong(chars, 0);
    }

    /**
     * Converts given chars to long, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     * @return long from chars
     */
    public static long toLong(@Nullable CharSequence chars, long defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to float, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     * @return float from chars
     */
    public static float toFloat(@Nullable CharSequence chars) {
        return toFloat(chars, 0);
    }

    /**
     * Converts given chars to float, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     * @return float from chars
     */
    public static float toFloat(@Nullable CharSequence chars, float defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to double, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     * @return double from chars
     */
    public static double toDouble(@Nullable CharSequence chars) {
        return toDouble(chars, 0);
    }

    /**
     * Converts given chars to double, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     * @return double from chars
     */
    public static double toDouble(@Nullable CharSequence chars, double defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
