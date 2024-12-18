package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.coll.JieArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class ByteStreamImpl implements ByteStream {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private List<Encoder> encoders;

    ByteStreamImpl(InputStream source) {
        this.source = source;
    }

    ByteStreamImpl(byte[] source) {
        this.source = source;
    }

    ByteStreamImpl(ByteBuffer source) {
        this.source = source;
    }

    @Override
    public ByteStream readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public ByteStream readBlockSize(int blockSize) {
        if (blockSize <= 0) {
            throw new IORuntimeException("blockSize must > 0!");
        }
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public ByteStream endOnZeroRead(boolean endOnZeroRead) {
        this.endOnZeroRead = endOnZeroRead;
        return this;
    }

    @Override
    public ByteStream encoder(Encoder encoder) {
        if (encoders == null) {
            encoders = new ArrayList<>();
        }
        encoders.add(encoder);
        return this;
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
            }
            return bytes;
        };
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
        if (src.length == 0) {
            return -1;
        }
        int len = getDirectLen(src.length);
        System.arraycopy(src, 0, dst, 0, len);
        return len;
    }

    private long bytesToBuffer(byte[] src, ByteBuffer dst) {
        if (src.length == 0) {
            return -1;
        }
        int len = getDirectLen(src.length);
        dst.put(src, 0, len);
        return len;
    }

    private long bufferToBytes(ByteBuffer src, byte[] dst) {
        if (src.remaining() == 0) {
            return -1;
        }
        int len = getDirectLen(src.remaining());
        src.get(dst, 0, len);
        return len;
    }

    private long bufferToBuffer(ByteBuffer src, ByteBuffer dst) {
        if (src.remaining() == 0) {
            return -1;
        }
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
            return blockSize;
        }
        return (int) Math.min(readLimit, blockSize);
    }

    private long readTo(BufferIn in, BufferOut out) throws Exception {
        long count = 0;
        while (true) {
            ByteBuffer buf = in.read();
            if (buf == null && count == 0) {
                return -1;
            }
            buf = buf == null ? JieBytes.emptyBuffer() : buf;
            Encoder encoder = buildEncoder();
            if (!buf.hasRemaining()) {
                if (encoder != null) {
                    ByteBuffer encoded = encode(encoder, buf, true);
                    out.write(encoded);
                }
                break;
            }
            int readSize = buf.remaining();
            count += readSize;
            if (encoder != null) {
                ByteBuffer encoded;
                if (readSize < blockSize) {
                    encoded = encode(encoder, buf, true);
                    out.write(encoded);
                    break;
                } else {
                    encoded = encode(encoder, buf, false);
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

    private ByteBuffer encode(Encoder encoder, ByteBuffer buf, boolean end) {
        try {
            return encoder.encode(buf, end);
        } catch (Exception e) {
            throw new IOEncodingException(e);
        }
    }

    private interface BufferIn {
        @Nullable
        ByteBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(ByteBuffer buffer) throws Exception;
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
                return JieBytes.emptyBuffer();
            }
            int hasRead = 0;
            boolean zeroRead = false;
            byte[] buf = new byte[bufSize];
            while (hasRead < readSize) {
                int size = source.read(buf, hasRead, readSize - hasRead);
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
                    return JieBytes.emptyBuffer();
                }
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
            int readSize = remaining < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return JieBytes.emptyBuffer();
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
            int readSize = remaining < 0 ? blockSize : (int) Math.min(remaining, blockSize);
            if (readSize <= 0) {
                return JieBytes.emptyBuffer();
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

        private final BufferIn in;
        private ByteBuffer buffer = JieBytes.emptyBuffer();

        // 0-init, 1-processing, 2-end, 3-closed
        private int state = 0;

        private StreamIn(BufferIn in) {
            this.in = in;
        }

        @Nullable
        private ByteBuffer read0() throws IOException {
            if (state == 2) {
                return null;
            }
            try {
                ByteBuffer buf = in.read();
                Encoder encoder = buildEncoder();
                if (buf == null || !buf.hasRemaining()) {
                    if (state == 0) {
                        state = 2;
                        return null;
                    }
                    state = 2;
                    if (encoder == null) {
                        return null;
                    }
                    ByteBuffer ret = encoder.encode(JieBytes.emptyBuffer(), true);
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
                return encoder.encode(buf, false);
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

        protected ByteBuffer totalData(ByteBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            ByteBuffer total = ByteBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
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
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            if (end) {
                return encoder.encode(totalData(data), true);
            }
            int size = totalSize(data);
            if (size == expectedBlockSize) {
                ByteBuffer total = totalData(data);
                buf = JieBytes.emptyBytes();
                return encoder.encode(total, false);
            }
            if (size < expectedBlockSize) {
                byte[] newBuf = new byte[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                data.get(newBuf, buf.length, data.remaining());
                buf = newBuf;
                return JieBytes.emptyBuffer();
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
        public ByteBuffer encode(ByteBuffer data, boolean end) {
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
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            ByteBuffer total = totalData(data);
            int totalSize = total.remaining();
            int times = totalSize / size;
            if (times == 0) {
                if (end) {
                    return encoder.encode(total, true);
                }
                buf = new byte[totalSize];
                total.get(buf);
                return JieBytes.emptyBuffer();
            }
            if (times == 1) {
                ByteBuffer slice = JieBytes.slice(total, 0, size);
                ByteBuffer ret1 = encoder.encode(slice, false);
                total.position(total.position() + size);
                if (end) {
                    ByteBuffer ret2 = encoder.encode(total, true);
                    int retSize1 = ret1.remaining();
                    int retSize2 = ret2.remaining();
                    byte[] ret = new byte[retSize1 + retSize2];
                    ret1.get(ret, 0, retSize1);
                    ret2.get(ret, retSize1, retSize2);
                    return ByteBuffer.wrap(ret);
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
                bytesBuilder.append(ret);
            }
            if (end) {
                ByteBuffer ret2 = encoder.encode(total, true);
                bytesBuilder.append(ret2);
            } else {
                buf = new byte[total.remaining()];
                total.get(buf);
            }
            return bytesBuilder.toByteBuffer();
        }
    }
}
