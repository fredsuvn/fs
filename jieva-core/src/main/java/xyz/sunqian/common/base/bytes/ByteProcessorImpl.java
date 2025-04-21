package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCoding;
import xyz.sunqian.common.base.chars.JieChars;
import xyz.sunqian.common.base.exception.ProcessingException;
import xyz.sunqian.common.coll.JieArray;
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

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int readBlockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<ByteEncoder> encoders;
    private ByteEncoder theOneEncoder;

    ByteProcessorImpl(InputStream source) {
        this.source = source;
    }

    ByteProcessorImpl(byte[] source) {
        this.source = source;
    }

    ByteProcessorImpl(ByteBuffer source) {
        this.source = source;
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
        return new ProcessorInputStream(toByteReader(source));
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
                if (source instanceof byte[]) {
                    if (dest instanceof byte[]) {
                        return bytesToBytes((byte[]) source, (byte[]) dest);
                    }
                    if (dest instanceof ByteBuffer) {
                        return bytesToBuffer((byte[]) source, (ByteBuffer) dest);
                    }
                } else if (source instanceof ByteBuffer) {
                    if (dest instanceof byte[]) {
                        return bufferToBytes((ByteBuffer) source, (byte[]) dest);
                    }
                    if (dest instanceof ByteBuffer) {
                        return bufferToBuffer((ByteBuffer) source, (ByteBuffer) dest);
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
        ByteReader in = toByteReader(source);
        DataWriter out = toBufferOut(dest);
        return readTo(in, out);
    }

    private long readTo(ByteReader in, DataWriter out) throws Exception {
        ByteEncoder oneEncoder = getTheOneEncoder();
        ByteReader reader = readLimit < 0 ? in : in.withReadLimit(readLimit);
        long count = 0;
        while (true) {
            ByteSegment segment = reader.read(readBlockSize, endOnZeroRead);
            count += segment.data().remaining();
            ByteBuffer encoded = oneEncoder.encode(segment.data(), segment.end());
            if (!JieBytes.isEmpty(encoded)) {
                out.write(encoded);
            }
            if (segment.end()) {
                return count;
            }
        }
    }

    private ByteEncoder getTheOneEncoder() {
        if (theOneEncoder != null) {
            return theOneEncoder;
        }
        if (JieColl.isEmpty(encoders)) {
            theOneEncoder = ByteEncoder.emptyEncoder();
            return theOneEncoder;
        }
        theOneEncoder = (data, end) -> {
            ByteBuffer bytes = data;
            for (ByteEncoder encoder : encoders) {
                bytes = encoder.encode(bytes, end);
                if (bytes == null) {
                    break;
                }
            }
            return bytes;
        };
        return theOneEncoder;
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
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
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
        throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
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

        private final ByteReader in;
        private ByteSegment nextSeg = null;
        private boolean closed = false;

        private ProcessorInputStream(ByteReader in) {
            this.in = in;
        }

        private ByteSegment read0() throws IOException {
            try {
                ByteEncoder oneEncoder = getTheOneEncoder();
                ByteSegment s0 = in.read(readBlockSize, endOnZeroRead);
                ByteBuffer encoded = oneEncoder.encode(s0.data(), s0.end());
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
        public ByteBuffer apply(Collection<ByteBuffer> byteBuffers) {
            if (byteBuffers.isEmpty()) {
                return null;
            }
            int size = 0;
            for (ByteBuffer byteBuffer : byteBuffers) {
                if (byteBuffer != null) {
                    size += byteBuffer.remaining();
                }
            }
            ByteBuffer result = ByteBuffer.allocate(size);
            for (ByteBuffer byteBuffer : byteBuffers) {
                if (byteBuffer != null) {
                    result.put(byteBuffer);
                }
            }
            result.flip();
            return result;
        }
    }

    private static abstract class AbsEncoder implements ByteEncoder {

        protected final ByteEncoder encoder;
        protected byte[] buf = JieBytes.emptyBytes();

        protected AbsEncoder(ByteEncoder encoder) {
            this.encoder = encoder;
        }

        // buf will be set null after total
        protected ByteBuffer totalData(ByteBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            ByteBuffer total = ByteBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
            buf = null;
            return total;
        }

        protected int totalSize(ByteBuffer data) {
            return buf.length + data.remaining();
        }
    }

    static final class RoundingEncoder extends AbsEncoder {

        private final int expectedBlockSize;

        RoundingEncoder(ByteEncoder encoder, int expectedBlockSize) {
            super(encoder);
            this.expectedBlockSize = expectedBlockSize;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
            if (end) {
                return encoder.encode(totalData(data), true);
            }
            int size = totalSize(data);
            if (size < expectedBlockSize) {
                byte[] newBuf = new byte[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                data.get(newBuf, buf.length, data.remaining());
                buf = newBuf;
                return null;
            }
            int remainder = size % expectedBlockSize;
            if (remainder == 0) {
                ByteBuffer total = totalData(data);
                buf = JieBytes.emptyBytes();
                return encoder.encode(total, false);
            }
            int roundSize = size / expectedBlockSize * expectedBlockSize;
            ByteBuffer round = roundData(data, roundSize);
            buf = new byte[remainder];
            data.get(buf);
            return encoder.encode(round, false);
        }

        private ByteBuffer roundData(ByteBuffer data, int roundSize) {
            ByteBuffer round = ByteBuffer.allocate(roundSize);
            round.put(buf);
            int sliceSize = roundSize - buf.length;
            ByteBuffer slice = JieBuffer.slice(data, sliceSize);
            data.position(data.position() + sliceSize);
            round.put(slice);
            round.flip();
            return round;
        }
    }

    static final class BufferingEncoder extends AbsEncoder {

        BufferingEncoder(ByteEncoder encoder) {
            super(encoder);
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
            ByteBuffer total = totalData(data);
            ByteBuffer ret = encoder.encode(total, end);
            if (end) {
                return ret;
            }
            if (total.hasRemaining()) {
                buf = new byte[total.remaining()];
                total.get(buf);
                return ret;
            }
            buf = JieBytes.emptyBytes();
            return ret;
        }
    }

    static final class FixedSizeEncoder extends AbsEncoder {

        private final int size;

        // Capacity is always the size.
        private @Nullable ByteBuffer buffer;

        FixedSizeEncoder(ByteEncoder encoder, int size) {
            super(encoder);
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
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
                ByteBuffer slice = data.slice();
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
                    buffer = ByteBuffer.allocate(size);
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

        // @Override
        // public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
        //     ByteBuffer total = totalData(data);
        //     int totalSize = total.remaining();
        //     int times = totalSize / size;
        //     if (times == 0) {
        //         if (end) {
        //             return encoder.encode(total, true);
        //         }
        //         buf = new byte[totalSize];
        //         total.get(buf);
        //         return null;
        //     }
        //     if (times == 1) {
        //         ByteBuffer slice = JieBuffer.slice(total, size);
        //         ByteBuffer ret1 = Jie.nonNull(encoder.encode(slice, false), JieBytes.emptyBuffer());
        //         total.position(total.position() + size);
        //         if (end) {
        //             ByteBuffer ret2 = Jie.nonNull(encoder.encode(total, true), JieBytes.emptyBuffer());
        //             int size12 = ret1.remaining() + ret2.remaining();
        //             if (size12 <= 0) {
        //                 return null;
        //             }
        //             ByteBuffer ret = ByteBuffer.allocate(size12);
        //             ret.put(ret1);
        //             ret.put(ret2);
        //             ret.flip();
        //             return ret;
        //         }
        //         buf = new byte[total.remaining()];
        //         total.get(buf);
        //         return ret1;
        //     }
        //     BytesBuilder bytesBuilder = new BytesBuilder();
        //     for (int i = 0; i < times; i++) {
        //         ByteBuffer slice = JieBuffer.slice(total, size);
        //         ByteBuffer ret = encoder.encode(slice, false);
        //         total.position(total.position() + size);
        //         if (!JieBytes.isEmpty(ret)) {
        //             bytesBuilder.append(ret);
        //         }
        //     }
        //     if (end) {
        //         ByteBuffer lastRet = encoder.encode(total, true);
        //         if (!JieBytes.isEmpty(lastRet)) {
        //             bytesBuilder.append(lastRet);
        //         }
        //     } else {
        //         buf = new byte[total.remaining()];
        //         total.get(buf);
        //     }
        //     if (bytesBuilder.size() <= 0) {
        //         return null;
        //     }
        //     return bytesBuilder.toByteBuffer();
        // }
    }

    static final class EmptyEncoder implements ByteEncoder {

        static final EmptyEncoder SINGLETON = new EmptyEncoder();

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
            return data;
        }
    }
}
