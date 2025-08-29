package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * This interface is a type of formatter to resolve and format naming case such as camel-case, underscore-case.
 *
 * @author fredsuvn
 */
public interface NameFormatter {

    /**
     * Upper Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    NameFormatter UPPER_CAMEL = camelCase(true);

    /**
     * Lower Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    NameFormatter LOWER_CAMEL = camelCase(false);

    /**
     * Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", null);
     * </pre>
     */
    NameFormatter UNDERSCORE = delimiterCase("_", null);

    /**
     * Upper Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::upperCase);
     * </pre>
     */
    NameFormatter UPPER_UNDERSCORE = delimiterCase("_", StringKit::upperCase);

    /**
     * Lower Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::lowerCase);
     * </pre>
     */
    NameFormatter LOWER_UNDERSCORE = delimiterCase("_", StringKit::lowerCase);

    /**
     * Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", null);
     * </pre>
     */
    NameFormatter HYPHEN = delimiterCase("-", null);

    /**
     * Upper Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::upperCase);
     * </pre>
     */
    NameFormatter UPPER_HYPHEN = delimiterCase("-", StringKit::upperCase);

    /**
     * Lower Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::lowerCase);
     * </pre>
     */
    NameFormatter LOWER_HYPHEN = delimiterCase("-", StringKit::lowerCase);

    /**
     * Returns a new {@link NameFormatter} represents {@code Camel Case}.
     * <p>
     * Note the continuous characters which are non-lowercase and non-uppercase (such as digits) will be separately
     * combined to a word.
     *
     * @param upperHead whether this case is upper camel case
     * @return a new {@link NameFormatter} represents {@code Camel Case}
     */
    static NameFormatter camelCase(boolean upperHead) {
        return new NameFormatterBack.CamelNameFormatter(upperHead);
    }

    /**
     * Returns a new {@link NameFormatter} represents {@code Delimiter Case}.
     *
     * @param delimiter  the delimiter
     * @param wordMapper the mapper to deal with each word before joining the words, may be {@code null} if no need
     * @return a new {@link NameFormatter} represents {@code Delimiter Case}
     */
    static NameFormatter delimiterCase(
        CharSequence delimiter, @Nullable Function<? super CharSequence, ? extends CharSequence> wordMapper) {
        return new NameFormatterBack.DelimiterNameFormatter(delimiter, wordMapper);
    }

    /**
     * Resolves given name to a list of words in rules of this name case.
     *
     * @param name given name
     * @return a list of words in rules of this name case
     */
    List<CharSequence> resolve(CharSequence name);

    /**
     * Formats and returns name by this name case from given list of words.
     *
     * @param wordList given list of words
     * @return name by this name case from given list of words
     */
    String format(List<? extends CharSequence> wordList);

    /**
     * Formats given name from this name case to specified other name case. This method is equivalent to:
     * <pre>
     *     return otherCase.format(resolve(name));
     * </pre>
     *
     * @param otherCase specified other name case
     * @param name      given name
     * @return formatted name
     * @see #resolve(CharSequence)
     * @see #format(List)
     */
    default String to(NameFormatter otherCase, CharSequence name) {
        if (Objects.equals(this, otherCase)) {
            return name.toString();
        }
        return otherCase.format(resolve(name));
    }
}
