package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCoding;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.base.bytes.JieBytes;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

final class CharProcessorImpl implements CharProcessor {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int readBlockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<CharEncoder> encoders;
    private CharEncoder theOneEncoder;

    CharProcessorImpl(Reader source) {
        this.source = source;
    }

    CharProcessorImpl(char[] source) {
        this.source = source;
    }

    CharProcessorImpl(CharBuffer source) {
        this.source = source;
    }

    CharProcessorImpl(CharSequence source) {
        this.source = source;
    }

    @Override
    public CharProcessor readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public CharProcessor readBlockSize(int readBlockSize) {
        if (readBlockSize <= 0) {
            throw new IllegalArgumentException("readBlockSize must > 0!");
        }
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public CharProcessor endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public CharProcessor encoder(CharEncoder encoder) {
        if (encoders == null) {
            encoders = new ArrayList<>();
        }
        encoders.add(encoder);
        return this;
    }

    @Override
    public long process() {
        this.dest = NullDataWriter.SINGLETON;
        return start();
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
    public String toString() {
        return new String(toCharArray());
    }

    @Override
    public Reader toReader() {
        return new ProcessorReader(toBufferIn(source));
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
            return startInBlocks();
        } catch (ProcessingException e) {
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

    private long startInBlocks() throws Exception {
        DataReader in = toBufferIn(source);
        DataWriter out = toBufferOut(dest);
        return readTo(in, out);
    }

    private DataReader toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof Reader) {
            return new ReaderDataReader((Reader) src, actualBlockSize);
        }
        if (src instanceof char[]) {
            return new CharsDataReader((char[]) src, actualBlockSize);
        }
        if (src instanceof CharBuffer) {
            return new BufferDataReader((CharBuffer) src, actualBlockSize);
        }
        if (src instanceof CharSequence) {
            return new CharSeqDataReader((CharSequence) src, actualBlockSize);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private DataWriter toBufferOut(Object dst) {
        if (dst instanceof DataWriter) {
            return (DataWriter) dst;
        }
        if (dst instanceof char[]) {
            return new AppendableDataWriter(CharBuffer.wrap((char[]) dst));
        }
        if (dst instanceof CharBuffer) {
            return new AppendableDataWriter(JieIO.writer((CharBuffer) dst));
        }
        if (dst instanceof Appendable) {
            return new AppendableDataWriter((Appendable) dst);
        }
        throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
    }

    private int getActualBlockSize() {
        if (readLimit < 0) {
            return readBlockSize;
        }
        return (int) Math.min(readLimit, readBlockSize);
    }

    private long readTo(DataReader in, DataWriter out) throws Exception {
        if (theOneEncoder == null) {
            theOneEncoder = buildOneEncoder();
        }
        long count = 0;
        while (true) {
            DataBlock block = in.read();
            if (block == null) {
                // Sends an empty buffer and the end signal.
                CharBuffer encoded = theOneEncoder.encode(JieChars.emptyBuffer(), true);
                out.write(encoded);
                return count;
            }
            count += block.data.remaining();
            CharBuffer encoded = theOneEncoder.encode(block.data, block.end);
            out.write(encoded);
        }
    }

    private CharEncoder buildOneEncoder() {
        if (encoders == null) {
            return CharEncoder.emptyEncoder();
        }
        return (data, end) -> {
            CharBuffer chars = data;
            for (CharEncoder encoder : encoders) {
                chars = encoder.encode(chars, end);
                if (chars == null) {
                    break;
                }
            }
            return chars;
        };
    }

    private interface DataReader {

        /*
         * Returns null if reaches the end of the input.
         * If the returned buffer is non-null, then it is definitely non-empty.
         */
        @Nullable
        DataBlock read() throws Exception;
    }

    private static final class DataBlock {

        private CharBuffer data;
        private boolean end;

        public DataBlock(CharBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }
    }

    private interface DataWriter {
        void write(CharBuffer buffer) throws Exception;
    }

    private final class EncodingReader {

        private final DataReader in;

        // null:  break end;
        // empty: for last empty invocation
        private CharBuffer buffer;
        private long count = -1;

        // init when a terminal method has invoked
        private CharEncoder en;

        private EncodingReader(DataReader in) {
            this.in = in;
        }

        /*
         * Returns null if reaches the end of the input.
         * If the returned buffer is non-null, then it is definitely non-empty.
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
                    CharEncoder encoder = getEncoder();
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
            } catch (ProcessingException e) {
                throw e;
            } catch (Exception e) {
                throw new IORuntimeException(e);
            }
        }

        private long count() {
            return count;
        }

        @Nullable
        private CharEncoder getEncoder() {
            if (en == null) {
                en = buildEncoder();
            }
            return en;
        }

        @Nullable
        private CharEncoder buildEncoder() {
            if (encoders == null) {
                return null;
            }
            return (data, end) -> {
                CharBuffer chars = data;
                for (CharEncoder encoder : encoders) {
                    chars = encoder.encode(chars, end);
                    if (chars == null) {
                        break;
                    }
                }
                return chars;
            };
        }

        @Nullable
        private CharBuffer encode(@Nullable CharEncoder encoder, CharBuffer buf, boolean end) {
            if (encoder == null) {
                return buf;
            }
            try {
                return encoder.encode(buf, end);
            } catch (Exception e) {
                throw new ProcessingException(e);
            }
        }
    }

    private abstract class BaseDataReader implements DataReader {

        protected final int bufSize;
        protected long remaining;

        private BaseDataReader(int blockSize) {
            this.bufSize = readLimit < 0 ? blockSize : (int) Math.min(blockSize, readLimit);
            this.remaining = readLimit;
        }
    }

    private final class ReaderDataReader extends BaseDataReader {

        private final Reader source;

        private ReaderDataReader(Reader source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public DataBlock read() throws IOException {
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
            CharBuffer buffer = CharBuffer.wrap(
                hasRead == bufSize ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            ).asReadOnlyBuffer();
            return new DataBlock(buffer, remaining == 0);
        }
    }

    private final class CharsDataReader extends BaseDataReader {

        private final char[] source;
        private int pos = 0;

        private CharsDataReader(char[] source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public DataBlock read() {
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
            return new DataBlock(ret, remaining == 0 || pos >= source.length);
        }
    }

    private final class BufferDataReader extends BaseDataReader {

        private final CharBuffer source;

        private BufferDataReader(CharBuffer source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public DataBlock read() {
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
            return new DataBlock(ret, remaining == 0 || !source.hasRemaining());
        }
    }

    private final class CharSeqDataReader extends BaseDataReader {

        private final CharSequence source;
        private int pos = 0;

        private CharSeqDataReader(CharSequence source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public DataBlock read() {
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
            return new DataBlock(ret, remaining == 0 || pos >= source.length());
        }
    }

    private static final class AppendableDataWriter implements DataWriter {

        private final Appendable dest;

        private AppendableDataWriter(Appendable dest) {
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
                    JieBuffer.arrayStartIndex(buffer),
                    JieBuffer.arrayEndIndex(buffer)
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
                writer.write(buffer.array(), JieBuffer.arrayStartIndex(buffer), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                writer.write(buf);
            }
        }
    }

    private static final class NullDataWriter implements DataWriter {

        static final NullDataWriter SINGLETON = new NullDataWriter();

        @Override
        public void write(CharBuffer buffer) {
            // Do nothing
        }
    }

    private final class ProcessorReader extends Reader {

        private final EncodingReader encodingReader;
        private CharBuffer buffer = JieChars.emptyBuffer();
        private boolean closed = false;

        private ProcessorReader(DataReader in) {
            this.encodingReader = new EncodingReader(in);
        }

        @Nullable
        private CharBuffer read0() throws IOException {
            try {
                return encodingReader.read();
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
            checkOffsetLength(dst.length, off, len);
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

    private static final class BufferMerger implements Function<Collection<CharBuffer>, CharBuffer> {

        private static final BufferMerger SINGLETON = new BufferMerger();

        @Override
        public CharBuffer apply(Collection<CharBuffer> charBuffers) {
            if (charBuffers.isEmpty()) {
                return null;
            }
            int size = 0;
            for (CharBuffer charBuffer : charBuffers) {
                if (charBuffer != null) {
                    size += charBuffer.remaining();
                }
            }
            CharBuffer result = CharBuffer.allocate(size);
            for (CharBuffer charBuffer : charBuffers) {
                if (charBuffer != null) {
                    result.put(charBuffer);
                }
            }
            result.flip();
            return result;
        }
    }

    private static abstract class AbsEncoder implements CharEncoder {

        protected final CharEncoder encoder;
        protected char[] buf = JieChars.emptyChars();

        protected AbsEncoder(CharEncoder encoder) {
            this.encoder = encoder;
        }

        // buf will be set null after total
        protected CharBuffer totalData(CharBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            CharBuffer total = CharBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
            buf = null;
            return total;
        }

        protected int totalSize(CharBuffer data) {
            return buf.length + data.remaining();
        }
    }

    static final class RoundingEncoder extends AbsEncoder {

        private final int expectedBlockSize;

        RoundingEncoder(CharEncoder encoder, int expectedBlockSize) {
            super(encoder);
            this.expectedBlockSize = expectedBlockSize;
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
            if (end) {
                return encoder.encode(totalData(data), true);
            }
            int size = totalSize(data);
            if (size < expectedBlockSize) {
                char[] newBuf = new char[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                data.get(newBuf, buf.length, data.remaining());
                buf = newBuf;
                return null;
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
            CharBuffer slice = JieBuffer.slice(data, sliceSize);
            data.position(data.position() + sliceSize);
            round.put(slice);
            round.flip();
            return round;
        }
    }

    static final class BufferingEncoder extends AbsEncoder {

        BufferingEncoder(CharEncoder encoder) {
            super(encoder);
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
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

        // Capacity is always the size.
        private @Nullable CharBuffer buffer;

        FixedSizeEncoder(CharEncoder encoder, int size) {
            super(encoder);
            this.size = size;
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
            @Nullable Object result = null;

            // clean buffer
            if (buffer != null && buffer.position() > 0) {
                JieBuffer.readTo(data, buffer);
                if (end && !data.hasRemaining()) {
                    buffer.flip();
                    return encoder.encode(buffer, true);
                }
                if (buffer.hasRemaining()) {
                    return null;
                }
                buffer.flip();
                result = JieCoding.ifAdd(result, encoder.encode(buffer, false));
                buffer.clear();
            }

            // split
            int pos = data.position();
            int limit = data.limit();
            while (limit - pos >= size) {
                data.position(pos);
                data.limit(pos + size);
                CharBuffer slice = data.slice();
                pos += size;
                if (end && !data.hasRemaining()) {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, true));
                } else {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, false));
                }
            }
            data.position(pos);
            data.limit(limit);

            // buffering
            if (data.hasRemaining()) {
                if (buffer == null) {
                    buffer = CharBuffer.allocate(size);
                }
                JieBuffer.readTo(data, buffer);
                if (end) {
                    buffer.flip();
                    result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
                    return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
                }
            }

            return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
        }
    }

    static final class EmptyEncoder implements CharEncoder {

        static final EmptyEncoder SINGLETON = new EmptyEncoder();

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) {
            return data;
        }
    }
}
