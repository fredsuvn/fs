package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class CharReaderImpl {

    static @Nonnull CharReader of(@Nonnull Reader src, int bufSize) throws IllegalArgumentException {
        return new CharStreamReader(src, bufSize);
    }

    static @Nonnull CharReader of(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return new CharArrayReader(src, off, len);
    }

    static @Nonnull CharReader of(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        return new CharSequenceReader(src, start, end);
    }

    static @Nonnull CharReader of(@Nonnull CharBuffer src) {
        return new CharBufferReader(src);
    }

    // static @Nonnull CharReader of(@Nonnull CharReader source, long readLimit) throws IllegalArgumentException {
    //     return new LimitedCharReader(source, readLimit);
    // }

    static final class CharSegmentImpl implements CharSegment {

        private static final @Nonnull CharSegmentImpl EMPTY_END = new CharSegmentImpl(JieChars.emptyBuffer(), true);
        private static final @Nonnull CharSegmentImpl EMPTY_SEG = new CharSegmentImpl(JieChars.emptyBuffer(), false);

        public static @Nonnull CharSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final @Nonnull CharBuffer data;
        private final boolean end;

        CharSegmentImpl(@Nonnull CharBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }

        @Override
        public @Nonnull CharBuffer data() {
            return data;
        }

        @Override
        public boolean end() {
            return end;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public @Nonnull CharSegment clone() {
            CharBuffer copy = CharBuffer.allocate(data.remaining());
            int pos = data.position();
            int limit = data.limit();
            copy.put(data);
            data.position(pos);
            data.limit(limit);
            copy.flip();
            return CharSegment.of(copy, end);
        }
    }

    private static final class CharStreamReader implements CharReader {

        private final @Nonnull Reader source;
        private final @Nonnull CharOperator operator;

        CharStreamReader(@Nonnull Reader src, int bufSize) throws IllegalArgumentException {
            this.source = src;
            this.operator = CharOperator.get(bufSize);
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            char[] chars = operator.read(source, len);
            if (chars == null) {
                return CharSegment.empty(true);
            }
            return CharSegment.of(CharBuffer.wrap(chars), chars.length == len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            try {
                return skip0(len);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long skip0(long size) throws Exception {
            long hasRead = 0;
            while (hasRead < size) {
                long onceSize = source.skip(size - hasRead);
                if (onceSize == 0) {
                    if (source.read() == -1) {
                        break;
                    } else {
                        hasRead++;
                    }
                }
                hasRead += onceSize;
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            return operator.readTo(source, dst, len);
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return operator.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return operator.readTo(source, dst, len);
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            try {
                source.mark(0);
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void reset() throws IORuntimeException {
            try {
                source.reset();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void close() throws IORuntimeException {
            try {
                source.close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }
    }

    private static final class CharArrayReader implements CharReader {

        private final char @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark;

        CharArrayReader(char @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            int remaining = endPos - pos;
            int actualLen = Math.min(remaining, len);
            CharBuffer data = CharBuffer.wrap(source, pos, actualLen).slice();
            pos += actualLen;
            return CharSegment.of(data, remaining <= len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return 0;
            }
            int remaining = endPos - pos;
            int skipped = (int) Math.min(remaining, len);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (endPos == pos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                JieIO.write(dst, source, pos, remaining);
                pos += remaining;
                return remaining;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (endPos == pos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                int actualLen = (int) Math.min(remaining, len);
                JieIO.write(dst, source, pos, actualLen);
                pos += actualLen;
                return actualLen;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public int readTo(char @Nonnull [] dst) {
            return readTo0(dst, 0, dst.length);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(dst.length, off, len);
            return readTo0(dst, off, len);
        }

        private int readTo0(char @Nonnull [] dst, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (endPos == pos) {
                return -1;
            }
            int remaining = endPos - pos;
            int copySize = Math.min(remaining, len);
            System.arraycopy(source, pos, dst, off, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int putSize = Math.min(remaining, dst.remaining());
            return putTo0(dst, putSize);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int putSize = Math.min(remaining, dst.remaining());
            return putTo0(dst, putSize);
        }

        private int putTo0(@Nonnull CharBuffer dst, int putSize) throws IORuntimeException {
            try {
                dst.put(source, pos, putSize);
                pos += putSize;
                return putSize;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() throws IORuntimeException {
            mark = pos;
        }

        @Override
        public void reset() throws IORuntimeException {
            pos = mark;
        }

        @Override
        public void close() throws IORuntimeException {
        }
    }

    private static final class CharSequenceReader implements CharReader {

        private final @Nonnull CharSequence source;
        private final int endPos;
        private int pos;
        private int mark;

        CharSequenceReader(@Nonnull CharSequence source, int start, int end) throws IndexOutOfBoundsException {
            JieCheck.checkStartEnd(source.length(), start, end);
            this.source = source;
            this.pos = start;
            this.endPos = end;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            int remaining = endPos - pos;
            int actualLen = Math.min(remaining, len);
            CharBuffer data = CharBuffer.wrap(source, pos, pos + actualLen).slice();
            pos += actualLen;
            return CharSegment.of(data, remaining <= len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return 0;
            }
            int remaining = endPos - pos;
            int skipped = (int) Math.min(remaining, len);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (endPos == pos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                dst.append(source, pos, pos + remaining);
                pos += remaining;
                return remaining;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (endPos == pos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                int actualLen = (int) Math.min(remaining, len);
                dst.append(source, pos, pos + actualLen);
                pos += actualLen;
                return actualLen;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public int readTo(char @Nonnull [] dst) {
            return readTo0(dst, 0, dst.length);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(dst.length, off, len);
            return readTo0(dst, off, len);
        }

        private int readTo0(char @Nonnull [] dst, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (endPos == pos) {
                return -1;
            }
            int remaining = endPos - pos;
            int copySize = Math.min(remaining, len);
            JieString.charsCopy(source, pos, dst, off, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int putSize = Math.min(remaining, dst.remaining());
            return putTo0(dst, putSize);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int putSize = Math.min(remaining, dst.remaining());
            return putTo0(dst, putSize);
        }

        private int putTo0(@Nonnull CharBuffer dst, int putSize) throws IORuntimeException {
            try {
                dst.put(CharBuffer.wrap(source, pos, pos + putSize));
                pos += putSize;
                return putSize;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() throws IORuntimeException {
            mark = pos;
        }

        @Override
        public void reset() throws IORuntimeException {
            pos = mark;
        }

        @Override
        public void close() throws IORuntimeException {
        }
    }

    private static final class CharBufferReader implements CharReader {

        private final @Nonnull CharBuffer source;

        CharBufferReader(@Nonnull CharBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (!source.hasRemaining()) {
                return CharSegment.empty(true);
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + len, limit);
            source.limit(newPos);
            CharBuffer data = source.slice();
            source.position(newPos);
            source.limit(limit);
            return CharSegment.of(data, newPos >= limit);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (!source.hasRemaining()) {
                return 0;
            }
            int pos = source.position();
            int newPos = (int) Math.min(pos + len, source.limit());
            source.position(newPos);
            return newPos - pos;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return JieBuffer.readTo(source, dst, actualLen);
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return JieBuffer.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return JieBuffer.readTo(source, dst, len);
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() throws IORuntimeException {
            source.mark();
        }

        @Override
        public void reset() throws IORuntimeException {
            source.reset();
        }

        @Override
        public void close() throws IORuntimeException {
        }
    }
}
