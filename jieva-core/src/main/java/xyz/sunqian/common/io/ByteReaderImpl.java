package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.bytes.BytesKit;
import xyz.sunqian.common.base.math.MathKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

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

        private final @Nonnull InputStream src;
        private final int bufSize;

        private ByteStreamReader(@Nonnull InputStream src, int bufSize) {
            this.src = src;
            this.bufSize = bufSize;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            IOChecker.checkLen(len);
            byte[] data = new byte[len];
            int readSize = IOKit.readTo0(src, data, 0, data.length, IOChecker.endChecker());
            if (readSize < 0) {
                return ByteSegment.empty(true);
            }
            data = readSize == len ? data : Arrays.copyOfRange(data, 0, readSize);
            return ByteSegment.of(ByteBuffer.wrap(data), readSize < len);
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
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, bufSize, IOChecker.endChecker());
        }

        @Override
        public int readTo(byte @Nonnull [] dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, 0, dst.length, IOChecker.endChecker());
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            IOChecker.checkOffLen(dst.length, off, len);
            return IOKit.readTo0(src, dst, off, len, IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, dst.remaining(), IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
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
        public @Nonnull InputStream asInputStream() {
            return src;
        }
    }

    private static final class ByteChannelReader implements ByteReader {

        private final @Nonnull ReadableByteChannel src;
        private final int bufSize;

        private ByteChannelReader(@Nonnull ReadableByteChannel src, int bufSize) {
            this.src = src;
            this.bufSize = bufSize;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            IOChecker.checkLen(len);
            byte[] data = new byte[len];
            int readSize = IOKit.readTo0(src, ByteBuffer.wrap(data), IOChecker.endChecker());
            if (readSize < 0) {
                return ByteSegment.empty(true);
            }
            data = readSize == len ? data : Arrays.copyOfRange(data, 0, readSize);
            return ByteSegment.of(ByteBuffer.wrap(data), readSize < len);
        }

        @Override
        public long skip(long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSkip(len);
            if (len == 0) {
                return 0;
            }
            return skip0(len);
        }

        private long skip0(long len) {
            long hasRead = 0;
            int onceBufSize = (int) Math.min(len, bufSize);
            ByteBuffer buf = ByteBuffer.allocate(onceBufSize);
            while (hasRead < len) {
                int onceSize = IOKit.readTo0(src, buf, (int) Math.min(onceBufSize, len - hasRead), IOChecker.endChecker());
                if (onceSize < 0) {
                    return hasRead;
                }
                hasRead += onceSize;
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, bufSize, IOChecker.endChecker());
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, bufSize, IOChecker.endChecker());
        }

        @Override
        public int readTo(byte @Nonnull [] dst) throws IORuntimeException {
            return IOKit.readTo0(src, ByteBuffer.wrap(dst, 0, dst.length), IOChecker.endChecker());
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException, IORuntimeException {
            return IOKit.readTo0(src, ByteBuffer.wrap(dst, off, len), IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return IOKit.readTo0(src, dst, dst.remaining(), IOChecker.endChecker());
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            return IOKit.readTo0(src, dst, len, IOChecker.endChecker());
        }

        @Override
        public boolean markSupported() {
            return false;
        }

        @Override
        public void mark() throws IORuntimeException {
            throw new IORuntimeException("Mark/Reset is unsupported.");
        }

        @Override
        public void reset() throws IORuntimeException {
            throw new IORuntimeException("Mark/Reset is unsupported.");
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
        public @Nonnull InputStream asInputStream() {
            return Channels.newInputStream(src);
        }
    }

    private static final class ByteArrayReader implements ByteReader {

        private final byte @Nonnull [] src;
        private int pos;
        private final int end;
        private int mark;

        private ByteArrayReader(byte @Nonnull [] src, int off, int len) {
            this.src = src;
            this.pos = off;
            this.end = off + len;
            this.mark = pos;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (pos == end) {
                return ByteSegment.empty(true);
            }
            int remaining = end - pos;
            int actualLen = Math.min(remaining, len);
            ByteBuffer data = ByteBuffer.wrap(src, pos, actualLen).slice();
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
            if (pos == end) {
                return 0;
            }
            int remaining = end - pos;
            int skipped = (int) Math.min(remaining, len);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            if (pos == end) {
                return -1;
            }
            try {
                int remaining = end - pos;
                dst.write(src, pos, remaining);
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
            if (pos == end) {
                return -1;
            }
            try {
                int remaining = end - pos;
                int actualLen = (int) Math.min(remaining, len);
                dst.write(src, pos, actualLen);
                pos += actualLen;
                return actualLen;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            if (pos == end) {
                return -1;
            }
            int remaining = end - pos;
            ByteBuffer buf = ByteBuffer.wrap(src, pos, remaining);
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
            if (pos == end) {
                return -1;
            }
            int remaining = end - pos;
            int actualLen = (int) Math.min(remaining, len);
            ByteBuffer buf = ByteBuffer.wrap(src, pos, actualLen);
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
            if (pos == end) {
                return -1;
            }
            int remaining = end - pos;
            int copySize = Math.min(remaining, len);
            System.arraycopy(src, pos, dst, off, copySize);
            pos += copySize;
            return copySize;
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
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
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
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

        private int putTo0(@Nonnull ByteBuffer dst, int putSize) throws IORuntimeException {
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

    private static final class ByteBufferReader implements ByteReader {

        private final @Nonnull ByteBuffer src;

        private ByteBufferReader(@Nonnull ByteBuffer src) {
            this.src = src;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            if (!src.hasRemaining()) {
                return ByteSegment.empty(true);
            }
            int pos = src.position();
            int limit = src.limit();
            int newPos = Math.min(pos + len, limit);
            src.limit(newPos);
            ByteBuffer data = src.slice();
            src.position(newPos);
            src.limit(limit);
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
            if (!src.hasRemaining()) {
                return 0;
            }
            int pos = src.position();
            int newPos = (int) Math.min(pos + len, src.limit());
            src.position(newPos);
            return newPos - pos;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo0(src, dst, actualLen);
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkLen(len);
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return BufferKit.readTo0(src, dst, actualLen);
        }

        @Override
        public int readTo(byte @Nonnull [] dst) {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            return BufferKit.readTo(src, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return BufferKit.readTo(src, dst);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
            return BufferKit.readTo(src, dst, len);
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark() {
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
        public void close() {
        }
    }

    private static final class LimitedReader implements ByteReader {

        private final @Nonnull ByteReader src;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedReader(@Nonnull ByteReader src, long limit) {
            this.src = src;
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
            ByteSegment segment = src.read(actualLen);
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
            long skipped = src.skip(actualLen);
            pos += skipped;
            return skipped;
        }

        @Override
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
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
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
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
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
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
            long readSize = src.readTo(dst, actualLen);
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
            int readSize = src.readTo(dst, off, actualLen);
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
            int actualLen = (int) MathKit.min(len, limit - pos, dst.remaining());
            int readSize = src.readTo(dst, actualLen);
            if (readSize < 0) {
                return readSize;
            }
            pos += readSize;
            return readSize;
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
}
