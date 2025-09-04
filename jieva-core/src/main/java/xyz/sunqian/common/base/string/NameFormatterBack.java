package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.value.Span;
import xyz.sunqian.common.collect.ArrayKit;

final class NameFormatterBack {

    static @Nonnull NameFormatter camelCase(boolean upperFirst) {
        return new CamelCase(upperFirst);
    }

    static @Nonnull NameFormatter delimiterCase(
        @Nonnull CharSequence delimiter, @Nonnull NameFormatter.Appender appender
    ) throws IllegalArgumentException {
        return new DelimiterCase(delimiter, appender);
    }

    static @Nonnull NameFormatter fileNaming() {
        return new FileNaming();
    }

    static @Nonnull NameFormatter.Appender simpleAppender() {
        return SimpleAppender.SINGLETON;
    }

    private static final class CamelCase implements NameFormatter {

        private static final int LOWER = 1;
        private static final int UPPER = 2;
        private static final int NUM = 4;
        private static final int OTHER = 8;

        private final boolean upperFirst;

        private CamelCase(boolean upperFirst) {
            this.upperFirst = upperFirst;
        }

        @Override
        public @Nonnull Span @Nonnull [] tokenize(@Nonnull CharSequence name) {
            int len = name.length();
            if (len == 0) {
                return new Span[]{Span.empty()};
            }
            if (len == 1) {
                return new Span[]{Span.of(0, 1)};
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
                        t1 = t2;
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
                t1 = t2;
            }
            Span[] spans = new Span[size];
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
                        t1 = t2;
                        continue;
                    } else {
                        // AAa: split as A + Aa
                        spans[wi++] = Span.of(start, i - 1);
                        start = i - 1;
                    }
                } else {
                    // others start from current char
                    spans[wi++] = Span.of(start, i);
                    start = i;
                }
                i++;
                t1 = t2;
            }
            spans[wi] = Span.of(start, name.length());
            return spans;
        }

        @Override
        public void format(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws NameFormatException {
            if (words.length == 0) {
                return;
            }
            try {
                format0(words, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws Exception {
            CharSequence first = words[0];
            appendFirstWord(dst, first, Span.of(0, first.length()));
            if (words.length > 1) {
                for (int i = 1; i < words.length; i++) {
                    appendWord(dst, words[i], Span.of(0, words[i].length()));
                }
            }
        }

        @Override
        public void format(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws NameFormatException {
            if (wordSpans.length == 0) {
                return;
            }
            try {
                format0(originalName, wordSpans, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws Exception {
            Span first = wordSpans[0];
            appendFirstWord(dst, originalName, first);
            if (wordSpans.length > 1) {
                for (int i = 1; i < wordSpans.length; i++) {
                    appendWord(dst, originalName, wordSpans[i]);
                }
            }
        }

        private void appendFirstWord(
            @Nonnull Appendable dst, @Nonnull CharSequence str, @Nonnull Span span
        ) throws Exception {
            if (span.isEmpty()) {
                return;
            }
            if (isAllUpper(str, span)) {
                dst.append(str, span.startIndex(), span.endIndex());
                return;
            }
            dst.append(upperFirst ?
                Character.toUpperCase(str.charAt(span.startIndex()))
                :
                Character.toLowerCase(str.charAt(span.startIndex()))
            );
            if (span.endIndex() - span.startIndex() == 1) {
                return;
            }
            dst.append(str, span.startIndex() + 1, span.endIndex());
        }

        private void appendWord(
            @Nonnull Appendable dst, @Nonnull CharSequence str, @Nonnull Span span
        ) throws Exception {
            if (span.isEmpty()) {
                return;
            }
            if (isAllUpper(str, span)) {
                dst.append(str, span.startIndex(), span.endIndex());
                return;
            }
            dst.append(Character.toUpperCase(str.charAt(span.startIndex())));
            if (span.endIndex() - span.startIndex() == 1) {
                return;
            }
            dst.append(str, span.startIndex() + 1, span.endIndex());
        }

        private boolean isAllUpper(@Nonnull CharSequence str, @Nonnull Span span) {
            if (span.length() < 2) {
                return false;
            }
            return charType(str.charAt(span.startIndex())) == UPPER
                && charType(str.charAt(span.startIndex() + 1)) == UPPER;
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

    private static final class DelimiterCase implements NameFormatter {

        private final @Nonnull CharSequence delimiter;
        private final @Nonnull NameFormatter.Appender appender;

        private DelimiterCase(
            @Nonnull CharSequence delimiter, @Nonnull NameFormatter.Appender appender
        ) throws IllegalArgumentException {
            CheckKit.checkArgument(delimiter.length() > 0, "The delimiter must not be empty.");
            this.delimiter = delimiter;
            this.appender = appender;
        }

        @Override
        public @Nonnull Span @Nonnull [] tokenize(@Nonnull CharSequence name) {
            if (name.length() < delimiter.length()) {
                return new Span[]{Span.of(0, name.length())};
            }
            int size = 1;
            int start = 0;
            while (start < name.length()) {
                int index = StringKit.indexOf(name, delimiter, start);
                if (index < 0) {
                    break;
                }
                size++;
                start = index + delimiter.length();
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
                start = index + delimiter.length();
            }
            spans[i] = start < name.length() ? Span.of(start, name.length()) : Span.empty();
            return spans;
        }

        @Override
        public void format(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws NameFormatException {
            if (words.length == 0) {
                return;
            }
            try {
                format0(words, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws Exception {
            if (words.length == 1) {
                CharSequence first = words[0];
                dst.append(first);
                return;
            }
            dst.append(words[0]);
            for (int i = 1; i < words.length; i++) {
                dst.append(delimiter);
                dst.append(words[i]);
            }
        }

        @Override
        public void format(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws NameFormatException {
            if (wordSpans.length == 0) {
                return;
            }
            try {
                format0(originalName, wordSpans, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws Exception {
            if (wordSpans.length == 1) {
                Span first = wordSpans[0];
                appender.append(dst, originalName, first, 0);
                return;
            }
            appender.append(dst, originalName, wordSpans[0], 0);
            for (int i = 1; i < wordSpans.length; i++) {
                dst.append(delimiter);
                appender.append(dst, originalName, wordSpans[i], i);
            }
        }
    }

    private static final class FileNaming implements NameFormatter {

        private static final SimpleAppender appender = (SimpleAppender) simpleAppender();

        @Override
        public @Nonnull Span @Nonnull [] tokenize(@Nonnull CharSequence name) {
            int lastDot = StringKit.lastIndexOf(name, '.');
            if (lastDot < 0 || lastDot == name.length() - 1) {
                return new Span[]{Span.of(0, name.length())};
            }
            return ArrayKit.array(
                Span.of(0, lastDot),
                Span.of(lastDot + 1, name.length())
            );
        }

        @Override
        public void format(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws NameFormatException {
            if (words.length == 0) {
                return;
            }
            try {
                format0(words, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence @Nonnull [] words, @Nonnull Appendable dst
        ) throws Exception {
            if (words.length == 1) {
                CharSequence first = words[0];
                dst.append(first);
                return;
            }
            if (words.length == 2) {
                CharSequence prefix = words[0];
                CharSequence suffix = words[1];
                dst.append(prefix);
                dst.append('.');
                dst.append(suffix);
                return;
            }
            for (int i = 0; i < words.length - 1; i++) {
                dst.append(words[i]);
            }
            dst.append('.');
            CharSequence suffix = words[words.length - 1];
            dst.append(suffix);
        }

        @Override
        public void format(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws NameFormatException {
            if (wordSpans.length == 0) {
                return;
            }
            try {
                format0(originalName, wordSpans, dst);
            } catch (Exception e) {
                throw new NameFormatException(e);
            }
        }

        private void format0(
            @Nonnull CharSequence originalName,
            @Nonnull Span @Nonnull [] wordSpans,
            @Nonnull Appendable dst
        ) throws Exception {
            if (wordSpans.length == 1) {
                Span first = wordSpans[0];
                appender.append(dst, originalName, first, 0);
                return;
            }
            if (wordSpans.length == 2) {
                Span prefix = wordSpans[0];
                Span suffix = wordSpans[1];
                appender.append(dst, originalName, prefix, 0);
                dst.append('.');
                appender.append(dst, originalName, suffix, 1);
                return;
            }
            for (int i = 0; i < wordSpans.length - 1; i++) {
                appender.append(dst, originalName, wordSpans[i], i);
            }
            dst.append('.');
            Span suffix = wordSpans[wordSpans.length - 1];
            appender.append(dst, originalName, suffix, wordSpans.length - 1);
        }
    }

    private static final class SimpleAppender implements NameFormatter.Appender {

        private static final @Nonnull NameFormatterBack.SimpleAppender SINGLETON = new SimpleAppender();

        @Override
        public void append(
            @Nonnull Appendable dst, @Nonnull CharSequence originalName, @Nonnull Span span, int index
        ) throws Exception {
            dst.append(originalName, span.startIndex(), span.endIndex());
        }
    }
}
