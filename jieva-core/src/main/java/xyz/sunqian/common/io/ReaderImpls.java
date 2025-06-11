package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

final class ReaderImpls {

    static @Nonnull ByteReader of(@Nonnull InputStream source) {
        return new ByteStreamReader(source);
    }

    static @Nonnull ByteReader of(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
        return new ByteArrayReader(source, offset, length);
    }

    static @Nonnull ByteReader of(@Nonnull ByteBuffer source) {
        return new ByteBufferReader(source);
    }

    static @Nonnull ByteReader of(@Nonnull ByteReader source, long readLimit) throws IllegalArgumentException {
        return new LimitedByteReader(source, readLimit);
    }

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

    static @Nonnull CharReader of(@Nonnull CharReader source, long readLimit) throws IllegalArgumentException {
        return new LimitedCharReader(source, readLimit);
    }

    private static void checkSize(int size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be >= 0.");
        }
    }

    private static void checkSize(long size) throws IllegalArgumentException {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be >= 0.");
        }
    }

    private static void checkReadLimit(long readLimit) throws IllegalArgumentException {
        if (readLimit < 0) {
            throw new IllegalArgumentException("The read limit must be >= 0.");
        }
    }

    static final class ByteSegmentImpl implements ByteSegment {

        private static final @Nonnull ByteSegmentImpl EMPTY_END = new ByteSegmentImpl(JieBytes.emptyBuffer(), true);
        private static final @Nonnull ByteSegmentImpl EMPTY_SEG = new ByteSegmentImpl(JieBytes.emptyBuffer(), false);

        public static @Nonnull ByteSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final @Nonnull ByteBuffer data;
        private boolean end;

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

    static final class CharSegmentImpl implements CharSegment {

        private static final @Nonnull CharSegmentImpl EMPTY_END = new CharSegmentImpl(JieChars.emptyBuffer(), true);
        private static final @Nonnull CharSegmentImpl EMPTY_SEG = new CharSegmentImpl(JieChars.emptyBuffer(), false);

        public static @Nonnull CharSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final @Nonnull CharBuffer data;
        private boolean end;

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

    private static final class ByteStreamReader implements ByteReader {

        private final @Nonnull InputStream source;

        ByteStreamReader(@Nonnull InputStream source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            try {
                return read0(size, endOnZeroRead);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            try {
                return skip0(size, endOnZeroRead);
            } catch (Exception e) {
                throw new IORuntimeException(e);
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

        private @Nonnull ByteSegment read0(int size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            boolean end = false;
            int hasRead = 0;
            byte[] buf = new byte[size];
            while (hasRead < size) {
                int onceSize = source.read(buf, hasRead, size - hasRead);
                if (onceSize < 0) {
                    end = true;
                    break;
                }
                if (onceSize == 0) {
                    if (endOnZeroRead) {
                        end = true;
                        break;
                    } else {
                        continue;
                    }
                }
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

        private long skip0(long size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return 0;
            }
            long hasRead = 0;
            while (hasRead < size) {
                long onceSize = source.skip(size - hasRead);
                if (onceSize == 0) {
                    if (endOnZeroRead) {
                        break;
                    }
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
    }

    private static final class ByteArrayReader implements ByteReader {

        private final byte @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        ByteArrayReader(byte @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
        }

        @Override
        public @Nonnull ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return ByteSegment.empty(true);
            }
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                ByteBuffer data = ByteBuffer.wrap(source, pos, size).slice();
                pos += size;
                return ByteSegment.of(data, remaining == size);
            }
            ByteBuffer data = ByteBuffer.wrap(source, pos, remaining).slice();
            pos += remaining;
            return ByteSegment.of(data, true);
        }

        @Override
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return 0;
            }
            if (size == 0) {
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

    private static final class ByteBufferReader implements ByteReader {

        private final @Nonnull ByteBuffer source;

        ByteBufferReader(@Nonnull ByteBuffer source) {
            this.source = source;
        }

        @Override
        public @Nonnull ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (!source.hasRemaining()) {
                return ByteSegment.empty(true);
            }
            if (size == 0) {
                return ByteSegment.empty(false);
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
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (!source.hasRemaining()) {
                return 0;
            }
            if (size == 0) {
                return 0;
            }
            int pos = source.position();
            int newPos = (int) Math.min(pos + size, source.limit());
            source.position(newPos);
            return newPos - pos;
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

    private static final class LimitedByteReader implements ByteReader {

        private final @Nonnull ByteReader source;
        private long remaining;
        private long mark;

        LimitedByteReader(@Nonnull ByteReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
            this.remaining = readLimit;
        }

        @Override
        public @Nonnull ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return ByteSegment.empty(true);
            }
            if (size == 0) {
                return ByteSegment.empty(false);
            }
            int readSize = (int) Math.min(size, remaining);
            ByteSegment seg = source.read(readSize, endOnZeroRead);
            remaining -= readSize;
            if (remaining <= 0 && !seg.end()) {
                return makeTrue(seg);
            }
            return seg;
        }

        @Override
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return 0;
            }
            if (size == 0) {
                return 0;
            }
            long readSize = Math.min(size, remaining);
            long skipped = source.skip(readSize, endOnZeroRead);
            remaining -= skipped;
            return skipped;
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            if (source.markSupported()) {
                source.mark();
                mark = remaining;
            }

        }

        @Override
        public void reset() throws IORuntimeException {
            if (source.markSupported()) {
                source.reset();
                remaining = mark;
            }
        }

        @Override
        public void close() throws IORuntimeException {
            source.close();
        }

        private @Nonnull ByteSegment makeTrue(@Nonnull ByteSegment seg) {
            if (seg instanceof ByteSegmentImpl) {
                ((ByteSegmentImpl) seg).end = true;
                return seg;
            }
            return ByteSegment.of(seg.data(), true);
        }
    }

    private static final class CharStreamReader implements CharReader {

        private final @Nonnull Reader source;

        CharStreamReader(@Nonnull Reader source) {
            this.source = source;
        }

        @Override
        public @Nonnull CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            try {
                return read0(size, endOnZeroRead);
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            try {
                return skip0(size, endOnZeroRead);
            } catch (Exception e) {
                throw new IORuntimeException(e);
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

        private @Nonnull CharSegment read0(int size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return CharSegment.empty(false);
            }
            boolean end = false;
            int hasRead = 0;
            char[] buf = new char[size];
            while (hasRead < size) {
                int onceSize = source.read(buf, hasRead, size - hasRead);
                if (onceSize < 0) {
                    end = true;
                    break;
                }
                if (onceSize == 0) {
                    if (endOnZeroRead) {
                        end = true;
                        break;
                    } else {
                        continue;
                    }
                }
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

        private long skip0(long size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return 0;
            }
            long hasRead = 0;
            while (hasRead < size) {
                long onceSize = source.skip(size - hasRead);
                if (onceSize == 0) {
                    if (endOnZeroRead) {
                        break;
                    }
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
    }

    private static final class CharArrayReader implements CharReader {

        private final char @Nonnull [] source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        CharArrayReader(char @Nonnull [] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
        }

        @Override
        public @Nonnull CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            if (size == 0) {
                return CharSegment.empty(false);
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
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return 0;
            }
            if (size == 0) {
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

    private static final class CharSequenceReader implements CharReader {

        private final @Nonnull CharSequence source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        CharSequenceReader(@Nonnull CharSequence source, int start, int end) throws IndexOutOfBoundsException {
            JieCheck.checkStartEnd(source.length(), start, end);
            this.source = source;
            this.pos = start;
            this.endPos = end;
        }

        @Override
        public @Nonnull CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return CharSegment.empty(true);
            }
            if (size == 0) {
                return CharSegment.empty(false);
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
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return 0;
            }
            if (size == 0) {
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
        public @Nonnull CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (!source.hasRemaining()) {
                return CharSegment.empty(true);
            }
            if (size == 0) {
                return CharSegment.empty(false);
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
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (!source.hasRemaining()) {
                return 0;
            }
            if (size == 0) {
                return 0;
            }
            int pos = source.position();
            int newPos = (int) Math.min(pos + size, source.limit());
            source.position(newPos);
            return newPos - pos;
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

    private static final class LimitedCharReader implements CharReader {

        private final @Nonnull CharReader source;
        private long remaining;
        private long mark;

        LimitedCharReader(@Nonnull CharReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
            this.remaining = readLimit;
        }

        @Override
        public @Nonnull CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return CharSegment.empty(true);
            }
            if (size == 0) {
                return CharSegment.empty(false);
            }
            int readSize = (int) Math.min(size, remaining);
            CharSegment seg = source.read(readSize, endOnZeroRead);
            remaining -= readSize;
            if (remaining <= 0 && !seg.end()) {
                return makeTrue(seg);
            }
            return seg;
        }

        @Override
        public long skip(long size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return 0;
            }
            if (size == 0) {
                return 0;
            }
            long readSize = Math.min(size, remaining);
            long skipped = source.skip(readSize, endOnZeroRead);
            remaining -= skipped;
            return skipped;
        }

        @Override
        public boolean markSupported() {
            return source.markSupported();
        }

        @Override
        public void mark() throws IORuntimeException {
            if (source.markSupported()) {
                source.mark();
                mark = remaining;
            }
        }

        @Override
        public void reset() throws IORuntimeException {
            if (source.markSupported()) {
                source.reset();
                remaining = mark;
            }
        }

        @Override
        public void close() throws IORuntimeException {
            source.close();
        }

        private @Nonnull CharSegment makeTrue(@Nonnull CharSegment seg) {
            if (seg instanceof CharSegmentImpl) {
                ((CharSegmentImpl) seg).end = true;
                return seg;
            }
            return CharSegment.of(seg.data(), true);
        }
    }
}
