package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.math.MathKit;
import xyz.sunqian.common.base.string.StringKit;

import java.io.Reader;
import java.nio.CharBuffer;
import java.util.Arrays;

final class CharReaderImpl {

    static @Nonnull CharReader of(@Nonnull Reader src, int bufSize) throws IllegalArgumentException {
        IOHelper.checkBufSize(bufSize);
        return new CharStreamReader(src, bufSize);
    }

    static @Nonnull CharReader of(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        IOHelper.checkOffLen(src.length, off, len);
        return new CharArrayReader(src, off, len);
    }

    static @Nonnull CharReader of(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        IOHelper.checkStartEnd(src.length(), start, end);
        return new CharSequenceReader(src, start, end);
    }

    static @Nonnull CharReader of(@Nonnull CharBuffer src) {
        return new CharBufferReader(src);
    }

    static @Nonnull CharReader limit(@Nonnull CharReader reader, long limit) throws IllegalArgumentException {
        IOHelper.checkLimit(limit);
        return new LimitedReader(reader, limit);
    }

    static @Nonnull CharSegment newSeg(@Nonnull CharBuffer data, boolean end) {
        return new CharSegmentImpl(data, end);
    }

    static @Nonnull CharSegment emptySeg(boolean end) {
        return end ? CharSegmentImpl.EMPTY_END : CharSegmentImpl.EMPTY_SEG;
    }

    private static final class CharSegmentImpl implements CharSegment {

        private static final @Nonnull CharSegmentImpl EMPTY_END = new CharSegmentImpl(CharsKit.emptyBuffer(), true);
        private static final @Nonnull CharSegmentImpl EMPTY_SEG = new CharSegmentImpl(CharsKit.emptyBuffer(), false);

        private final @Nonnull CharBuffer data;
        private final boolean end;

        private CharSegmentImpl(@Nonnull CharBuffer data, boolean end) {
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

        private final @Nonnull Reader src;
        private final int bufSize;

        private CharStreamReader(@Nonnull Reader src, int bufSize) {
            this.src = src;
            this.bufSize = bufSize;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return CharSegment.empty(false);
            }
            IOHelper.checkLen(len);
            char[] data = new char[len];
            int readSize = IOOperations.readTo0(src, data, 0, data.length);
            if (readSize < 0) {
                return CharSegment.empty(true);
            }
            data = readSize == len ? data : Arrays.copyOfRange(data, 0, readSize);
            return CharSegment.of(CharBuffer.wrap(data), readSize < len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            try {
                return skip0(len);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long skip0(long len) throws Exception {
            long hasRead = 0;
            while (hasRead < len) {
                long onceSize = src.skip(len - hasRead);
                if (onceSize == 0) {
                    if (src.read() == -1) {
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
            return IOOperations.readTo0(src, dst, bufSize);
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            return IOOperations.readTo0(src, dst, len, bufSize);
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return IOOperations.readTo0(src, dst, 0, dst.length);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            IOHelper.checkOffLen(dst.length, off, len);
            return IOOperations.readTo0(src, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return IOOperations.readTo0(src, dst, dst.remaining());
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            return IOOperations.readTo0(src, dst, len);
        }

        @Override
        public boolean markSupported() {
            return src.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            try {
                src.mark(Integer.MAX_VALUE);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void reset() throws IORuntimeException {
            try {
                src.reset();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void close() throws IORuntimeException {
            try {
                src.close();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public @Nonnull Reader asReader() {
            return src;
        }
    }

    private static final class CharArrayReader implements CharReader {

        private final char @Nonnull [] src;
        private int pos;
        private final int end;
        private int mark;

        private CharArrayReader(char @Nonnull [] src, int offset, int length) {
            this.src = src;
            this.pos = offset;
            this.end = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos == end) {
                return CharSegment.empty(true);
            }
            int remaining = end - pos;
            int actualLen = Math.min(remaining, len);
            CharBuffer data = CharBuffer.wrap(src, pos, actualLen).slice();
            pos += actualLen;
            return CharSegment.of(data, remaining <= len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException {
            IOHelper.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            if (pos == end) {
                return 0;
            }
            int remaining = end - pos;
            int skipped = (int) Math.min(remaining, len);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (end == pos) {
                return -1;
            }
            try {
                int remaining = end - pos;
                IOKit.write(dst, src, pos, remaining);
                pos += remaining;
                return remaining;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (end == pos) {
                return -1;
            }
            try {
                int remaining = end - pos;
                int actualLen = (int) Math.min(remaining, len);
                IOKit.write(dst, src, pos, actualLen);
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
            IOHelper.checkOffLen(dst.length, off, len);
            return readTo0(dst, off, len);
        }

        private int readTo0(char @Nonnull [] dst, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (end == pos) {
                return -1;
            }
            int remaining = end - pos;
            int copySize = Math.min(remaining, len);
            System.arraycopy(src, pos, dst, off, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == end) {
                return -1;
            }
            int remaining = end - pos;
            int putSize = Math.min(remaining, dst.remaining());
            return putTo0(dst, putSize);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (dst.remaining() == 0) {
                return 0;
            }
            if (pos == end) {
                return -1;
            }
            int remaining = end - pos;
            int putSize = MathKit.min(remaining, dst.remaining(), len);
            return putTo0(dst, putSize);
        }

        private int putTo0(@Nonnull CharBuffer dst, int putSize) throws IORuntimeException {
            try {
                dst.put(src, pos, putSize);
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
        public void mark() {
            mark = pos;
        }

        @Override
        public void reset() {
            pos = mark;
        }

        @Override
        public void close() {
        }
    }

    private static final class CharSequenceReader implements CharReader {

        private final @Nonnull CharSequence source;
        private final int endPos;
        private int pos;
        private int mark;

        private CharSequenceReader(@Nonnull CharSequence source, int start, int end) {
            this.source = source;
            this.pos = start;
            this.endPos = end;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
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
        public long skip(long len) throws IllegalArgumentException {
            IOHelper.checkSkip(len);
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
            IOHelper.checkLen(len);
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
            IOHelper.checkOffLen(dst.length, off, len);
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
            StringKit.charsCopy(source, pos, dst, off, copySize);
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
            IOHelper.checkLen(len);
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
            int putSize = MathKit.min(remaining, dst.remaining(), len);
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
        public void mark() {
            mark = pos;
        }

        @Override
        public void reset() {
            pos = mark;
        }

        @Override
        public void close() {
        }
    }

    private static final class CharBufferReader implements CharReader {

        private final @Nonnull CharBuffer src;

        private CharBufferReader(@Nonnull CharBuffer src) {
            this.src = src;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (!src.hasRemaining()) {
                return CharSegment.empty(true);
            }
            int pos = src.position();
            int limit = src.limit();
            int newPos = Math.min(pos + len, limit);
            src.limit(newPos);
            CharBuffer data = src.slice();
            src.position(newPos);
            src.limit(limit);
            return CharSegment.of(data, newPos >= limit);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            if (!src.hasRemaining()) {
                return 0;
            }
            int pos = src.position();
            int newPos = (int) Math.min(pos + len, src.limit());
            src.position(newPos);
            return newPos - pos;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo0(src, dst, actualLen);
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return BufferKit.readTo(src, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return BufferKit.readTo(src, dst, len);
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() throws IORuntimeException {
            src.mark();
        }

        @Override
        public void reset() throws IORuntimeException {
            try {
                src.reset();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void close() throws IORuntimeException {
        }
    }

    private static final class LimitedReader implements CharReader {

        private final @Nonnull CharReader source;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedReader(@Nonnull CharReader source, long limit) {
            this.source = source;
            this.limit = limit;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos >= limit) {
                return CharSegment.empty(true);
            }
            int actualLen = (int) Math.min(len, limit - pos);
            CharSegment segment = source.read(actualLen);
            pos += segment.data().remaining();
            if (actualLen < len) {
                if (!segment.end()) {
                    return newSeg(segment.data(), true);
                }
            }
            return segment;
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return 0;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            long skipped = source.skip(actualLen);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (pos >= limit) {
                return -1;
            }
            long readSize = source.readTo(dst, limit - pos);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            long readSize = source.readTo(dst, actualLen);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst, 0, dst.length);
        }

        @Override
        public int readTo(
            char @Nonnull [] dst, int off, int len
        ) throws IndexOutOfBoundsException, IORuntimeException {
            IOHelper.checkOffLen(dst.length, off, len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            int readSize = source.readTo(dst, off, actualLen);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return readTo(dst, dst.remaining());
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOHelper.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (!dst.hasRemaining()) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            actualLen = Math.min(actualLen, dst.remaining());
            int readSize = source.readTo(dst, actualLen);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            source.mark();
            mark = pos;
        }

        @Override
        public void reset() throws IORuntimeException {
            source.reset();
            pos = mark;
        }

        @Override
        public void close() throws IORuntimeException {
            source.close();
        }
    }
}
