package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieChars;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.*;

/**
 * This class provides implementations and utilities for {@link InputStream}/{@link Reader}.
 *
 * @author sunqian
 */
public class JieInput {

    /**
     * Wraps given array as an {@link InputStream}.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array given array
     * @return given array as an {@link InputStream}
     */
    public static InputStream wrap(byte[] array) {
        return new BytesInputStream(array);
    }

    /**
     * Wraps given array as an {@link InputStream} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link ByteArrayInputStream} but is not the same, its methods are not modified
     * by {@code synchronized} thus do not guarantee thread safety. It also supports mark/reset operations, and the
     * close method does nothing (similar to {@link ByteArrayInputStream}).
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link InputStream}
     */
    public static InputStream wrap(byte[] array, int offset, int length) {
        return new BytesInputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link InputStream}.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link InputStream}
     */
    public static InputStream wrap(ByteBuffer buffer) {
        return new BufferInputStream(buffer);
    }

    /**
     * Wraps given random access file as an {@link InputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It supports mark/reset operations, and first seeks to specified
     * initial file pointer when creating the stream and re-seeks if calls reset method. The close method will close the
     * file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link InputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static InputStream wrap(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomInputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Wraps given reader as an {@link InputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader given reader
     * @return given reader as an {@link InputStream}
     */
    public static InputStream wrap(Reader reader) {
        return wrap(reader, JieChars.defaultCharset());
    }

    /**
     * Wraps given reader as an {@link InputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. It does support mark/reset operations. The read position of the
     * reader and stream may not correspond, the close method will close both reader and stream at their current
     * positions.
     *
     * @param reader  given reader
     * @param charset specified charset
     * @return given reader as an {@link InputStream}
     */
    public static InputStream wrap(Reader reader, Charset charset) {
        return new ReaderInputStream(reader, charset);
    }

    /**
     * Wraps given array as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array given array
     * @return given array as an {@link Reader}
     */
    public static Reader wrap(char[] array) {
        return new BufferReader(array);
    }

    /**
     * Wraps given array as an {@link Reader} from specified offset up to specified length.
     * <p>
     * The returned stream is similar to {@link CharArrayReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Reader}
     */
    public static Reader wrap(char[] array, int offset, int length) {
        return new BufferReader(array, offset, length);
    }

    /**
     * Wraps given chars as an {@link Reader}.
     * <p>
     * The returned stream is similar to {@link StringReader} but is not the same. Returned reader does not guarantee
     * thread safety. It supports mark/reset operations, and the close method does nothing.
     *
     * @param chars given chars
     * @return given array as an {@link Reader}
     */
    public static Reader wrap(CharSequence chars) {
        return new BufferReader(chars);
    }

    /**
     * Wraps given buffer as an {@link Reader}.
     * <p>
     * Returned reader does not guarantee thread safety. It supports mark/reset operations, and the close method does
     * nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Reader}
     */
    public static Reader wrap(CharBuffer buffer) {
        return new BufferReader(buffer);
    }

    /**
     * Wraps given stream as an {@link Reader} with {@link JieChars#defaultCharset()}.
     * <p>
     * The returned stream is similar to {@link InputStreamReader} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. It does support mark/reset operations. The read
     * position of the reader and stream may not correspond, the close method will close both reader and stream at their
     * current positions.
     *
     * @param inputStream given stream
     * @return given stream as an {@link Reader}
     */
    public static Reader wrap(InputStream inputStream) {
        return wrap(inputStream, JieChars.defaultCharset());
    }

    /**
     * Wraps given stream as an {@link Reader} with specified charset.
     * <p>
     * The returned stream is similar to {@link InputStreamReader} but is not the same, its methods are not modified by
     * {@code synchronized} thus do not guarantee thread safety. It does support mark/reset operations. The read
     * position of the reader and stream may not correspond, the close method will close both reader and stream at their
     * current positions.
     *
     * @param inputStream given stream
     * @param charset     specified charset
     * @return given stream as an {@link Reader}
     */
    public static Reader wrap(InputStream inputStream, Charset charset) {
        return new BytesReader(inputStream, charset);
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
            IOMisc.checkReadBounds(buf, offset, length);
            this.buf = buf;
            this.pos = offset;
            this.count = Math.min(offset + length, buf.length);
        }

        public int read() {
            return (pos < count) ? (buf[pos++] & 0xff) : -1;
        }

        public int read(byte[] b, int off, int len) {
            IOMisc.checkReadBounds(b, off, len);
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
            IOMisc.checkReadBounds(b, off, len);
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
            IOMisc.checkReadBounds(b, off, len);
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
            IOMisc.checkReadBounds(b, off, len);
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
            IOMisc.checkReadBounds(cbuf, offset, length);
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
            IOMisc.checkReadBounds(c, off, len);
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
            IOMisc.checkReadBounds(c, off, len);
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
}
