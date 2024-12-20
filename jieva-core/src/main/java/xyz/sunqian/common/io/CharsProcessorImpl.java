package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieChars;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.coll.JieArray;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class CharsProcessorImpl implements CharsProcessor {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int readBlockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<Encoder> encoders;

    CharsProcessorImpl(Reader source) {
        this.source = source;
    }

    CharsProcessorImpl(char[] source) {
        this.source = source;
    }

    CharsProcessorImpl(CharBuffer source) {
        this.source = source;
    }

    CharsProcessorImpl(CharSequence source) {
        this.source = source;
    }

    @Override
    public CharsProcessor readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public CharsProcessor readBlockSize(int readBlockSize) {
        if (readBlockSize <= 0) {
            throw new IORuntimeException("readBlockSize must > 0!");
        }
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public CharsProcessor endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public CharsProcessor encoder(Encoder encoder) {
        if (encoders == null) {
            encoders = new ArrayList<>();
        }
        encoders.add(encoder);
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
    public Reader toReader() {
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
            if (encoders == null) {
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
        int len = getDirectLen(src.length);
        System.arraycopy(src, 0, dst, 0, len);
        return len;
    }

    private long charsToBuffer(char[] src, CharBuffer dst) {
        int len = getDirectLen(src.length);
        dst.put(src, 0, len);
        return len;
    }

    private long charsToAppender(char[] src, Appendable dst) throws IOException {
        int len = getDirectLen(src.length);
        dst.append(JieString.asChars(src, 0, len));
        return len;
    }

    private long bufferToChars(CharBuffer src, char[] dst) {
        int len = getDirectLen(src.remaining());
        src.get(dst, 0, len);
        return len;
    }

    private long bufferToAppender(CharBuffer src, Appendable dst) throws IOException {
        int len = getDirectLen(src.remaining());
        int pos = src.position();
        int newPos = pos + len;
        dst.append(src, 0, len);
        src.position(newPos);
        return len;
    }

    private long charSeqToChars(CharSequence src, char[] dst) throws IOException {
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
            return readBlockSize;
        }
        return (int) Math.min(readLimit, readBlockSize);
    }

    private long readTo(BufferIn in, BufferOut out) throws Exception {
        EnReader enReader = new EnReader(in);
        while (true) {
            CharBuffer buffer = enReader.read();
            if (buffer == null) {
                break;
            }
            out.write(buffer);
        }
        return enReader.count();
    }

    private interface BufferIn {

        /*
         * Note if the return buffer is non-null, it must be non-empty.
         */
        @Nullable
        CharBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(CharBuffer buffer) throws Exception;
    }

    private final class EnReader {

        private final BufferIn in;

        // null:  break end;
        // empty: for last empty invocation
        private CharBuffer buffer;
        private long count = -1;

        // init when a terminal method has invoked
        private Encoder en;

        private EnReader(BufferIn in) {
            this.in = in;
        }

        /*
         * Note if the return buffer is non-null, it must be non-empty.
         */
        @Nullable
        private CharBuffer read() {
            try {
                if (count == -1) {
                    buffer = in.read();
                    count = 0;
                }
                while (true) {
                    if (buffer == null) {
                        return null;
                    }
                    Encoder encoder = getEncoder();
                    CharBuffer encoded;
                    if (!buffer.hasRemaining()) {
                        buffer = null;
                        encoded = encode(encoder, JieChars.emptyBuffer(), true);
                        if (JieChars.isEmpty(encoded)) {
                            continue;
                        }
                        return encoded;
                    }
                    int readSize = buffer.remaining();
                    count += readSize;
                    if (readSize < readBlockSize) {
                        encoded = encode(encoder, buffer, true);
                        buffer = null;
                    } else {
                        encoded = encode(encoder, buffer, false);
                        buffer = in.read();
                        if (buffer == null) {
                            buffer = JieChars.emptyBuffer();
                        }
                    }
                    if (JieChars.isEmpty(encoded)) {
                        continue;
                    }
                    return encoded;
                }
            } catch (IOEncodingException e) {
                throw e;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long count() {
            return count;
        }

        @Nullable
        private Encoder getEncoder() {
            if (en == null) {
                en = buildEncoder();
            }
            return en;
        }

        @Nullable
        private Encoder buildEncoder() {
            if (encoders == null) {
                return null;
            }
            return (data, end) -> {
                CharBuffer chars = data;
                for (Encoder encoder : encoders) {
                    chars = encoder.encode(chars, end);
                }
                return chars;
            };
        }

        @Nullable
        private CharBuffer encode(@Nullable Encoder encoder, CharBuffer buf, boolean end) {
            if (encoder == null) {
                return buf;
            }
            try {
                return encoder.encode(buf, end);
            } catch (Exception e) {
                throw new IOEncodingException(e);
            }
        }
    }

    private abstract class BaseBufferIn implements BufferIn {

        protected final int bufSize;
        protected long remaining;

        private BaseBufferIn(int blockSize) {
            this.bufSize = readLimit < 0 ? blockSize : (int) Math.min(blockSize, readLimit);
            this.remaining = readLimit;
        }
    }

    private final class ReaderBufferIn extends BaseBufferIn {

        private final Reader source;

        private ReaderBufferIn(Reader source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public CharBuffer read() throws IOException {
            int readSize = remaining < 0 ? bufSize : (int) Math.min(remaining, bufSize);
            if (readSize == 0) {
                return null;
            }
            int hasRead = 0;
            char[] buf = new char[bufSize];
            while (hasRead < readSize) {
                int size = source.read(buf, hasRead, readSize - hasRead);
                if (size < 0 || (size == 0 && endOnZeroRead)) {
                    break;
                }
                hasRead += size;
            }
            if (hasRead == 0) {
                return null;
            }
            if (readLimit > 0) {
                remaining -= hasRead;
            }
            return CharBuffer.wrap(
                hasRead == bufSize ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            ).asReadOnlyBuffer();
        }
    }

    private final class CharsBufferIn extends BaseBufferIn {

        private final char[] source;
        private int pos = 0;

        private CharsBufferIn(char[] source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? readBlockSize : (int) Math.min(remaining, readBlockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length) {
                return null;
            }
            int newPos = Math.min(pos + readSize, source.length);
            int size = newPos - pos;
            CharBuffer ret = CharBuffer.wrap(source, pos, size).slice();
            pos = newPos;
            if (readLimit > 0) {
                remaining -= size;
            }
            return ret;
        }
    }

    private final class BufferBufferIn extends BaseBufferIn {

        private final CharBuffer source;

        private BufferBufferIn(CharBuffer source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? readBlockSize : (int) Math.min(remaining, readBlockSize);
            if (readSize <= 0) {
                return null;
            }
            if (!source.hasRemaining()) {
                return null;
            }
            int pos = source.position();
            int limit = source.limit();
            int newPos = Math.min(pos + readSize, limit);
            int size = newPos - pos;
            source.limit(newPos);
            CharBuffer ret = source.slice();
            source.limit(limit);
            source.position(newPos);
            if (readLimit > 0) {
                remaining -= size;
            }
            return ret;
        }
    }

    private final class CharSeqBufferIn extends BaseBufferIn {

        private final CharSequence source;
        private int pos = 0;

        private CharSeqBufferIn(CharSequence source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public CharBuffer read() {
            int readSize = remaining < 0 ? readBlockSize : (int) Math.min(remaining, readBlockSize);
            if (readSize <= 0) {
                return null;
            }
            if (pos >= source.length()) {
                return null;
            }
            int newPos = Math.min(pos + readSize, source.length());
            int size = newPos - pos;
            CharBuffer ret = CharBuffer.wrap(source, pos, newPos).slice();
            pos = newPos;
            if (readLimit > 0) {
                remaining -= size;
            }
            return ret;
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

        private final EnReader enReader;
        private CharBuffer buffer = JieChars.emptyBuffer();
        private boolean closed = false;

        private ReaderIn(BufferIn in) {
            this.enReader = new EnReader(in);
        }

        @Nullable
        private CharBuffer read0() throws IOException {
            try {
                return enReader.read();
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
                return buffer.get() & 0xffff;
            }
            CharBuffer newBuf = read0();
            if (newBuf == null) {
                buffer = null;
                return -1;
            }
            buffer = newBuf;
            return buffer.get() & 0xffff;
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
            if (closed) {
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
            closed = true;
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Reader closed.");
            }
        }
    }

    private static abstract class AbsEncoder implements CharsProcessor.Encoder {

        protected final CharsProcessor.Encoder encoder;
        protected char[] buf = JieChars.emptyChars();

        protected AbsEncoder(CharsProcessor.Encoder encoder) {
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

        RoundEncoder(CharsProcessor.Encoder encoder, int expectedBlockSize) {
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

        BufferedEncoder(CharsProcessor.Encoder encoder) {
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
