package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.string.StringKit;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * This interface is a type of formatter to resolve and format naming case such as camel-case, underscore-case.
 *
 * @author fredsuvn
 */
public interface CaseFormatter {

    /**
     * Upper Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    CaseFormatter UPPER_CAMEL = camelCase(true);

    /**
     * Lower Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    CaseFormatter LOWER_CAMEL = camelCase(false);

    /**
     * Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", null);
     * </pre>
     */
    CaseFormatter UNDERSCORE = delimiterCase("_", null);

    /**
     * Upper Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::upperCase);
     * </pre>
     */
    CaseFormatter UPPER_UNDERSCORE = delimiterCase("_", StringKit::upperCase);

    /**
     * Lower Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::lowerCase);
     * </pre>
     */
    CaseFormatter LOWER_UNDERSCORE = delimiterCase("_", StringKit::lowerCase);

    /**
     * Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", null);
     * </pre>
     */
    CaseFormatter HYPHEN = delimiterCase("-", null);

    /**
     * Upper Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::upperCase);
     * </pre>
     */
    CaseFormatter UPPER_HYPHEN = delimiterCase("-", StringKit::upperCase);

    /**
     * Lower Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::lowerCase);
     * </pre>
     */
    CaseFormatter LOWER_HYPHEN = delimiterCase("-", StringKit::lowerCase);

    /**
     * Returns a new {@link CaseFormatter} represents {@code Camel Case}.
     * <p>
     * Note the continuous characters which are non-lowercase and non-uppercase (such as digits) will be separately
     * combined to a word.
     *
     * @param upperHead whether this case is upper camel case
     * @return a new {@link CaseFormatter} represents {@code Camel Case}
     */
    static CaseFormatter camelCase(boolean upperHead) {
        return new CaseFormatterImpls.CamelCaseFormatter(upperHead);
    }

    /**
     * Returns a new {@link CaseFormatter} represents {@code Delimiter Case}.
     *
     * @param delimiter  the delimiter
     * @param wordMapper the mapper to deal with each word before joining the words, may be {@code null} if no need
     * @return a new {@link CaseFormatter} represents {@code Delimiter Case}
     */
    static CaseFormatter delimiterCase(
        CharSequence delimiter, @Nullable Function<? super CharSequence, ? extends CharSequence> wordMapper) {
        return new CaseFormatterImpls.DelimiterCaseFormatter(delimiter, wordMapper);
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
    default String to(CaseFormatter otherCase, CharSequence name) {
        if (Objects.equals(this, otherCase)) {
            return name.toString();
        }
        return otherCase.format(resolve(name));
    }
}
