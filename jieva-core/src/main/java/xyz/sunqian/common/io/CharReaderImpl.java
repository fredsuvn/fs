package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;
import xyz.sunqian.common.base.math.MathKit;
import xyz.sunqian.common.base.string.StringKit;
import xyz.sunqian.common.io.IOChecker.ReadChecker;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class CharReaderImpl {

    static @Nonnull CharReader of(@Nonnull Reader src, int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return new CharStreamReader(src, bufSize);
    }

    static @Nonnull CharReader of(char @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.length, off, len);
        return new CharArrayReader(src, off, len);
    }

    static @Nonnull CharReader of(@Nonnull CharSequence src, int start, int end) throws IndexOutOfBoundsException {
        IOChecker.checkStartEnd(src.length(), start, end);
        return new CharSequenceReader(src, start, end);
    }

    static @Nonnull CharReader of(@Nonnull CharBuffer src) {
        return new CharBufferReader(src);
    }

    static @Nonnull CharReader limit(@Nonnull CharReader reader, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedReader(reader, limit);
    }

    static @Nonnull CharSegment newSeg(@Nonnull CharBuffer data, boolean end) {
        return new CharSegmentImpl(data, end);
    }

    static @Nonnull CharSegment emptySeg(boolean end) {
        return end ? CharSegmentImpl.EMPTY_END : CharSegmentImpl.EMPTY_SEG;
    }

    static @Nonnull Reader asReader(@Nonnull CharReader reader) {
        return new AsReader(reader);
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
            IOChecker.checkLen(len);
            char[] data = IOKit.read0(src, len, bufSize, IOChecker.endChecker());
            if (data == null) {
                return CharSegment.empty(true);
            }
            return CharSegment.of(CharBuffer.wrap(data), data.length < len);
        }

        @Override
        public @Nullable CharBuffer read() throws IORuntimeException {
            char[] buf = IOKit.read0(src, bufSize, IOChecker.endChecker());
            if (buf == null) {
                return null;
            }
            return CharBuffer.wrap(buf);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSkip(len);
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
            return readTo(dst, IOChecker.endChecker());
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len, IOChecker.endChecker());
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst, IOChecker.endChecker());
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return readTo(dst, off, len, IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return readTo(dst, IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len, IOChecker.endChecker());
        }

        @Override
        public @Nonnull CharSegment available(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            char[] data = IOKit.read0(src, len, bufSize, IOChecker.availableChecker());
            if (data == null) {
                return CharSegment.empty(true);
            }
            return CharSegment.of(CharBuffer.wrap(data), false);
        }

        @Override
        public @Nonnull CharSegment available() throws IORuntimeException {
            char[] data = IOKit.read0(src, bufSize, IOChecker.availableChecker());
            if (data == null) {
                return CharSegment.empty(true);
            }
            return CharSegment.of(CharBuffer.wrap(data), false);
        }

        @Override
        public long availableTo(@Nonnull Appendable dst) throws IORuntimeException {
            return readTo(dst, IOChecker.availableChecker());
        }

        @Override
        public long availableTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len, IOChecker.availableChecker());
        }

        @Override
        public int availableTo(char @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst, IOChecker.availableChecker());
        }

        @Override
        public int availableTo(
            char @Nonnull [] dst, int off, int len
        ) throws IndexOutOfBoundsException, IORuntimeException {
            return readTo(dst, off, len, IOChecker.availableChecker());
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return readTo(dst, IOChecker.availableChecker());
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len, IOChecker.availableChecker());
        }

        private long readTo(@Nonnull Appendable dst, ReadChecker readChecker) throws IORuntimeException {
            return IOKit.readTo0(src, dst, bufSize, readChecker);
        }

        private long readTo(
            @Nonnull Appendable dst, long len, ReadChecker readChecker
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, bufSize, readChecker);
        }

        private int readTo(char @Nonnull [] dst, ReadChecker readChecker) throws IORuntimeException {
            return IOKit.readTo0(src, dst, 0, dst.length, readChecker);
        }

        private int readTo(
            char @Nonnull [] dst, int off, int len, ReadChecker readChecker
        ) throws IndexOutOfBoundsException, IORuntimeException {
            IOChecker.checkOffLen(dst.length, off, len);
            return IOKit.readTo0(src, dst, off, len, readChecker);
        }

        private int readTo(@Nonnull CharBuffer dst, ReadChecker readChecker) throws IORuntimeException {
            return IOKit.readTo0(src, dst, dst.remaining(), readChecker);
        }

        private int readTo(
            @Nonnull CharBuffer dst, int len, ReadChecker readChecker
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, readChecker);
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

    private static abstract class InMemoryReader implements CharReader {

        @Override
        public @Nonnull CharSegment available(int len) throws IllegalArgumentException, IORuntimeException {
            return read(len);
        }

        @Override
        public long availableTo(@Nonnull Appendable dst) throws IORuntimeException {
            return readTo(dst);
        }

        @Override
        public long availableTo(
            @Nonnull Appendable dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len);
        }

        @Override
        public int availableTo(char @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst);
        }

        @Override
        public int availableTo(
            char @Nonnull [] dst, int off, int len
        ) throws IndexOutOfBoundsException, IORuntimeException {
            return readTo(dst, off, len);
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return readTo(dst);
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return readTo(dst, len);
        }
    }

    private static final class CharArrayReader extends InMemoryReader {

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
            IOChecker.checkLen(len);
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
        public @Nullable CharBuffer read() throws IORuntimeException {
            if (pos >= end) {
                return null;
            }
            CharBuffer ret = CharBuffer.wrap(src, pos, end - pos);
            pos = end;
            return ret;
        }

        @Override
        public long skip(long len) throws IllegalArgumentException {
            IOChecker.checkSkip(len);
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
            IOChecker.checkLen(len);
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
            IOChecker.checkOffLen(dst.length, off, len);
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
            IOChecker.checkLen(len);
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

        @Override
        public @Nonnull CharSegment available() throws IORuntimeException {
            return read(end - pos);
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

    private static final class CharSequenceReader extends InMemoryReader {

        private final @Nonnull CharSequence src;
        private final int endPos;
        private int pos;
        private int mark;

        private CharSequenceReader(@Nonnull CharSequence src, int start, int end) {
            this.src = src;
            this.pos = start;
            this.endPos = end;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            int remaining = endPos - pos;
            int actualLen = Math.min(remaining, len);
            CharBuffer data = CharBuffer.wrap(src, pos, pos + actualLen).slice();
            pos += actualLen;
            return CharSegment.of(data, remaining <= len);
        }

        @Override
        public @Nullable CharBuffer read() throws IORuntimeException {
            if (pos >= endPos) {
                return null;
            }
            CharBuffer ret = CharBuffer.wrap(src, pos, endPos);
            pos = endPos;
            return ret;
        }

        @Override
        public long skip(long len) throws IllegalArgumentException {
            IOChecker.checkSkip(len);
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
                dst.append(src, pos, pos + remaining);
                pos += remaining;
                return remaining;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (endPos == pos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                int actualLen = (int) Math.min(remaining, len);
                dst.append(src, pos, pos + actualLen);
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
            IOChecker.checkOffLen(dst.length, off, len);
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
            StringKit.charsCopy(src, pos, dst, off, copySize);
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
            IOChecker.checkLen(len);
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

        @Override
        public @Nonnull CharSegment available() throws IORuntimeException {
            return read(endPos - pos);
        }

        private int putTo0(@Nonnull CharBuffer dst, int putSize) throws IORuntimeException {
            try {
                dst.put(CharBuffer.wrap(src, pos, pos + putSize));
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

    private static final class CharBufferReader extends InMemoryReader {

        private final @Nonnull CharBuffer src;

        private CharBufferReader(@Nonnull CharBuffer src) {
            this.src = src;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
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
        public @Nullable CharBuffer read() throws IORuntimeException {
            if (!src.hasRemaining()) {
                return null;
            }
            CharBuffer ret = src.slice();
            src.position(src.limit());
            return ret;
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSkip(len);
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
            IOChecker.checkLen(len);
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
        public @Nonnull CharSegment available() throws IORuntimeException {
            return read(src.remaining());
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

        private final @Nonnull CharReader src;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedReader(@Nonnull CharReader src, long limit) {
            this.src = src;
            this.limit = limit;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            return read(len, false);
        }

        @Override
        public @Nullable CharBuffer read() throws IORuntimeException {
            if (pos >= limit) {
                return null;
            }
            int len = MathKit.intValue(limit - pos);
            return read(len).data();
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return 0;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            long skipped = src.skip(actualLen);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (pos >= limit) {
                return -1;
            }
            long readSize = src.readTo(dst, limit - pos);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            long readSize = src.readTo(dst, actualLen);
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
            IOChecker.checkOffLen(dst.length, off, len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            int readSize = src.readTo(dst, off, actualLen);
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
            IOChecker.checkLen(len);
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
            int readSize = src.readTo(dst, actualLen);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public @Nonnull CharSegment available(int len) throws IllegalArgumentException, IORuntimeException {
            return read(len, true);
        }

        @Override
        public @Nonnull CharSegment available() throws IORuntimeException {
            return available(MathKit.intValue(limit - pos));
        }

        @Override
        public long availableTo(@Nonnull Appendable dst) throws IORuntimeException {
            if (pos >= limit) {
                return -1;
            }
            long readSize = src.availableTo(dst, limit - pos);
            if (readSize <= 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public long availableTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            long readSize = src.availableTo(dst, actualLen);
            if (readSize <= 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public int availableTo(char @Nonnull [] dst) throws IORuntimeException {
            return availableTo(dst, 0, dst.length);
        }

        @Override
        public int availableTo(
            char @Nonnull [] dst, int off, int len
        ) throws IndexOutOfBoundsException, IORuntimeException {
            IOChecker.checkOffLen(dst.length, off, len);
            if (len == 0) {
                return 0;
            }
            if (pos >= limit) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit - pos);
            int readSize = src.availableTo(dst, off, actualLen);
            if (readSize <= 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return availableTo(dst, dst.remaining());
        }

        @Override
        public int availableTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
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
            int readSize = src.availableTo(dst, actualLen);
            if (readSize <= 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
        }

        private @Nonnull CharSegment read(int len, boolean available) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return CharSegment.empty(false);
            }
            if (pos >= limit) {
                return CharSegment.empty(true);
            }
            int maxLen = (int) Math.min(len, limit - pos);
            CharSegment segment = available ? src.available(maxLen) : src.read(maxLen);
            pos += segment.data().remaining();
            if (!segment.end()) {
                if (pos >= limit) {
                    return newSeg(segment.data(), true);
                }
            }
            return segment;
        }

        @Override
        public boolean markSupported() {
            return src.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            src.mark();
            mark = pos;
        }

        @Override
        public void reset() throws IORuntimeException {
            src.reset();
            pos = mark;
        }

        @Override
        public void close() throws IORuntimeException {
            src.close();
        }
    }

    static final class AsReader extends DoReadReader {

        private final @Nonnull CharReader in;
        private char[] oneChar;

        private AsReader(@Nonnull CharReader in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            try {
                if (oneChar == null) {
                    oneChar = new char[1];
                }
                int ret = in.readTo(oneChar);
                return ret < 0 ? -1 : oneChar[0] & 0xFFFF;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        protected int doRead(char @Nonnull [] b, int off, int len) throws IOException {
            try {
                return in.readTo(b, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public long skip(long n) throws IllegalArgumentException, IOException {
            try {
                return in.skip(n);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            try {
                in.mark();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void reset() throws IOException {
            try {
                in.reset();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() throws IOException {
            try {
                in.close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }
}
