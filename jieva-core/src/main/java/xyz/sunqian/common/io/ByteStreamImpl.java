package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieBytes;
import xyz.sunqian.common.coll.JieArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

final class ByteStreamImpl implements ByteStream {

    private final Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean endOnZeroRead = false;
    private Encoder encoder;

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
    public ByteStream to(OutputStream dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ByteStream to(byte[] dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ByteStream to(byte[] dest, int offset, int length) {
        if (offset == 0 && length == dest.length) {
            return to(dest);
        }
        try {
            this.dest = ByteBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    @Override
    public ByteStream to(ByteBuffer dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ByteStream readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public ByteStream blockSize(int blockSize) {
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
            if (encoder == null) {
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
            return start0();
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

    private long start0() throws Exception {
        BufferIn in = toBufferIn(source);
        BufferOut out = toBufferOut(dest);
        return readTo(in, out);
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof InputStream) {
            return new InputStreamBufferIn((InputStream) src, actualBlockSize, readLimit);
        }
        if (src instanceof byte[]) {
            return new BytesBufferIn((byte[]) src, actualBlockSize, readLimit);
        }
        if (src instanceof ByteBuffer) {
            return new BufferBufferIn((ByteBuffer) src, actualBlockSize, readLimit);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof OutputStream) {
            return new OutputSteamBufferOut((OutputStream) dst);
        }
        if (dst instanceof byte[]) {
            return new OutputSteamBufferOut(JieOut.wrap((byte[]) dst));
        }
        if (dst instanceof ByteBuffer) {
            return new OutputSteamBufferOut(JieOut.wrap((ByteBuffer) dst));
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
            if (buf == null || !buf.hasRemaining()) {
                if (count == 0) {
                    return buf == null ? -1 : 0;
                }
                if (encoder != null) {
                    ByteBuffer encoded = encoder.encode(JieBytes.emptyBuffer(), true);
                    out.write(encoded);
                }
                break;
            }
            int readSize = buf.remaining();
            count += readSize;
            if (encoder != null) {
                ByteBuffer encoded;
                if (readSize < blockSize) {
                    encoded = encoder.encode(buf, true);
                    out.write(encoded);
                    break;
                } else {
                    encoded = encoder.encode(buf, false);
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

    private interface BufferIn {
        @Nullable
        ByteBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(ByteBuffer buffer) throws Exception;
    }

    private final class InputStreamBufferIn implements BufferIn {

        private final InputStream source;
        private final byte[] block;
        private final ByteBuffer blockBuffer;
        private final long limit;
        private long remaining;

        private InputStreamBufferIn(InputStream source, int blockSize, long limit) {
            this.source = source;
            this.block = new byte[limit < 0 ? blockSize : (int) Math.min(blockSize, limit)];
            this.blockBuffer = ByteBuffer.wrap(block);
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public ByteBuffer read() throws IOException {
            int readSize = limit < 0 ? block.length : (int) Math.min(remaining, block.length);
            if (readSize <= 0) {
                return null;
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
                    return JieBytes.emptyBuffer();
                }
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(hasRead);
            if (limit > 0) {
                remaining -= hasRead;
            }
            return blockBuffer;
        }
    }

    private static final class BytesBufferIn implements BufferIn {

        private final byte[] source;
        private final ByteBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;

        private BytesBufferIn(byte[] source, int blockSize, long limit) {
            this.source = source;
            this.sourceBuffer = ByteBuffer.wrap(source);
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
        }

        @Override
        public ByteBuffer read() {
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

        private final ByteBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;
        private final long limit;
        private long remaining;
        private final int sourceRemaining;

        private BufferBufferIn(ByteBuffer source, int blockSize, long limit) {
            this.sourceBuffer = source.slice();
            this.blockSize = blockSize;
            this.limit = limit;
            this.remaining = limit;
            this.sourceRemaining = source.remaining();
        }

        @Override
        public ByteBuffer read() {
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

    final static class BufferedEncoder implements Encoder {

        private final Encoder encoder;
        private final int expectedBlockSize;
        private final @Nullable Function<ByteBuffer, ByteBuffer> filter;
        private byte[] buf = JieBytes.emptyBytes();

        BufferedEncoder(
            Encoder encoder, int expectedBlockSize, @Nullable Function<ByteBuffer, ByteBuffer> filter) {
            this.encoder = encoder;
            this.expectedBlockSize = expectedBlockSize;
            this.filter = filter;
        }

        @Override
        public ByteBuffer encode(ByteBuffer data, boolean end) {
            ByteBuffer actualData = filter == null ? data : filter.apply(data);
            if (end) {
                return encoder.encode(totalData(actualData), true);
            }
            int size = totalSize(actualData);
            if (size == expectedBlockSize) {
                ByteBuffer total = totalData(actualData);
                buf = JieBytes.emptyBytes();
                return encoder.encode(total, false);
            }
            if (size < expectedBlockSize) {
                byte[] newBuf = new byte[size];
                System.arraycopy(buf, 0, newBuf, 0, buf.length);
                actualData.get(newBuf, buf.length, actualData.remaining());
                buf = newBuf;
                return JieBytes.emptyBuffer();
            }
            int remainder = size % expectedBlockSize;
            if (remainder == 0) {
                ByteBuffer total = totalData(actualData);
                buf = JieBytes.emptyBytes();
                return encoder.encode(total, false);
            }
            int roundSize = size / expectedBlockSize * expectedBlockSize;
            ByteBuffer round = roundData(actualData, roundSize);
            buf = new byte[remainder];
            actualData.get(buf);
            return encoder.encode(round, false);
        }

        private ByteBuffer totalData(ByteBuffer data) {
            if (JieArray.isEmpty(buf)) {
                return data;
            }
            ByteBuffer total = ByteBuffer.allocate(totalSize(data));
            total.put(buf);
            total.put(data);
            total.flip();
            return total;
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

        private int totalSize(ByteBuffer data) {
            return buf.length + data.remaining();
        }
    }
}
