package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.chars.CharsKit;

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

        private final @Nonnull Reader source;
        private final @Nonnull CharIO operator;

        private CharStreamReader(@Nonnull Reader src, int bufSize) {
            this.source = src;
            this.operator = CharIO.get(bufSize);
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return CharSegment.empty(false);
            }
            char[] chars = operator.read(source, len);
            if (chars == null) {
                return CharSegment.empty(true);
            }
            return CharSegment.of(CharBuffer.wrap(chars), chars.length < len);
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
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void reset() throws IORuntimeException {
            try {
                source.reset();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void close() throws IORuntimeException {
            try {
                source.close();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public Reader asReader() {
            return source;
        }
    }

    private static final class CharArrayReader implements CharReader {

        private final char @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark;

        private CharArrayReader(char @Nonnull [] source, int offset, int length) {
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException {
            IOChecker.checkLen(len);
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
                IOKit.write(dst, source, pos, remaining);
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
                IOKit.write(dst, source, pos, actualLen);
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
            int putSize = Math.min(remaining, dst.remaining());
            putSize = Math.min(putSize, len);
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

        @Override
        public Reader asReader() {
            return new Reader() {

                @Override
                public int read() {
                    if (pos == endPos) {
                        return -1;
                    }
                    return source[pos++] & 0x0000ffff;
                }

                @Override
                public int read(char @Nonnull [] cbuf, int off, int len) throws IndexOutOfBoundsException {
                    return CharArrayReader.this.readTo(cbuf, off, len);
                }

                @Override
                public long skip(long n) throws IllegalArgumentException {
                    return CharArrayReader.this.skip(n);
                }

                @Override
                public boolean ready() {
                    return true;
                }

                @Override
                public void close() {
                }

                @Override
                public void mark(int readlimit) {
                    CharArrayReader.this.mark();
                }

                @Override
                public void reset() {
                    CharArrayReader.this.reset();
                }

                @Override
                public boolean markSupported() {
                    return true;
                }
            };
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
            IOChecker.checkLen(len);
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
                dst.append(source, pos, pos + remaining);
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
            int putSize = Math.min(remaining, dst.remaining());
            putSize = Math.min(putSize, len);
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

        @Override
        public Reader asReader() {
            return new Reader() {

                @Override
                public int read() {
                    if (pos == endPos) {
                        return -1;
                    }
                    return source.charAt(pos++) & 0x0000ffff;
                }

                @Override
                public int read(char @Nonnull [] cbuf, int off, int len) throws IndexOutOfBoundsException {
                    return CharSequenceReader.this.readTo(cbuf, off, len);
                }

                @Override
                public long skip(long n) throws IllegalArgumentException {
                    return CharSequenceReader.this.skip(n);
                }

                @Override
                public boolean ready() {
                    return true;
                }

                @Override
                public void close() {
                }

                @Override
                public void mark(int readlimit) {
                    CharSequenceReader.this.mark();
                }

                @Override
                public void reset() {
                    CharSequenceReader.this.reset();
                }

                @Override
                public boolean markSupported() {
                    return true;
                }
            };
        }
    }

    private static final class CharBufferReader implements CharReader {

        private final @Nonnull CharBuffer source;

        private CharBufferReader(@Nonnull CharBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull CharSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
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
            IOChecker.checkSkip(len);
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
            return BufferKit.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull Appendable dst, long len) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo(source, dst, actualLen);
        }

        @Override
        public int readTo(char @Nonnull [] dst) throws IORuntimeException {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public int readTo(char @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return BufferKit.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst) throws IORuntimeException {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull CharBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return BufferKit.readTo(source, dst, len);
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
            try {
                source.reset();
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void close() throws IORuntimeException {
        }

        @Override
        public Reader asReader() {
            return new Reader() {

                @Override
                public int read() {
                    if (!source.hasRemaining()) {
                        return -1;
                    }
                    return source.get() & 0x0000ffff;
                }

                @Override
                public int read(char @Nonnull [] cbuf, int off, int len) throws IndexOutOfBoundsException {
                    return CharBufferReader.this.readTo(cbuf, off, len);
                }

                @Override
                public long skip(long n) throws IllegalArgumentException {
                    return CharBufferReader.this.skip(n);
                }

                @Override
                public boolean ready() {
                    return true;
                }

                @Override
                public void close() {
                }

                @Override
                public void mark(int readlimit) {
                    CharBufferReader.this.mark();
                }

                @Override
                public void reset() throws IOException {
                    try {
                        source.reset();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public boolean markSupported() {
                    return true;
                }
            };
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
            IOChecker.checkLen(len);
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
            IOChecker.checkSkip(len);
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
            IOChecker.checkLen(len);
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
            IOChecker.checkOffLen(dst.length, off, len);
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

        @Override
        public Reader asReader() {
            return new Reader() {

                @Override
                public int read() {
                    CharSegment segment = LimitedReader.this.read(1);
                    CharBuffer buffer = segment.data();
                    if (!buffer.hasRemaining()) {
                        return -1;
                    }
                    return buffer.get() & 0x0000ffff;
                }

                @Override
                public int read(char @Nonnull [] cbuf, int off, int len) throws IndexOutOfBoundsException {
                    return LimitedReader.this.readTo(cbuf, off, len);
                }

                @Override
                public long skip(long n) throws IllegalArgumentException, IOException {
                    try {
                        return LimitedReader.this.skip(n);
                    } catch (IllegalArgumentException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public boolean ready() {
                    return false;
                }

                @Override
                public void close() throws IOException {
                    try {
                        LimitedReader.this.close();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public void mark(int readlimit) throws IOException {
                    try {
                        LimitedReader.this.mark();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public void reset() throws IOException {
                    try {
                        LimitedReader.this.reset();
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public boolean markSupported() {
                    return LimitedReader.this.markSupported();
                }
            };
        }
    }
}
