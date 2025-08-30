package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.Span;

import java.util.List;
import java.util.Objects;

/**
 * Represents the specification for naming, such as camel-case, snake-case, file-naming, etc.
 *
 * @author sunqian
 */
public interface NameSpec {

    /**
     * Returns a new {@link NameSpec} for {@code Camel Case} (e.g. someName, SomeName). The returned {@link NameSpec}
     * splits letters (a-z, A-Z) using {@code Camel Case}, and separate the continuous digits (0-9) or continuous other
     * characters as a word.
     *
     * @param capitalized specifies whether the first letter is capitalized (e.g. SomeName), in some cases, this is
     *                    called {@code Pascal Case}
     * @return a new {@link NameSpec} for {@code Camel Case} (e.g. someName, SomeName)
     */
    static @Nonnull NameSpec camelCase(boolean capitalized) {
        return NameSpecBack.camelCase(capitalized);
    }

    /**
     * Returns a new {@link NameSpec} base on the specified delimiter (e.g. some-name, some_name).
     *
     * @param delimiter the specified delimiter
     * @return a new {@link NameSpec} base on the specified delimiter (e.g. some-name, some_name)
     */
    static @Nonnull NameSpec delimiterCase(@Nonnull CharSequence delimiter) {
        return delimiterCase(delimiter, null);
    }

    /**
     * Returns a new {@link NameSpec} base on the specified delimiter (e.g. some-name, some_name).
     *
     * @param delimiter    the specified delimiter
     * @param wordAppender the word appender used in {@link #join(CharSequence, List)} method, for appending each word
     *                     into the specified string builder
     * @return a new {@link NameSpec} base on the specified delimiter (e.g. some-name, some_name)
     */
    static @Nonnull NameSpec delimiterCase(
        @Nonnull CharSequence delimiter, @Nullable NameSpec.WordAppender wordAppender
    ) {
        return NameSpecBack.delimiterCase(delimiter, wordAppender);
    }

    /**
     * Returns a new {@link NameSpec} used to separate file base name and file extension (e.g. some-name.txt to
     * some-name and txt).
     *
     * @return a new {@link NameSpec} used to separate file base name and file extension (e.g. some-name.txt to
     * some-name and txt)
     */
    static @Nonnull NameSpec fileNaming() {
        return NameSpecBack.fileNaming();
    }

    /**
     * Splits the given name to words by rules of this name spec. Returns a list of {@link Span} define the range of
     * each word within the given name.
     *
     * @param name the given name where the returned spans are derived
     * @return a list of {@link Span} define the range of  each word within the given name
     * @throws UnsupportedOperationException if this name spec does not support the split operation for the given name
     */
    @Nonnull
    List<@Nonnull Span> split(@Nonnull CharSequence name) throws UnsupportedOperationException;

    /**
     * Joins the words into a name by rules of this name spec. The words are specified by the list of {@link Span} that
     * define the range of each word within the given original name.
     *
     * @param originalName the given original name where the word spans are derived
     * @param wordSpans    the list of {@link Span} that define the range of each word within the given original name
     * @return a name joined from the words by rules of this name spec
     * @throws UnsupportedOperationException if this name spec does not support the join operation for the given words
     */
    @Nonnull
    String join(
        @Nonnull CharSequence originalName, @Nonnull List<@Nonnull Span> wordSpans
    ) throws UnsupportedOperationException;

    /**
     * Converts the given name from this name spec to the other specified name spec. This method is equivalent to:
     * <pre>
     * return otherSpec.join(name, split(name));
     * </pre>
     *
     * @param otherSpec the other specified name spec
     * @param name      the given name
     * @return the converted name
     * @throws UnsupportedOperationException if the conversion is not supported for the given name
     * @see #split(CharSequence)
     * @see #join(CharSequence, List)
     */
    default @Nonnull String to(
        @Nonnull NameSpec otherSpec, @Nonnull CharSequence name
    ) throws UnsupportedOperationException {
        if (Objects.equals(this, otherSpec)) {
            return name.toString();
        }
        return otherSpec.join(name, split(name));
    }

    /**
     * Appender for appending each word split by {@link #split(CharSequence)} into a specified {@link Appendable}. This
     * interface is typically used in {@link #join(CharSequence, List)}.
     */
    interface WordAppender {

        /**
         * Appends the word into the specified {@link StringBuilder}, the word is specified by the given span that
         * define the range of the word within the given original name.
         *
         * @param builder      the specified {@link StringBuilder}
         * @param originalName the given original name where the word span is derived
         * @param index        the index of the word in the returned list of {@link #split(CharSequence)}
         */
        void append(
            @Nonnull StringBuilder builder,
            @Nonnull CharSequence originalName,
            @Nonnull Span span,
            int index
        );
    }
}
