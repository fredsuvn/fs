package xyz.sunqian.common.base.chars;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCoding;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.io.CharReader;
import xyz.sunqian.common.io.CharSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
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

    // initials after starting process
    private CharReader sourceReader;
    private CharEncoder oneEncoder;

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

    private void initProcessing() {
        this.sourceReader = toCharReader(source);
        this.oneEncoder = getTheOneEncoder(encoders);
    }

    private CharReader toCharReader(Object src) {
        if (src instanceof Reader) {
            return CharReader.from((Reader) src);
        }
        if (src instanceof char[]) {
            return CharReader.from((char[]) src);
        }
        if (src instanceof CharBuffer) {
            return CharReader.from((CharBuffer) src);
        }
        if (src instanceof CharSequence) {
            return CharReader.from((CharSequence) src);
        }
        throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
    }

    private CharEncoder getTheOneEncoder(List<CharEncoder> encoders) {
        if (JieColl.isEmpty(encoders)) {
            return CharEncoder.emptyEncoder();
        }
        if (encoders.size() == 1) {
            return encoders.get(0);
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
        if (JieColl.isEmpty(encoders)) {
            return toReader(source);
        }
        return new ProcessorReader();
    }

    private Reader toReader(Object src) {
        if (src instanceof Reader) {
            return (Reader) src;
        }
        if (src instanceof char[]) {
            return JieIO.reader((char[]) src);
        }
        if (src instanceof CharBuffer) {
            return JieIO.reader((CharBuffer) src);
        }
        if (src instanceof CharSequence) {
            return JieIO.reader((CharSequence) src);
        }
        throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
    }

    private long start() {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readLimit == 0) {
            return 0;
        }
        try {
            if (JieColl.isEmpty(encoders)) {
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
        } catch (ProcessingException | IORuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessingException(e);
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
        if (dst instanceof Writer) {
            ((Writer) dst).write(src, 0, len);
        } else {
            dst.append(new String(src, 0, len));
        }
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
        initProcessing();
        DataWriter out = toBufferOut(dest);
        return readTo(sourceReader, oneEncoder, out);
    }

    private long readTo(CharReader in, CharEncoder oneEncoder, DataWriter out) throws Exception {
        CharReader reader = readLimit < 0 ? in : in.withReadLimit(readLimit);
        long count = 0;
        while (true) {
            CharSegment segment = reader.read(readBlockSize, endOnZeroRead);
            count += segment.data().remaining();
            CharBuffer encoded = oneEncoder.encode(segment.data(), segment.end());
            if (!JieChars.isEmpty(encoded)) {
                out.write(encoded);
            }
            if (segment.end()) {
                return count;
            }
        }
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
        throw new IORuntimeException("The type of destination is unsupported: " + dst.getClass());
    }

    private interface DataWriter {
        void write(CharBuffer buffer) throws Exception;
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
                dest.append(new String(
                    buffer.array(),
                    JieBuffer.arrayStartIndex(buffer),
                    buffer.remaining()
                ));
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                dest.append(new String(buf));
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

        private CharSegment nextSeg = null;
        private boolean closed = false;

        private ProcessorReader() {
            initProcessing();
        }

        private CharSegment read0() throws IOException {
            try {
                CharSegment s0 = sourceReader.read(readBlockSize, endOnZeroRead);
                CharBuffer encoded = oneEncoder.encode(s0.data(), s0.end());
                if (encoded == s0.data()) {
                    return s0;
                }
                return CharSegment.of(encoded, s0.end());
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            while (true) {
                if (nextSeg == null) {
                    nextSeg = read0();
                }
                if (nextSeg == CharSegment.empty(true)) {
                    return -1;
                }
                if (nextSeg.data().hasRemaining()) {
                    return nextSeg.data().get() & 0xffff;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    return -1;
                }
                nextSeg = null;
            }
        }

        @Override
        public int read(char[] dst) throws IOException {
            return read(dst, 0, dst.length);
        }

        @Override
        public int read(char[] dst, int off, int len) throws IOException {
            checkClosed();
            checkOffsetLength(dst.length, off, len);
            if (len <= 0) {
                return 0;
            }
            int pos = off;
            int remaining = len;
            while (remaining > 0) {
                if (nextSeg == CharSegment.empty(true)) {
                    return -1;
                }
                if (nextSeg == null) {
                    nextSeg = read0();
                }
                if (nextSeg.data().hasRemaining()) {
                    int readSize = Math.min(nextSeg.data().remaining(), remaining);
                    nextSeg.data().get(dst, pos, readSize);
                    pos += readSize;
                    remaining -= readSize;
                    continue;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    break;
                } else {
                    nextSeg = null;
                }
            }
            if (nextSeg.end() && pos == off) {
                return -1;
            }
            return pos - off;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            long pos = 0;
            long remaining = n;
            while (remaining > 0) {
                if (nextSeg == CharSegment.empty(true)) {
                    return 0;
                }
                if (nextSeg == null) {
                    nextSeg = read0();
                }
                if (nextSeg.data().hasRemaining()) {
                    int readSize = (int) Math.min(nextSeg.data().remaining(), remaining);
                    nextSeg.data().position(nextSeg.data().position() + readSize);
                    pos += readSize;
                    remaining -= readSize;
                    continue;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    break;
                } else {
                    nextSeg = null;
                }
            }
            if (nextSeg.end() && pos == 0) {
                return 0;
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
                size += charBuffer.remaining();
            }
            CharBuffer result = CharBuffer.allocate(size);
            for (CharBuffer charBuffer : charBuffers) {
                result.put(charBuffer);
            }
            result.flip();
            return result;
        }
    }

    static final class FixedSizeEncoder implements CharEncoder {

        private final CharEncoder encoder;
        private final int size;

        // Capacity is always the size.
        private @Nullable CharBuffer buffer;

        FixedSizeEncoder(CharEncoder encoder, int size) throws IllegalArgumentException {
            checkSize(size);
            this.encoder = encoder;
            this.size = size;
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
            @Nullable Object result = null;
            boolean encoded = false;

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
                encoded = true;
                buffer.clear();
            }

            // split
            int pos = data.position();
            int limit = data.limit();
            while (limit - pos >= size) {
                pos += size;
                data.limit(pos);
                CharBuffer slice = data.slice();
                data.position(pos);
                if (end && pos == limit) {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, true));
                    return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
                } else {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, false));
                    encoded = true;
                }
            }
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
                    encoded = true;
                }
            }

            @Nullable CharBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
            if (end && !encoded) {
                return encoder.encode(JieChars.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class RoundingEncoder implements CharEncoder {

        private final CharEncoder encoder;
        private final int size;

        // Capacity is always the size.
        private @Nullable CharBuffer buffer;

        RoundingEncoder(CharEncoder encoder, int size) {
            checkSize(size);
            this.encoder = encoder;
            this.size = size;
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
            @Nullable Object result = null;
            boolean encoded = false;

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
                encoded = true;
                buffer.clear();
            }

            // rounding
            int remaining = data.remaining();
            int roundingSize = remaining / size * size;
            if (roundingSize > 0) {
                int pos = data.position();
                pos += roundingSize;
                int limit = data.limit();
                data.limit(pos);
                CharBuffer slice = data.slice();
                data.position(pos);
                data.limit(limit);
                if (end && pos == limit) {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, true));
                    return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
                } else {
                    result = JieCoding.ifAdd(result, encoder.encode(slice, false));
                    encoded = true;
                }
            }

            // buffering
            if (data.hasRemaining()) {
                if (buffer == null) {
                    buffer = CharBuffer.allocate(size);
                }
                JieBuffer.readTo(data, buffer);
                if (end) {
                    buffer.flip();
                    result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
                    encoded = true;
                }
            }

            @Nullable CharBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
            if (end && !encoded) {
                return encoder.encode(JieChars.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class BufferingEncoder implements CharEncoder {

        private final CharEncoder encoder;
        private char[] buffer = null;

        BufferingEncoder(CharEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) throws Exception {
            CharBuffer totalBuffer;
            if (buffer != null) {
                CharBuffer newBuffer = CharBuffer.allocate(buffer.length + data.remaining());
                newBuffer.put(buffer);
                newBuffer.put(data);
                newBuffer.flip();
                totalBuffer = newBuffer;
            } else {
                totalBuffer = data;
            }
            CharBuffer ret = encoder.encode(totalBuffer, end);
            if (end) {
                buffer = null;
                return ret;
            }
            if (totalBuffer.hasRemaining()) {
                char[] remainingBuffer = new char[totalBuffer.remaining()];
                totalBuffer.get(remainingBuffer);
                buffer = remainingBuffer;
            } else {
                buffer = null;
            }
            return ret;
        }
    }

    static final class EmptyEncoder implements CharEncoder {

        static final EmptyEncoder SINGLETON = new EmptyEncoder();

        @Override
        public @Nullable CharBuffer encode(CharBuffer data, boolean end) {
            return data;
        }
    }

    private static void checkSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must > 0.");
        }
    }
}
