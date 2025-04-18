package xyz.sunqian.common.base.bytes;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
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
import java.util.Arrays;
import java.util.List;

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
        } catch (ProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new IORuntimeException(e);
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
        if (theOneEncoder == null) {
            theOneEncoder = buildOneEncoder();
        }
        ByteReader reader = readLimit < 0 ? in : in.withReadLimit(readLimit);
        long count = 0;
        while (true) {
            ByteSegment segment = reader.read(readBlockSize, endOnZeroRead);
            count += segment.data().remaining();
            ByteBuffer encoded = theOneEncoder.encode(segment.data(), segment.end());
            out.write(encoded);
            if (segment.end()) {
                return count;
            }
        }
    }

    private ByteEncoder buildOneEncoder() {
        if (JieColl.isEmpty(encoders)) {
            return ByteEncoder.emptyEncoder();
        }
        return (data, end) -> {
            ByteBuffer bytes = data;
            for (ByteEncoder encoder : encoders) {
                bytes = encoder.encode(bytes, end);
                if (bytes == null) {
                    break;
                }
            }
            return bytes;
        };
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

    private interface DataReader {

        /*
         * Returns null if reaches the end of the input.
         * If the returned buffer is non-null, then it is definitely non-empty.
         */
        @Nullable
        DataBlock read() throws Exception;
    }

    private static final class DataBlock {

        private ByteBuffer data;
        private boolean end;

        public DataBlock(ByteBuffer data, boolean end) {
            this.data = data;
            this.end = end;
        }
    }

    private interface DataWriter {
        void write(ByteBuffer buffer) throws Exception;
    }

    private abstract class BaseDataReader implements DataReader {

        protected final int bufSize;
        protected long remaining;

        private BaseDataReader(int blockSize) {
            this.bufSize = readLimit < 0 ? blockSize : (int) Math.min(blockSize, readLimit);
            this.remaining = readLimit;
        }
    }

    private final class InputStreamDataReader extends BaseDataReader {

        private final InputStream source;

        private InputStreamDataReader(InputStream source, int blockSize) {
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
            byte[] buf = new byte[bufSize];
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
            ByteBuffer buffer = ByteBuffer.wrap(
                hasRead == bufSize ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            ).asReadOnlyBuffer();
            return new DataBlock(buffer, remaining == 0);
        }
    }

    private final class BytesDataReader extends BaseDataReader {

        private final byte[] source;
        private int pos = 0;

        private BytesDataReader(byte[] source, int blockSize) {
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
            ByteBuffer ret = ByteBuffer.wrap(source, pos, size).slice();
            pos = newPos;
            if (readLimit > 0) {
                remaining -= size;
            }
            return new DataBlock(ret, remaining == 0 || pos >= source.length);
        }
    }

    private final class BufferDataReader extends BaseDataReader {

        private final ByteBuffer source;

        private BufferDataReader(ByteBuffer source, int blockSize) {
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
            ByteBuffer ret = source.slice();
            source.limit(limit);
            source.position(newPos);
            if (readLimit > 0) {
                remaining -= size;
            }
            return new DataBlock(ret, remaining == 0 || !source.hasRemaining());
        }
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
        private ByteSegment buffer = null;
        private boolean closed = false;

        private ProcessorInputStream(ByteReader in) {
            this.in = in;
        }

        private ByteSegment read0() throws IOException {
            try {
                return in.read(readBlockSize, endOnZeroRead);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            if (buffer == null) {
                buffer = read0();
            }
            while (true) {
                if (buffer.data().hasRemaining()) {
                    return buffer.data().get() & 0xff;
                }
                if (buffer.end()) {
                    return -1;
                }
                buffer = read0();
            }
        }

        public int read(byte[] dst, int off, int len) throws IOException {
            checkClosed();
            checkOffsetLength(dst.length, off, len);
            if (len <= 0) {
                return 0;
            }
            if (buffer == null) {
                buffer = read0();
            }
            int pos = off;
            while (pos < off + len) {
                if (buffer.data().hasRemaining()) {
                    int readSize = Math.min(buffer.data().remaining(), len);
                    buffer.data().get(dst, pos, readSize);
                    pos += readSize;
                    continue;
                }
                if (buffer.end()) {
                    break;
                }
                buffer = read0();
            }
            if (buffer.end() && pos == off) {
                return -1;
            }
            return pos - off;
        }

        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            if (buffer == null) {
                buffer = read0();
            }
            int pos = 0;
            while (pos < n) {
                if (buffer.data().hasRemaining()) {
                    int readSize = (int) Math.min(buffer.data().remaining(), n);
                    buffer.data().position(buffer.data().position() + readSize);
                    pos += readSize;
                    continue;
                }
                if (buffer.end()) {
                    break;
                }
                buffer = read0();
            }
            if (buffer.end() && pos == 0) {
                return 0;
            }
            return pos;
        }

        public int available() {
            return buffer == null ? 0 : buffer.data().remaining();
        }

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

        FixedSizeEncoder(ByteEncoder encoder, int size) {
            super(encoder);
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) throws Exception {
            ByteBuffer total = totalData(data);
            int totalSize = total.remaining();
            int times = totalSize / size;
            if (times == 0) {
                if (end) {
                    return encoder.encode(total, true);
                }
                buf = new byte[totalSize];
                total.get(buf);
                return null;
            }
            if (times == 1) {
                ByteBuffer slice = JieBuffer.slice(total, size);
                ByteBuffer ret1 = Jie.nonNull(encoder.encode(slice, false), JieBytes.emptyBuffer());
                total.position(total.position() + size);
                if (end) {
                    ByteBuffer ret2 = Jie.nonNull(encoder.encode(total, true), JieBytes.emptyBuffer());
                    int size12 = ret1.remaining() + ret2.remaining();
                    if (size12 <= 0) {
                        return null;
                    }
                    ByteBuffer ret = ByteBuffer.allocate(size12);
                    ret.put(ret1);
                    ret.put(ret2);
                    ret.flip();
                    return ret;
                }
                buf = new byte[total.remaining()];
                total.get(buf);
                return ret1;
            }
            BytesBuilder bytesBuilder = new BytesBuilder();
            for (int i = 0; i < times; i++) {
                ByteBuffer slice = JieBuffer.slice(total, size);
                ByteBuffer ret = encoder.encode(slice, false);
                total.position(total.position() + size);
                if (!JieBytes.isEmpty(ret)) {
                    bytesBuilder.append(ret);
                }
            }
            if (end) {
                ByteBuffer lastRet = encoder.encode(total, true);
                if (!JieBytes.isEmpty(lastRet)) {
                    bytesBuilder.append(lastRet);
                }
            } else {
                buf = new byte[total.remaining()];
                total.get(buf);
            }
            if (bytesBuilder.size() <= 0) {
                return null;
            }
            return bytesBuilder.toByteBuffer();
        }
    }

    static final class EmptyEncoder implements ByteEncoder {

        static final EmptyEncoder SINGLETON = new EmptyEncoder();

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
            return data;
        }
    }
}
