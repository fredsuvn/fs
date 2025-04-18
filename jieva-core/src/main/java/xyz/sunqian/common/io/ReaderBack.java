package xyz.sunqian.common.io;

import xyz.sunqian.common.base.JieCheck;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.chars.JieChars;

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

    static CharReader of(CharSequence source, int offset, int length) throws IndexOutOfBoundsException {
        return new CharSequenceReader(source, offset, length);
    }

    static CharReader of(CharBuffer source) {
        return new CharBufferReader(source);
    }

    static CharReader of(CharReader source, long readLimit) throws IllegalArgumentException {
        return new LimitedCharReader(source, readLimit);
    }

    private static void checkSize(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("The size must be >= 0.");
        }
    }

    private static void checkReadLimit(long readLimit) {
        if (readLimit < 0) {
            throw new IllegalArgumentException("The read limit must be >= 0.");
        }
    }

    private static final class ByteBlock implements ByteSegment {

        private static final ByteBlock EMPTY_END = new ByteBlock(JieBytes.emptyBuffer(), true);
        private static final ByteBlock EMPTY_SEG = new ByteBlock(JieBytes.emptyBuffer(), false);

        public static ByteBlock empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final ByteBuffer data;
        private boolean end;

        private ByteBlock(ByteBuffer data, boolean end) {
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
            return new ByteBlock(copy, end);
        }
    }

    private static final class ByteStreamReader implements ByteReader {

        private final InputStream source;
        private boolean end = false;

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

        private ByteSegment read0(int size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return ByteBlock.empty(end);
            }
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
                return ByteBlock.empty(true);
            }
            ByteBuffer data = ByteBuffer.wrap(
                hasRead == size ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            );
            return new ByteBlock(data, end);
        }
    }

    private static final class ByteArrayReader implements ByteReader {

        private final byte[] source;
        private final int endPos;
        private int pos;

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
                return ByteBlock.EMPTY_END;
            }
            if (size == 0) {
                return ByteBlock.EMPTY_SEG;
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                ByteBuffer data = ByteBuffer.wrap(source, pos, size).slice();
                pos += size;
                return new ByteBlock(data, false);
            }
            ByteBuffer data = ByteBuffer.wrap(source, pos, remaining).slice();
            pos += remaining;
            return new ByteBlock(data, true);
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
                return ByteBlock.EMPTY_END;
            }
            if (size == 0) {
                return ByteBlock.EMPTY_SEG;
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + size, limit);
            source.limit(newPos);
            ByteBuffer data = source.slice();
            source.position(newPos);
            return new ByteBlock(data, newPos >= limit);
        }
    }

    private static final class LimitedByteReader implements ByteReader {

        private final ByteReader source;
        private long remaining;

        LimitedByteReader(ByteReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
        }

        @Override
        public ByteSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return ByteBlock.EMPTY_END;
            }
            if (size == 0) {
                return ByteBlock.EMPTY_SEG;
            }
            int readSize = (int) Math.min(size, remaining);
            ByteBlock block = (ByteBlock) source.read(readSize, endOnZeroRead);
            remaining -= readSize;
            if (remaining <= 0 && !block.end) {
                block.end = true;
            }
            return block;
        }
    }

    private static final class CharBlock implements CharSegment {

        private static final CharBlock EMPTY_END = new CharBlock(JieChars.emptyBuffer(), true);
        private static final CharBlock EMPTY_SEG = new CharBlock(JieChars.emptyBuffer(), false);

        public static CharBlock empty(boolean end) {
            return end ? EMPTY_END : EMPTY_SEG;
        }

        private final CharBuffer data;
        private boolean end;

        private CharBlock(CharBuffer data, boolean end) {
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
            return new CharBlock(copy, end);
        }
    }

    private static final class CharStreamReader implements CharReader {

        private final Reader source;
        private boolean end = false;

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

        private CharSegment read0(int size, boolean endOnZeroRead) throws Exception {
            if (size == 0) {
                return CharBlock.empty(end);
            }
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
                return CharBlock.empty(true);
            }
            CharBuffer data = CharBuffer.wrap(
                hasRead == size ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            );
            return new CharBlock(data, end);
        }
    }

    private static final class CharArrayReader implements CharReader {

        private final char[] source;
        private final int endPos;
        private int pos;

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
                return CharBlock.EMPTY_END;
            }
            if (size == 0) {
                return CharBlock.EMPTY_SEG;
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                CharBuffer data = CharBuffer.wrap(source, pos, size).slice();
                pos += size;
                return new CharBlock(data, false);
            }
            CharBuffer data = CharBuffer.wrap(source, pos, remaining).slice();
            pos += remaining;
            return new CharBlock(data, true);
        }
    }

    private static final class CharSequenceReader implements CharReader {

        private final CharSequence source;
        private final int endPos;
        private int pos;

        CharSequenceReader(CharSequence source, int offset, int length) throws IndexOutOfBoundsException {
            JieCheck.checkOffsetLength(source.length(), offset, length);
            this.source = source;
            this.pos = offset;
            this.endPos = offset + length;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (pos == endPos) {
                return CharBlock.EMPTY_END;
            }
            if (size == 0) {
                return CharBlock.EMPTY_SEG;
            }
            int remaining = endPos - pos;
            if (remaining >= size) {
                CharBuffer data = CharBuffer.wrap(source, pos, size).slice();
                pos += size;
                return new CharBlock(data, false);
            }
            CharBuffer data = CharBuffer.wrap(source, pos, remaining).slice();
            pos += remaining;
            return new CharBlock(data, true);
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
                return CharBlock.EMPTY_END;
            }
            if (size == 0) {
                return CharBlock.EMPTY_SEG;
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + size, limit);
            source.limit(newPos);
            CharBuffer data = source.slice();
            source.position(newPos);
            return new CharBlock(data, newPos >= limit);
        }
    }

    private static final class LimitedCharReader implements CharReader {

        private final CharReader source;
        private long remaining;

        LimitedCharReader(CharReader source, long readLimit) throws IllegalArgumentException {
            checkReadLimit(readLimit);
            this.source = source;
        }

        @Override
        public CharSegment read(int size, boolean endOnZeroRead) throws IllegalArgumentException, IORuntimeException {
            checkSize(size);
            if (remaining <= 0) {
                return CharBlock.EMPTY_END;
            }
            if (size == 0) {
                return CharBlock.EMPTY_SEG;
            }
            int readSize = (int) Math.min(size, remaining);
            CharBlock block = (CharBlock) source.read(readSize, endOnZeroRead);
            remaining -= readSize;
            if (remaining <= 0 && !block.end) {
                block.end = true;
            }
            return block;
        }
    }
}
