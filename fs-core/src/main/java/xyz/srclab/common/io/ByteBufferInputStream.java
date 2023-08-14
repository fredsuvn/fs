package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

final class ByteBufferInputStream extends InputStream {

    private final ByteBuffer buffer;

    ByteBufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read(byte[] array, int off, int len) throws IOException {
        FsCheck.checkRangeInBounds(off, off + len, 0, array.length);
        if (len == 0) {
            return 0;
        }
        try {
            int actualLength = Math.min(buffer.remaining(), len);
            if (actualLength <= 0) {
                return -1;
            }
            buffer.get(array, off, actualLength);
            return actualLength;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read() throws IOException {
        try {
            if (buffer.remaining() <= 0) {
                return -1;
            }
            return buffer.get() & 0xFF;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        FsCheck.checkArgument(n>=0, "Skip number must > 0");
        if (n <= buffer.remaining()) {
            buffer.position((int) (buffer.position() + n));
            return n;
        }

        return super.skip(n);
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        super.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        super.reset();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void close() throws IOException {
    }
}
