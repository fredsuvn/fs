package space.sunqian.fs.io;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.math.MathKit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

final class IOBack {

    private static final @Nonnull String MARK_NOT_SET = "Mark has not been set.";
    private static final @Nonnull String STREAM_CLOSED = "Stream closed.";
    private static final int CHARS_BUFFER_SIZE = 64;
    private static final int BYTES_BUFFER_SIZE = 64;

    private static @Nonnull String insufficientRemainingSpace(int len, int remaining) {
        return "Insufficient remaining space: " + len + " to " + remaining + ".";
    }

    private static @Nonnull String insufficientRemainingSpace(int len, long remaining) {
        return "Insufficient remaining space: " + len + " to " + remaining + ".";
    }

    private static @Nonnull String encodingFailed(CoderResult result) {
        return "Chars encoding failed: " + result + ".";
    }

    private static @Nonnull String decodingFailed(CoderResult result) {
        return "Bytes decoding failed: " + result + ".";
    }

    static @Nonnull InputStream inputStream(byte @Nonnull [] array) {
        return new BytesInputStream(array, 0, array.length);
    }

    static @Nonnull InputStream inputStream(
        byte @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(off, len, array.length);
        return new BytesInputStream(array, off, len);
    }

    static @Nonnull InputStream inputStream(@Nonnull ByteBuffer buffer) {
        return new BufferInputStream(buffer);
    }

    static @Nonnull InputStream inputStream(
        @Nonnull RandomAccessFile raf, long seek
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkSeek(seek);
        try {
            return new RafInputStream(raf, seek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static @Nonnull InputStream inputStream(@Nonnull Reader reader, @Nonnull Charset charset) {
        return new CharsInputStream(reader, charset);
    }

    static @Nonnull InputStream inputStream(@Nonnull InputStream in, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedInputStream(in, limit);
    }

    static @Nonnull InputStream emptyInputStream() {
        return EmptyInputStream.SINGLETON;
    }

    static @Nonnull Reader reader(char @Nonnull [] array) {
        return new CharsReader(array, 0, array.length);
    }

    static @Nonnull Reader reader(char @Nonnull [] array, int off, int len) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(off, len, array.length);
        return new CharsReader(array, off, len);
    }

    static @Nonnull Reader reader(@Nonnull CharSequence chars) {
        return new BufferReader(CharBuffer.wrap(chars));
    }

    static @Nonnull Reader reader(@Nonnull CharSequence chars, int start, int end) throws IndexOutOfBoundsException {
        IOChecker.checkStartEnd(start, end, chars.length());
        return new BufferReader(CharBuffer.wrap(chars, start, end));
    }

    static @Nonnull Reader reader(@Nonnull CharBuffer buffer) {
        return new BufferReader(buffer);
    }

    static @Nonnull Reader reader(@Nonnull InputStream inputStream, @Nonnull Charset charset) {
        return new BytesReader(inputStream, charset);
    }

    static @Nonnull Reader reader(@Nonnull Reader in, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedReader(in, limit);
    }

    static @Nonnull Reader emptyReader() {
        return EmptyReader.SINGLETON;
    }

    static @Nonnull OutputStream outputStream(byte @Nonnull [] array) {
        return new BytesOutputStream(array, 0, array.length);
    }

    static @Nonnull OutputStream outputStream(
        byte @Nonnull [] array, int off, int len
    ) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(off, len, array.length);
        return new BytesOutputStream(array, off, len);
    }

    static @Nonnull OutputStream outputStream(@Nonnull ByteBuffer buffer) {
        return new BufferOutputStream(buffer);
    }

    static @Nonnull OutputStream outputStream(
        @Nonnull RandomAccessFile raf, long seek
    ) throws IllegalArgumentException, IORuntimeException {
        IOChecker.checkSeek(seek);
        try {
            return new RafOutputStream(raf, seek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static @Nonnull OutputStream outputStream(@Nonnull Appendable appender, @Nonnull Charset charset) {
        return new AppenderOutputStream(appender, charset);
    }

    static @Nonnull OutputStream outputStream(@Nonnull OutputStream in, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedOutputStream(in, limit);
    }

    static @Nonnull OutputStream nullOutputStream() {
        return NullOutputStream.SINGLETON;
    }

    static @Nonnull Writer writer(char @Nonnull [] array) {
        return new CharsWriter(array, 0, array.length);
    }

    static @Nonnull Writer writer(char @Nonnull [] array, int off, int len) throws IndexOutOfBoundsException {
        IOChecker.checkOffLen(off, len, array.length);
        return new CharsWriter(array, off, len);
    }

    static @Nonnull Writer writer(@Nonnull CharBuffer buffer) {
        return new BufferWriter(buffer);
    }

    static @Nonnull Writer writer(@Nonnull OutputStream outputStream, Charset charset) {
        return new BytesWriter(outputStream, charset);
    }

    static @Nonnull Writer writer(@Nonnull Writer in, long limit) throws IllegalArgumentException {
        IOChecker.checkLimit(limit);
        return new LimitedWriter(in, limit);
    }

    static @Nonnull Writer nullWriter() {
        return NullWriter.SINGLETON;
    }

    private static final class BytesInputStream extends DoReadStream {

        private final byte @Nonnull [] buf;
        private int pos;
        private final int end;
        private int mark = -1;

        private BytesInputStream(byte @Nonnull [] buf, int off, int len) {
            this.buf = buf;
            this.pos = off;
            this.end = off + len;
        }

        @Override
        public int read() {
            return (pos < end) ? (buf[pos++] & 0xff) : -1;
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (pos >= end) {
                return -1;
            }
            int avail = end - pos;
            avail = Math.min(len, avail);
            System.arraycopy(buf, pos, b, off, avail);
            pos += avail;
            return avail;
        }

        @Override
        public long skip(long n) {
            if (n <= 0) {
                return 0;
            }
            int avail = end - pos;
            avail = (int) Math.min(n, avail);
            if (avail <= 0) {
                return 0;
            }
            pos += avail;
            return avail;
        }

        @Override
        public int available() {
            return end - pos;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readAheadLimit) {
            mark = pos;
        }

        @Override
        public void reset() throws IOException {
            if (mark < 0) {
                throw new IOException(MARK_NOT_SET);
            }
            pos = mark;
        }
    }

    private static final class BufferInputStream extends DoReadStream {

        private final @Nonnull ByteBuffer buffer;

        private BufferInputStream(@Nonnull ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() {
            if (!buffer.hasRemaining()) {
                return -1;
            }
            return buffer.get() & 0xff;
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (!buffer.hasRemaining()) {
                return -1;
            }
            int avail = Math.min(buffer.remaining(), len);
            buffer.get(b, off, avail);
            return avail;
        }

        @Override
        public long skip(long n) {
            if (n <= 0) {
                return 0;
            }
            int avail = (int) Math.min(buffer.remaining(), n);
            if (avail <= 0) {
                return 0;
            }
            buffer.position(buffer.position() + avail);
            return avail;
        }

        @Override
        public int available() {
            return buffer.remaining();
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readlimit) {
            buffer.mark();
        }

        @Override
        public void reset() throws IOException {
            try {
                buffer.reset();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private static final class RafInputStream extends DoReadStream {

        private final @Nonnull RandomAccessFile raf;
        private long mark = -1;

        private RafInputStream(@Nonnull RandomAccessFile raf, long seek) throws IOException {
            this.raf = raf;
            this.raf.seek(seek);
        }

        @Override
        public int read() throws IOException {
            return raf.read();
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) throws IOException {
            if (len == 0) {
                return 0;
            }
            return raf.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }
            return raf.skipBytes(MathKit.safeInt(n));
        }

        @Override
        public int available() throws IOException {
            return MathKit.safeInt(raf.length() - raf.getFilePointer());
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readlimit) {
            try {
                this.mark = raf.getFilePointer();
            } catch (IOException ignored) {
            }
        }

        @Override
        public void reset() throws IOException {
            if (mark < 0) {
                throw new IOException(MARK_NOT_SET);
            }
            raf.seek(mark);
        }

        @Override
        public void close() throws IOException {
            raf.close();
        }
    }

    private static final class CharsInputStream extends DoReadStream {

        private final @Nonnull Reader reader;
        private final @Nonnull CharsetEncoder encoder;
        private @Nonnull CharBuffer inBuffer;
        private @Nonnull ByteBuffer outBuffer;
        private boolean endOfInput = false;
        private boolean closed = false;
        private final byte @Nonnull [] buf = {0};

        // snapshot for mark/reset
        private CharBuffer inCopy;
        private ByteBuffer outCopy;
        private boolean endCopy;

        private CharsInputStream(
            @Nonnull Reader reader,
            @Nonnull CharsetEncoder encoder,
            int inBufferSize, int outBufferSize
        ) {
            this.reader = reader;
            this.encoder = encoder;
            this.inBuffer = CharBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = ByteBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private CharsInputStream(
            @Nonnull Reader reader,
            @Nonnull Charset charset,
            int inBufferSize, int outBufferSize
        ) {
            this(
                reader,
                charset.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        private CharsInputStream(@Nonnull Reader reader, @Nonnull Charset charset) {
            this(reader, charset, CHARS_BUFFER_SIZE, BYTES_BUFFER_SIZE);
        }

        @Override
        public int read() throws IOException {
            int readNum = read(buf, 0, 1);
            return readNum < 0 ? -1 : (buf[0] & 0xff);
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) throws IOException {
            checkClosed();
            if (len == 0) {
                return 0;
            }
            int readNum = (int) read0(b, off, len);
            return readNum == 0 ? -1 : readNum;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            return read0(null, 0, n);
        }

        @Override
        public int available() {
            return outBuffer.remaining();
        }

        @Override
        public void close() throws IOException {
            reader.close();
            closed = true;
        }

        @Override
        public boolean markSupported() {
            return reader.markSupported();
        }

        @Override
        public void mark(int readlimit) {
            try {
                reader.mark(readlimit);
            } catch (IOException ignored) {
            }
            inCopy = BufferKit.copy(inBuffer);
            outCopy = BufferKit.copy(outBuffer);
            endCopy = endOfInput;
        }

        @Override
        public void reset() throws IOException {
            if (inCopy == null) {
                throw new IOException(MARK_NOT_SET);
            }
            reader.reset();
            inBuffer = inCopy;
            outBuffer = outCopy;
            endOfInput = endCopy;
            encoder.reset();
            inCopy = null;
            outCopy = null;
        }

        private long read0(byte @Nullable [] b, int off, long len) throws IOException {
            long count = 0;
            while (count < len) {
                if (outBuffer.hasRemaining()) {
                    int avail = (int) Math.min(outBuffer.remaining(), len - count);
                    if (b != null) {
                        outBuffer.get(b, (int) (off + count), avail);
                    } else {
                        outBuffer.position(outBuffer.position() + avail);
                    }
                    count += avail;
                } else if (endOfInput) {
                    if (inBuffer.hasRemaining()) {
                        flushOutBuffer();
                    } else {
                        break;
                    }
                } else {
                    readToInBuffer();
                }
            }
            return count;
        }

        private void readToInBuffer() throws IOException {
            flushInBuffer();
            flushOutBuffer();
        }

        private void flushInBuffer() throws IOException {
            inBuffer.compact();
            int readSize = reader.read(inBuffer);
            if (readSize < 0) {
                endOfInput = true;
            }
            inBuffer.flip();
        }

        private void flushOutBuffer() throws IOException {
            outBuffer.compact();
            CoderResult coderResult = encoder.encode(inBuffer, outBuffer, endOfInput);
            if (coderResult.isUnderflow() || coderResult.isOverflow()) {
                outBuffer.flip();
                return;
            }
            throw new IOException(encodingFailed(coderResult));
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException(STREAM_CLOSED);
            }
        }
    }

    private static final class LimitedInputStream extends DoReadStream {

        private final @Nonnull InputStream in;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedInputStream(@Nonnull InputStream in, long limit) {
            this.in = in;
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            if (pos >= limit) {
                return -1;
            }
            int ret = in.read();
            if (ret == -1) {
                return -1;
            }
            pos++;
            return ret;
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) throws IOException {
            if (pos >= limit) {
                return -1;
            }
            int ret = in.read(b, off, (int) Math.min(limit - pos, len));
            if (ret < 0) {
                return -1;
            }
            pos += ret;
            return ret;
        }

        @Override
        public long skip(long n) throws IOException {
            long remaining = limit - pos;
            long ret = in.skip(Math.min(n, remaining));
            pos += ret;
            return ret;
        }

        @Override
        public int available() throws IOException {
            return Math.min(in.available(), MathKit.safeInt(limit - pos));
        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }

        @Override
        public void mark(int readAheadLimit) {
            in.mark(readAheadLimit);
            mark = pos;
        }

        @Override
        public void reset() throws IOException {
            in.reset();
            pos = mark;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    private static final class EmptyInputStream extends DoReadStream {

        private static final @Nonnull EmptyInputStream SINGLETON = new EmptyInputStream();

        @Override
        public int read() {
            return -1;
        }

        @Override
        protected int doRead(byte @Nonnull [] b, int off, int len) {
            return len == 0 ? 0 : -1;
        }

        @Override
        public long skip(long n) {
            return 0;
        }
    }

    private static final class CharsReader extends DoReadReader {

        private final char @Nonnull [] buf;
        private int pos;
        private final int end;
        private int mark = -1;

        private CharsReader(char @Nonnull [] buf, int off, int len) {
            this.buf = buf;
            this.pos = off;
            this.end = off + len;
        }

        @Override
        public int read() {
            return (pos < end) ? buf[pos++] : -1;
        }

        @Override
        protected int doRead(char @Nonnull [] b, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (pos >= end) {
                return -1;
            }
            int avail = end - pos;
            avail = Math.min(len, avail);
            System.arraycopy(buf, pos, b, off, avail);
            pos += avail;
            return avail;
        }

        @Override
        public int read(@Nonnull CharBuffer target) {
            if (pos >= end) {
                return -1;
            }
            int avail = end - pos;
            avail = Math.min(target.remaining(), avail);
            target.put(buf, pos, avail);
            pos += avail;
            return avail;
        }

        @Override
        public long skip(long n) throws IllegalArgumentException {
            IOChecker.checkSkip(n);
            if (n == 0) {
                return 0;
            }
            int avail = end - pos;
            avail = (int) Math.min(n, avail);
            if (avail <= 0) {
                return 0;
            }
            pos += avail;
            return avail;
        }

        @Override
        public boolean ready() {
            return true;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readAheadLimit) {
            mark = pos;
        }

        @Override
        public void reset() throws IOException {
            if (mark < 0) {
                throw new IOException(MARK_NOT_SET);
            }
            pos = mark;
        }

        @Override
        public void close() {
        }
    }

    private static final class BufferReader extends DoReadReader {

        private final @Nonnull CharBuffer buffer;

        private BufferReader(@Nonnull CharBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() {
            if (buffer.remaining() <= 0) {
                return -1;
            }
            return buffer.get();
        }

        @Override
        protected int doRead(char @Nonnull [] c, int off, int len) {
            if (len == 0) {
                return 0;
            }
            if (!buffer.hasRemaining()) {
                return -1;
            }
            int avail = Math.min(buffer.remaining(), len);
            buffer.get(c, off, avail);
            return avail;
        }

        @Override
        public int read(@Nonnull CharBuffer target) throws IOException {
            return buffer.read(target);
        }

        @Override
        public long skip(long n) throws IllegalArgumentException {
            IOChecker.checkSkip(n);
            if (n == 0) {
                return 0;
            }
            int avail = (int) Math.min(buffer.remaining(), n);
            if (avail <= 0) {
                return 0;
            }
            buffer.position(buffer.position() + avail);
            return avail;
        }

        @Override
        public boolean ready() {
            return true;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readlimit) {
            buffer.mark();
        }

        @Override
        public void reset() throws IOException {
            try {
                buffer.reset();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void close() {
        }
    }

    private static final class BytesReader extends DoReadReader {

        private final @Nonnull InputStream inputStream;
        private final @Nonnull CharsetDecoder decoder;
        private ByteBuffer inBuffer;
        private CharBuffer outBuffer;
        private boolean endOfInput = false;
        private boolean closed = false;
        private final char @Nonnull [] cbuf = {0};

        // snapshot for mark/reset
        private ByteBuffer inCopy;
        private CharBuffer outCopy;
        private boolean endCopy;

        private BytesReader(
            @Nonnull InputStream inputStream,
            @Nonnull CharsetDecoder decoder,
            int inBufferSize, int outBufferSize
        ) {
            this.inputStream = inputStream;
            this.decoder = decoder;
            this.inBuffer = ByteBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = CharBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private BytesReader(
            @Nonnull InputStream inputStream,
            @Nonnull Charset charset,
            int inBufferSize, int outBufferSize
        ) {
            this(
                inputStream,
                charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        private BytesReader(@Nonnull InputStream inputStream, @Nonnull Charset charset) {
            this(inputStream, charset, BYTES_BUFFER_SIZE, CHARS_BUFFER_SIZE);
        }

        @Override
        public int read() throws IOException {
            int readNum = read(cbuf, 0, 1);
            return readNum < 0 ? -1 : cbuf[0];
        }

        @Override
        protected int doRead(char @Nonnull [] c, int off, int len) throws IOException {
            checkClosed();
            if (len == 0) {
                return 0;
            }
            int readNum = (int) read0(c, off, len);
            return readNum == 0 ? -1 : readNum;
        }

        @Override
        public long skip(long n) throws IllegalArgumentException, IOException {
            IOChecker.checkSkip(n);
            checkClosed();
            if (n == 0) {
                return 0;
            }
            return read0(null, 0, n);
        }

        @Override
        public boolean ready() {
            return outBuffer.hasRemaining();
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
            closed = true;
        }

        @Override
        public boolean markSupported() {
            return inputStream.markSupported();
        }

        @Override
        public void mark(int readAheadLimit) {
            inputStream.mark(readAheadLimit);
            inCopy = BufferKit.copy(inBuffer);
            outCopy = BufferKit.copy(outBuffer);
            endCopy = endOfInput;
        }

        @Override
        public void reset() throws IOException {
            if (inCopy == null) {
                throw new IOException(MARK_NOT_SET);
            }
            inputStream.reset();
            inBuffer = inCopy;
            outBuffer = outCopy;
            endOfInput = endCopy;
            decoder.reset();
            inCopy = null;
            outCopy = null;
        }

        private long read0(char @Nullable [] b, int off, long len) throws IOException {
            long count = 0;
            while (count < len) {
                if (outBuffer.hasRemaining()) {
                    int avail = (int) Math.min(outBuffer.remaining(), len - count);
                    if (b != null) {
                        outBuffer.get(b, (int) (off + count), avail);
                    } else {
                        outBuffer.position(outBuffer.position() + avail);
                    }
                    count += avail;
                } else if (endOfInput) {
                    if (inBuffer.hasRemaining()) {
                        flushOutBuffer();
                    } else {
                        break;
                    }
                } else {
                    flushBuffer();
                }
            }
            return count;
        }

        private void flushBuffer() throws IOException {
            flushInBuffer();
            flushOutBuffer();
        }

        private void flushInBuffer() throws IOException {
            inBuffer.compact();
            int readSize = inputStream.read(inBuffer.array(), inBuffer.position(), inBuffer.remaining());
            if (readSize < 0) {
                endOfInput = true;
            }
            if (readSize > 0) {
                inBuffer.position(inBuffer.position() + readSize);
            }
            inBuffer.flip();
        }

        private void flushOutBuffer() throws IOException {
            outBuffer.compact();
            CoderResult coderResult = decoder.decode(inBuffer, outBuffer, endOfInput);
            if (coderResult.isUnderflow() || coderResult.isOverflow()) {
                outBuffer.flip();
                return;
            }
            throw new IOException(decodingFailed(coderResult));
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException(STREAM_CLOSED);
            }
        }
    }

    private static final class LimitedReader extends DoReadReader {

        private final @Nonnull Reader in;
        private final long limit;

        private long pos = 0;
        private long mark = 0;

        private LimitedReader(@Nonnull Reader in, long limit) {
            this.in = in;
            this.limit = limit;
        }

        @Override
        public int read() throws IOException {
            if (pos >= limit) {
                return -1;
            }
            int ret = in.read();
            if (ret == -1) {
                return -1;
            }
            pos++;
            return ret;
        }

        @Override
        protected int doRead(char @Nonnull [] b, int off, int len) throws IOException {
            if (pos >= limit) {
                return -1;
            }
            int ret = in.read(b, off, (int) Math.min(limit - pos, len));
            if (ret < 0) {
                return -1;
            }
            pos += ret;
            return ret;
        }

        @Override
        public long skip(long n) throws IllegalArgumentException, IOException {
            IOChecker.checkSkip(n);
            long remaining = limit - pos;
            long ret = in.skip(Math.min(n, remaining));
            pos += ret;
            return ret;
        }

        @Override
        public boolean ready() throws IOException {
            return in.ready();
        }

        @Override
        public boolean markSupported() {
            return in.markSupported();
        }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            in.mark(readAheadLimit);
            mark = pos;
        }

        @Override
        public void reset() throws IOException {
            in.reset();
            pos = mark;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    private static final class EmptyReader extends DoReadReader {

        private static final @Nonnull EmptyReader SINGLETON = new EmptyReader();

        @Override
        public int read(@Nonnull CharBuffer target) {
            return -1;
        }

        @Override
        public int read() {
            return -1;
        }

        @Override
        protected int doRead(char @Nonnull [] cbuf, int off, int len) {
            return len == 0 ? 0 : -1;
        }

        @Override
        public long skip(long n) throws IllegalArgumentException {
            IOChecker.checkSkip(n);
            return 0;
        }

        @Override
        public boolean ready() {
            return true;
        }

        @Override
        public void close() {
        }
    }

    private static final class BytesOutputStream extends DoWriteStream {

        private final byte @Nonnull [] buf;
        private int pos;
        private final int end;

        private BytesOutputStream(byte @Nonnull [] buf, int off, int len) {
            this.buf = buf;
            this.pos = off;
            this.end = off + len;
        }

        @Override
        public void write(int b) throws IOException {
            int remaining = end - pos;
            if (remaining < 1) {
                throw new IOException(insufficientRemainingSpace(1, 0));
            }
            buf[pos] = (byte) b;
            pos++;
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            int remaining = end - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            System.arraycopy(b, off, buf, pos, len);
            pos += len;
        }
    }

    private static final class BufferOutputStream extends DoWriteStream {

        private final @Nonnull ByteBuffer buffer;

        private BufferOutputStream(@Nonnull ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) throws IOException {
            try {
                buffer.put((byte) b);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            try {
                buffer.put(b, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private static final class RafOutputStream extends DoWriteStream {

        private final @Nonnull RandomAccessFile raf;

        private RafOutputStream(@Nonnull RandomAccessFile raf, long seek) throws IOException {
            this.raf = raf;
            this.raf.seek(seek);
        }

        @Override
        public void write(int b) throws IOException {
            raf.write(b);
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            raf.write(b, off, len);
        }

        @Override
        public void flush() throws IOException {
            raf.getFD().sync();
        }

        @Override
        public void close() throws IOException {
            raf.close();
        }
    }

    private static final class AppenderOutputStream extends DoWriteStream {

        private final @Nonnull Appendable appender;
        private final @Nonnull CharsetDecoder decoder;
        private final @Nonnull ByteBuffer inBuffer;
        private final @Nonnull CharBuffer outBuffer;

        private boolean closed = false;
        private byte @Nullable [] writeOneBuf;

        private AppenderOutputStream(
            @Nonnull Appendable appender,
            @Nonnull CharsetDecoder decoder,
            int inBufferSize,
            int outBufferSize
        ) {
            this.appender = appender;
            this.decoder = decoder;
            this.inBuffer = ByteBuffer.allocate(inBufferSize);
            this.outBuffer = CharBuffer.allocate(outBufferSize);
        }

        private AppenderOutputStream(
            @Nonnull Appendable appender,
            @Nonnull Charset charset,
            int inBufferSize,
            int outBufferSize
        ) {
            this(
                appender,
                charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        private AppenderOutputStream(@Nonnull Appendable appender, @Nonnull Charset charset) {
            this(appender, charset, BYTES_BUFFER_SIZE, CHARS_BUFFER_SIZE);
        }

        @Override
        public void write(int b) throws IOException {
            if (writeOneBuf == null) {
                writeOneBuf = new byte[1];
            }
            writeOneBuf[0] = (byte) b;
            write(writeOneBuf, 0, 1);
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) throws IOException {
            checkClosed();
            if (len == 0) {
                return;
            }
            int count = 0;
            while (count < len) {
                int actualLen = Math.min(inBuffer.remaining(), len - count);
                inBuffer.put(b, off + count, actualLen);
                inBuffer.flip();
                while (true) {
                    CoderResult coderResult = decoder.decode(inBuffer, outBuffer, false);
                    if (coderResult.isOverflow()) {
                        outBuffer.flip();
                        appender.append(outBuffer);
                        outBuffer.clear();
                    } else if (coderResult.isUnderflow()) {
                        outBuffer.flip();
                        appender.append(outBuffer);
                        outBuffer.clear();
                        break;
                    } else {
                        throw new IOException(decodingFailed(coderResult));
                    }
                }
                count += actualLen;
                inBuffer.compact();
            }
        }

        @Override
        public void flush() throws IOException {
            checkClosed();
            IOKit.flush(appender);
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            IOKit.close(appender);
            closed = true;
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException(STREAM_CLOSED);
            }
        }
    }

    private static final class LimitedOutputStream extends DoWriteStream {

        private final @Nonnull OutputStream out;
        private final long limit;

        private long pos;

        private LimitedOutputStream(@Nonnull OutputStream out, long limit) {
            this.out = out;
            this.limit = limit;
        }

        @Override
        public void write(int b) throws IOException {
            if (pos >= limit) {
                throw new IOException(insufficientRemainingSpace(1, 0));
            }
            out.write(b);
            pos++;
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) throws IOException {
            long remaining = limit - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            out.write(b, off, len);
            pos += len;
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    private static final class NullOutputStream extends DoWriteStream {

        private static final @Nonnull NullOutputStream SINGLETON = new NullOutputStream();

        @Override
        public void write(int b) {
        }

        @Override
        protected void doWrite(byte @Nonnull [] b, int off, int len) {
        }
    }

    private static final class CharsWriter extends DoWriteWriter {

        private final char @Nonnull [] buf;
        private int pos;
        private final int end;

        private CharsWriter(char @Nonnull [] buf, int off, int len) {
            this.buf = buf;
            this.pos = off;
            this.end = off + len;
        }

        @Override
        public void write(int c) throws IOException {
            int remaining = end - pos;
            if (remaining < 1) {
                throw new IOException(insufficientRemainingSpace(1, remaining));
            }
            buf[pos] = (char) c;
            pos++;
        }

        @Override
        protected void doWrite(char @Nonnull [] cbuf, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            int remaining = end - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            System.arraycopy(cbuf, off, buf, pos, len);
            pos += len;
        }

        @Override
        protected void doWrite(@Nonnull String str, int off, int len) throws IOException {
            if (len == 0) {
                return;
            }
            int remaining = end - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            str.getChars(off, off + len, buf, pos);
            pos += len;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    private static final class BufferWriter extends DoWriteWriter {

        private final CharBuffer buffer;

        private BufferWriter(@Nonnull CharBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int c) throws IOException {
            try {
                buffer.put((char) c);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        protected void doWrite(char @Nonnull [] c, int off, int len) throws IOException {
            try {
                buffer.put(c, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        protected void doWrite(@Nonnull String str, int off, int len) throws IOException {
            try {
                buffer.put(str, off, off + len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    private static final class BytesWriter extends DoWriteWriter {

        private final @Nonnull OutputStream outputStream;
        private final @Nonnull CharsetEncoder encoder;
        private final @Nonnull CharBuffer inBuffer;
        private final @Nonnull ByteBuffer outBuffer;

        private boolean closed = false;
        private char @Nullable [] writeOneBuf;

        private BytesWriter(@Nonnull OutputStream outputStream, @Nonnull CharsetEncoder encoder, int inBufferSize, int outBufferSize) {
            this.outputStream = outputStream;
            this.encoder = encoder;
            this.inBuffer = CharBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = ByteBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private BytesWriter(@Nonnull OutputStream outputStream, @Nonnull Charset charset, int inBufferSize, int outBufferSize) {
            this(
                outputStream,
                charset.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        private BytesWriter(@Nonnull OutputStream outputStream, @Nonnull Charset charset) {
            this(outputStream, charset, 64, 64);
        }

        @Override
        public void write(int c) throws IOException {
            if (writeOneBuf == null) {
                writeOneBuf = new char[1];
            }
            writeOneBuf[0] = (char) c;
            write(writeOneBuf, 0, 1);
        }

        @Override
        protected void doWrite(char @Nonnull [] cbuf, int off, int len) throws IOException {
            doWrite0(cbuf, off, len);
        }

        @Override
        protected void doWrite(@Nonnull String str, int off, int len) throws IOException {
            doWrite0(str, off, len);
        }

        private void doWrite0(@Nonnull Object cbuf, int off, int len) throws IOException {
            checkClosed();
            if (len == 0) {
                return;
            }
            int count = 0;
            while (count < len) {
                int actualLen = Math.min(inBuffer.remaining(), len - count);
                if (cbuf instanceof char[]) {
                    inBuffer.put((char[]) cbuf, off + count, actualLen);
                } else {
                    inBuffer.put((String) cbuf, off + count, off + count + actualLen);
                }
                inBuffer.flip();
                while (true) {
                    CoderResult coderResult = encoder.encode(inBuffer, outBuffer, false);
                    if (coderResult.isOverflow()) {
                        outBuffer.flip();
                        BufferKit.readTo(outBuffer, outputStream);
                        // outputStream.append(outBuffer);
                        outBuffer.clear();
                    } else if (coderResult.isUnderflow()) {
                        outBuffer.flip();
                        BufferKit.readTo(outBuffer, outputStream);
                        // appender.append(outBuffer);
                        outBuffer.clear();
                        break;
                    } else {
                        throw new IOException(decodingFailed(coderResult));
                    }
                }
                count += actualLen;
                inBuffer.compact();
            }
        }

        @Override
        public void flush() throws IOException {
            checkClosed();
            outputStream.flush();
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            outputStream.close();
            closed = true;
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException(STREAM_CLOSED);
            }
        }
    }

    private static final class LimitedWriter extends DoWriteWriter {

        private final @Nonnull Writer out;
        private final long limit;

        private long pos;

        private LimitedWriter(@Nonnull Writer out, long limit) {
            this.out = out;
            this.limit = limit;
        }

        @Override
        public void write(int c) throws IOException {
            if (pos >= limit) {
                throw new IOException(insufficientRemainingSpace(1, 0));
            }
            out.write(c);
            pos++;
        }

        @Override
        protected void doWrite(char @Nonnull [] cbuf, int off, int len) throws IOException {
            long remaining = limit - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            out.write(cbuf, off, len);
            pos += len;
        }

        @Override
        protected void doWrite(@Nonnull String str, int off, int len) throws IOException {
            long remaining = limit - pos;
            if (remaining < len) {
                throw new IOException(insufficientRemainingSpace(len, remaining));
            }
            out.write(str, off, len);
            pos += len;
        }

        @Override
        public void flush() throws IOException {
            out.flush();
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    private static final class NullWriter extends DoWriteWriter {

        private static final @Nonnull NullWriter SINGLETON = new NullWriter();

        @Override
        public void write(int c) {
        }

        @Override
        protected void doWrite(char @Nonnull [] cbuf, int off, int len) {
        }

        @Override
        protected void doWrite(@Nonnull String str, int off, int len) {
        }

        @Override
        public Writer append(CharSequence csq) {
            return this;
        }

        @Override
        public Writer append(CharSequence csq, int start, int end) {
            IOChecker.checkOffLen(start, end - start, csq.length());
            return this;
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }

    private IOBack() {
    }
}
