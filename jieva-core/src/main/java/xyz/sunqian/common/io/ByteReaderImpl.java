package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.bytes.BytesKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

final class ByteReaderImpl {

    static @Nonnull ByteReader of(@Nonnull InputStream src, int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return new ByteStreamReader(src, bufSize);
    }

    static @Nonnull ByteReader of(@Nonnull ReadableByteChannel src, int bufSize) throws IllegalArgumentException {
        IOChecker.checkBufSize(bufSize);
        return new ByteChannelReader(src, bufSize);
    }

    static @Nonnull ByteReader of(byte @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(src.length, off, len);
        return new ByteArrayReader(src, off, len);
    }

    static @Nonnull ByteReader of(@Nonnull ByteBuffer src) {
        return new ByteBufferReader(src);
    }

    static @Nonnull ByteReader limit(@Nonnull ByteReader reader, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedReader(reader, limit);
    }

    static @Nonnull ByteSegment newSeg(@Nonnull ByteBuffer data, boolean end) {
        return new ByteSegmentImpl(data, end);
    }

    static @Nonnull ByteSegment emptySeg(boolean end) {
        return end ? ByteSegmentImpl.EMPTY_END : ByteSegmentImpl.EMPTY_SEG;
    }

    private static final class ByteSegmentImpl implements ByteSegment {

        private static final @Nonnull ByteSegmentImpl EMPTY_END = new ByteSegmentImpl(BytesKit.emptyBuffer(), true);
        private static final @Nonnull ByteSegmentImpl EMPTY_SEG = new ByteSegmentImpl(BytesKit.emptyBuffer(), false);

        private final @Nonnull ByteBuffer data;
        private final boolean end;

        private ByteSegmentImpl(@Nonnull ByteBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }

        @Override
        public @Nonnull ByteBuffer data() {
            return data;
        }

        @Override
        public boolean end() {
            return end;
        }

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public @Nonnull ByteSegment clone() {
            ByteBuffer copy = ByteBuffer.allocate(data.remaining());
            int pos = data.position();
            int limit = data.limit();
            copy.put(data);
            data.position(pos);
            data.limit(limit);
            copy.flip();
            return new ByteSegmentImpl(copy, end);
        }
    }

    private static final class ByteStreamReader implements ByteReader {

        private final @Nonnull InputStream source;
        private final @Nonnull ByteIO operator;

        private long limit = -1;

        private ByteStreamReader(@Nonnull InputStream src, int bufSize) {
            this.source = src;
            this.operator = ByteIO.get(bufSize);
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (limit == 0) {
                return ByteSegment.empty(true);
            }
            int actualLen = limit < 0 ? len : (int) Math.min(len, limit);
            byte[] bytes = operator.read(source, actualLen);
            if (bytes == null) {
                limit = 0;
                return ByteSegment.empty(true);
            }
            limit -= bytes.length;
            return ByteSegment.of(ByteBuffer.wrap(bytes), bytes.length < actualLen);
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

        private long skip0(long len) throws IOException {
            long actualLen = limit < 0 ? len : Math.min(len, limit);
            long hasRead = 0;
            while (hasRead < actualLen) {
                long onceSize = source.skip(actualLen - hasRead);
                if (onceSize == 0) {
                    if (source.read() == -1) {
                        break;
                    } else {
                        hasRead++;
                    }
                }
                hasRead += onceSize;
            }
            if (limit > 0) {
                limit -= hasRead;
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            if (limit < 0) {
                return operator.readTo(source, dst);
            }
            if (limit == 0) {
                return -1;
            }
            long readSize = operator.readTo(source, dst, limit);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return 0;
            }
            if (limit < 0) {
                return operator.readTo(source, dst, len);
            }
            if (limit == 0) {
                return -1;
            }
            long actualLen = Math.min(len, limit);
            long readSize = operator.readTo(source, dst, actualLen);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            if (limit < 0) {
                return operator.readTo(source, dst);
            }
            if (limit == 0) {
                return -1;
            }
            long readSize = operator.readTo(source, dst, limit);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return 0;
            }
            if (limit < 0) {
                return operator.readTo(source, dst, len);
            }
            if (limit == 0) {
                return -1;
            }
            long actualLen = Math.min(len, limit);
            long readSize = operator.readTo(source, dst, actualLen);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public int readTo(byte @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst, 0, dst.length);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            if (limit < 0) {
                return operator.readTo(source, dst, off, len);
            }
            if (limit == 0) {
                return -1;
            }
            int actualLen = (int) Math.min(len, limit);
            int readSize = operator.readTo(source, dst, off, actualLen);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            if (limit < 0) {
                return operator.readTo(source, dst);
            }
            if (limit == 0) {
                return -1;
            }
            int actualLen = (int) Math.min(dst.remaining(), limit);
            int readSize = operator.readTo(source, dst, actualLen);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            if (limit < 0) {
                return operator.readTo(source, dst, len);
            }
            if (limit == 0) {
                return -1;
            }
            int actualLen = Math.min(dst.remaining(), len);
            actualLen = (int) Math.min(actualLen, limit);
            int readSize = operator.readTo(source, dst, actualLen);
            if (readSize > 0) {
                limit -= readSize;
            }
            return readSize;
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            try {
                source.mark(Integer.MAX_VALUE);
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
        public ByteReader limit(long limit) throws IllegalArgumentException {
            IOChecker.checkLimit(limit);
            if (this.limit < 0) {
                this.limit = limit;
            } else {
                this.limit = Math.min(this.limit, limit);
            }
            return this;
        }

        @Override
        public InputStream asInputStream() {
            return limit < 0 ? source : IOKit.limitedInputStream(source, limit);
        }
    }

    private static final class ByteChannelReader implements ByteReader {

        private final @Nonnull ReadableByteChannel source;
        private final @Nonnull ByteIO operator;

        private ByteChannelReader(@Nonnull ReadableByteChannel src, int bufSize) {
            this.source = src;
            this.operator = ByteIO.get(bufSize);
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            ByteBuffer buf = operator.read(source, len);
            if (buf == null) {
                return ByteSegment.empty(true);
            }
            return ByteSegment.of(buf, buf.remaining() < len);
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
            ByteBuffer buf = ByteBuffer.allocate((int) Math.min(operator.bufferSize(), size));
            while (hasRead < size) {
                buf.position(0);
                buf.limit((int) Math.min(buf.capacity(), size - hasRead));
                long onceSize = source.read(buf);
                if (onceSize < 0) {
                    break;
                }
                hasRead += onceSize;
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            return operator.readTo(source, dst, len);
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            return operator.readTo(source, dst, len);
        }

        @Override
        public int readTo(byte @Nonnull [] dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return operator.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return operator.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return operator.readTo(source, dst, len);
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void mark() throws IORuntimeException {
            throw new IORuntimeException("Mark is unsupported.");
        }

        @Override
        public void reset() throws IORuntimeException {
            throw new IORuntimeException("Mark is unsupported.");
        }

        @Override
        public void close() throws IORuntimeException {
            try {
                source.close();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public InputStream asInputStream() {
            return Channels.newInputStream(source);
        }
    }

    private static final class ByteArrayReader implements ByteReader {

        private final byte @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark;

        private ByteArrayReader(byte @Nonnull [] source, int offset, int length) {
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (pos == endPos) {
                return ByteSegment.empty(true);
            }
            int remaining = endPos - pos;
            int actualLen = Math.min(remaining, len);
            ByteBuffer data = ByteBuffer.wrap(source, pos, actualLen).slice();
            pos += actualLen;
            return ByteSegment.of(data, remaining <= len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException {
            IOChecker.checkSkip(len);
            return skip0(len);
        }

        private long skip0(long len) {
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
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            if (pos == endPos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                dst.write(source, pos, remaining);
                pos += remaining;
                return remaining;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                int actualLen = (int) Math.min(remaining, len);
                dst.write(source, pos, actualLen);
                pos += actualLen;
                return actualLen;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            ByteBuffer buf = ByteBuffer.wrap(source, pos, remaining);
            int ret = BufferKit.readTo(buf, dst);
            pos += ret;
            return ret;
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int actualLen = (int) Math.min(remaining, len);
            ByteBuffer buf = ByteBuffer.wrap(source, pos, actualLen);
            int ret = BufferKit.readTo(buf, dst);
            pos += ret;
            return ret;
        }

        @Override
        public int readTo(byte @Nonnull [] dst) {
            return readTo0(dst, 0, dst.length);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            IOChecker.checkOffLen(dst.length, off, len);
            return readTo0(dst, off, len);
        }

        private int readTo0(byte @Nonnull [] dst, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            int remaining = endPos - pos;
            int copySize = Math.min(remaining, len);
            System.arraycopy(source, pos, dst, off, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
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
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
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

        private int putTo0(@Nonnull ByteBuffer dst, int putSize) throws IORuntimeException {
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
        public InputStream asInputStream() {
            return new InputStream() {

                @Override
                public int read() {
                    if (pos == endPos) {
                        return -1;
                    }
                    return source[pos++] & 0x00ff;
                }

                @Override
                public int read(byte @Nonnull [] b, int off, int len) throws IndexOutOfBoundsException {
                    return ByteArrayReader.this.readTo(b, off, len);
                }

                @Override
                public long skip(long n) {
                    if (n < 0) {
                        return 0;
                    }
                    return ByteArrayReader.this.skip0(n);
                }

                @Override
                public int available() {
                    return endPos - pos;
                }

                @Override
                public void close() {
                }

                @Override
                public void mark(int readlimit) {
                    ByteArrayReader.this.mark();
                }

                @Override
                public void reset() {
                    ByteArrayReader.this.reset();
                }

                @Override
                public boolean markSupported() {
                    return true;
                }
            };
        }
    }

    private static final class ByteBufferReader implements ByteReader {

        private final @Nonnull ByteBuffer source;

        private ByteBufferReader(@Nonnull ByteBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (!source.hasRemaining()) {
                return ByteSegment.empty(true);
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + len, limit);
            source.limit(newPos);
            ByteBuffer data = source.slice();
            source.position(newPos);
            source.limit(limit);
            return ByteSegment.of(data, newPos >= limit);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException {
            IOChecker.checkSkip(len);
            return skip0(len);
        }

        private long skip0(long len) {
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
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo(source, dst, actualLen);
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo(source, dst, actualLen);
        }

        @Override
        public int readTo(byte @Nonnull [] dst) {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            return BufferKit.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return BufferKit.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return BufferKit.readTo(source, dst, len);
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() {
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
        public void close() {
        }

        @Override
        public InputStream asInputStream() {
            return new InputStream() {

                @Override
                public int read() {
                    if (!source.hasRemaining()) {
                        return -1;
                    }
                    return source.get() & 0x00ff;
                }

                @Override
                public int read(byte @Nonnull [] b, int off, int len) throws IndexOutOfBoundsException {
                    return ByteBufferReader.this.readTo(b, off, len);
                }

                @Override
                public long skip(long n) {
                    if (n < 0) {
                        return 0;
                    }
                    return ByteBufferReader.this.skip0(n);
                }

                @Override
                public int available() {
                    return source.remaining();
                }

                @Override
                public void close() {
                }

                @Override
                public void mark(int readlimit) {
                    ByteBufferReader.this.mark();
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

    private static final class LimitedReader implements ByteReader {

        private final @Nonnull ByteReader source;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedReader(@Nonnull ByteReader source, long limit) {
            this.source = source;
            this.limit = limit;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (pos >= limit) {
                return ByteSegment.empty(true);
            }
            int actualLen = (int) Math.min(len, limit - pos);
            ByteSegment segment = source.read(actualLen);
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
            return skip0(len);
        }

        private long skip0(long len) throws IORuntimeException {
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
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
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
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
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
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
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
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
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
        public int readTo(byte @Nonnull [] dst) throws IORuntimeException {
            return readTo(dst, 0, dst.length);
        }

        @Override
        public int readTo(
            byte @Nonnull [] dst, int off, int len
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
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return readTo(dst, dst.remaining());
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
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
        public InputStream asInputStream() {
            return new InputStream() {

                @Override
                public int read() {
                    ByteSegment segment = LimitedReader.this.read(1);
                    ByteBuffer buffer = segment.data();
                    if (!buffer.hasRemaining()) {
                        return -1;
                    }
                    return buffer.get() & 0x00ff;
                }

                @Override
                public int read(byte @Nonnull [] b, int off, int len) throws IndexOutOfBoundsException {
                    return LimitedReader.this.readTo(b, off, len);
                }

                @Override
                public long skip(long n) throws IOException {
                    if (n < 0) {
                        return 0;
                    }
                    try {
                        return LimitedReader.this.skip0(n);
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public int available() {
                    return 0;
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
                public void mark(int readlimit) {
                    LimitedReader.this.mark();
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
