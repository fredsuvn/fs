package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.Span;
import xyz.sunqian.common.collect.ListKit;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class NameSpecBack {

    static @Nonnull NameSpec camelCase(boolean capitalized) {
        return new CamelCase(capitalized);
    }

    static @Nonnull NameSpec delimiterCase
        (@Nonnull CharSequence delimiter, @Nullable NameSpec.WordAppender wordAppender
        ) {
        return new DelimiterCase(delimiter, Jie.nonnull(wordAppender, SimpleAppender.SINGLETON));
    }

    static @Nonnull NameSpec fileNaming() {
        return new FileNaming();
    }

    private static final class CamelCase implements NameSpec {

        private static final int LOWER = 1;
        private static final int UPPER = 2;
        private static final int NUM = 4;
        private static final int OTHER = 8;

        private final boolean capitalized;

        private CamelCase(boolean capitalized) {
            this.capitalized = capitalized;
        }

        @Override
        public @Nonnull List<@Nonnull Span> split(@Nonnull CharSequence name) {
            if (name.length() == 0) {
                return Collections.singletonList(Span.empty());
            }
            int size = 1;
            int start = 0;
            int i = 1;
            int t1 = charType(name.charAt(0));
            while (i < name.length()) {
                int t2 = charType(name.charAt(i));
                if (t1 == t2) {
                    i++;
                    continue;
                }
                // Aa: check AAa and aAa
                if (t1 == UPPER && t2 == LOWER) {
                    if (start == i - 1) {
                        // Aa: one word
                        i++;
                        continue;
                    } else {
                        // AAa: split as A + Aa
                        start = i - 1;
                    }
                } else {
                    // others start from current char
                    start = i;
                }
                // others add one word
                size++;
                i++;
            }
            Span[] words = new Span[size];
            start = 0;
            i = 1;
            int wi = 0;
            t1 = charType(name.charAt(0));
            while (i < name.length()) {
                int t2 = charType(name.charAt(i));
                if (t1 == t2) {
                    i++;
                    continue;
                }
                // Aa: check AAa and aAa
                if (t1 == UPPER && t2 == LOWER) {
                    if (start == i - 1) {
                        // Aa: one word
                        i++;
                        continue;
                    } else {
                        // AAa: split as A + Aa
                        words[wi++] = Span.of(start, i - 1);
                        start = i - 1;
                    }
                } else {
                    // others start from current char
                    words[wi++] = Span.of(start, i);
                    start = i;
                }
                i++;
            }
            words[wi] = Span.of(start, name.length());
            return ListKit.list(words);
        }

        @Override
        public @Nonnull String join(
            @Nonnull CharSequence originalName, @Nonnull List<@Nonnull Span> wordSpans
        ) throws UnsupportedOperationException {
            if (wordSpans.isEmpty()) {
                throw new UnsupportedOperationException("wordSpan is empty.");
            }
            StringBuilder builder = new StringBuilder(originalName.length());
            Span first = wordSpans.get(0);
            appendFirstWord(builder, originalName, first);
            if (wordSpans.size() > 1) {
                for (Span span : wordSpans.subList(1, wordSpans.size())) {
                    appendWord(builder, originalName, span);
                }
            }
            return builder.toString();
        }

        private void appendFirstWord(
            @Nonnull StringBuilder builder, @Nonnull CharSequence originalName, @Nonnull Span span
        ) throws UnsupportedOperationException {
            if (span.isEmpty()) {
                throw new UnsupportedOperationException("The first span is empty.");
            }
            if (capitalized) {
                builder.append(Character.toUpperCase(originalName.charAt(span.startIndex())));
                builder.append(originalName, span.startIndex() + 1, span.endIndex());
            } else {
                if (isAllUpper(originalName, span)) {
                    builder.append(originalName, span.startIndex(), span.endIndex());
                } else {
                    builder.append(Character.toLowerCase(originalName.charAt(span.startIndex())));
                    builder.append(originalName, span.startIndex() + 1, span.endIndex());
                }
            }
        }

        private void appendWord(
            @Nonnull StringBuilder builder, @Nonnull CharSequence originalName, @Nonnull Span span
        ) throws UnsupportedOperationException {
            if (span.isEmpty()) {
                throw new UnsupportedOperationException("The span is empty.");
            }
            if (isAllUpper(originalName, span)) {
                builder.append(originalName, span.startIndex(), span.endIndex());
            } else {
                builder.append(Character.toUpperCase(originalName.charAt(span.startIndex())));
                builder.append(originalName, span.startIndex() + 1, span.endIndex());
            }
        }

        private boolean isAllUpper(@Nonnull CharSequence originalName, @Nonnull Span span) {
            if (span.length() < 2) {
                return false;
            }
            return charType(originalName.charAt(span.startIndex())) == UPPER
                && charType(originalName.charAt(span.startIndex() + 1)) == UPPER;
        }

        private int charType(char c) {
            if (c >= 'a' && c <= 'z') {
                return LOWER;
            }
            if (c >= 'A' && c <= 'Z') {
                return UPPER;
            }
            if (c >= '0' && c <= '9') {
                return NUM;
            }
            return OTHER;
        }
    }

    private static final class DelimiterCase implements NameSpec {

        private final @Nonnull CharSequence delimiter;
        private final @Nonnull NameSpec.WordAppender wordAppender;

        private DelimiterCase(
            @Nonnull CharSequence delimiter, @Nonnull NameSpec.WordAppender wordAppender
        ) {
            this.delimiter = delimiter;
            this.wordAppender = wordAppender;
        }

        @Override
        public @Nonnull List<@Nonnull Span> split(@Nonnull CharSequence name) {
            int size = 1;
            int start = 0;
            while (start < name.length()) {
                int index = StringKit.indexOf(name, delimiter, start);
                if (index < 0) {
                    break;
                }
                size++;
                start += index + delimiter.length();
            }
            Span[] spans = new Span[size];
            int i = 0;
            start = 0;
            while (start < name.length()) {
                int index = StringKit.indexOf(name, delimiter, start);
                if (index < 0) {
                    break;
                }
                spans[i++] = Span.of(start, index);
                start += index + delimiter.length();
            }
            spans[i] = start < name.length() ? Span.of(start, name.length()) : Span.empty();
            return ListKit.list(spans);
        }

        @Override
        public @Nonnull String join(
            @Nonnull CharSequence originalName, @Nonnull List<@Nonnull Span> wordSpans
        ) throws UnsupportedOperationException {
            if (wordSpans.isEmpty()) {
                throw new UnsupportedOperationException("wordSpan is empty.");
            }
            StringBuilder builder = new StringBuilder(originalName.length());
            if (wordSpans.size() == 1) {
                Span first = wordSpans.get(0);
                wordAppender.append(builder, originalName, first, 0);
                return builder.toString();
            }
            int i = 0;
            Iterator<Span> iterator = wordSpans.iterator();
            while (iterator.hasNext()) {
                Span span = iterator.next();
                wordAppender.append(builder, originalName, span, i++);
                if (iterator.hasNext()) {
                    builder.append(delimiter);
                }
            }
            return builder.toString();
        }
    }

    private static final class FileNaming implements NameSpec {

        @Override
        public @Nonnull List<@Nonnull Span> split(@Nonnull CharSequence name) {
            int lastDot = StringKit.lastIndexOf(name, '.');
            if (lastDot < 0 || lastDot == name.length() - 1) {
                return Collections.singletonList(Span.of(0, name.length()));
            }
            return ListKit.list(
                Span.of(0, lastDot),
                Span.of(lastDot + 1, name.length())
            );
        }

        @Override
        public @Nonnull String join(
            @Nonnull CharSequence originalName, @Nonnull List<@Nonnull Span> wordSpans
        ) throws UnsupportedOperationException {
            if (wordSpans.isEmpty()) {
                throw new UnsupportedOperationException("wordSpan is empty.");
            }
            StringBuilder builder = new StringBuilder(originalName.length());
            if (wordSpans.size() == 1) {
                Span first = wordSpans.get(0);
                appendWord(builder, originalName, first);
                return builder.toString();
            }
            if (wordSpans.size() == 2) {
                Span prefix = wordSpans.get(0);
                Span suffix = wordSpans.get(1);
                appendWord(builder, originalName, prefix);
                builder.append('.');
                appendWord(builder, originalName, suffix);
                return builder.toString();
            }
            List<@Nonnull Span> prefixList = wordSpans.subList(0, wordSpans.size() - 1);
            for (Span prefix : prefixList) {
                appendWord(builder, originalName, prefix);
            }
            builder.append('.');
            Span suffix = wordSpans.get(wordSpans.size() - 1);
            appendWord(builder, originalName, suffix);
            return builder.toString();
        }
    }

    private static void appendWord(
        @Nonnull StringBuilder builder, @Nonnull CharSequence originalName, @Nonnull Span span
    ) {
        SimpleAppender.SINGLETON.append(builder, originalName, span, 0);
    }

    private static final class SimpleAppender implements NameSpec.WordAppender {

        private static final @Nonnull NameSpecBack.SimpleAppender SINGLETON = new SimpleAppender();

        @Override
        public void append(
            @Nonnull StringBuilder builder, @Nonnull CharSequence originalName, @Nonnull Span span, int index
        ) {
            builder.append(originalName, span.startIndex(), span.endIndex());
        }
    }
}
