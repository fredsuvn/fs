package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.chars.CharsKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

final class ByteEncoderImpl implements ByteEncoder {

    private final @Nonnull ByteReader src;
    private long readLimit = -1;
    private int readBlockSize = IOKit.bufferSize();
    private @Nullable List<Handler> handlers = null;

    ByteEncoderImpl(@Nonnull ByteReader src) {
        this.src = src;
    }

    @Override
    public @Nonnull ByteEncoder readLimit(long readLimit) throws IllegalArgumentException {
        IOHelper.checkReadLimit(readLimit);
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public @Nonnull ByteEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException {
        IOHelper.checkReadBlockSize(readBlockSize);
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public @Nonnull ByteEncoder handler(@Nonnull Handler handler) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(handler);
        return this;
    }

    @Override
    public long encode() throws IORuntimeException {
        try {
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                long count = 0;
                while (true) {
                    ByteSegment block = reader.read(readBlockSize);
                    count += block.data().remaining();
                    if (block.end()) {
                        break;
                    }
                }
                return count == 0L ? -1 : count;
            }
            return encode(null, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(@Nonnull OutputStream dst) throws IORuntimeException {
        try {
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(dst);
            }
            return encode(dst, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(@Nonnull WritableByteChannel dst) throws IORuntimeException {
        try {
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(dst);
            }
            return encode(dst, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(byte @Nonnull [] dst) throws IORuntimeException {
        try {
            OutputStream out = IOKit.newOutputStream(dst);
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(byte @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException {
        OutputStream out = IOKit.newOutputStream(dst, off, dst.length - off);
        try {
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(@Nonnull ByteBuffer dst) throws IORuntimeException {
        try {
            OutputStream out = IOKit.newOutputStream(dst);
            if (handlers == null) {
                ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public @Nonnull String toString() {
        return new String(toByteArray(), CharsKit.defaultCharset());
    }

    private long encode(@Nullable Object dst, @Nonnull List<@Nonnull Handler> handlers) throws Exception {
        ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
        long count = 0;
        while (true) {
            ByteSegment block = reader.read(readBlockSize);
            ByteBuffer data = block.data();
            count += data.remaining();
            boolean end = block.end();
            for (Handler handler : handlers) {
                if (data == null) {
                    break;
                }
                data = handler.handle(data, end);
            }
            if (data != null && dst != null) {
                writeTo(data, dst);
            }
            if (end) {
                break;
            }
        }
        return count == 0L ? -1 : count;
    }

    private void writeTo(@Nonnull ByteBuffer data, @Nonnull Object dst) {
        if (dst instanceof OutputStream) {
            BufferKit.readTo(data, (OutputStream) dst);
        } else if (dst instanceof WritableByteChannel) {
            BufferKit.readTo(data, (WritableByteChannel) dst);
        } else {
            throw new UnsupportedOperationException("Unsupported destination: " + dst.getClass() + ".");
        }
    }

    @Override
    public @Nonnull InputStream asInputStream() {
        ByteReader reader = readLimit < 0 ? src : src.limit(readLimit);
        if (handlers == null) {
            return reader.asInputStream();
        }
        return new EncoderInputStream(reader, readBlockSize, handlers);
    }

    @Override
    public @Nonnull ByteReader asByteReader() {
        if (handlers == null) {
            return readLimit < 0 ? src : src.limit(readLimit);
        }
        return ByteReader.from(asInputStream());
    }

    private static final class EncoderInputStream extends InputStream {

        private final @Nonnull ByteReader reader;
        private final int readBlockSize;
        private final @Nonnull List<@Nonnull Handler> handlers;

        private @Nullable ByteSegment nextSeg = null;
        private boolean closed = false;

        private EncoderInputStream(
            @Nonnull ByteReader reader, int readBlockSize, @Nonnull List<@Nonnull Handler> handlers
        ) {
            this.reader = reader;
            this.readBlockSize = readBlockSize;
            this.handlers = handlers;
        }

        private @Nonnull ByteSegment nextSeg() throws IOException {
            try {
                ByteSegment next;
                do {
                    next = next();
                } while (next == null);
                return next;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        private @Nullable ByteSegment next() throws Exception {
            ByteSegment block = reader.read(readBlockSize);
            ByteBuffer data = block.data();
            boolean end = block.end();
            for (Handler handler : handlers) {
                if (data == null) {
                    break;
                }
                data = handler.handle(data, end);
            }
            if (data == null) {
                return null;
            }
            if (data == block.data()) {
                return block;
            }
            return ByteSegment.of(data, end);
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            while (true) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
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
        public int read(byte @Nonnull [] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(byte @Nonnull [] dst, int off, int len) throws IOException {
            checkClosed();
            IOHelper.checkOffLen(dst.length, off, len);
            if (len == 0) {
                return 0;
            }
            return read0(dst, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            return read0(null, 0, n);
        }

        private int read0(byte @Nullable [] dst, int off, long len) throws IOException {
            int pos = 0;
            while (pos < len) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
                }
                if (nextSeg == ByteSegment.empty(true)) {
                    break;
                }
                ByteBuffer data = nextSeg.data();
                if (data.hasRemaining()) {
                    int readSize = (int) Math.min(data.remaining(), len - pos);
                    if (dst != null) {
                        data.get(dst, pos + off, readSize);
                    } else {
                        data.position(data.position() + readSize);
                    }
                    pos += readSize;
                    continue;
                }
                if (nextSeg.end()) {
                    nextSeg = ByteSegment.empty(true);
                    break;
                }
                nextSeg = null;
            }
            return pos == 0 ? (dst == null ? 0 : -1) : pos;
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
            try {
                reader.close();
            } catch (Exception e) {
                throw new IOException(e);
            }
            closed = true;
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static abstract class ResidualSizeHandler implements Handler {

        protected final @Nonnull Handler handler;
        protected final int size;

        // Residual data;
        // Its capacity is always the size.
        private @Nullable ByteBuffer residual;

        protected ResidualSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            this.handler = handler;
            this.size = size;
        }

        protected abstract @Nullable List<ByteBuffer> handleMultiple(
            @Nonnull ByteBuffer data, boolean end
        ) throws Exception;

        @Override
        public @Nullable ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) throws Exception {

            // clean buffer
            ByteBuffer previousResult = null;
            if (residual != null && residual.position() > 0) {
                BufferKit.readTo(data, residual);
                if (residual.hasRemaining()) {
                    // in this case data must be empty
                    if (end) {
                        residual.flip();
                        return handler.handle(residual, true);
                    } else {
                        return null;
                    }
                } else {
                    residual.flip();
                    if (end) {
                        if (data.hasRemaining()) {
                            previousResult = handler.handle(BufferKit.copy(residual), false);
                        } else {
                            return handler.handle(residual, true);
                        }
                    } else {
                        previousResult = handler.handle(BufferKit.copy(residual), false);
                    }
                    residual.clear();
                }
            }

            // multiple
            List<ByteBuffer> multipleResult = handleMultiple(data, end);

            // remainder
            ByteBuffer residualResult = null;
            if (data.hasRemaining()) {
                if (residual == null) {
                    residual = ByteBuffer.allocate(size);
                }
                BufferKit.readTo(data, residual);
                if (end) {
                    residual.flip();
                    residualResult = handler.handle(residual, true);
                }
            }

            // empty end
            if (end && previousResult == null && multipleResult == null && residualResult == null) {
                return handler.handle(ByteBuffer.allocate(0), true);
            }

            return mergeResult(previousResult, multipleResult, residualResult);
        }

        private @Nullable ByteBuffer mergeResult(
            @Nullable ByteBuffer previousResult,
            @Nullable List<@Nonnull ByteBuffer> multipleResult,
            @Nullable ByteBuffer residualResult
        ) {
            int totalSize = 0;
            if (previousResult != null) {
                totalSize += previousResult.remaining();
            }
            if (multipleResult != null) {
                for (ByteBuffer buf : multipleResult) {
                    totalSize += buf.remaining();
                }
            }
            if (residualResult != null) {
                totalSize += residualResult.remaining();
            }
            if (totalSize == 0) {
                return null;
            }
            ByteBuffer result = ByteBuffer.allocate(totalSize);
            if (previousResult != null) {
                BufferKit.readTo(previousResult, result);
            }
            if (multipleResult != null) {
                for (ByteBuffer buf : multipleResult) {
                    BufferKit.readTo(buf, result);
                }
            }
            if (residualResult != null) {
                BufferKit.readTo(residualResult, result);
            }
            result.flip();
            return result;
        }
    }

    static final class FixedSizeHandler extends ResidualSizeHandler {

        FixedSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            super(handler, size);
        }

        @Override
        protected @Nullable List<ByteBuffer> handleMultiple(@Nonnull ByteBuffer data, boolean end) throws Exception {
            int remainingSize = data.remaining();
            if (remainingSize <= 0) {
                return null;
            }
            List<ByteBuffer> multipleResult = null;
            int multipleSize = remainingSize / size * size;
            if (multipleSize > 0) {
                multipleResult = new ArrayList<>(multipleSize / size);
                int curSize = multipleSize;
                while (curSize > 0) {
                    ByteBuffer multiple = BufferKit.slice0(data, 0, size);
                    data.position(data.position() + size);
                    ByteBuffer multipleRet = handler.handle(
                        multiple,
                        end && multipleSize == remainingSize && curSize == size
                    );
                    multipleResult.add(multipleRet);
                    curSize -= size;
                }
            }
            return multipleResult;
        }
    }

    static final class MultipleSizeHandler extends ResidualSizeHandler {

        private final @Nonnull List<ByteBuffer> multipleResult = new ArrayList<>(1);

        MultipleSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            super(handler, size);
        }

        @Override
        protected @Nullable List<ByteBuffer> handleMultiple(@Nonnull ByteBuffer data, boolean end) throws Exception {
            int remainingSize = data.remaining();
            int multipleSize = remainingSize / size * size;
            if (multipleSize <= 0) {
                return null;
            }
            ByteBuffer multiple = BufferKit.slice0(data, 0, multipleSize);
            data.position(data.position() + multipleSize);
            ByteBuffer multipleRet = handler.handle(
                multiple,
                end && multipleSize == remainingSize
            );
            multipleResult.add(multipleRet);
            return multipleResult;
        }
    }

    static final class BufferedHandler implements Handler {

        private final Handler handler;
        private byte @Nullable [] buffer = null;

        BufferedHandler(Handler handler) {
            this.handler = handler;
        }

        @Override
        public @Nullable ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) throws Exception {
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
            ByteBuffer ret = handler.handle(totalBuffer, end);
            if (end) {
                buffer = null;
            } else {
                buffer = BufferKit.read(totalBuffer);
            }
            return ret;
        }
    }

    static final class EmptyHandler implements Handler {

        static final EmptyHandler SINGLETON = new EmptyHandler();

        @Override
        public ByteBuffer handle(@Nonnull ByteBuffer data, boolean end) {
            return data;
        }
    }
}
