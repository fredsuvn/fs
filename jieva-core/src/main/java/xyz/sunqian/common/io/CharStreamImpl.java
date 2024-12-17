package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.coll.JieArray;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

final class CharStreamImpl implements CharStream {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
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
    public CharStream endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public CharStream encoder(Encoder encoder) {
        this.encoder = encoder;
        return this;
    }

    @Override
    public long writeTo(Appendable dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long writeTo(char[] dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long writeTo(char[] dest, int offset, int length) {
        try {
            this.dest = CharBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return start();
    }

    @Override
    public long writeTo(CharBuffer dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long writeTo() {
        this.dest = NullBufferOut.SINGLETON;
        return start();
    }

    @Override
    public Reader asReader() {
        return new ReaderIn(toBufferIn(source));
    }

    private long start() {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readLimit == 0) {
            return 0;
        }
        try {
            if (encoder == null) {
                if (source instanceof char[]) {
                    if (dest instanceof char[]) {
                        return charsToChars((char[]) source, (char[]) dest);
                    }
                    if (dest instanceof CharBuffer) {
                        return charsToBuffer((char[]) source, (CharBuffer) dest);
                    }
                    return charsToAppender((char[]) source, (Appendable) dest);
                } else if (source instanceof CharBuffer) {
                    if (dest instanceof char[]) {
                        return bufferToChars((CharBuffer) source, (char[]) dest);
                    }
                    return bufferToAppender((CharBuffer) source, (Appendable) dest);
                } else if (source instanceof CharSequence) {
                    if (dest instanceof char[]) {
                        return charSeqToChars((CharSequence) source, (char[]) dest);
                    }
                    return charSeqToAppender((CharSequence) source, (Appendable) dest);
                }
            }
            return startInBlock();
        } catch (IOEncodingException e) {
            throw e;
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private long charsToChars(char[] src, char[] dst) {
        if (src.length == 0) {
            return -1;
        }
        int len = getDirectLen(src.length);
        System.arraycopy(src, 0, dst, 0, len);
        return len;
    }

    private long charsToBuffer(char[] src, CharBuffer dst) {
        if (src.length == 0) {
            return -1;
        }
        int len = getDirectLen(src.length);
        dst.put(src, 0, len);
        return len;
    }

    private long charsToAppender(char[] src, Appendable dst) throws IOException {
        if (src.length == 0) {
            return -1;
        }
        int len = getDirectLen(src.length);
        dst.append(JieString.asChars(src, 0, len));
        return len;
    }

    private long bufferToChars(CharBuffer src, char[] dst) {
        if (src.remaining() == 0) {
            return -1;
        }
        int len = getDirectLen(src.remaining());
        src.get(dst, 0, len);
        return len;
    }

    private long bufferToAppender(CharBuffer src, Appendable dst) throws IOException {
        if (src.remaining() == 0) {
            return -1;
        }
        int len = getDirectLen(src.remaining());
        int pos = src.position();
        int newPos = pos + len;
        dst.append(src, 0, len);
        src.position(newPos);
        return len;
    }

    private long charSeqToChars(CharSequence src, char[] dst) throws IOException {
        if (src.length() == 0) {
            return -1;
        }
        int len = getDirectLen(src.length());
        if (src instanceof String) {
            ((String) src).getChars(0, len, dst, 0);
        } else {
            for (int i = 0; i < len; i++) {
                dst[i] = src.charAt(i);
            }
        }
        return len;
    }

    private long charSeqToAppender(CharSequence src, Appendable dst) throws IOException {
        if (src.length() == 0) {
            return -1;
        }
        int len = getDirectLen(src.length());
        dst.append(src, 0, len);
        return len;
    }

    private int getDirectLen(int srcSize) {
        return readLimit < 0 ? srcSize : Math.min(srcSize, (int) readLimit);
    }

    private long startInBlock() throws Exception {
        BufferIn in = toBufferIn(source);
        BufferOut out = toBufferOut(dest);
        return readTo(in, out);
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof Reader) {
            return new ReaderBufferIn((Reader) src, actualBlockSize);
        }
        if (src instanceof char[]) {
            return new CharsBufferIn((char[]) src, actualBlockSize);
        }
        if (src instanceof CharBuffer) {
            return new BufferBufferIn((CharBuffer) src, actualBlockSize);
        }
        if (src instanceof CharSequence) {
            return new CharSeqBufferIn((CharSequence) src, actualBlockSize);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof BufferOut) {
            return (BufferOut) dst;
        }
        if (dst instanceof char[]) {
            return new AppendableBufferOut(CharBuffer.wrap((char[]) dst));
        }
        if (dst instanceof CharBuffer) {
            return new AppendableBufferOut(JieIO.writer((CharBuffer) dst));
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
            if (buf == null && count == 0) {
                return -1;
            }
            buf = buf == null ? JieChars.emptyBuffer() : buf.asReadOnlyBuffer();
            if (!buf.hasRemaining()) {
                if (encoder != null) {
                    CharBuffer encoded = encode(buf, true);
                    out.write(encoded);
                }
                break;
            }
            int readSize = buf.remaining();
            count += readSize;
            if (encoder != null) {
                CharBuffer encoded;
                if (readSize < blockSize) {
                    encoded = encode(buf, true);
                    out.write(encoded);
                    break;
                } else {
                    encoded = encode(buf, false);
                    out.write(encoded);
                }
            } else {
                out.write(buf);
                if (readSize < blockSize) {
                    break;
                }
            }
        }
        return count;
    }

    private CharBuffer encode(CharBuffer buf, boolean end) {
        try {
            return encoder.encode(buf, end);
        } catch (Exception e) {
            throw new IOEncodingException(e);
        }
    }

    private interface BufferIn {
        @Nullable
        CharBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(CharBuffer buffer) throws Exception;
    }

    private final class ReaderBufferIn implements BufferIn {

        private final Reader source;
        private final char[] block;
        private final CharBuffer blockBuffer;
        private long remaining;

        private ReaderBufferIn(Reader source, int blockSize) {
            this.source = source;
            this.block = new char[readLimit < 0 ? blockSize : (int) Math.min(blockSize, readLimit)];
            this.blockBuffer = CharBuffer.wrap(block);
            this.remaining = readLimit;
        }

        @Override
        public CharBuffer read() throws IOException {
            int readSize = remaining < 0 ? block.length : (int) Math.min(remaining, block.length);
            if (readSize == 0) {
                return JieChars.emptyBuffer();
            }
            int hasRead = 0;
            boolean zeroRead = false;
            while (hasRead < readSize) {
                int size = source.read(block, hasRead, readSize - hasRead);
                if (size < 0) {
                    break;
                }
                if (size == 0 && endOnZeroRead) {
                    zeroRead = true;
                    break;
                }
                hasRead += size;
            }
            if (hasRead == 0) {
                if (zeroRead) {
                    return JieChars.emptyBuffer();
                }
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(hasRead);
            if (readLimit > 0) {
                remaining -= hasRead;
            }
            return blockBuffer;
        }
    }

    private final class CharsBufferIn implements BufferIn {

        private final char[] source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private long remaining;

        private CharsBufferIn(char[] source, int blockSize) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.remaining = readLimit;
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return JieChars.emptyBuffer();
            }
            if (pos >= source.length) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (readLimit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private final class BufferBufferIn implements BufferIn {

        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private long remaining;
        private final int sourceRemaining;

        private BufferBufferIn(CharBuffer source, int blockSize) {
            this.sourceBuffer = source.slice();
            this.blockSize = blockSize;
            this.remaining = readLimit;
            this.sourceRemaining = source.remaining();
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return JieChars.emptyBuffer();
            }
            if (pos >= sourceRemaining) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, sourceRemaining);
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (readLimit > 0) {
                remaining -= size;
            }
            return sourceBuffer;
        }
    }

    private final class CharSeqBufferIn implements BufferIn {

        private final CharSequence source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private long remaining;

        private CharSeqBufferIn(CharSequence source, int blockSize) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
            this.remaining = readLimit;
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return JieChars.emptyBuffer();
            }
            if (pos >= source.length()) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + readSize, source.length());
            sourceBuffer.limit(newPos);
            int size = newPos - pos;
            pos = newPos;
            if (readLimit > 0) {
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
                dest.append(JieString.asChars(
                    buffer.array(),
                    JieBuffer.getArrayStartIndex(buffer),
                    JieBuffer.getArrayEndIndex(buffer)
                ));
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
                writer.write(buffer.array(), JieBuffer.getArrayStartIndex(buffer), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                writer.write(buf);
            }
        }
    }

    private static final class NullBufferOut implements BufferOut {

        static final NullBufferOut SINGLETON = new NullBufferOut();

        @Override
        public void write(CharBuffer buffer) {
            // Do nothing
        }
    }

    private final class ReaderIn extends Reader {

        private final BufferIn in;
        private CharBuffer buffer = JieChars.emptyBuffer();

        // 0-init, 1-processing, 2-end, 3-closed
        private int state = 0;

        private ReaderIn(BufferIn in) {
            this.in = in;
        }

        @Nullable
        private CharBuffer read0() throws IOException {
            if (state == 2) {
                return null;
            }
            try {
                CharBuffer buf = in.read();
                if (buf == null || !buf.hasRemaining()) {
                    if (state == 0) {
                        state = 2;
                        return null;
                    }
                    state = 2;
                    if (encoder == null) {
                        return null;
                    }
                    CharBuffer ret = encoder.encode(JieChars.emptyBuffer(), true);
                    if (ret.hasRemaining()) {
                        return ret;
                    }
                    return null;
                }
                if (state == 0) {
                    state = 1;
                }
                if (encoder == null) {
                    return buf;
                }
                return encoder.encode(buf.slice().asReadOnlyBuffer(), false);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            if (buffer == null) {
                return -1;
            }
            if (buffer.hasRemaining()) {
                return buffer.get() & 0xff;
            }
            CharBuffer newBuf = read0();
            if (newBuf == null) {
                buffer = null;
                return -1;
            }
            buffer = newBuf;
            return buffer.get() & 0xff;
        }

        @Override
        public int read(char[] dst, int off, int len) throws IOException {
            checkClosed();
            IOMisc.checkReadBounds(dst, off, len);
            if (len <= 0) {
                return 0;
            }
            if (buffer == null) {
                return -1;
            }
            final int endPos = off + len;
            int pos = off;
            while (pos < endPos) {
                if (!buffer.hasRemaining()) {
                    CharBuffer newBuf = read0();
                    if (newBuf == null) {
                        buffer = null;
                        return pos - off;
                    }
                    buffer = newBuf;
                }
                int getLen = Math.min(buffer.remaining(), endPos - pos);
                buffer.get(dst, pos, getLen);
                pos += getLen;
            }
            return pos - off;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0 || buffer == null) {
                return 0;
            }
            int pos = 0;
            while (pos < n) {
                if (!buffer.hasRemaining()) {
                    CharBuffer newBuf = read0();
                    if (newBuf == null) {
                        buffer = null;
                        return pos;
                    }
                    buffer = newBuf;
                }
                int getLen = (int) Math.min(buffer.remaining(), n - pos);
                buffer.position(buffer.position() + getLen);
                pos += getLen;
            }
            return pos;
        }

        @Override
        public void close() throws IOException {
            if (state == 3) {
                return;
            }
            if (source instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) source).close();
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            state = 3;
        }

        private void checkClosed() throws IOException {
            if (state == 3) {
                throw new IOException("Reader closed.");
            }
        }
    }

    private static abstract class AbsEncoder implements CharStream.Encoder {

        protected final CharStream.Encoder encoder;
        protected char[] buf = JieChars.emptyChars();

        protected AbsEncoder(CharStream.Encoder encoder) {
            this.encoder = encoder;
        }

        protected CharBuffer totalData(CharBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            CharBuffer total = CharBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
            return total;
        }

        protected int totalSize(CharBuffer data) {
            return buf.length + data.remaining();
        }
    }

    static final class RoundEncoder extends AbsEncoder {

        private final int expectedBlockSize;

        RoundEncoder(CharStream.Encoder encoder, int expectedBlockSize) {
            super(encoder);
            this.expectedBlockSize = expectedBlockSize;
        }

        @Override
        public CharBuffer encode(CharBuffer data, boolean end) {
            if (end) {
                return encoder.encode(totalData(data), true);
            }
            int size = totalSize(data);
            if (size == expectedBlockSize) {
                CharBuffer total = totalData(data);
                buf = JieChars.emptyChars();
                return encoder.encode(total, false);
            }
            if (size < expectedBlockSize) {
                char[] newBuf = new char[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                data.get(newBuf, buf.length, data.remaining());
                buf = newBuf;
                return JieChars.emptyBuffer();
            }
            int remainder = size % expectedBlockSize;
            if (remainder == 0) {
                CharBuffer total = totalData(data);
                buf = JieChars.emptyChars();
                return encoder.encode(total, false);
            }
            int roundSize = size / expectedBlockSize * expectedBlockSize;
            CharBuffer round = roundData(data, roundSize);
            buf = new char[remainder];
            data.get(buf);
            return encoder.encode(round, false);
        }

        private CharBuffer roundData(CharBuffer data, int roundSize) {
            CharBuffer round = CharBuffer.allocate(roundSize);
            round.put(buf);
            int sliceSize = roundSize - buf.length;
            CharBuffer slice = JieChars.slice(data, 0, sliceSize);
            data.position(data.position() + sliceSize);
            round.put(slice);
            round.flip();
            return round;
        }
    }

    static final class BufferedEncoder extends AbsEncoder {

        BufferedEncoder(CharStream.Encoder encoder) {
            super(encoder);
        }

        @Override
        public CharBuffer encode(CharBuffer data, boolean end) {
            CharBuffer total = totalData(data);
            CharBuffer ret = encoder.encode(total, end);
            if (end) {
                return ret;
            }
            if (total.hasRemaining()) {
                buf = new char[total.remaining()];
                total.get(buf);
                return ret;
            }
            buf = JieChars.emptyChars();
            return ret;
        }
    }

    static final class FixedSizeEncoder extends AbsEncoder {

        private final int size;

        FixedSizeEncoder(Encoder encoder, int size) {
            super(encoder);
            this.size = size;
        }

        @Override
        public CharBuffer encode(CharBuffer data, boolean end) {
            CharBuffer total = totalData(data);
            int totalSize = total.remaining();
            int times = totalSize / size;
            if (times == 0) {
                if (end) {
                    return encoder.encode(total, true);
                }
                buf = new char[totalSize];
                total.get(buf);
                return JieChars.emptyBuffer();
            }
            if (times == 1) {
                CharBuffer slice = JieChars.slice(total, 0, size);
                CharBuffer ret1 = encoder.encode(slice, false);
                total.position(total.position() + size);
                if (end) {
                    CharBuffer ret2 = encoder.encode(total, true);
                    int retSize1 = ret1.remaining();
                    int retSize2 = ret2.remaining();
                    char[] ret = new char[retSize1 + retSize2];
                    ret1.get(ret, 0, retSize1);
                    ret2.get(ret, retSize1, retSize2);
                    return CharBuffer.wrap(ret);
                }
                buf = new char[total.remaining()];
                total.get(buf);
                return ret1;
            }
            StringBuilder charsBuilder = new StringBuilder();
            for (int i = 0; i < times; i++) {
                CharBuffer slice = JieChars.slice(total, 0, size);
                CharBuffer ret = encoder.encode(slice, false);
                total.position(total.position() + size);
                charsBuilder.append(ret);
            }
            if (end) {
                CharBuffer ret2 = encoder.encode(total, true);
                charsBuilder.append(ret2);
            } else {
                buf = new char[total.remaining()];
                total.get(buf);
            }
            return CharBuffer.wrap(charsBuilder.toString());
        }
    }
}
