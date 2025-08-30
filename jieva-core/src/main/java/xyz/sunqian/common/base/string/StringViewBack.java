package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.annotations.ValueClass;
import xyz.sunqian.common.base.CheckKit;
import xyz.sunqian.common.base.exception.UnreachablePointException;

final class StringViewBack {

    static @Nonnull StringView view(@Nonnull CharSequence @Nonnull @RetainedParam ... strings) {
        return new StringViewImpl(strings);
    }

    private static final class StringViewImpl implements StringView {

        private final @Nonnull CharSequence[] chars;
        private final int length;

        private StringViewImpl(@Nonnull CharSequence[] chars) {
            this.chars = chars;
            int c = 0;
            for (CharSequence aChar : chars) {
                c += aChar.length();
            }
            this.length = c;
        }

        @Override
        public int length() {
            return length;
        }

        @Override
        public char charAt(int index) {
            CheckKit.checkInBounds(index, 0, length);
            Node node = findNode(index);
            return chars[node.charsIndex].charAt(node.charIndex);
        }

        @Override
        public @Nonnull CharSequence subSequence(int start, int end) {
            CheckKit.checkRangeInBounds(start, end, 0, length);
            if (start == end) {
                Node node = findNode(start);
                return chars[node.charsIndex].subSequence(node.charIndex, node.charIndex);
            }
            Node startNode = findNode(start);
            Node endNode = findNode(end - 1);
            if (startNode.charsIndex == endNode.charsIndex) {
                return chars[startNode.charsIndex].subSequence(startNode.charIndex, endNode.charIndex + 1);
            }
            CharSequence[] subChars = new CharSequence[endNode.charsIndex - startNode.charsIndex + 1];
            CharSequence startCs = chars[startNode.charsIndex];
            subChars[0] = startCs.subSequence(startNode.charIndex, startCs.length());
            CharSequence endCs = chars[endNode.charsIndex];
            subChars[subChars.length - 1] = endCs.subSequence(0, endNode.charIndex + 1);
            for (int i = 1, j = startNode.charsIndex + 1; i < subChars.length - 1; i++, j++) {
                subChars[i] = chars[j];
            }
            return new StringViewImpl(subChars);
        }

        @Override
        public @Nonnull String toString() {
            StringBuilder sb = new StringBuilder(length);
            for (CharSequence cs : chars) {
                sb.append(cs);
            }
            return sb.toString();
        }

        private @Nonnull Node findNode(int index) {
            int c = 0;
            for (int i = 0; i < chars.length; i++) {
                CharSequence cs = chars[i];
                if (index < c + cs.length()) {
                    return new Node(i, index - c);
                }
                c += cs.length();
            }
            throw new UnreachablePointException("index: " + index);
        }

        @ValueClass
        private static final class Node {

            private final int charsIndex;
            private final int charIndex;

            private Node(int charsIndex, int charIndex) {
                this.charsIndex = charsIndex;
                this.charIndex = charIndex;
            }
        }
    }
}
