package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Arrays;

final class ReaderBack {

    static ByteReader of(InputStream source) {
        return new ByteStreamReader(source);
    }

    static ByteReader of(byte[] source, int offset, int length) throws IndexOutOfBoundsException {
        return new ByteArrayReader(source, offset, length);
    }

    static ByteReader of(ByteBuffer source) {
        return new ByteBufferReader(source);
    }

    static ByteReader of(ByteReader source, long readLimit) throws IllegalArgumentException {
        return new LimitedByteReader(source, readLimit);
    }

    static CharReader of(Reader source) {
        return new CharStreamReader(source);
    }

    static CharReader of(char[] source, int offset, int length) throws IndexOutOfBoundsException {
        return new CharArrayReader(source, offset, length);
    }

    static CharReader of(CharSequence source, int start, int end) throws IndexOutOfBoundsException {
        return new CharSequenceReader(source, start, end);
    }

    static CharReader of(CharBuffer source) {
        return new CharBufferReader(source);
    }

    static CharReader of(CharReader source, long readLimit) throws IllegalArgumentException {
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

        private static final ByteSegmentImpl EMPTY_END = new ByteSegmentImpl(JieBytes.emptyBuffer(), true);
        private static final ByteSegmentImpl EMPTY_SEG = new ByteSegmentImpl(JieBytes.emptyBuffer(), false);

        public static ByteSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final ByteBuffer data;
        private boolean end;

        ByteSegmentImpl(ByteBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }

        @Override
        public ByteBuffer data() {
            return data;
        }

        @Override
        public boolean end() {
            return end;
        }

        @Override
        public ByteSegment clone() {
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

        private static final CharSegmentImpl EMPTY_END = new CharSegmentImpl(JieChars.emptyBuffer(), true);
        private static final CharSegmentImpl EMPTY_SEG = new CharSegmentImpl(JieChars.emptyBuffer(), false);

        public static CharSegmentImpl empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final CharBuffer data;
        private boolean end;

        CharSegmentImpl(CharBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }

        @Override
        public CharBuffer data() {
            return data;
        }

        @Override
        public boolean end() {
            return end;
        }

        @Override
        public CharSegment clone() {
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

        private final InputStream source;

        ByteStreamReader(InputStream source) {
            this.source = source;
        }

        @Override
        public ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private ByteSegment read0(int size, boolean endOnZeroRead) throws Exception {
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

        private final byte[] source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        ByteArrayReader(byte[] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
        }

        @Override
        public ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private final ByteBuffer source;

        ByteBufferReader(ByteBuffer source) {
            this.source = source;
        }

        @Override
        public ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private final ByteReader source;
        private long remaining;
        private long mark;

        LimitedByteReader(ByteReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
            this.remaining = readLimit;
        }

        @Override
        public ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private ByteSegment makeTrue(ByteSegment seg) {
            if (seg instanceof ByteSegmentImpl) {
                ((ByteSegmentImpl) seg).end = true;
                return seg;
            }
            return ByteSegment.of(seg.data(), true);
        }
    }

    private static final class CharStreamReader implements CharReader {

        private final Reader source;

        CharStreamReader(Reader source) {
            this.source = source;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private CharSegment read0(int size, boolean endOnZeroRead) throws Exception {
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

        private final char[] source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        CharArrayReader(char[] source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length, offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private final CharSequence source;
        private final int endPos;
        private int pos;
        private int mark = 0;

        CharSequenceReader(CharSequence source, int start, int end) throws IndexOutOfBoundsException {
            JieCheck.checkStartEnd(source.length(), start, end);
            this.source = source;
            this.pos = start;
            this.endPos = end;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private final CharBuffer source;

        CharBufferReader(CharBuffer source) {
            this.source = source;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private final CharReader source;
        private long remaining;
        private long mark;

        LimitedCharReader(CharReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
            this.remaining = readLimit;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
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

        private CharSegment makeTrue(CharSegment seg) {
            if (seg instanceof CharSegmentImpl) {
                ((CharSegmentImpl) seg).end = true;
                return seg;
            }
            return CharSegment.of(seg.data(), true);
        }
    }
}
