package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class CharEncoderImpl implements CharEncoder {

    private final @Nonnull CharReader src;
    private long readLimit = -1;
    private int readBlockSize = IOKit.bufferSize();
    private @Nullable List<Handler> handlers = null;

    CharEncoderImpl(@Nonnull CharReader src) {
        this.src = src;
    }

    @Override
    public @Nonnull CharEncoder readLimit(long readLimit) throws IllegalArgumentException {
        IOHelper.checkReadLimit(readLimit);
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public @Nonnull CharEncoder readBlockSize(int readBlockSize) throws IllegalArgumentException {
        IOHelper.checkReadBlockSize(readBlockSize);
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public @Nonnull CharEncoder handler(@Nonnull Handler handler) {
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
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                long count = 0;
                while (true) {
                    CharSegment block = reader.read(readBlockSize);
                    CharBuffer data = block.data();
                    count += data.remaining();
                    boolean end = block.end();
                    if (end) {
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
    public long encodeTo(@Nonnull Appendable dst) throws IORuntimeException {
        try {
            if (handlers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(dst);
            }
            return encode(dst, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(char @Nonnull [] dst) throws IORuntimeException {
        try {
            Writer out = IOKit.newWriter(dst);
            if (handlers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(char @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException {
        try {
            Writer out = IOKit.newWriter(dst, off, dst.length - off);
            if (handlers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long encodeTo(@Nonnull CharBuffer dst) throws IORuntimeException {
        try {
            Writer out = IOKit.newWriter(dst);
            if (handlers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, handlers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public @Nonnull String toString() {
        return new String(toCharArray());
    }

    private long encode(@Nullable Object dst, @Nonnull List<@Nonnull Handler> handlers) throws Exception {
        CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
        long count = 0;
        while (true) {
            CharSegment block = reader.read(readBlockSize);
            CharBuffer data = block.data();
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

    private void writeTo(@Nonnull CharBuffer data, @Nonnull Object dst) {
        if (dst instanceof Writer) {
            BufferKit.readTo(data, (Writer) dst);
        }
        throw new UnsupportedOperationException("Unsupported destination: " + dst.getClass() + ".");
    }

    @Override
    public @Nonnull Reader asReader() {
        CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
        if (handlers == null) {
            return reader.asReader();
        }
        return new CharEncoderImpl.EncoderReader(reader, readBlockSize, handlers);
    }

    private static final class EncoderReader extends Reader {

        private final @Nonnull CharReader reader;
        private final int readBlockSize;
        private final @Nonnull List<@Nonnull Handler> handlers;

        private @Nullable CharSegment nextSeg = null;
        private boolean closed = false;

        private EncoderReader(
            @Nonnull CharReader reader, int readBlockSize, @Nonnull List<@Nonnull Handler> handlers
        ) {
            this.reader = reader;
            this.readBlockSize = readBlockSize;
            this.handlers = handlers;
        }

        private @Nonnull CharSegment nextSeg() throws IOException {
            try {
                CharSegment next;
                do {
                    next = next();
                } while (next == null);
                return next;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        private @Nullable CharSegment next() throws Exception {
            CharSegment block = reader.read(readBlockSize);
            CharBuffer data = block.data();
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
            return CharSegment.of(data, end);
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            while (true) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
                }
                if (nextSeg == CharSegment.empty(true)) {
                    return -1;
                }
                if (nextSeg.data().hasRemaining()) {
                    return nextSeg.data().get() & 0xff;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    return -1;
                }
                nextSeg = null;
            }
        }

        @Override
        public int read(char @Nonnull [] b) throws IOException {
            return read(b, 0, b.length);
        }

        @Override
        public int read(char[] dst, int off, int len) throws IOException {
            checkClosed();
            IOHelper.checkOffLen(dst.length, off, len);
            if (len == 0) {
                return 0;
            }
            int pos = off;
            final int endIndex = off + len;
            while (pos < endIndex) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
                }
                if (nextSeg == CharSegment.empty(true)) {
                    return -1;
                }
                CharBuffer data = nextSeg.data();
                if (data.hasRemaining()) {
                    int readSize = Math.min(data.remaining(), endIndex - pos);
                    data.get(dst, pos, readSize);
                    pos += readSize;
                    continue;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    break;
                }
                nextSeg = null;
            }
            return pos - off;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            int pos = 0;
            while (pos < n) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
                }
                if (nextSeg == CharSegment.empty(true)) {
                    return 0;
                }
                CharBuffer data = nextSeg.data();
                if (data.hasRemaining()) {
                    int readSize = (int) Math.min(data.remaining(), n - pos);
                    data.position(data.position() + readSize);
                    pos += readSize;
                    continue;
                }
                if (nextSeg.end()) {
                    nextSeg = CharSegment.empty(true);
                    break;
                }
                nextSeg = null;
            }
            return pos;
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

    @Override
    public @Nonnull CharReader asCharReader() {
        if (handlers == null) {
            return readLimit < 0 ? src : src.limit(readLimit);
        }
        return CharReader.from(asReader());
    }

    private static abstract class ResidualSizeHandler implements Handler {

        protected final @Nonnull Handler handler;
        protected final int size;

        // Residual data;
        // Its capacity is always the size.
        private @Nullable CharBuffer residual;

        protected ResidualSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            this.handler = handler;
            this.size = size;
        }

        protected abstract @Nullable List<CharBuffer> handleMultiple(
            @Nonnull CharBuffer data, boolean end
        ) throws Exception;

        @Override
        public @Nullable CharBuffer handle(@Nonnull CharBuffer data, boolean end) throws Exception {

            // clean buffer
            CharBuffer previousResult = null;
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
                            previousResult = handler.handle(residual, false);
                        } else {
                            return handler.handle(residual, true);
                        }
                    } else {
                        previousResult = handler.handle(residual, false);
                    }
                    residual.clear();
                }
            }
            if (!data.hasRemaining()) {
                return previousResult;
            }

            // multiple
            List<CharBuffer> multipleResult = handleMultiple(data, end);

            // remainder
            CharBuffer residualResult = null;
            if (data.hasRemaining()) {
                if (residual == null) {
                    residual = CharBuffer.allocate(size);
                }
                BufferKit.readTo(data, residual);
                if (end) {
                    residual.flip();
                    residualResult = handler.handle(residual, true);
                }
            }

            // empty end
            if (end && previousResult == null && multipleResult == null && residualResult == null) {
                return handler.handle(CharBuffer.allocate(0), true);
            }

            return mergeResult(previousResult, multipleResult, residualResult);
        }

        private @Nullable CharBuffer mergeResult(
            @Nullable CharBuffer previousResult,
            @Nullable List<@Nonnull CharBuffer> multipleResult,
            @Nullable CharBuffer residualResult
        ) {
            int totalSize = 0;
            if (previousResult != null) {
                totalSize += previousResult.remaining();
            }
            if (residualResult != null) {
                totalSize += residualResult.remaining();
            }
            if (multipleResult != null) {
                for (CharBuffer buf : multipleResult) {
                    totalSize += buf.remaining();
                }
            }
            if (totalSize == 0) {
                return null;
            }
            CharBuffer result = CharBuffer.allocate(totalSize);
            if (previousResult != null) {
                BufferKit.readTo(previousResult, result);
            }
            if (residualResult != null) {
                BufferKit.readTo(residualResult, result);
            }
            if (multipleResult != null) {
                for (CharBuffer buf : multipleResult) {
                    BufferKit.readTo(buf, result);
                }
            }
            result.flip();
            return result;
        }
    }

    static final class FixedSizeHandler extends CharEncoderImpl.ResidualSizeHandler {

        FixedSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            super(handler, size);
        }

        @Override
        protected @Nullable List<CharBuffer> handleMultiple(@Nonnull CharBuffer data, boolean end) throws Exception {
            List<CharBuffer> multipleResult = null;
            int remainingSize = data.remaining();
            int multipleSize = remainingSize / size * size;
            if (multipleSize > 0) {
                multipleResult = new ArrayList<>(multipleSize / size);
                int curSize = multipleSize;
                while (curSize > 0) {
                    CharBuffer multiple = BufferKit.slice0(data, 0, size);
                    data.position(data.position() + size);
                    CharBuffer multipleRet = handler.handle(
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

    static final class MultipleSizeHandler extends CharEncoderImpl.ResidualSizeHandler {

        private final @Nonnull List<CharBuffer> multipleResult = new ArrayList<>(1);

        MultipleSizeHandler(@Nonnull Handler handler, int size) throws IllegalArgumentException {
            super(handler, size);
        }

        @Override
        protected @Nullable List<CharBuffer> handleMultiple(@Nonnull CharBuffer data, boolean end) throws Exception {
            int remainingSize = data.remaining();
            int multipleSize = remainingSize / size * size;
            if (multipleSize <= 0) {
                return null;
            }
            CharBuffer multiple = BufferKit.slice0(data, 0, multipleSize);
            data.position(data.position() + multipleSize);
            CharBuffer multipleRet = handler.handle(
                multiple,
                end && multipleSize == remainingSize
            );
            multipleResult.set(0, multipleRet);
            return multipleResult;
        }
    }

    static final class BufferedHandler implements Handler {

        private final Handler handler;
        private char @Nullable [] buffer = null;

        BufferedHandler(Handler handler) {
            this.handler = handler;
        }

        @Override
        public @Nullable CharBuffer handle(@Nonnull CharBuffer data, boolean end) throws Exception {
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
            CharBuffer ret = handler.handle(totalBuffer, end);
            if (end) {
                buffer = null;
            } else {
                buffer = BufferKit.read(totalBuffer);
            }
            return ret;
        }
    }

    static final class EmptyHandler implements Handler {

        static final CharEncoderImpl.EmptyHandler SINGLETON = new CharEncoderImpl.EmptyHandler();

        @Override
        public CharBuffer handle(@Nonnull CharBuffer data, boolean end) {
            return data;
        }
    }
}
