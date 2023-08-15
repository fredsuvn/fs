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
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return 0;
            }
            int actualLength = Math.min(buffer.remaining(), len);
            if (actualLength <= 0) {
                return -1;
            }
            buffer.get(b, off, actualLength);
            return actualLength;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized int read() throws IOException {
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
    public synchronized long skip(long n) throws IOException {
        try {
            int remaining = buffer.remaining();
            if (remaining <= 0) {
                return 0;
            }
            if (n >= remaining) {
                buffer.position(buffer.limit());
                return remaining;
            }
            long k = buffer.position() + n;
            buffer.position((int) k);
            return k;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int available() {
        return buffer.remaining();
    }

    @Override
    public synchronized void mark(int readlimit) {
        buffer.mark();
    }

    @Override
    public synchronized void reset() throws IOException {
        try {
            buffer.reset();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean markSupported() {
        return true;
    }
}
