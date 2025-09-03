package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

public class CharProcessorImpl implements CharProcessor {

    private final @Nonnull CharReader src;
    private long readLimit = -1;
    private int readBlockSize = IOKit.bufferSize();
    private @Nullable List<CharTransformer> transformers = null;

    CharProcessorImpl(@Nonnull CharReader src) {
        this.src = src;
    }

    @Override
    public @Nonnull CharProcessor readLimit(long readLimit) throws IllegalArgumentException {
        IOChecker.checkReadLimit(readLimit);
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public @Nonnull CharProcessor readBlockSize(int readBlockSize) throws IllegalArgumentException {
        IOChecker.checkReadBlockSize(readBlockSize);
        this.readBlockSize = readBlockSize;
        return this;
    }

    @Override
    public @Nonnull CharProcessor transformer(@Nonnull CharTransformer transformer) {
        if (transformers == null) {
            transformers = new ArrayList<>();
        }
        transformers.add(transformer);
        return this;
    }

    @Override
    public long processTo(@Nonnull Appendable dst) throws IORuntimeException {
        try {
            if (transformers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(dst);
            }
            return encode(dst, transformers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long processTo(char @Nonnull [] dst) throws IORuntimeException {
        try {
            Writer out = IOKit.newWriter(dst);
            if (transformers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, transformers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long processTo(char @Nonnull [] dst, int off) throws IndexOutOfBoundsException, IORuntimeException {
        Writer out = IOKit.newWriter(dst, off, dst.length - off);
        try {
            if (transformers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, transformers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public long processTo(@Nonnull CharBuffer dst) throws IORuntimeException {
        try {
            Writer out = IOKit.newWriter(dst);
            if (transformers == null) {
                CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
                return reader.readTo(out);
            }
            return encode(out, transformers);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public @Nonnull String toString() {
        return new String(toCharArray());
    }

    private long encode(@Nonnull Object dst, @Nonnull List<@Nonnull CharTransformer> transformers) throws Exception {
        CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
        long count = 0;
        while (true) {
            CharSegment block = reader.read(readBlockSize);
            CharBuffer data = block.data();
            count += data.remaining();
            boolean end = block.end();
            for (CharTransformer transformer : transformers) {
                if (data == null) {
                    break;
                }
                data = transformer.transform(data, end);
            }
            if (data != null) {
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
        } else {
            throw new UnsupportedOperationException("Unsupported destination: " + dst.getClass() + ".");
        }
    }

    @Override
    public @Nonnull Reader asReader() {
        CharReader reader = readLimit < 0 ? src : src.limit(readLimit);
        if (transformers == null) {
            return reader.asReader();
        }
        return new EncoderReader(reader, readBlockSize, transformers);
    }

    @Override
    public @Nonnull CharReader asCharReader() {
        if (transformers == null) {
            return readLimit < 0 ? src : src.limit(readLimit);
        }
        return CharReader.from(asReader());
    }

    private static final class EncoderReader extends Reader {

        private final @Nonnull CharReader reader;
        private final int readBlockSize;
        private final @Nonnull List<@Nonnull CharTransformer> transformers;

        private @Nullable CharSegment nextSeg = null;
        private boolean closed = false;

        private EncoderReader(
            @Nonnull CharReader reader, int readBlockSize, @Nonnull List<@Nonnull CharTransformer> transformers
        ) {
            this.reader = reader;
            this.readBlockSize = readBlockSize;
            this.transformers = transformers;
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
            for (CharTransformer transformer : transformers) {
                if (data == null) {
                    break;
                }
                data = transformer.transform(data, end);
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
                    return nextSeg.data().get();
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
        public int read(char @Nonnull [] dst, int off, int len) throws IOException {
            checkClosed();
            IOChecker.checkOffLen(off, len, dst.length);
            if (len == 0) {
                return 0;
            }
            return read0(dst, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n < 0L) {
                throw new IllegalArgumentException("skip value is negative");
            }
            if (n == 0) {
                return 0;
            }
            return read0(null, 0, n);
        }

        private int read0(char @Nullable [] dst, int off, long len) throws IOException {
            int pos = 0;
            while (pos < len) {
                if (nextSeg == null) {
                    nextSeg = nextSeg();
                }
                if (nextSeg == CharSegment.empty(true)) {
                    break;
                }
                CharBuffer data = nextSeg.data();
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
                    nextSeg = CharSegment.empty(true);
                    break;
                }
                nextSeg = null;
            }
            return pos == 0 ? (dst == null ? 0 : -1) : pos;
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

    private static abstract class ResidualSizeHandler implements CharTransformer {

        protected final @Nonnull CharTransformer transformer;
        protected final int size;

        // Residual data;
        // Its capacity is always the size.
        private @Nullable CharBuffer residual;

        protected ResidualSizeHandler(@Nonnull CharTransformer transformer, int size) throws IllegalArgumentException {
            this.transformer = transformer;
            this.size = size;
        }

        protected abstract @Nullable List<CharBuffer> handleMultiple(
            @Nonnull CharBuffer data, boolean end
        ) throws Exception;

        @Override
        public @Nullable CharBuffer transform(@Nonnull CharBuffer data, boolean end) throws Exception {

            // clean buffer
            CharBuffer previousResult = null;
            if (residual != null && residual.position() > 0) {
                BufferKit.readTo(data, residual);
                if (residual.hasRemaining()) {
                    // in this case data must be empty
                    if (end) {
                        residual.flip();
                        return transformer.transform(residual, true);
                    } else {
                        return null;
                    }
                } else {
                    residual.flip();
                    if (end) {
                        if (data.hasRemaining()) {
                            previousResult = transformer.transform(BufferKit.copy(residual), false);
                        } else {
                            return transformer.transform(residual, true);
                        }
                    } else {
                        previousResult = transformer.transform(BufferKit.copy(residual), false);
                    }
                    residual.clear();
                }
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
                    residualResult = transformer.transform(residual, true);
                }
            }

            // empty end
            if (end && previousResult == null && multipleResult == null && residualResult == null) {
                return transformer.transform(CharBuffer.allocate(0), true);
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
            if (multipleResult != null) {
                for (CharBuffer buf : multipleResult) {
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

        FixedSizeHandler(@Nonnull CharTransformer transformer, int size) throws IllegalArgumentException {
            super(transformer, size);
        }

        @Override
        protected @Nullable List<CharBuffer> handleMultiple(@Nonnull CharBuffer data, boolean end) throws Exception {
            int remainingSize = data.remaining();
            if (remainingSize <= 0) {
                return null;
            }
            List<CharBuffer> multipleResult = null;
            int multipleSize = remainingSize / size * size;
            if (multipleSize > 0) {
                multipleResult = new ArrayList<>(multipleSize / size);
                int curSize = multipleSize;
                while (curSize > 0) {
                    CharBuffer multiple = BufferKit.slice0(data, 0, size);
                    data.position(data.position() + size);
                    CharBuffer multipleRet = transformer.transform(
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

        private final @Nonnull List<CharBuffer> multipleResult = new ArrayList<>(1);

        MultipleSizeHandler(@Nonnull CharTransformer transformer, int size) throws IllegalArgumentException {
            super(transformer, size);
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
            CharBuffer multipleRet = transformer.transform(
                multiple,
                end && multipleSize == remainingSize
            );
            multipleResult.add(multipleRet);
            return multipleResult;
        }
    }

    static final class BufferedHandler implements CharTransformer {

        private final CharTransformer transformer;
        private char @Nullable [] buffer = null;

        BufferedHandler(CharTransformer transformer) {
            this.transformer = transformer;
        }

        @Override
        public @Nullable CharBuffer transform(@Nonnull CharBuffer data, boolean end) throws Exception {
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
            CharBuffer ret = transformer.transform(totalBuffer, end);
            if (end) {
                buffer = null;
            } else {
                buffer = BufferKit.read(totalBuffer);
            }
            return ret;
        }
    }

    static final class EmptyHandler implements CharTransformer {

        static final EmptyHandler SINGLETON = new EmptyHandler();

        @Override
        public CharBuffer transform(@Nonnull CharBuffer data, boolean end) {
            return data;
        }
    }
}
