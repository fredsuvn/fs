package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieString;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

final class CharStreamImpl implements CharStream {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean breakOnZeroRead = false;
    private Encoder encoder;

    CharStreamImpl(Reader source) {
        this.source = source;
    }

    CharStreamImpl(char[] source) {
        this.source = source;
    }

    CharStreamImpl(CharBuffer source) {
        this.source = source;
    }

    CharStreamImpl(CharSequence source) {
        this.source = source;
    }

    @Override
    public CharStream to(Appendable dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public CharStream to(char[] dest) {
        this.dest = CharBuffer.wrap(dest);
        return this;
    }

    @Override
    public CharStream to(char[] dest, int offset, int length) {
        try {
            this.dest = CharBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    @Override
    public CharStream to(CharBuffer dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public CharStream readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public CharStream blockSize(int blockSize) {
        if (blockSize <= 0) {
            throw new IORuntimeException("blockSize must > 0!");
        }
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public CharStream breakOnZeroRead(boolean breakOnZeroRead) {
        this.breakOnZeroRead = breakOnZeroRead;
        return this;
    }

    @Override
    public CharStream encoder(Encoder encoder) {
        this.encoder = encoder;
        return this;
    }

    @Override
    public long start() throws IORuntimeException {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readLimit == 0) {
            return 0;
        }
        try {
            BufferIn in = toBufferIn(source);
            BufferOut out = toBufferOut(dest);
            return readTo(in, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof Reader) {
            return new ReaderBufferIn((Reader) src, actualBlockSize, readLimit);
        }
        if (src instanceof char[]) {
            return new CharsBufferIn((char[]) src, actualBlockSize, readLimit);
        }
        if (src instanceof CharBuffer) {
            return new BufferBufferIn((CharBuffer) src, actualBlockSize, readLimit);
        }
        if (src instanceof CharSequence) {
            return new StringBufferIn((CharSequence) src, actualBlockSize, readLimit);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof CharBuffer) {
            return new AppendableBufferOut(JieOutput.wrap((CharBuffer) dst));
        }
        if (dst instanceof Appendable) {
            return new AppendableBufferOut((Appendable) dst);
        }
        throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
    }

    private int getActualBlockSize() {
        if (readLimit < 0) {
            return blockSize;
        }
        return (int) Math.min(readLimit, blockSize);
    }

    private long readTo(BufferIn in, BufferOut out) throws Exception {
        long count = 0;
        while (true) {
            CharBuffer buf = in.read();
            if (buf == null) {
                return count == 0 ? -1 : count;
            }
            if (!buf.hasRemaining()) {
                if (breakOnZeroRead) {
                    return count;
                }
                continue;
            }
            count += buf.remaining();
            if (encoder != null) {
                CharBuffer converted = encoder.encode(buf);
                out.write(converted);
            } else {
                out.write(buf);
            }
        }
    }

    private interface BufferIn {
        @Nullable
        CharBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(CharBuffer buffer) throws Exception;
    }

    private static final class ReaderBufferIn implements BufferIn {

        private final Reader source;
        private final char[] block;
        private final CharBuffer blockBuffer;
        private final long limit;
        private long remaining;

        private ReaderBufferIn(Reader source, int blockSize, long limit) {
            this.source = source;
            this.block = new char[limit < 0 ? blockSize : (int) Math.min(blockSize, limit)];
            this.blockBuffer = CharBuffer.wrap(block);
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() throws IOException {
            int readSize = limit < 0 ? block.length : (int) Math.min(remaining, block.length);
            if (readSize <= 0) {
                return null;
            }
            int size = source.read(block, 0, readSize);
            if (size < 0) {
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(size);
            if (limit > 0) {
                remaining -= size;
            }
            return blockBuffer;
        }
    }

    private static final class CharsBufferIn implements BufferIn {

        private final char[] source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;

        private CharsBufferIn(char[] source, int blockSize, long limit) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class BufferBufferIn implements BufferIn {

        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;
        private final int sourceRemaining;

        private BufferBufferIn(CharBuffer source, int blockSize, long limit) {
            this.sourceBuffer = source.slice();
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
            this.sourceRemaining = source.remaining();
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= sourceRemaining) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, sourceRemaining);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class StringBufferIn implements BufferIn {

        private final CharSequence source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;

        private StringBufferIn(CharSequence source, int blockSize, long limit) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public CharBuffer read() {
            int readSize = limit < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length()) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length());
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (limit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private static final class AppendableBufferOut implements BufferOut {

        private final Appendable dest;

        private AppendableBufferOut(Appendable dest) {
            this.dest = dest;
        }

        @Override
        public void write(CharBuffer buffer) throws IOException {
            if (dest instanceof Writer) {
                write(buffer, (Writer) dest);
                return;
            }
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                int start = buffer.arrayOffset() + buffer.position();
                dest.append(JieString.asChars(buffer.array(), start, start + remaining));
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                dest.append(JieString.asChars(buf, 0, buf.length));
            }
        }

        private void write(CharBuffer buffer, Writer writer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                writer.write(buffer.array(), buffer.arrayOffset() + buffer.position(), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                writer.write(buf);
            }
        }
    }
}
