package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

final class CharReaderImpl {

    static @Nonnull CharReader of(@Nonnull Reader source) {
        return new CharStreamReader(source);
    }

    static @Nonnull CharReader of(char @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
        return new CharArrayReader(source, offset, length);
    }

    static @Nonnull CharReader of(@Nonnull CharSequence source, int start, int end) throws IndexOutOfBoundsException {
        return new CharSequenceReader(source, start, end);
    }

    static @Nonnull CharReader of(@Nonnull CharBuffer source) {
        return new CharBufferReader(source);
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

        CharStreamReader(@Nonnull Reader source) {
            this.source = source;
        }

        @Override
        public @Nonnull CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return CharSegment.empty(false);
            }
            return Jie.uncheck(() -> read0(size), IORuntimeException::new);
        }

        private @Nonnull CharSegment read0(int size) throws Exception {
            boolean end = false;
            int hasRead = 0;
            char[] buf = new char[size];
            while (hasRead < size) {
                int onceSize = source.read(buf, hasRead, size - hasRead);
                if (onceSize < 0) {
                    end = true;
                    break;
                }
                // if (onceSize == 0) {
                //     if (endOnZeroRead) {
                //         end = true;
                //         break;
                //     } else {
                //         continue;
                //     }
                // }
                hasRead += onceSize;
            }
            if (hasRead == 0) {
                return CharSegment.empty(true);
            }
            CharBuffer data = CharBuffer.wrap(
                hasRead == size ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            );
            return CharSegment.of(data, end);
        }

        @Override
        public long skip(long size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return 0;
            }
            return Jie.uncheck(() -> skip0(size), IORuntimeException::new);
        }

        private long skip0(long size) throws Exception {
            long hasRead = 0;
            while (hasRead < size) {
                long onceSize = source.skip(size - hasRead);
                if (onceSize == 0) {
                    // if (endOnZeroRead) {
                    //     break;
                    // }
                    // tests whether reaches the end
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
        public int readTo(
            char @Nonnull [] dest, int offset, int length
        ) throws IndexOutOfBoundsException, IORuntimeException {
            JieCheck.checkOffsetLength(dest.length, offset, length);
            if (length == 0) {
                return 0;
            }
            return Jie.uncheck(() -> readTo0(dest, offset, length), IORuntimeException::new);
        }

        private int readTo0(char @Nonnull [] dest, int offset, int length) throws IOException {
            int hasRead = 0;
            while (hasRead < length) {
                int c = source.read(dest, offset + hasRead, length - hasRead);
                if (c < 0) {
                    return hasRead == 0 ? -1 : hasRead;
                }
                hasRead += c;
            }
            return hasRead;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dest) throws IORuntimeException {
            if (dest.remaining() == 0) {
                return 0;
            }
            if (dest.hasArray()) {
                int ret = readTo(dest.array(), dest.arrayOffset() + dest.position(), dest.remaining());
                if (ret > 0) {
                    dest.position(dest.position() + ret);
                }
                return ret;
            }
            char[] buf = new char[dest.remaining()];
            int hasRead = readTo(buf);
            if (hasRead > 0) {
                dest.put(buf, 0, hasRead);
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull Appendable dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            return Jie.uncheck(() -> readTo0(dest, length), IORuntimeException::new);
        }

        private long readTo0(@Nonnull Appendable dest, long length) throws IOException {
            long count = 0;
            int bufferSize = JieIO.bufferSize();
            char[] buf = new char[length < 0 ? bufferSize : (int) Math.min(length - count, bufferSize)];
            while (true) {
                int len = (int) (length < 0 ? buf.length : Math.min(length - count, buf.length));
                int c = source.read(buf, 0, len);
                if (c < 0) {
                    return count == 0 ? -1 : count;
                }
                JieIO.write(dest, buf, 0, c);
                count += c;
                if (count == length) {
                    return count;
                }
            }
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
        public @Nonnull CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return CharSegment.empty(false);
            }
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                CharBuffer data = CharBuffer.wrap(source, pos, size).slice();
                pos += size;
                return CharSegment.of(data, remaining == size);
            }
            CharBuffer data = CharBuffer.wrap(source, pos, remaining).slice();
            pos += remaining;
            return CharSegment.of(data, true);
        }

        @Override
        public long skip(long size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return 0;
            }
            if (pos == endPos) {
                return 0;
            }
            int remaining = endPos - pos;
            int skipped = (int) Math.min(remaining, size);
            pos += skipped;
            return skipped;
        }

        @Override
        public int readTo(
            char @Nonnull [] dest, int offset, int length
        ) throws IndexOutOfBoundsException, IORuntimeException {
            JieCheck.checkOffsetLength(dest.length, offset, length);
            if (length == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            int copySize = Math.min(remaining, length);
            System.arraycopy(source, pos, dest, offset, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dest) throws IORuntimeException {
            if (dest.remaining() == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            int copySize = Math.min(remaining, dest.remaining());
            dest.put(source, pos, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public long readTo(@Nonnull Appendable dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            return Jie.uncheck(() -> readTo0(dest, length, remaining), IORuntimeException::new);
        }

        private long readTo0(@Nonnull Appendable dest, long length, int remaining) throws IOException {
            int actual = length < 0 ? remaining : (int) Math.min(remaining, length);
            JieIO.write(dest, source, pos, actual);
            pos += actual;
            return actual;
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
        public @Nonnull CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return CharSegment.empty(false);
            }
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                CharBuffer data = CharBuffer.wrap(source, pos, pos + size).slice();
                pos += size;
                return CharSegment.of(data, false);
            }
            CharBuffer data = CharBuffer.wrap(source, pos, pos + remaining).slice();
            pos += remaining;
            return CharSegment.of(data, true);
        }

        @Override
        public int readTo(
            char @Nonnull [] dest, int offset, int length
        ) throws IndexOutOfBoundsException, IORuntimeException {
            JieCheck.checkOffsetLength(dest.length, offset, length);
            if (length == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            int copySize = Math.min(remaining, length);
            source.toString().getChars(pos, pos + copySize, dest, offset);
            // System.arraycopy(source, pos, dest, offset, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dest) throws IORuntimeException {
            if (dest.remaining() == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            int copySize = Math.min(remaining, dest.remaining());
            dest.put(CharBuffer.wrap(source, pos, pos + copySize));
            pos += copySize;
            return copySize;
        }

        @Override
        public long readTo(@Nonnull Appendable dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            return Jie.uncheck(() -> readTo0(dest, length, remaining), IORuntimeException::new);
        }

        private long readTo0(@Nonnull Appendable dest, long length, int remaining) throws IOException {
            int actual = length < 0 ? remaining : (int) Math.min(remaining, length);
            dest.append(source, pos, pos + actual);
            pos += actual;
            return actual;
        }

        @Override
        public long skip(long size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return 0;
            }
            if (pos == endPos) {
                return 0;
            }
            int remaining = endPos - pos;
            int skipped = (int) Math.min(remaining, size);
            pos += skipped;
            return skipped;
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
        public @Nonnull CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return CharSegment.empty(false);
            }
            if (!source.hasRemaining()) {
                return CharSegment.empty(true);
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + size, limit);
            source.limit(newPos);
            CharBuffer data = source.slice();
            source.position(newPos);
            source.limit(limit);
            return CharSegment.of(data, newPos >= limit);
        }

        @Override
        public long skip(long size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return 0;
            }
            if (!source.hasRemaining()) {
                return 0;
            }
            int pos = source.position();
            int newPos = (int) Math.min(pos + size, source.limit());
            source.position(newPos);
            return newPos - pos;
        }

        @Override
        public int readTo(
            char @Nonnull [] dest, int offset, int length
        ) throws IndexOutOfBoundsException, IORuntimeException {
            JieCheck.checkOffsetLength(dest.length, offset, length);
            if (length == 0) {
                return 0;
            }
            if (source.remaining() == 0) {
                return -1;
            }
            int copySize = Math.min(source.remaining(), length);
            source.get(dest, offset, copySize);
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dest) throws IORuntimeException {
            if (dest.remaining() == 0) {
                return 0;
            }
            if (source.remaining() == 0) {
                return -1;
            }
            int copySize = Math.min(source.remaining(), dest.remaining());
            if (copySize == source.remaining()) {
                dest.put(source);
            } else {
                CharBuffer src = JieBuffer.slice(source, copySize);
                dest.put(src);
                source.position(source.position() + copySize);
            }
            return copySize;
        }

        @Override
        public long readTo(@Nonnull Appendable dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            if (source.remaining() == 0) {
                return -1;
            }
            return Jie.uncheck(() -> readTo0(dest, length), IORuntimeException::new);
        }

        private long readTo0(@Nonnull Appendable dest, long length) throws IOException {
            int copySize = (int) (length < 0 ? source.remaining() : Math.min(source.remaining(), length));
            if (source.hasArray()) {
                JieIO.write(dest, source.array(), source.arrayOffset() + source.position(), copySize);
                source.position(source.position() + copySize);
            } else {
                char[] data = JieBuffer.read(source, copySize);
                JieIO.write(dest, data, 0, data.length);
            }
            return copySize;
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

    // private static final class LimitedCharReader implements CharReader {
    //
    //     private final @Nonnull CharReader source;
    //     private long remaining;
    //     private long mark;
    //
    //     LimitedCharReader(@Nonnull CharReader source, long readLimit) throws IllegalArgumentException {
    //         IOChecker.checkReadLimit(readLimit);
    //         this.source = source;
    //         this.remaining = readLimit;
    //     }
    //
    //     @Override
    //     public @Nonnull CharSegment read(int size) throws IllegalArgumentException, IORuntimeException {
    //         IOChecker.checkSize(size);
    //         if (remaining <= 0) {
    //             return CharSegment.empty(true);
    //         }
    //         if (size == 0) {
    //             return CharSegment.empty(false);
    //         }
    //         int readSize = (int) Math.min(size, remaining);
    //         CharSegment seg = source.read(readSize);
    //         remaining -= readSize;
    //         if (remaining <= 0 && !seg.end()) {
    //             return makeTrue(seg);
    //         }
    //         return seg;
    //     }
    //
    //     @Override
    //     public long skip(long size) throws IllegalArgumentException, IORuntimeException {
    //         IOChecker.checkSize(size);
    //         if (remaining <= 0) {
    //             return 0;
    //         }
    //         if (size == 0) {
    //             return 0;
    //         }
    //         long readSize = Math.min(size, remaining);
    //         long skipped = source.skip(readSize);
    //         remaining -= skipped;
    //         return skipped;
    //     }
    //
    //     @Override
    //     public boolean markSupported() {
    //         return source.markSupported();
    //     }
    //
    //     @Override
    //     public void mark() throws IORuntimeException {
    //         if (source.markSupported()) {
    //             source.mark();
    //             mark = remaining;
    //         }
    //     }
    //
    //     @Override
    //     public void reset() throws IORuntimeException {
    //         if (source.markSupported()) {
    //             source.reset();
    //             remaining = mark;
    //         }
    //     }
    //
    //     @Override
    //     public void close() throws IORuntimeException {
    //         source.close();
    //     }
    //
    //     private @Nonnull CharSegment makeTrue(@Nonnull CharSegment seg) {
    //         if (seg instanceof CharSegmentImpl) {
    //             ((CharSegmentImpl) seg).end = true;
    //             return seg;
    //         }
    //         return CharSegment.of(seg.data(), true);
    //     }
    // }
}
