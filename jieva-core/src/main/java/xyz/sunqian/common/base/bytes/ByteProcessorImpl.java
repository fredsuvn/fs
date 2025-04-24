package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCoding;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.io.ByteReader;
import xyz.sunqian.common.io.ByteSegment;
import xyz.sunqian.common.io.IORuntimeException;
import xyz.sunqian.common.io.JieBuffer;
import xyz.sunqian.common.io.JieIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

final class ByteProcessorImpl implements ByteProcessor {

    private final @Nullable Object source;
    private @Nullable Object dest;
    private long readLimit = -1;
    private int readBlockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private @Nullable List<ByteEncoder> encoders;

    // initials after starting process
    private @Nullable ByteReader sourceReader;
    private @Nullable ByteEncoder oneEncoder;

    ByteProcessorImpl(InputStream source) {
        this.source = source;
    }

    ByteProcessorImpl(byte[] source) {
        this.source = source;
    }

    ByteProcessorImpl(ByteBuffer source) {
        this.source = source;
    }

    private Object getSource() {
        if (source == null) {
            throw new IORuntimeException("The source is null!");
        }
        return source;
    }

    private Object getDest() {
        if (dest == null) {
            throw new IORuntimeException("The destination is null!");
        }
        return dest;
    }

    private ByteReader getSourceReader() {
        if (sourceReader == null) {
            sourceReader = toByteReader(getSource());
        }
        return sourceReader;
    }

    private ByteEncoder getEncoder() {
        if (oneEncoder == null) {
            oneEncoder = toOneEncoder(encoders);
        }
        return oneEncoder;
    }

    @Override
    public ByteProcessor readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public ByteProcessor readBlockSize(int readBlockSize) {
        if (readBlockSize <= 0) {
            throw new IllegalArgumentException("readBlockSize must > 0!");
        }
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public ByteProcessor endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public ByteProcessor encoder(ByteEncoder encoder) {
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
    public long writeTo(OutputStream dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long writeTo(byte[] dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public long writeTo(byte[] dest, int offset, int length) {
        if (offset == 0 && length == dest.length) {
            return writeTo(dest);
        }
        try {
            this.dest = ByteBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return start();
    }

    @Override
    public long writeTo(ByteBuffer dest) {
        this.dest = dest;
        return start();
    }

    @Override
    public String toString() {
        return toString(JieChars.defaultCharset());
    }

    @Override
    public InputStream toInputStream() {
        if (JieColl.isEmpty(encoders)) {
            return toInputStream(getSource());
        }
        return new ProcessorInputStream();
    }

    private InputStream toInputStream(Object src) {
        if (src instanceof InputStream) {
            return (InputStream) src;
        }
        if (src instanceof byte[]) {
            return JieIO.inStream((byte[]) src);
        }
        if (src instanceof ByteBuffer) {
            return JieIO.inStream((ByteBuffer) src);
        }
        throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
    }

    private long start() {
        if (readLimit == 0) {
            return 0;
        }
        try {
            if (JieColl.isEmpty(encoders)) {
                Object src = getSource();
                Object dst = getDest();
                if (src instanceof byte[]) {
                    if (dst instanceof byte[]) {
                        return bytesToBytes((byte[]) src, (byte[]) dst);
                    }
                    if (dst instanceof ByteBuffer) {
                        return bytesToBuffer((byte[]) src, (ByteBuffer) dst);
                    }
                } else if (src instanceof ByteBuffer) {
                    if (dst instanceof byte[]) {
                        return bufferToBytes((ByteBuffer) src, (byte[]) dst);
                    }
                    if (dst instanceof ByteBuffer) {
                        return bufferToBuffer((ByteBuffer) src, (ByteBuffer) dst);
                    }
                }
            }
            return startInBlocks();
        } catch (ProcessingException | IORuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

    private long bytesToBytes(byte[] src, byte[] dst) {
        int len = getDirectLen(src.length);
        System.arraycopy(src, 0, dst, 0, len);
        return len;
    }

    private long bytesToBuffer(byte[] src, ByteBuffer dst) {
        int len = getDirectLen(src.length);
        dst.put(src, 0, len);
        return len;
    }

    private long bufferToBytes(ByteBuffer src, byte[] dst) {
        int len = getDirectLen(src.remaining());
        src.get(dst, 0, len);
        return len;
    }

    private long bufferToBuffer(ByteBuffer src, ByteBuffer dst) {
        int len = getDirectLen(src.remaining());
        ByteBuffer share = src.slice();
        share.limit(len);
        dst.put(share);
        src.position(src.position() + len);
        return len;
    }

    private int getDirectLen(int srcSize) {
        return readLimit < 0 ? srcSize : Math.min(srcSize, (int) readLimit);
    }

    private long startInBlocks() throws Exception {
        DataWriter out = toBufferOut(getDest());
        return readTo(getSourceReader(), getEncoder(), out);
    }

    private long readTo(ByteReader in, ByteEncoder oneEncoder, DataWriter out) throws Exception {
        ByteReader reader = readLimit < 0 ? in : in.withReadLimit(readLimit);
        long count = 0;
        while (true) {
            ByteSegment segment = reader.read(readBlockSize, endOnZeroRead);
            count += segment.data().remaining();
            @Nullable ByteBuffer encoded = oneEncoder.encode(segment.data(), segment.end());
            if (!JieBytes.isEmpty(encoded)) {
                out.write(encoded);
            }
            if (segment.end()) {
                return count;
            }
        }
    }

    private ByteReader toByteReader(Object src) {
        if (src instanceof InputStream) {
            return ByteReader.from((InputStream) src);
        }
        if (src instanceof byte[]) {
            return ByteReader.from((byte[]) src);
        }
        if (src instanceof ByteBuffer) {
            return ByteReader.from((ByteBuffer) src);
        }
        throw new IORuntimeException("The type of source is unsupported: " + src.getClass());
    }

    private DataWriter toBufferOut(Object dst) {
        if (dst instanceof DataWriter) {
            return (DataWriter) dst;
        }
        if (dst instanceof OutputStream) {
            return new OutputSteamDataWriter((OutputStream) dst);
        }
        if (dst instanceof byte[]) {
            return new OutputSteamDataWriter(JieIO.outStream((byte[]) dst));
        }
        if (dst instanceof ByteBuffer) {
            return new OutputSteamDataWriter(JieIO.outStream((ByteBuffer) dst));
        }
        throw new IORuntimeException("The type of destination is unsupported: " + dst.getClass());
    }

    private ByteEncoder toOneEncoder(@Nullable List<ByteEncoder> encoders) {
        if (JieColl.isEmpty(encoders)) {
            return ByteEncoder.emptyEncoder();
        }
        if (encoders.size() == 1) {
            return encoders.get(0);
        }
        return (data, end) -> {
            @Nullable ByteBuffer bytes = data;
            for (ByteEncoder encoder : encoders) {
                bytes = encoder.encode(bytes, end);
                if (bytes == null) {
                    break;
                }
            }
            return bytes;
        };
    }

    private interface DataWriter {
        void write(ByteBuffer buffer) throws Exception;
    }

    private static final class OutputSteamDataWriter implements DataWriter {

        private final OutputStream dest;

        private OutputSteamDataWriter(OutputStream dest) {
            this.dest = dest;
        }

        @Override
        public void write(ByteBuffer buffer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                dest.write(buffer.array(), JieBuffer.arrayStartIndex(buffer), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                byte[] buf = new byte[buffer.remaining()];
                buffer.get(buf);
                dest.write(buf);
            }
        }
    }

    private static final class NullDataWriter implements DataWriter {

        static final NullDataWriter SINGLETON = new NullDataWriter();

        @Override
        public void write(ByteBuffer buffer) {
            // Do nothing
        }
    }

    private final class ProcessorInputStream extends InputStream {

        private @Nullable ByteSegment nextSeg = null;
        private boolean closed = false;

        private ProcessorInputStream() {
        }

        private ByteSegment read0() throws IOException {
            try {
                ByteSegment s0 = getSourceReader().read(readBlockSize, endOnZeroRead);
                @Nullable ByteBuffer encoded = getEncoder().encode(s0.data(), s0.end());
                if (encoded == s0.data()) {
                    return s0;
                }
                return ByteSegment.of(encoded, s0.end());
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
                if (nextSeg == ByteSegment.empty(true)) {
                    return -1;
                }
                if (nextSeg.data().hasRemaining()) {
                    return nextSeg.data().get() & 0xff;
                }
                if (nextSeg.end()) {
                    nextSeg = ByteSegment.empty(true);
                    return -1;
                }
                nextSeg = null;
            }
        }

        @Override
        public int read(byte[] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte[] dst, int off, int len) throws IOException {
            checkClosed();
            checkOffsetLength(dst.length, off, len);
            if (len <= 0) {
                return 0;
            }
            int pos = off;
            int remaining = len;
            while (remaining > 0) {
                if (nextSeg == ByteSegment.empty(true)) {
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
                    nextSeg = ByteSegment.empty(true);
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
                if (nextSeg == ByteSegment.empty(true)) {
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
                    nextSeg = ByteSegment.empty(true);
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
        public int available() {
            return nextSeg == null ? 0 : nextSeg.data().remaining();
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
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class BufferMerger implements Function<Collection<ByteBuffer>, ByteBuffer> {

        private static final BufferMerger SINGLETON = new BufferMerger();

        @Override
        public @Nullable ByteBuffer apply(Collection<ByteBuffer> byteBuffers) {
            if (byteBuffers.isEmpty()) {
                return null;
            }
            int size = 0;
            for (ByteBuffer byteBuffer : byteBuffers) {
                size += byteBuffer.remaining();
            }
            ByteBuffer result = ByteBuffer.allocate(size);
            for (ByteBuffer byteBuffer : byteBuffers) {
                result.put(byteBuffer);
            }
            result.flip();
            return result;
        }
    }

    static final class FixedSizeEncoder implements ByteEncoder {

        private final ByteEncoder encoder;
        private final int size;

        // Capacity is always the size.
        private @Nullable ByteBuffer buffer;

        FixedSizeEncoder(ByteEncoder encoder, int size) throws IllegalArgumentException {
            checkSize(size);
            this.encoder = encoder;
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
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
                ByteBuffer slice = data.slice();
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
                    buffer = ByteBuffer.allocate(size);
                }
                JieBuffer.readTo(data, buffer);
                if (end) {
                    buffer.flip();
                    result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
                    encoded = true;
                }
            }

            @Nullable ByteBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
            if (end && !encoded) {
                return encoder.encode(JieBytes.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class RoundingEncoder implements ByteEncoder {

        private final ByteEncoder encoder;
        private final int size;

        // Capacity is always the size.
        private @Nullable ByteBuffer buffer;

        RoundingEncoder(ByteEncoder encoder, int size) {
            checkSize(size);
            this.encoder = encoder;
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
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
                ByteBuffer slice = data.slice();
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
                    buffer = ByteBuffer.allocate(size);
                }
                JieBuffer.readTo(data, buffer);
                if (end) {
                    buffer.flip();
                    result = JieCoding.ifAdd(result, encoder.encode(buffer, true));
                    encoded = true;
                }
            }

            @Nullable ByteBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
            if (end && !encoded) {
                return encoder.encode(JieBytes.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class BufferingEncoder implements ByteEncoder {

        private final ByteEncoder encoder;
        private byte @Nullable [] buffer = null;

        BufferingEncoder(ByteEncoder encoder) {
            this.encoder = encoder;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
            ByteBuffer totalBuffer;
            if (buffer != null) {
                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.length + data.remaining());
                newBuffer.put(buffer);
                newBuffer.put(data);
                newBuffer.flip();
                totalBuffer = newBuffer;
            } else {
                totalBuffer = data;
            }
            @Nullable ByteBuffer ret = encoder.encode(totalBuffer, end);
            if (end) {
                buffer = null;
                return ret;
            }
            if (totalBuffer.hasRemaining()) {
                byte[] remainingBuffer = new byte[totalBuffer.remaining()];
                totalBuffer.get(remainingBuffer);
                buffer = remainingBuffer;
            } else {
                buffer = null;
            }
            return ret;
        }
    }

    static final class EmptyEncoder implements ByteEncoder {

        static final EmptyEncoder SINGLETON = new EmptyEncoder();

        @Override
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            return data;
        }
    }

    private static void checkSize(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must > 0.");
        }
    }
}
