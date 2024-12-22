package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.coll.JieArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class BytesProcessorImpl implements BytesProcessor {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int readBlockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<Encoder> encoders;

    BytesProcessorImpl(InputStream source) {
        this.source = source;
    }

    BytesProcessorImpl(byte[] source) {
        this.source = source;
    }

    BytesProcessorImpl(ByteBuffer source) {
        this.source = source;
    }

    @Override
    public BytesProcessor readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public BytesProcessor readBlockSize(int readBlockSize) {
        if (readBlockSize <= 0) {
            throw new IORuntimeException("readBlockSize must > 0!");
        }
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public BytesProcessor endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public BytesProcessor encoder(Encoder encoder) {
        if (encoders == null) {
            encoders = new ArrayList<>();
        }
        encoders.add(encoder);
        return this;
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
    public long writeTo() {
        this.dest = NullBufferOut.SINGLETON;
        return start();
    }

    @Override
    public InputStream toInputStream() {
        return new StreamIn(toBufferIn(source));
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
            return startInBlock();
        } catch (IOEncodingException e) {
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

    private long startInBlock() throws Exception {
        BufferIn in = toBufferIn(source);
        BufferOut out = toBufferOut(dest);
        return readTo(in, out);
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof InputStream) {
            return new InputStreamBufferIn((InputStream) src, actualBlockSize);
        }
        if (src instanceof byte[]) {
            return new BytesBufferIn((byte[]) src, actualBlockSize);
        }
        if (src instanceof ByteBuffer) {
            return new BufferBufferIn((ByteBuffer) src, actualBlockSize);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof BufferOut) {
            return (BufferOut) dst;
        }
        if (dst instanceof OutputStream) {
            return new OutputSteamBufferOut((OutputStream) dst);
        }
        if (dst instanceof byte[]) {
            return new OutputSteamBufferOut(JieIO.outputStream((byte[]) dst));
        }
        if (dst instanceof ByteBuffer) {
            return new OutputSteamBufferOut(JieIO.outputStream((ByteBuffer) dst));
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
            ByteBuffer buffer = enReader.read();
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
        ByteBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(ByteBuffer buffer) throws Exception;
    }

    private final class EnReader {

        private final BufferIn in;

        // null:  break end;
        // empty: for last empty invocation
        private ByteBuffer buffer;
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
        private ByteBuffer read() {
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
                    ByteBuffer encoded;
                    if (!buffer.hasRemaining()) {
                        buffer = null;
                        encoded = encode(encoder, JieBytes.emptyBuffer(), true);
                        if (JieBytes.isEmpty(encoded)) {
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
                            buffer = JieBytes.emptyBuffer();
                        }
                    }
                    if (JieBytes.isEmpty(encoded)) {
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
                ByteBuffer bytes = data;
                for (Encoder encoder : encoders) {
                    bytes = encoder.encode(bytes, end);
                    if (bytes == null) {
                        break;
                    }
                }
                return bytes;
            };
        }

        @Nullable
        private ByteBuffer encode(@Nullable Encoder encoder, ByteBuffer buf, boolean end) {
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

    private final class InputStreamBufferIn extends BaseBufferIn {

        private final InputStream source;

        private InputStreamBufferIn(InputStream source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public ByteBuffer read() throws IOException {
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
            return ByteBuffer.wrap(
                hasRead == bufSize ? buf : Arrays.copyOfRange(buf, 0, hasRead)
            ).asReadOnlyBuffer();
        }
    }

    private final class BytesBufferIn extends BaseBufferIn {

        private final byte[] source;
        private int pos = 0;

        private BytesBufferIn(byte[] source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public ByteBuffer read() {
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
            return ret;
        }
    }

    private final class BufferBufferIn extends BaseBufferIn {

        private final ByteBuffer source;

        private BufferBufferIn(ByteBuffer source, int blockSize) {
            super(blockSize);
            this.source = source;
        }

        @Override
        public ByteBuffer read() {
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
            return ret;
        }
    }

    private static final class OutputSteamBufferOut implements BufferOut {

        private final OutputStream dest;

        private OutputSteamBufferOut(OutputStream dest) {
            this.dest = dest;
        }

        @Override
        public void write(ByteBuffer buffer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                dest.write(buffer.array(), JieBuffer.getArrayStartIndex(buffer), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                byte[] buf = new byte[buffer.remaining()];
                buffer.get(buf);
                dest.write(buf);
            }
        }
    }

    private static final class NullBufferOut implements BufferOut {

        static final NullBufferOut SINGLETON = new NullBufferOut();

        @Override
        public void write(ByteBuffer buffer) {
            // Do nothing
        }
    }

    private final class StreamIn extends InputStream {

        private final EnReader enReader;
        private ByteBuffer buffer = JieBytes.emptyBuffer();
        private boolean closed = false;

        private StreamIn(BufferIn in) {
            this.enReader = new EnReader(in);
        }

        @Nullable
        private ByteBuffer read0() throws IOException {
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
                return buffer.get() & 0xff;
            }
            ByteBuffer newBuf = read0();
            if (newBuf == null) {
                buffer = null;
                return -1;
            }
            buffer = newBuf;
            return buffer.get() & 0xff;
        }

        public int read(byte[] dst, int off, int len) throws IOException {
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
                    ByteBuffer newBuf = read0();
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

        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0 || buffer == null) {
                return 0;
            }
            int pos = 0;
            while (pos < n) {
                if (!buffer.hasRemaining()) {
                    ByteBuffer newBuf = read0();
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

        public int available() {
            return buffer == null ? 0 : buffer.remaining();
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

    private static abstract class AbsEncoder implements Encoder {

        protected final Encoder encoder;
        protected byte[] buf = JieBytes.emptyBytes();

        protected AbsEncoder(Encoder encoder) {
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

    static final class RoundEncoder extends AbsEncoder {

        private final int expectedBlockSize;

        RoundEncoder(Encoder encoder, int expectedBlockSize) {
            super(encoder);
            this.expectedBlockSize = expectedBlockSize;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
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
            ByteBuffer slice = JieBytes.slice(data, 0, sliceSize);
            data.position(data.position() + sliceSize);
            round.put(slice);
            round.flip();
            return round;
        }
    }

    static final class BufferedEncoder extends AbsEncoder {

        BufferedEncoder(Encoder encoder) {
            super(encoder);
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
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

        FixedSizeEncoder(Encoder encoder, int size) {
            super(encoder);
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer encode(ByteBuffer data, boolean end) {
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
                ByteBuffer slice = JieBytes.slice(total, 0, size);
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
                ByteBuffer slice = JieBytes.slice(total, 0, size);
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
}
