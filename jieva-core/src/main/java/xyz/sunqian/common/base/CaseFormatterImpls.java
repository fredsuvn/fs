package xyz.sunqian.common.base;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.collect.JieCollect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

final class CaseFormatterImpls {

    static final class CamelCaseFormatter implements CaseFormatter {

        private static final int LOWER = 0;
        private static final int UPPER = 1;
        private static final int OTHER = 2;

        private final boolean upperHead;

        CamelCaseFormatter(boolean upperHead) {
            this.upperHead = upperHead;
        }

        @Override
        public List<CharSequence> resolve(CharSequence name) {
            if (JieString.isBlank(name)) {
                return Collections.emptyList();
            }
            int len = name.length();
            if (len == 1) {
                return Collections.singletonList(name);
            }
            List<CharSequence> result = new LinkedList<>();
            int startIndex = 0;
            int lastCharType = getCharType(name.charAt(0));
            for (int i = 1; i < name.length(); i++) {
                char c = name.charAt(i);
                int currentCharType = getCharType(c);
                // AA, aa, 00: just i++
                if (lastCharType == currentCharType) {
                    continue;
                }
                // Aa: one token
                if (lastCharType == UPPER && currentCharType == LOWER) {
                    int endIndex = i - 1;
                    if (endIndex > startIndex) {
                        result.add(new Word(name, startIndex, endIndex));
                    }
                    startIndex = endIndex;
                }
                // aA: two tokens
                else if (lastCharType == LOWER && currentCharType == UPPER) {
                    if (i > startIndex) {
                        result.add(new Word(name, startIndex, i));
                    }
                    startIndex = i;
                }
                // A0: three tokens
                else if (lastCharType == UPPER && currentCharType == OTHER) {
                    // first token
                    int endIndex = i - 1;
                    if (endIndex > startIndex) {
                        result.add(new Word(name, startIndex, endIndex));
                    }
                    // second token: A
                    result.add(new Word(name, endIndex, i));
                    // third token
                    startIndex = i;
                }
                // 0A, a0, 0a: two words
                else {
                    if (i > startIndex) {
                        result.add(new Word(name, startIndex, i));
                    }
                    startIndex = i;
                }
                lastCharType = currentCharType;
            }
            if (startIndex < len) {
                result.add(new Word(name, startIndex, len));
            }
            return result;
        }

        private int getCharType(char c) {
            if (Character.isLowerCase(c)) {
                return LOWER;
            }
            if (Character.isUpperCase(c)) {
                return UPPER;
            }
            return OTHER;
        }

        @Override
        public String format(List<? extends CharSequence> wordList) {
            if (JieCollect.isEmpty(wordList)) {
                return "";
            }
            int length = 0;
            for (CharSequence chars : wordList) {
                length += chars.length();
            }
            StringBuilder sb = new StringBuilder(length);
            int i = 0;
            for (CharSequence chars : wordList) {
                if (JieString.isEmpty(chars)) {
                    continue;
                }
                buildCamel(chars, sb, i++);
            }
            return sb.toString();
        }

        private void buildCamel(CharSequence chars, StringBuilder builder, int i) {
            char first = chars.charAt(0);
            int type0 = getCharType(first);
            if (chars.length() == 1) {
                switch (type0) {
                    case OTHER: {
                        builder.append(first);
                        return;
                    }
                    case UPPER:
                    case LOWER: {
                        builder.append(i == 0 ? Character.toLowerCase(first) : Character.toUpperCase(first));
                        return;
                    }
                }
                return;
            }
            boolean formatted = true;
            boolean allUpper = true;
            boolean allOther = true;
            switch (type0) {
                case UPPER: {
                    allOther = false;
                    if (i == 0 && !upperHead) {
                        formatted = false;
                    }
                    break;
                }
                case LOWER: {
                    allOther = false;
                    allUpper = false;
                    if (i == 0 && upperHead) {
                        formatted = false;
                    }
                    break;
                }
                case OTHER: {
                    formatted = false;
                    allUpper = false;
                    break;
                }
            }
            if (!formatted && !allUpper && !allOther) {
                buildNewWord(chars, builder, i);
                return;
            }
            for (int j = 1; j < chars.length(); j++) {
                int typeJ = getCharType(chars.charAt(j));
                switch (typeJ) {
                    case UPPER: {
                        allOther = false;
                        formatted = false;
                        break;
                    }
                    case LOWER: {
                        allOther = false;
                        allUpper = false;
                        break;
                    }
                    case OTHER: {
                        formatted = false;
                        allUpper = false;
                        break;
                    }
                }
                if (!formatted && !allUpper && !allOther) {
                    break;
                }
            }
            if (formatted || allUpper || allOther) {
                builder.append(chars);
                return;
            }
            buildNewWord(chars, builder, i);
        }

        private void buildNewWord(CharSequence chars, StringBuilder builder, int i) {
            char first = chars.charAt(0);
            builder.append(i == 0 && !upperHead ? Character.toLowerCase(first) : Character.toUpperCase(first));
            for (int j = 1; j < chars.length(); j++) {
                builder.append(Character.toLowerCase(chars.charAt(j)));
            }
        }
    }

    static final class DelimiterCaseFormatter implements CaseFormatter {

        private final CharSequence delimiter;
        private final @Nullable Function<? super CharSequence, ? extends CharSequence> wordMapper;

        DelimiterCaseFormatter(
            CharSequence delimiter, @Nullable Function<? super CharSequence, ? extends CharSequence> wordMapper) {
            this.delimiter = delimiter;
            this.wordMapper = wordMapper;
        }

        @Override
        public List<CharSequence> resolve(CharSequence name) {
            return JieString.split(name, delimiter, Word::new);
        }

        @Override
        public String format(List<? extends CharSequence> wordList) {
            if (wordList.isEmpty()) {
                return "";
            }
            if (wordMapper == null) {
                return String.join(delimiter, wordList);
            }
            return wordList.stream().map(wordMapper).collect(Collectors.joining(delimiter));
        }
    }

    private static final class Word implements CharSequence {

        private final CharSequence source;
        private final int startIndex;
        private final int endIndex;

        private Word(CharSequence source, int startIndex, int endIndex) {
            this.source = source;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        public int length() {
            return endIndex - startIndex;
        }

        @Override
        public char charAt(int index) {
            return source.charAt(startIndex + index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return new Word(source, startIndex + start, startIndex + end);
        }

        @Override
        public String toString() {
            return source.subSequence(startIndex, endIndex).toString();
        }
    }
}
