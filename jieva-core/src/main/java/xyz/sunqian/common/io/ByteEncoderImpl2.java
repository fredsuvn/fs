package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.JieCoding;
import xyz.sunqian.common.base.bytes.BytesKit;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Collection;
import java.util.function.Function;

final class ByteEncoderImpl2 implements ByteEncoder{



    @Override
    public @Nonnull ByteEncoder readLimit(long readLimit) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @Nonnull ByteEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException {
        return null;
    }

    @Override
    public @Nonnull ByteEncoder handler(Handler handler) {
        return null;
    }

    @Override
    public long encode() throws IORuntimeException {
        return 0;
    }

    @Override
    public long encodeTo(@Nonnull OutputStream dst) throws IORuntimeException {
        return 0;
    }

    @Override
    public long encodeTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
        return 0;
    }

    @Override
    public int encodeTo(byte @Nonnull [] dst) throws IORuntimeException {
        return 0;
    }

    @Override
    public int encodeTo(byte @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException {
        return 0;
    }

    @Override
    public int encodeTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
        return 0;
    }

    @Override
    public @Nonnull InputStream asInputStream() {
        return null;
    }

    @Override
    public @Nonnull ByteReader asByteReader() {
        return null;
    }

    private static class HandlerHelper {

        protected static @Nonnull ByteBuffer cutAndConcatenate(ByteBuffer b1, ByteBuffer b2, int size) {
            ByteBuffer ret = ByteBuffer.allocate(size);
            int size1 = b1.remaining();
            int size2 = b2.remaining();
            ret.put(b1);
            int actualSize = Math.min(size - size1, size2);
            int limit = b2.limit();
            int newLimit = b2.position() + actualSize;
            b2.limit(newLimit);
            ret.put(b2);
            b2.limit(limit);
            ret.flip();
            return ret;
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

    static final class FixedSizeHandler implements Handler {

        private final @Nonnull Handler handler;
        private final int size;

        // Capacity is always the size.
        private @Nullable ByteBuffer buf;

        FixedSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            this.handler = handler;
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) throws Exception {

            @Nullable Object result = null;
            boolean encoded = false;

            // clean buffer
            if (buf != null && buf.position() > 0) {
                BufferKit.readTo(data, buf);
                if (end && !data.hasRemaining()) {
                    buf.flip();
                    return handler.handle(buf, true);
                }
                if (buf.hasRemaining()) {
                    return null;
                }
                buf.flip();
                result = JieCoding.ifAdd(result, handler.handle(buf, false));
                encoded = true;
                buf.clear();
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
                    result = JieCoding.ifAdd(result, handler.handle(slice, true));
                    return JieCoding.ifMerge(result, BufferMerger.SINGLETON);
                } else {
                    result = JieCoding.ifAdd(result, handler.handle(slice, false));
                    encoded = true;
                }
            }
            data.limit(limit);

            // buffering
            if (data.hasRemaining()) {
                if (buf == null) {
                    buf = ByteBuffer.allocate(size);
                }
                BufferKit.readTo(data, buf);
                if (end) {
                    buf.flip();
                    result = JieCoding.ifAdd(result, handler.handle(buf, true));
                    encoded = true;
                }
            }

            @Nullable ByteBuffer ret = JieCoding.ifMerge(result, BufferMerger.SINGLETON);
            if (end && !encoded) {
                return handler.handle(BytesKit.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class RoundingEncoder implements Handler {

        private final Handler encoder;
        private final int size;

        // Capacity is always the size.
        private @Nullable ByteBuffer buffer;

        RoundingEncoder(Handler encoder, int size) {
            checkSize(size);
            this.encoder = encoder;
            this.size = size;
        }

        @Override
        public @Nullable ByteBuffer handle(ByteBuffer data, boolean end) throws Exception {
            @Nullable Object result = null;
            boolean encoded = false;

            // clean buffer
            if (buffer != null && buffer.position() > 0) {
                BufferKit.readTo(data, buffer);
                if (end && !data.hasRemaining()) {
                    buffer.flip();
                    return encoder.handle(buffer, true);
                }
                if (buffer.hasRemaining()) {
                    return null;
                }
                buffer.flip();
                result = JieCoding.ifAdd(result, encoder.handle(buffer, false));
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
                    result = JieCoding.ifAdd(result, encoder.handle(slice, true));
                    return JieCoding.ifMerge(result, ByteEncoderImpl.BufferMerger.SINGLETON);
                } else {
                    result = JieCoding.ifAdd(result, encoder.handle(slice, false));
                    encoded = true;
                }
            }

            // buffering
            if (data.hasRemaining()) {
                if (buffer == null) {
                    buffer = ByteBuffer.allocate(size);
                }
                BufferKit.readTo(data, buffer);
                if (end) {
                    buffer.flip();
                    result = JieCoding.ifAdd(result, encoder.handle(buffer, true));
                    encoded = true;
                }
            }

            @Nullable ByteBuffer ret = JieCoding.ifMerge(result, ByteEncoderImpl.BufferMerger.SINGLETON);
            if (end && !encoded) {
                return encoder.handle(JieBytes.emptyBuffer(), true);
            }
            return ret;
        }
    }

    static final class BufferingEncoder implements Handler {

        private final Handler encoder;
        private byte @Nullable [] buffer = null;

        BufferingEncoder(Handler encoder) {
            this.encoder = encoder;
        }

        @Override
        public @Nullable ByteBuffer handle(ByteBuffer data, boolean end) throws Exception {
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
            @Nullable ByteBuffer ret = encoder.handle(totalBuffer, end);
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

    static final class EmptyEncoder implements Handler {

        static final ByteEncoderImpl.EmptyEncoder SINGLETON = new ByteEncoderImpl.EmptyEncoder();

        @Override
        public ByteBuffer handle(ByteBuffer data, boolean end) {
            return data;
        }
    }
}
