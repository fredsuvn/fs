package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.bytes.JieBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

final class ByteReaderImpl {

    static @Nonnull ByteReader of(@Nonnull InputStream src, int bufSize) throws IllegalArgumentException {
        return new ByteStreamReader(src, bufSize);
    }

    static @Nonnull ByteReader of(@Nonnull ReadableByteChannel src, int bufSize) throws IllegalArgumentException {
        return new ByteChannelReader(src, bufSize);
    }

    static @Nonnull ByteReader of(byte @Nonnull [] src, int off, int len) throws IndexOutOfBoundsException {
        return new ByteArrayReader(src, off, len);
    }

    static @Nonnull ByteReader of(@Nonnull ByteBuffer src) {
        return new ByteBufferReader(src);
    }

    static final class ByteSegmentImpl implements ByteSegment {

        private static final @Nonnull ByteSegmentImpl EMPTY_END = new ByteSegmentImpl(JieBytes.emptyBuffer(), true);
        private static final @Nonnull ByteSegmentImpl EMPTY_SEG = new ByteSegmentImpl(JieBytes.emptyBuffer(), false);

        public static @Nonnull ByteSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final @Nonnull ByteBuffer data;
        private final boolean end;

        ByteSegmentImpl(@Nonnull ByteBuffer data, boolean end) {
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
        private final @Nonnull ByteOperator operator;

        ByteStreamReader(@Nonnull InputStream src, int bufSize) throws IllegalArgumentException {
            this.source = src;
            this.operator = ByteOperator.get(bufSize);
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            if (len == 0) {
                return ByteSegment.empty(false);
            }
            byte[] bytes = operator.read(source, len);
            if (bytes == null) {
                return ByteSegment.empty(true);
            }
            return ByteSegment.of(ByteBuffer.wrap(bytes), bytes.length < len);
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

    private static final class ByteChannelReader implements ByteReader {

        private final @Nonnull ReadableByteChannel source;
        private final @Nonnull ByteOperator operator;

        ByteChannelReader(@Nonnull ReadableByteChannel src, int bufSize) throws IllegalArgumentException {
            this.source = src;
            this.operator = ByteOperator.get(bufSize);
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
    }

    private static final class ByteArrayReader implements ByteReader {

        private final byte @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark;

        ByteArrayReader(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
            try {
                int remaining = endPos - pos;
                dst.write(ByteBuffer.wrap(source, pos, remaining));
                pos += remaining;
                return remaining;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
            if (len == 0) {
                return 0;
            }
            if (pos == endPos) {
                return -1;
            }
            try {
                int remaining = endPos - pos;
                int actualLen = (int) Math.min(remaining, len);
                dst.write(ByteBuffer.wrap(source, pos, actualLen));
                pos += actualLen;
                return actualLen;
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public int readTo(byte @Nonnull [] dst) {
            return readTo0(dst, 0, dst.length);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(dst.length, off, len);
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

    private static final class ByteBufferReader implements ByteReader {

        private final @Nonnull ByteBuffer source;

        ByteBufferReader(@Nonnull ByteBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int len) throws IllegalArgumentException, IORuntimeException {
            JieCheck.checkArgument(len >= 0, "len must >= 0.");
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
        public long readTo(@Nonnull OutputStream dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public long readTo(@Nonnull OutputStream dst, long len) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return JieBuffer.readTo(source, dst, actualLen);
        }

        @Override
        public long readTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public long readTo(
            @Nonnull WritableByteChannel dst, long len
        ) throws IllegalArgumentException, IORuntimeException {
            int actualLen = (int) Math.min(Integer.MAX_VALUE, len);
            return JieBuffer.readTo(source, dst, actualLen);
        }

        @Override
        public int readTo(byte @Nonnull [] dst) {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public int readTo(byte @Nonnull [] dst, int off, int len) throws IndexOutOfBoundsException {
            return JieBuffer.readTo(source, dst, off, len);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
            return JieBuffer.readTo(source, dst);
        }

        @Override
        public int readTo(@Nonnull ByteBuffer dst, int len) throws IllegalArgumentException, IORuntimeException {
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
