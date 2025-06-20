package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.bytes.JieBytes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class ByteReaderXImpl {

    static @Nonnull ByteReaderX of(@Nonnull InputStream source) {
        return new ByteStreamReaderX(source);
    }

    static @Nonnull ByteReaderX of(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
        return new ByteArrayReaderX(source, offset, length);
    }

    static @Nonnull ByteReaderX of(@Nonnull ByteBuffer source) {
        return new ByteBufferReaderX(source);
    }

    // static @Nonnull ByteReader of(@Nonnull ByteReader source, long readLimit) throws IllegalArgumentException {
    //     return new LimitedByteReader(source, readLimit);
    // }

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

    private static final class ByteStreamReaderX implements ByteReaderX {

        private final @Nonnull InputStream source;

        ByteStreamReaderX(@Nonnull InputStream source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            try {
                return read0(size);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private @Nonnull ByteSegment read0(int size) throws Exception {
            boolean end = false;
            int hasRead = 0;
            byte[] buf = new byte[size];
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
                return ByteSegment.empty(true);
            }
            ByteBuffer data = ByteBuffer.wrap(
                hasRead == size ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            );
            return ByteSegment.of(data, end);
        }

        @Override
        public long skip(long size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return 0;
            }
            try {
                return skip0(size);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
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
            byte @Nonnull [] dest, int offset, int length
        ) throws IndexOutOfBoundsException, IORuntimeException {
            JieCheck.checkOffsetLength(dest.length, offset, length);
            if (length == 0) {
                return 0;
            }
            try {
                return readTo0(dest, offset, length);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private int readTo0(byte @Nonnull [] dest, int offset, int length) throws IOException {
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
        public int readTo(@Nonnull ByteBuffer dest) throws IORuntimeException {
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
            byte[] buf = new byte[dest.remaining()];
            int hasRead = readTo(buf);
            if (hasRead > 0) {
                dest.put(buf, 0, hasRead);
            }
            return hasRead;
        }

        @Override
        public long readTo(@Nonnull OutputStream dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            try {
                return readTo0(dest, length);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long readTo0(@Nonnull OutputStream dest, long length) throws IOException {
            long count = 0;
            int bufferSize = JieIO.bufferSize();
            byte[] buf = new byte[length < 0 ? bufferSize : (int) Math.min(length - count, bufferSize)];
            while (true) {
                int len = (int) (length < 0 ? buf.length : Math.min(length - count, buf.length));
                int c = source.read(buf, 0, len);
                if (c < 0) {
                    return count == 0 ? -1 : count;
                }
                dest.write(buf, 0, c);
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

    private static final class ByteArrayReaderX implements ByteReaderX {

        private final byte @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark;

        ByteArrayReaderX(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
            this.mark = pos;
        }

        @Override
        public @Nonnull ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            if (pos == endPos) {
                return ByteSegment.empty(true);
            }
            int remaining = endPos - pos;
            int actualLen = Math.min(remaining, size);
            ByteBuffer data = ByteBuffer.wrap(source, pos, actualLen).slice();
            pos += actualLen;
            return ByteSegment.of(data, remaining <= size);
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
            byte @Nonnull [] dest, int offset, int length
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
        public int readTo(@Nonnull ByteBuffer dest) throws IORuntimeException {
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
        public long readTo(@Nonnull OutputStream dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            int remaining = endPos - pos;
            if (remaining == 0) {
                return -1;
            }
            try {
                return readTo0(dest, length, remaining);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long readTo0(@Nonnull OutputStream dest, long length, int remaining) throws IOException {
            int actual = length < 0 ? remaining : (int) Math.min(remaining, length);
            dest.write(source, pos, actual);
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

    private static final class ByteBufferReaderX implements ByteReaderX {

        private final @Nonnull ByteBuffer source;

        ByteBufferReaderX(@Nonnull ByteBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
            IOChecker.checkSize(size);
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            if (!source.hasRemaining()) {
                return ByteSegment.empty(true);
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + size, limit);
            source.limit(newPos);
            ByteBuffer data = source.slice();
            source.position(newPos);
            source.limit(limit);
            return ByteSegment.of(data, newPos >= limit);
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
            byte @Nonnull [] dest, int offset, int length
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
        public int readTo(@Nonnull ByteBuffer dest) throws IORuntimeException {
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
                ByteBuffer src = JieBuffer.slice(source, copySize);
                dest.put(src);
                source.position(source.position() + copySize);
            }
            return copySize;
        }

        @Override
        public long readTo(@Nonnull OutputStream dest, long length) throws IORuntimeException {
            if (length == 0) {
                return 0;
            }
            if (source.remaining() == 0) {
                return -1;
            }
            try {
                return readTo0(dest, length);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long readTo0(@Nonnull OutputStream dest, long length) throws IOException {
            int copySize = (int) (length < 0 ? source.remaining() : Math.min(source.remaining(), length));
            if (source.hasArray()) {
                dest.write(source.array(), source.arrayOffset() + source.position(), copySize);
                source.position(source.position() + copySize);
            } else {
                byte[] data = JieBuffer.read(source, copySize);
                dest.write(data);
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

    // private static final class ByteChannelReader implements ByteReader {
    //
    //     private final @Nonnull ReadableByteChannel source;
    //
    //     ByteChannelReader(@Nonnull ReadableByteChannel source) {
    //         this.source = source;
    //     }
    //
    //     @Override
    //     public @Nonnull ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
    //         IOChecker.checkSize(size);
    //         if (size == 0) {
    //             return ByteSegment.empty(false);
    //         }
    //         try {
    //             return read0(size);
    //         } catch (Exception e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     private @Nonnull ByteSegment read0(int size) throws IOException {
    //         ByteBuffer tmp = ByteBuffer.allocate(size);
    //         int count = 0;
    //         while (true) {
    //             int readSize = source.read(tmp);
    //             if (readSize < 0) {
    //                 break;
    //             }
    //             count += readSize;
    //         }
    //         ByteBuffer buf;
    //         if (tmp.position() == tmp.capacity()) {
    //             buf = tmp;
    //         } else {
    //             buf = ByteBuffer.allocate()
    //         }
    //         return ByteSegment.of(
    //             tmp.position() == tmp.capacity() ? tmp.flip() : JieBuffer.,
    //             count < size
    //         );
    //     }
    //
    //     @Override
    //     public long skip(long size) throws IllegalArgumentException, IORuntimeException {
    //         IOChecker.checkSize(size);
    //         if (size == 0) {
    //             return 0;
    //         }
    //         try {
    //             return skip0(size);
    //         } catch (Exception e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     private long skip0(long size) throws IOException {
    //         ByteBuffer dst = ByteBuffer.allocate((int) Math.min(JieIO.bufferSize(), size));
    //         long remaining = size;
    //         while (true) {
    //             int readSize;
    //             if (remaining >= dst.remaining()) {
    //                 readSize = source.read(dst);
    //             } else {
    //                 dst.limit((int) remaining);
    //                 readSize = source.read(dst);
    //             }
    //             if (readSize < 0) {
    //                 break;
    //             }
    //             remaining -= readSize;
    //             if (remaining <= 0) {
    //                 break;
    //             }
    //             dst.position(0);
    //         }
    //         return size - remaining;
    //     }
    //
    //     @Override
    //     public int readTo(
    //         byte @Nonnull [] dest, int offset, int length
    //     ) throws IndexOutOfBoundsException, IORuntimeException {
    //         JieCheck.checkOffsetLength(dest.length, offset, length);
    //         if (length == 0) {
    //             return 0;
    //         }
    //         return readTo0(ByteBuffer.wrap(dest, offset, length));
    //     }
    //
    //     @Override
    //     public int readTo(@Nonnull ByteBuffer dest) throws IORuntimeException {
    //         if (dest.remaining() == 0) {
    //             return 0;
    //         }
    //         return readTo0(ByteBuffer.wrap(dest, offset, length));
    //     }
    //
    //     private int readTo0(@Nonnull ByteBuffer dest) throws IORuntimeException {
    //         if (dest.remaining() == 0) {
    //             return 0;
    //         }
    //         if (source.remaining() == 0) {
    //             return -1;
    //         }
    //         int copySize = Math.min(source.remaining(), dest.remaining());
    //         if (copySize == source.remaining()) {
    //             dest.put(source);
    //         } else {
    //             ByteBuffer src = JieBuffer.slice(source, copySize);
    //             dest.put(src);
    //             source.position(source.position() + copySize);
    //         }
    //         return copySize;
    //     }
    //
    //     @Override
    //     public long readTo(@Nonnull OutputStream dest, long length) throws IORuntimeException {
    //         if (length == 0) {
    //             return 0;
    //         }
    //         if (source.remaining() == 0) {
    //             return -1;
    //         }
    //         try {
    //             return readTo0(dest, length);
    //         } catch (Exception e) {
    //             throw new IORuntimeException(e);
    //         }
    //     }
    //
    //     private long readTo0(@Nonnull OutputStream dest, long length) throws IOException {
    //         int copySize = (int) (length < 0 ? source.remaining() : Math.min(source.remaining(), length));
    //         if (source.hasArray()) {
    //             dest.write(source.array(), source.arrayOffset() + source.position(), copySize);
    //             source.position(source.position() + copySize);
    //         } else {
    //             byte[] data = JieBuffer.read(source, copySize);
    //             dest.write(data);
    //         }
    //         return copySize;
    //     }
    //
    //     @Override
    //     public boolean markSupported() {
    //         return true;
    //     }
    //
    //     @Override
    //     public void mark() throws IORuntimeException {
    //         source.mark();
    //     }
    //
    //     @Override
    //     public void reset() throws IORuntimeException {
    //         source.reset();
    //     }
    //
    //     @Override
    //     public void close() throws IORuntimeException {
    //     }
    // }

    // private static final class LimitedByteReader implements ByteReader {
    //
    //     private final @Nonnull ByteReader source;
    //     private long remaining;
    //     private long mark;
    //
    //     LimitedByteReader(@Nonnull ByteReader source, long readLimit) throws IllegalArgumentException {
    //         IOChecker.checkReadLimit(readLimit);
    //         this.source = source;
    //         this.remaining = readLimit;
    //     }
    //
    //     @Override
    //     public @Nonnull ByteSegment read(int size) throws IllegalArgumentException, IORuntimeException {
    //         IOChecker.checkSize(size);
    //         if (remaining <= 0) {
    //             return ByteSegment.empty(true);
    //         }
    //         if (size == 0) {
    //             return ByteSegment.empty(false);
    //         }
    //         int readSize = (int) Math.min(size, remaining);
    //         ByteSegment seg = source.read(readSize);
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
    //
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
    //     private @Nonnull ByteSegment makeTrue(@Nonnull ByteSegment seg) {
    //         if (seg instanceof ByteSegmentImpl) {
    //             ((ByteSegmentImpl) seg).end = true;
    //             return seg;
    //         }
    //         return ByteSegment.of(seg.data(), true);
    //     }
    // }
}
