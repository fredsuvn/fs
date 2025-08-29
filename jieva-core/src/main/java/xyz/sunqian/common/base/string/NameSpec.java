package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents the specification for naming, such as camel-case, snake-case, file-naming, etc.
 *
 * @author sunqian
 */
public interface NameSpec {

    /**
     * Upper Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    NameSpec UPPER_CAMEL = camelCase(true);

    /**
     * Lower Camel Case from {@link #camelCase(boolean)}. It is equivalent to ({@link #camelCase(boolean)}):
     * <pre>
     *     camelCase(true);
     * </pre>
     */
    NameSpec LOWER_CAMEL = camelCase(false);

    /**
     * Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", null);
     * </pre>
     */
    NameSpec UNDERSCORE = delimiterCase("_", null);

    /**
     * Upper Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::upperCase);
     * </pre>
     */
    NameSpec UPPER_UNDERSCORE = delimiterCase("_", StringKit::upperCase);

    /**
     * Lower Underscore Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("_", JieString::lowerCase);
     * </pre>
     */
    NameSpec LOWER_UNDERSCORE = delimiterCase("_", StringKit::lowerCase);

    /**
     * Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", null);
     * </pre>
     */
    NameSpec HYPHEN = delimiterCase("-", null);

    /**
     * Upper Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::upperCase);
     * </pre>
     */
    NameSpec UPPER_HYPHEN = delimiterCase("-", StringKit::upperCase);

    /**
     * Lower Hyphen Delimiter Case from {@link #delimiterCase(CharSequence, Function)}. It is equivalent to
     * ({@link #delimiterCase(CharSequence, Function)}):
     * <pre>
     *     delimiterCase("-", JieString::lowerCase);
     * </pre>
     */
    NameSpec LOWER_HYPHEN = delimiterCase("-", StringKit::lowerCase);

    /**
     * Returns a new {@link NameSpec} represents {@code Camel Case}.
     * <p>
     * Note the continuous characters which are non-lowercase and non-uppercase (such as digits) will be separately
     * combined to a word.
     *
     * @param upperHead whether this case is upper camel case
     * @return a new {@link NameSpec} represents {@code Camel Case}
     */
    static NameSpec camelCase(boolean upperHead) {
        return new NameSpecBack.CamelNameSpec(upperHead);
    }

    /**
     * Returns a new {@link NameSpec} represents {@code Delimiter Case}.
     *
     * @param delimiter  the delimiter
     * @param wordMapper the mapper to deal with each word before joining the words, may be {@code null} if no need
     * @return a new {@link NameSpec} represents {@code Delimiter Case}
     */
    static NameSpec delimiterCase(
        CharSequence delimiter, @Nullable Function<? super CharSequence, ? extends CharSequence> wordMapper) {
        return new NameSpecBack.DelimiterNameSpec(delimiter, wordMapper);
    }

    /**
     * Splits the given name to words by rules of this name spec.
     *
     * @param name the given name
     * @return a list of words in rules of this name spec
     */
    @Nonnull
    List<@Nonnull CharSequence> split(@Nonnull CharSequence name);

    /**
     * Joins the given list of words into a name by rules of this name spec.
     *
     * @param words the given list of words
     * @return a name joined from the given list of words by rules of this name spec
     */
    @Nonnull
    String join(@Nonnull List<? extends @Nonnull CharSequence> words);

    /**
     * Converts the given name from this name spec to the other specified name spec. This method is equivalent to:
     * <pre>
     * return otherSpec.join(split(name));
     * </pre>
     *
     * @param otherSpec the other specified name spec
     * @param name      the given name
     * @return the converted name
     * @see #split(CharSequence)
     * @see #join(List)
     */
    default @Nonnull String to(@Nonnull NameSpec otherSpec, @Nonnull CharSequence name) {
        if (Objects.equals(this, otherSpec)) {
            return name.toString();
        }
        return otherSpec.join(split(name));
    }
}
