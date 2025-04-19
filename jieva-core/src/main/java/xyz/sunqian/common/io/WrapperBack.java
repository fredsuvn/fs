package xyz.sunqian.common.io;

import xyz.sunqian.annotations.Nullable;

import java.io.Closeable;
import java.io.Flushable;
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

import static xyz.sunqian.common.base.JieCheck.checkOffsetLength;

final class WrapperBack {

    static InputStream in(byte[] array) {
        return new BytesInputStream(array);
    }

    static InputStream in(byte[] array, int offset, int length) {
        return new BytesInputStream(array, offset, length);
    }

    static InputStream in(ByteBuffer buffer) {
        return new BufferInputStream(buffer);
    }

    static InputStream in(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomInputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static InputStream in(Reader reader, Charset charset) {
        return new ReaderInputStream(reader, charset);
    }

    static Reader reader(char[] array) {
        return new BufferReader(array);
    }

    static Reader reader(char[] array, int offset, int length) {
        return new BufferReader(array, offset, length);
    }

    static Reader reader(CharSequence chars) {
        return new BufferReader(chars);
    }

    static Reader reader(CharBuffer buffer) {
        return new BufferReader(buffer);
    }

    static Reader reader(InputStream inputStream, Charset charset) {
        return new BytesReader(inputStream, charset);
    }

    static InputStream emptyIn() {
        return EmptyInputStream.SINGLETON;
    }

    static Reader emptyReader() {
        return EmptyReader.SINGLETON;
    }

    static OutputStream out(byte[] array) {
        return new BytesOutputStream(array);
    }

    static OutputStream out(byte[] array, int offset, int length) {
        return new BytesOutputStream(array, offset, length);
    }

    static OutputStream out(ByteBuffer buffer) {
        return new BufferOutputStream(buffer);
    }

    static OutputStream out(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomOutputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    static OutputStream out(Appendable appender, Charset charset) {
        return new AppenderOutputStream(appender, charset);
    }

    static Writer writer(char[] array) {
        return new BufferWriter(array);
    }

    static Writer writer(char[] array, int offset, int length) {
        return new BufferWriter(array, offset, length);
    }

    static Writer writer(CharBuffer buffer) {
        return new BufferWriter(buffer);
    }

    static Writer writer(OutputStream outputStream, Charset charset) {
        return new BytesWriter(outputStream, charset);
    }

    static OutputStream nullOut() {
        return NullOutputStream.SINGLETON;
    }

    static Writer nullWriter() {
        return NullWriter.SINGLETON;
    }

    private static final class BytesInputStream extends InputStream {

        private final byte[] buf;
        private int pos;
        private int mark = -1;
        private final int count;

        BytesInputStream(byte[] buf) {
            this(buf, 0, buf.length);
        }

        BytesInputStream(byte[] buf, int offset, int length) {
            checkOffsetLength(buf.length, offset, length);
            this.buf = buf;
            this.pos = offset;
            this.count = Math.min(offset + length, buf.length);
        }

        public int read() {
            return (pos < count) ? (buf[pos++] & 0xff) : -1;
        }

        public int read(byte[] b, int off, int len) {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return 0;
            }
            if (pos >= count) {
                return -1;
            }
            int avail = count - pos;
            avail = Math.min(len, avail);
            System.arraycopy(buf, pos, b, off, avail);
            pos += avail;
            return avail;
        }

        public long skip(long n) {
            if (n <= 0) {
                return 0;
            }
            int avail = count - pos;
            avail = (int) Math.min(n, avail);
            if (avail <= 0) {
                return 0;
            }
            pos += avail;
            return avail;
        }

        public int available() {
            return count - pos;
        }

        public boolean markSupported() {
            return true;
        }

        public void mark(int readAheadLimit) {
            mark = pos;
        }

        public void reset() throws IOException {
            if (mark < 0) {
                throw new IOException("Mark has not been set.");
            }
            pos = mark;
        }

        public void close() {
        }
    }

    private static final class BufferInputStream extends InputStream {

        private final ByteBuffer buffer;

        BufferInputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() throws IOException {
            if (!buffer.hasRemaining()) {
                return -1;
            }
            return read0();
        }

        private int read0() throws IOException {
            try {
                return buffer.get() & 0xff;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return 0;
            }
            if (!buffer.hasRemaining()) {
                return -1;
            }
            int avail = Math.min(buffer.remaining(), len);
            read0(b, off, avail);
            return avail;
        }

        private void read0(byte[] b, int off, int avail) throws IOException {
            try {
                buffer.get(b, off, avail);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }
            int avail = (int) Math.min(buffer.remaining(), n);
            if (avail <= 0) {
                return 0;
            }
            skip0(avail);
            return avail;
        }

        private void skip0(int avail) throws IOException {
            try {
                buffer.position(buffer.position() + avail);
            } catch (Exception e) {
                throw new IOException(e);
            }
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

        @Override
        public void close() {
        }
    }

    private static final class ReaderInputStream extends InputStream {

        private final Reader reader;
        private final CharsetEncoder encoder;
        private final CharBuffer inBuffer;
        private final ByteBuffer outBuffer;
        private boolean endOfInput;
        private boolean closed = false;
        private final byte[] buf = {0};

        private ReaderInputStream(Reader reader, CharsetEncoder encoder, int inBufferSize, int outBufferSize) {
            this.reader = reader;
            this.encoder = encoder;
            this.inBuffer = CharBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = ByteBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private ReaderInputStream(Reader reader, Charset charset, int inBufferSize, int outBufferSize) {
            this(
                reader,
                charset.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        ReaderInputStream(Reader reader, Charset charset) {
            this(reader, charset, 64, 64);
        }

        @Override
        public int read() throws IOException {
            int readNum = read(buf, 0, 1);
            return readNum == -1 ? -1 : (buf[0] & 0xff);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            checkClosed();
            if (len <= 0) {
                return 0;
            }
            int readNum = read0(b, off, len, true);
            return readNum == 0 ? -1 : readNum;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            return read0(null, 0, (int) n, false);
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

        private int read0(@Nullable byte[] b, int off, int len, boolean fillBytes) throws IOException {
            int readNum = 0;
            int offset = off;
            int remaining = len;
            while (true) {
                if (outBuffer.hasRemaining()) {
                    int avail = Math.min(outBuffer.remaining(), remaining);
                    if (fillBytes) {
                        outBuffer.get(b, offset, avail);
                    } else {
                        outBuffer.position(outBuffer.position() + avail);
                    }
                    offset += avail;
                    remaining -= avail;
                    readNum += avail;
                } else if (endOfInput) {
                    if (inBuffer.hasRemaining()) {
                        encodeBuffer();
                    } else {
                        break;
                    }
                } else {
                    fillBuffer();
                }
                if (remaining <= 0) {
                    break;
                }
            }
            return readNum;
        }

        private void fillBuffer() throws IOException {
            inBuffer.compact();
            int readSize = reader.read(inBuffer);
            if (readSize == -1) {
                endOfInput = true;
            }
            inBuffer.flip();
            encodeBuffer();
        }

        private void encodeBuffer() throws IOException {
            outBuffer.compact();
            CoderResult coderResult = encoder.encode(inBuffer, outBuffer, endOfInput);
            if (coderResult.isUnderflow() || coderResult.isOverflow()) {
                outBuffer.flip();
                return;
            }
            throw new IOException("Chars encoding failed: " + coderResult);
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class RandomInputStream extends InputStream {

        private final RandomAccessFile random;
        private long mark = -1;

        RandomInputStream(RandomAccessFile random, long initialSeek) throws IOException {
            this.random = random;
            this.random.seek(initialSeek);
        }

        @Override
        public int read() throws IOException {
            return random.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return 0;
            }
            return random.read(b, off, len);
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }
            return random.skipBytes((int) n);
        }

        @Override
        public int available() throws IOException {
            return (int) (random.length() - random.getFilePointer());
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readlimit) {
            try {
                this.mark = random.getFilePointer();
            } catch (IOException e) {
                throw new IORuntimeException(e);
            }
        }

        @Override
        public void reset() throws IOException {
            if (mark < 0) {
                throw new IOException("Mark has not been set.");
            }
            random.seek(mark);
        }

        @Override
        public void close() throws IOException {
            random.close();
        }
    }

    private static final class BufferReader extends Reader {

        private final CharBuffer buffer;

        BufferReader(char[] cbuf) {
            this(cbuf, 0, cbuf.length);
        }

        BufferReader(char[] cbuf, int offset, int length) {
            checkOffsetLength(cbuf.length, offset, length);
            this.buffer = CharBuffer.wrap(cbuf, offset, length);
        }

        BufferReader(CharSequence chars) {
            this.buffer = CharBuffer.wrap(chars);
        }

        BufferReader(CharBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public int read() throws IOException {
            if (buffer.remaining() <= 0) {
                return -1;
            }
            return read0();
        }

        private int read0() throws IOException {
            try {
                return buffer.get() & 0xffff;
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read(char[] c, int off, int len) throws IOException {
            checkOffsetLength(c.length, off, len);
            if (len <= 0) {
                return 0;
            }
            if (!buffer.hasRemaining()) {
                return -1;
            }
            int avail = Math.min(buffer.remaining(), len);
            read0(c, off, avail);
            return avail;
        }

        private void read0(char[] c, int off, int avail) throws IOException {
            try {
                buffer.get(c, off, avail);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public int read(CharBuffer target) throws IOException {
            return buffer.read(target);
        }

        @Override
        public long skip(long n) throws IOException {
            if (n <= 0) {
                return 0;
            }
            int avail = (int) Math.min(buffer.remaining(), n);
            if (avail <= 0) {
                return 0;
            }
            skip0(avail);
            return avail;
        }

        private void skip0(int avail) throws IOException {
            try {
                buffer.position(buffer.position() + avail);
            } catch (Exception e) {
                throw new IOException(e);
            }
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

    private static final class BytesReader extends Reader {

        private final InputStream inputStream;
        private final CharsetDecoder decoder;
        private final ByteBuffer inBuffer;
        private final CharBuffer outBuffer;
        private boolean endOfInput;
        private boolean closed = false;
        private final char[] cbuf = {0};

        private BytesReader(InputStream inputStream, CharsetDecoder decoder, int inBufferSize, int outBufferSize) {
            this.inputStream = inputStream;
            this.decoder = decoder;
            this.inBuffer = ByteBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = CharBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private BytesReader(InputStream inputStream, Charset charset, int inBufferSize, int outBufferSize) {
            this(
                inputStream,
                charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        BytesReader(InputStream inputStream, Charset charset) {
            this(inputStream, charset, 64, 64);
        }

        @Override
        public int read() throws IOException {
            int readNum = read(cbuf, 0, 1);
            return readNum == -1 ? -1 : (cbuf[0] & 0xffff);
        }

        @Override
        public int read(char[] c, int off, int len) throws IOException {
            checkOffsetLength(c.length, off, len);
            checkClosed();
            if (len <= 0) {
                return 0;
            }
            int readNum = read0(c, off, len, true);
            return readNum == 0 ? -1 : readNum;
        }

        @Override
        public long skip(long n) throws IOException {
            checkClosed();
            if (n <= 0) {
                return 0;
            }
            return read0(null, 0, (int) n, false);
        }

        @Override
        public boolean ready() throws IOException {
            return inputStream.available() > 0;
        }

        @Override
        public void close() throws IOException {
            inputStream.close();
            closed = true;
        }

        private int read0(@Nullable char[] c, int off, int len, boolean fill) throws IOException {
            int readNum = 0;
            int offset = off;
            int remaining = len;
            while (true) {
                if (outBuffer.hasRemaining()) {
                    int avail = Math.min(outBuffer.remaining(), remaining);
                    if (fill) {
                        outBuffer.get(c, offset, avail);
                    } else {
                        outBuffer.position(outBuffer.position() + avail);
                    }
                    offset += avail;
                    remaining -= avail;
                    readNum += avail;
                } else if (endOfInput) {
                    if (inBuffer.hasRemaining()) {
                        encodeBuffer();
                    } else {
                        break;
                    }
                } else {
                    fillBuffer();
                }
                if (remaining <= 0) {
                    break;
                }
            }
            return readNum;
        }

        private void fillBuffer() throws IOException {
            inBuffer.compact();
            int readSize = inputStream.read(inBuffer.array(), inBuffer.position(), inBuffer.remaining());
            if (readSize == -1) {
                endOfInput = true;
            } else {
                inBuffer.position(inBuffer.position() + readSize);
            }
            inBuffer.flip();
            encodeBuffer();
        }

        private void encodeBuffer() throws IOException {
            outBuffer.compact();
            CoderResult coderResult = decoder.decode(inBuffer, outBuffer, endOfInput);
            if (coderResult.isUnderflow() || coderResult.isOverflow()) {
                outBuffer.flip();
                return;
            }
            throw new IOException("Bytes decoding failed: " + coderResult);
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class EmptyInputStream extends InputStream {

        private static final EmptyInputStream SINGLETON = new EmptyInputStream();

        @Override
        public int read() {
            return -1;
        }
    }

    private static final class EmptyReader extends Reader {

        private static final EmptyReader SINGLETON = new EmptyReader();

        @Override
        public int read(char[] cbuf, int off, int len) {
            return -1;
        }

        @Override
        public void close() {
        }
    }

    private static final class BytesOutputStream extends OutputStream {

        private final byte[] buf;
        private final int end;
        private int pos;

        BytesOutputStream(byte[] buf) {
            this(buf, 0, buf.length);
        }

        BytesOutputStream(byte[] buf, int offset, int length) {
            checkOffsetLength(buf.length, offset, length);
            this.buf = buf;
            this.end = offset + length;
            this.pos = offset;
        }

        @Override
        public void write(int b) throws IOException {
            if (end - pos < 1) {
                throw new IOException("The backing array has insufficient capacity remaining.");
            }
            buf[pos] = (byte) b;
            pos++;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return;
            }
            if (end - pos < len) {
                throw new IOException("The backing array has insufficient capacity remaining.");
            }
            System.arraycopy(b, off, buf, pos, len);
            pos += len;
        }
    }

    private static final class BufferOutputStream extends OutputStream {

        private final ByteBuffer buffer;

        BufferOutputStream(ByteBuffer buffer) {
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
        public void write(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return;
            }
            try {
                buffer.put(b, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private static final class AppenderOutputStream extends OutputStream {

        private final Appendable appender;
        private final CharsetDecoder decoder;
        private final ByteBuffer inBuffer;

        // Should be keep flush to empty.
        private final CharBuffer outBuffer;

        private boolean closed = false;
        private final byte[] buf = {0};

        private AppenderOutputStream(Appendable appender, CharsetDecoder decoder, int inBufferSize, int outBufferSize) {
            this.appender = appender;
            this.decoder = decoder;
            this.inBuffer = ByteBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = CharBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private AppenderOutputStream(Appendable appender, Charset charset, int inBufferSize, int outBufferSize) {
            this(
                appender,
                charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        AppenderOutputStream(Appendable appender, Charset charset) {
            this(appender, charset, 64, 64);
        }

        @Override
        public void write(int b) throws IOException {
            buf[0] = (byte) b;
            write(buf, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            checkClosed();
            if (len <= 0) {
                return;
            }
            int offset = off;
            int remaining = len;
            while (remaining > 0) {
                inBuffer.compact();
                int rollbackLimit = inBuffer.position();
                int avail = Math.min(inBuffer.remaining(), remaining);
                inBuffer.put(b, offset, avail);
                remaining -= avail;
                offset += avail;
                inBuffer.flip();
                decodeBuffer(rollbackLimit, false);
            }
        }

        @Override
        public void flush() throws IOException {
            checkClosed();
            if (appender instanceof Flushable) {
                ((Flushable) appender).flush();
            }
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            if (appender instanceof Closeable) {
                ((Closeable) appender).close();
            } else if (appender instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) appender).close();
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            closed = true;
        }

        private void decodeBuffer(int rollbackLimit, boolean endOfInput) throws IOException {
            while (true) {
                outBuffer.compact();
                CoderResult coderResult = decoder.decode(inBuffer, outBuffer, endOfInput);
                if (coderResult.isUnderflow()) {
                    outBuffer.flip();
                    flushBuffer(rollbackLimit);
                    return;
                }
                if (coderResult.isOverflow()) {
                    outBuffer.flip();
                    flushBuffer(rollbackLimit);
                    continue;
                }
                throw new IOException("Bytes decoding failed: " + coderResult);
            }
        }

        private void flushBuffer(int rollbackLimit) throws IOException {
            if (!outBuffer.hasRemaining()) {
                return;
            }
            try {
                if (appender instanceof Writer) {
                    ((Writer) appender).write(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
                    outBuffer.position(outBuffer.limit());
                } else if (appender instanceof StringBuilder) {
                    ((StringBuilder) appender).append(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
                    outBuffer.position(outBuffer.limit());
                } else if (appender instanceof StringBuffer) {
                    ((StringBuffer) appender).append(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
                    outBuffer.position(outBuffer.limit());
                } else if (appender instanceof CharBuffer) {
                    ((CharBuffer) appender).put(outBuffer);
                } else {
                    while (outBuffer.hasRemaining()) {
                        appender.append(outBuffer.get());
                    }
                }
            } catch (IOException e) {
                rollbackBuffer(rollbackLimit);
                throw e;
            } catch (Exception e) {
                rollbackBuffer(rollbackLimit);
                throw new IOException(e);
            }
        }

        private void rollbackBuffer(int limit) {
            inBuffer.position(0);
            inBuffer.limit(limit);
            outBuffer.position(0);
            outBuffer.limit(0);
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class RandomOutputStream extends OutputStream {

        private final RandomAccessFile random;

        RandomOutputStream(RandomAccessFile random, long initialSeek) throws IOException {
            this.random = random;
            this.random.seek(initialSeek);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkOffsetLength(b.length, off, len);
            if (len <= 0) {
                return;
            }
            random.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            random.write(b);
        }

        @Override
        public void flush() throws IOException {
            random.getFD().sync();
        }

        @Override
        public void close() throws IOException {
            random.close();
        }
    }

    private static final class BufferWriter extends AbstractWriter {

        private final CharBuffer buffer;

        BufferWriter(char[] cbuf) {
            this(cbuf, 0, cbuf.length);
        }

        BufferWriter(char[] cbuf, int offset, int length) {
            checkOffsetLength(cbuf.length, offset, length);
            this.buffer = CharBuffer.wrap(cbuf, offset, length);
        }

        BufferWriter(CharBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        protected void doWrite(char c) {
            buffer.put(c);
        }

        @Override
        protected void doWrite(char[] c, int off, int len) {
            buffer.put(c, off, len);
        }

        @Override
        protected void doWrite(String str, int off, int len) {
            buffer.put(str, off, off + len);
        }

        @Override
        protected void doAppend(CharSequence csq, int start, int end) {
            buffer.append(csq, start, end);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws IOException {
        }
    }

    private static final class BytesWriter extends AbstractWriter {

        private final OutputStream outputStream;
        private final CharsetEncoder encoder;
        private final CharBuffer inBuffer;

        // Should be keep flush to empty.
        private final ByteBuffer outBuffer;

        private boolean closed = false;
        private final char[] cbuf = {0};

        private BytesWriter(OutputStream outputStream, CharsetEncoder encoder, int inBufferSize, int outBufferSize) {
            this.outputStream = outputStream;
            this.encoder = encoder;
            this.inBuffer = CharBuffer.allocate(inBufferSize);
            this.inBuffer.flip();
            this.outBuffer = ByteBuffer.allocate(outBufferSize);
            this.outBuffer.flip();
        }

        private BytesWriter(OutputStream outputStream, Charset charset, int inBufferSize, int outBufferSize) {
            this(
                outputStream,
                charset.newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        BytesWriter(OutputStream outputStream, Charset charset) {
            this(outputStream, charset, 64, 64);
        }

        @Override
        protected void doWrite(char c) throws Exception {
            cbuf[0] = c;
            doWrite(cbuf, 0, 1);
        }

        @Override
        protected void doWrite(char[] c, int off, int len) throws Exception {
            doWrite0(c, off, len);
        }

        @Override
        protected void doWrite(String str, int off, int len) throws Exception {
            doWrite0(str, off, len);
        }

        @Override
        protected void doAppend(CharSequence csq, int start, int end) throws Exception {
            doWrite0(csq, start, end - start);
        }

        private void doWrite0(Object c, int off, int len) throws Exception {
            checkClosed();
            int offset = off;
            int remaining = len;
            while (remaining > 0) {
                inBuffer.compact();
                int rollbackLimit = inBuffer.position();
                int avail = Math.min(inBuffer.remaining(), remaining);
                if (c instanceof char[]) {
                    inBuffer.put((char[]) c, offset, avail);
                } else if (c instanceof String) {
                    inBuffer.put((String) c, offset, offset + avail);
                } else {
                    CharSequence cs = (CharSequence) c;
                    for (int i = 0; i < avail; i++) {
                        inBuffer.put(cs.charAt(i + offset));
                    }
                }
                remaining -= avail;
                offset += avail;
                inBuffer.flip();
                encodeBuffer(rollbackLimit, false);
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

        private void encodeBuffer(int rollbackLimit, boolean endOfInput) throws IOException {
            while (true) {
                outBuffer.compact();
                CoderResult coderResult = encoder.encode(inBuffer, outBuffer, endOfInput);
                if (coderResult.isUnderflow()) {
                    outBuffer.flip();
                    flushBuffer(rollbackLimit);
                    return;
                }
                if (coderResult.isOverflow()) {
                    outBuffer.flip();
                    flushBuffer(rollbackLimit);
                    continue;
                }
                throw new IOException("Chars encoding failed: " + coderResult);
            }
        }

        private void flushBuffer(int rollbackLimit) throws IOException {
            if (!outBuffer.hasRemaining()) {
                return;
            }
            try {
                outputStream.write(outBuffer.array(), outBuffer.position(), outBuffer.remaining());
                outBuffer.position(outBuffer.position() + outBuffer.remaining());
            } catch (IOException e) {
                rollbackBuffer(rollbackLimit);
                throw e;
            }
        }

        private void rollbackBuffer(int limit) {
            inBuffer.position(0);
            inBuffer.limit(limit);
            outBuffer.position(0);
            outBuffer.limit(0);
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class NullOutputStream extends OutputStream {

        private static final NullOutputStream SINGLETON = new NullOutputStream();

        @Override
        public void write(int b) {
        }
    }

    private static final class NullWriter extends Writer {

        private static final NullWriter SINGLETON = new NullWriter();

        @Override
        public void write(char[] cbuf, int off, int len) {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
