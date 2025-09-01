package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.value.Span;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Name formatter, used to parse or format a given name. A name formatter represents a formatting scheme, such as
 * {@code Camel Case}, {@code Snake Case}, {@code File Naming}, etc.
 *
 * @author sunqian
 */
public interface NameFormatter {

    /**
     * Returns a new {@link NameFormatter} for {@code Camel Case} (e.g. someName, SomeName). The returned instance
     * applies {@code Camel Case} parsing to sequences of letters ({@code a-z} and {@code A-Z}), but treats digits
     * ({@code 0-9}) and other characters as separate words.
     *
     * @param capitalized specifies whether the first letter is capitalized (e.g. SomeName), and this is also called
     *                    {@code Pascal Case}
     * @return a new {@link NameFormatter} for {@code Camel Case} (e.g. someName, SomeName)
     */
    static @Nonnull NameFormatter camelCase(boolean capitalized) {
        return NameFormatterBack.camelCase(capitalized);
    }

    /**
     * Returns a new {@link NameFormatter} base on the specified delimiter (e.g. some-name, some_name).
     *
     * @param delimiter the specified delimiter
     * @return a new {@link NameFormatter} base on the specified delimiter (e.g. some-name, some_name)
     */
    static @Nonnull NameFormatter delimiterCase(@Nonnull CharSequence delimiter) {
        return delimiterCase(delimiter, null);
    }

    /**
     * Returns a new {@link NameFormatter} base on the specified delimiter (e.g. some-name, some_name).
     *
     * @param delimiter    the specified delimiter
     * @param wordAppender the word appender used in {@link #join(CharSequence, List)} method, for appending each word
     *                     into the specified string builder
     * @return a new {@link NameFormatter} base on the specified delimiter (e.g. some-name, some_name)
     */
    static @Nonnull NameFormatter delimiterCase(
        @Nonnull CharSequence delimiter, @Nullable NameFormatter.WordAppender wordAppender
    ) {
        return NameFormatterBack.delimiterCase(delimiter, wordAppender);
    }

    /**
     * Returns a new {@link NameFormatter} used to separate file base name and file extension (e.g.
     * {@code some-name.txt} split to {@code some-name} and {@code txt}).
     *
     * @return a new {@link NameFormatter} used to separate file base name and file extension (e.g.
     * {@code some-name.txt} split to {@code some-name} and {@code txt})
     */
    static @Nonnull NameFormatter fileNaming() {
        return NameFormatterBack.fileNaming();
    }

    /**
     * Parses the given name, splits it into a list of words by scheme of this formatter.
     *
     * @param name the given name
     * @return a list of words split from the given name
     * @throws NameFormatException if failed to parse the given name
     */
    default @Nonnull List<@Nonnull String> parse(@Nonnull CharSequence name) throws NameFormatException {
        List<Span> spans = tokenize(name);
        return spans.stream()
            .map(span -> name.subSequence(span.startIndex(), span.endIndex()).toString())
            .collect(Collectors.toList());
    }

    /**
     * Parses the given name, splits it into a list of words by scheme of this formatter. Returns a list of
     * {@link Span}s define the range of each word within the given name.
     *
     * @param name the given name
     * @return a list of {@link Span}s define the range of each word within the given name
     * @throws NameFormatException if failed to parse the given name
     */
    @Nonnull
    List<@Nonnull Span> tokenize(@Nonnull CharSequence name) throws NameFormatException;

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
     * @see #tokenize(CharSequence)
     * @see #join(CharSequence, List)
     */
    default @Nonnull String to(
        @Nonnull NameFormatter otherSpec, @Nonnull CharSequence name
    ) throws UnsupportedOperationException {
        if (Objects.equals(this, otherSpec)) {
            return name.toString();
        }
        return otherSpec.join(name, tokenize(name));
    }

    /**
     * Appender for appending each word split by {@link #tokenize(CharSequence)} into a specified {@link Appendable}.
     * This interface is typically used in {@link #join(CharSequence, List)}.
     */
    interface WordAppender {

        static @Nonnull WordAppender simpleAppender() {
            return NameFormatterBack.simpleAppender();
        }

        /**
         * Appends the word into the specified {@link StringBuilder}, the word is specified by the given span that
         * define the range of the word within the given original name.
         *
         * @param builder      the specified {@link StringBuilder}
         * @param originalName the given original name where the word span is derived
         * @param index        the index of the word in the returned list of {@link #tokenize(CharSequence)}
         */
        void append(
            @Nonnull StringBuilder builder,
            @Nonnull CharSequence originalName,
            @Nonnull Span span,
            int index
        );
    }
}
