package space.sunqian.fs.base.chars;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.io.IOException;

final class SimpleAppender implements Appendable {

    private static final int SEG_SIZE = 1024;

    private final int segSize;

    private final @Nonnull Chars head;
    private @Nonnull Chars tail;

    SimpleAppender() {
        this(SEG_SIZE);
    }

    SimpleAppender(int segSize) {
        this.segSize = segSize;
        Chars first = new Chars(new char[segSize]);
        this.head = first;
        this.tail = first;
    }

    @Override
    public Appendable append(@Nullable CharSequence csq) throws IOException {
        CharSequence cs = asNonnull(csq);
        return append0(cs, 0, cs.length());
    }

    @Override
    public Appendable append(@Nullable CharSequence csq, int start, int end) throws IOException {
        CharSequence cs = asNonnull(csq);
        return append0(cs, start, end);
    }

    @Override
    public Appendable append(char c) throws IOException {
        int appendedSize = tail.append(c);
        if (appendedSize == 1) {
            return this;
        }
        Chars next = new Chars(new char[segSize]);
        tail.setNext(next);
        tail = next;
        tail.append(c);
        return this;
    }

    private Appendable append0(@Nonnull CharSequence csq, int start, int end) throws IOException {
        String str = csq.toString();
        int appendedSize = tail.append(str, start, end);
        int totalSize = end - start;
        if (appendedSize == totalSize) {
            return this;
        }
        // needs to new chars
        int actualStart = start + appendedSize;
        while (true) {
            Chars next = new Chars(new char[segSize]);
            tail.setNext(next);
            tail = next;
            appendedSize = tail.append(str, actualStart, end);
            actualStart += appendedSize;
            if (actualStart >= end) {
                return this;
            }
        }
    }

    private @Nonnull CharSequence asNonnull(@Nullable CharSequence csq) {
        return csq == null ? "null" : csq;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public @Nonnull String toString() {
        int len = 0;
        Chars cur = head;
        while (cur != tail) {
            len += segSize;
            cur = cur.next();
        }
        len += tail.pos();
        char[] chars = new char[len];
        cur = head;
        int curPos = 0;
        while (cur != tail) {
            System.arraycopy(cur.chars(), 0, chars, curPos, segSize);
            curPos += segSize;
        }
        System.arraycopy(tail.chars(), 0, chars, curPos, tail.pos());
        return new String(chars);
    }

    private static final class Chars {

        private final char @Nonnull [] chars;
        private int pos = 0;
        private @Nullable Chars next;

        private Chars(char @Nonnull [] chars) {
            this.chars = chars;
        }

        public int append(@Nonnull String csq, int start, int end) {
            int appendedSize = Math.min(end - start, chars.length - pos);
            if (appendedSize <= 0) {
                return 0;
            }
            csq.getChars(start, start + appendedSize, chars, pos);
            pos += appendedSize;
            return appendedSize;
        }

        public int append(char c) {
            if (pos >= chars.length) {
                return 0;
            }
            chars[pos++] = c;
            return 1;
        }

        public char @Nonnull [] chars() {
            return chars;
        }

        public @Nullable Chars next() {
            return next;
        }

        public void setNext(@Nullable Chars next) {
            this.next = next;
        }

        public int pos() {
            return pos;
        }
    }
}
