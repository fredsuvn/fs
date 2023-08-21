package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.OutputStream;

final class LimitedOutputStream extends OutputStream {

    private final OutputStream source;
    private long remaining;

    LimitedOutputStream(OutputStream source, long limit) {
        try {
            FsCheck.checkArgument(limit >= 0, "limit must >= 0.");
            this.source = source;
            this.remaining = limit;
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return;
            }
            if (len > remaining) {
                throw new IOException("Written length over limit, len: " + len + ", remaining: " + remaining + ".");
            }
            source.write(b, off, len);
            remaining -= len;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            if (remaining <= 0) {
                throw new IOException("Written length over limit, remaining: " + remaining + ".");
            }
            source.write(b);
            remaining--;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() throws IOException {
        source.flush();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}