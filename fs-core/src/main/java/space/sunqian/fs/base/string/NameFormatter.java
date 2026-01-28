package space.sunqian.fs.base.string;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.value.Span;

import java.util.Objects;

/**
 * Name formatter, used to parse or format a given name. A name formatter represents a formatting scheme, such as
 * {@code Camel Case}, {@code Snake Case}, {@code File Naming}, etc.
 *
 * @author sunqian
 */
public interface NameFormatter {

    /**
     * Returns a new {@link NameFormatter} for lower camel case (e.g. {@code someName}). The returned instance applies
     * the lower camel case to parse letters in {@code a-z} and {@code A-Z}, but treats digits ({@code 0-9}) and other
     * characters as separate words.
     *
     * @return a new {@link NameFormatter} for lower camel case (e.g. {@code someName})
     */
    static @Nonnull NameFormatter lowerCamel() {
        return NameFormatterBack.camelCase(false);
    }

    /**
     * Returns a new {@link NameFormatter} for upper camel case (e.g. {@code SomeName}), also called pascal case. The
     * returned instance applies the upper camel case to parse letters in {@code a-z} and {@code A-Z}, but treats digits
     * ({@code 0-9}) and other characters as separate words.
     *
     * @return a new {@link NameFormatter} for upper camel case (e.g. {@code SomeName})
     */
    static @Nonnull NameFormatter upperCamel() {
        return NameFormatterBack.camelCase(true);
    }

    /**
     * Returns a new {@link NameFormatter} base on the specified delimiter, and keeps the original case of the word
     * (e.g. {@code some-Name}, {@code some_Name}).
     *
     * @param delimiter the specified delimiter
     * @return a new {@link NameFormatter} base on the specified delimiter, the original case is kept
     * @throws IllegalArgumentException if the delimiter is empty
     */
    static @Nonnull NameFormatter delimiter(
        @Nonnull CharSequence delimiter
    ) throws IllegalArgumentException {
        return delimiter(delimiter, simpleAppender());
    }

    /**
     * Returns a new {@link NameFormatter} base on the specified delimiter, and specifies the case of the word (e.g.
     * {@code some-name}, {@code SOME_NAME}).
     *
     * @param delimiter the specified delimiter
     * @param lower     specifies the case of the word, {@code true} for lower, {@code false} for upper
     * @return a new {@link NameFormatter} base on the specified delimiter, the case is specified
     * @throws IllegalArgumentException if the delimiter is empty
     */
    static @Nonnull NameFormatter delimiter(
        @Nonnull CharSequence delimiter, boolean lower
    ) throws IllegalArgumentException {
        return delimiter(delimiter, lower ?
            NameFormatterBack.LowerAppender.INST : NameFormatterBack.UpperAppender.INST
        );
    }

    /**
     * Returns a new {@link NameFormatter} base on the specified delimiter (e.g. {@code some-name}, {@code some_name}).
     *
     * @param delimiter the specified delimiter, can not be empty
     * @param appender  the appender used to append each word into the destination appendable object
     * @return a new {@link NameFormatter} base on the specified delimiter (e.g. {@code some-name}, {@code some_name})
     * @throws IllegalArgumentException if the delimiter is empty
     */
    static @Nonnull NameFormatter delimiter(
        @Nonnull CharSequence delimiter, @Nonnull NameFormatter.Appender appender
    ) throws IllegalArgumentException {
        return NameFormatterBack.delimiter(delimiter, appender);
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
     * Returns an instance of {@link NameFormatter.Appender} which simply adds the word without any modifications.
     *
     * @return an instance of {@link NameFormatter.Appender} which simply adds the word without any modifications
     */
    static @Nonnull NameFormatter.Appender simpleAppender() {
        return NameFormatterBack.simpleAppender();
    }

    /**
     * Parses the given name, splits it into an array of words by scheme of this formatter.
     *
     * @param name the given name
     * @return an array of words split from the given name
     * @throws NameFormatException if failed to parse the given name
     */
    default @Nonnull String @Nonnull [] parse(@Nonnull CharSequence name) throws NameFormatException {
        Span[] spans = tokenize(name);
        if (spans.length <= 1) {
            return new String[]{name.toString()};
        }
        String[] ret = new String[spans.length];
        for (int i = 0; i < spans.length; i++) {
            ret[i] = name.subSequence(spans[i].startIndex(), spans[i].endIndex()).toString();
        }
        return ret;
    }

    /**
     * Parses the given name, splits it into an array of words by scheme of this formatter. Returns an array of
     * {@link Span}s define the range of each word within the given name.
     *
     * @param name the given name
     * @return an array of {@link Span}s define the range of each word within the given name
     * @throws NameFormatException if failed to parse the given name
     */
    @Nonnull
    Span @Nonnull [] tokenize(@Nonnull CharSequence name) throws NameFormatException;

    /**
     * Joins the given words into a name by rules of this name formatter.
     *
     * @param words the given words
     * @return a name joined by the given words by rules of this name formatter
     * @throws NameFormatException if failed to join the words
     */
    default @Nonnull String format(@Nonnull CharSequence @Nonnull ... words) throws NameFormatException {
        StringBuilder sb = new StringBuilder();
        format(words, sb);
        return sb.toString();
    }

    /**
     * Joins the given words into a name by rules of this name formatter. The joined name will be appended to the
     * specified destination appendable.
     *
     * @param words the given words
     * @param dst   the specified destination appendable
     * @throws NameFormatException if failed to join the words
     */
    void format(
        @Nonnull CharSequence @Nonnull [] words,
        @Nonnull Appendable dst
    ) throws NameFormatException;

    /**
     * Joins the words into a name by rules of this name formatter. The words are specified by the array of {@link Span}
     * that define the range of each word within the given original name.
     *
     * @param originalName the given original name where the word spans are derived
     * @param wordSpans    the array of {@link Span} that define the range of each word within the given original name
     * @return a name joined from the words by rules of this name formatter
     * @throws NameFormatException if failed to join the words
     */
    default @Nonnull String format(
        @Nonnull CharSequence originalName, @Nonnull Span @Nonnull [] wordSpans
    ) throws NameFormatException {
        StringBuilder sb = new StringBuilder(originalName.length());
        format(originalName, wordSpans, sb);
        return sb.toString();
    }

    /**
     * Joins the words into a name by rules of this name formatter. The words are specified by the array of {@link Span}
     * that define the range of each word within the given original name. The joined name will be appended to the
     * specified destination appendable.
     *
     * @param origin    the given original name where the word spans are derived
     * @param wordSpans the array of {@link Span} that define the range of each word within the given original name
     * @param dst       the specified destination appendable
     * @throws NameFormatException if failed to join the words
     */
    void format(
        @Nonnull CharSequence origin,
        @Nonnull Span @Nonnull [] wordSpans,
        @Nonnull Appendable dst
    ) throws NameFormatException;

    /**
     * Converts the given original name from this name formatter to the specified name formatter. This method is
     * equivalent to: {@code toFormatter.format(origin, tokenize(name))}.
     *
     * @param origin      the given name
     * @param toFormatter the specified name formatter to convert to
     * @return the converted name
     * @throws NameFormatException if the conversion is not supported for the given name
     */
    default @Nonnull String format(
        @Nonnull CharSequence origin, @Nonnull NameFormatter toFormatter
    ) throws NameFormatException {
        if (Objects.equals(this, toFormatter)) {
            return origin.toString();
        }
        return toFormatter.format(origin, tokenize(origin));
    }

    /**
     * Appender for appending each word split by {@link #tokenize(CharSequence)} into the specified {@link Appendable}.
     */
    interface Appender {

        /**
         * Appends the word into the specified {@link Appendable}, the word is specified by the given span that define
         * the range of the word within the given original name.
         *
         * @param dst    the specified {@link Appendable}
         * @param origin the given original name where the word span is derived
         * @param span   the span that define the range of the word within the given original name
         * @param index  the index of the word in the returned list of {@link #tokenize(CharSequence)}
         * @throws Exception if failed to append
         */
        void append(
            @Nonnull Appendable dst,
            @Nonnull CharSequence origin,
            @Nonnull Span span,
            int index
        ) throws Exception;
    }
}
